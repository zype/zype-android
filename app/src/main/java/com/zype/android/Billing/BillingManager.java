package com.zype.android.Billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 01.07.2017.
 */

public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = BillingManager.class.getSimpleName();

    private final Context context;
    private BillingClient mBillingClient;
    private final BillingUpdatesListener mBillingUpdatesListener;
    private List<Purchase> purchases;

    /**
     * True if billing service is connected now.
     */
    private boolean mIsServiceConnected;

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingClient.BillingResponseCode int result);
        void onPurchasesUpdated(List<Purchase> purchases);
        void onPurchaseCancelled();
    }

    public BillingManager(Context context, final BillingUpdatesListener updatesListener) {
        Log.d(TAG, "Creating Billing client.");

        this.context = context;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();

        Log.d(TAG, "Starting setup.");
        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startBillingServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notifying the listener that billing client is ready
                mBillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                queryPurchases();
            }
        });
    }

    public void startBillingServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d(TAG, "Setup finished. Response code: " + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        }
        else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startBillingServiceConnection(runnable);
        }
    }

    //
    // Billing service queries
    //

    /**
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    public boolean areSubscriptionsSupported() {
        BillingResult responseCode = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        if (responseCode.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }

        return responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(SkuType.INAPP);
                Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time) + "ms");
                // If there are subscriptions supported, we add subscription rows as well
                if (areSubscriptionsSupported()) {
                    Purchase.PurchasesResult subscriptionResult = mBillingClient.queryPurchases(SkuType.SUBS);
                    Log.i(TAG, "Querying purchases and subscriptions elapsed time: " + (System.currentTimeMillis() - time) + "ms");
                    Log.i(TAG, "Querying subscriptions result code: " + subscriptionResult.getResponseCode()
                            + " res: " + subscriptionResult.getPurchasesList().size());
                    if (subscriptionResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    }
                    else {
                        Log.e(TAG, "Got an error response trying to query subscription purchases");
                    }
                }
                else if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Skipped subscription purchases query since they are not supported");
                }
                else {
                    Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult.getResponseCode());
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };

        executeServiceRequest(queryToExecute);
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode() + ") was bad - quitting");
            return;
        }
        Log.d(TAG, "Query inventory was successful.");
        // Update the UI and purchases inventory with new list of purchases
//        mPurchases.clear();
        onPurchasesUpdated(result.getBillingResult(), result.getPurchasesList());
    }

    /**
     * Query SKUs available for purchase
     */
    public void querySkuDetailsAsync(@SkuType final String itemType, final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(itemType);
                mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        listener.onSkuDetailsResponse(billingResult, list);
                    }
                });
            }
        };

        executeServiceRequest(queryRequest);
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlowWithSKuDetails(Activity activity, final String skuId, final @SkuType String billingType, SkuDetails item) {
        initiatePurchaseFlow(activity, skuId, null, billingType, item);
    }

    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final Activity activity, final String skuId, final ArrayList<String> oldSkus,
                                     final @SkuType String billingType, SkuDetails item) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(item)
                        .build();
                mBillingClient.launchBillingFlow(activity, purchaseParams);
            }
        };

        executeServiceRequest(purchaseFlowRequest);
    }

    //
    // 'PurchasesUpdatedListener' implementation
    //
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            this.purchases = purchases;
            mBillingUpdatesListener.onPurchasesUpdated(purchases);
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            mBillingUpdatesListener.onPurchaseCancelled();
        }
        else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + billingResult.getResponseCode());
        }
    }

    public List<Purchase> getPurchases() {
        return this.purchases;
    }

    public Purchase getPurchase(String sku) {
        for (Purchase purchase : purchases) {
            for (String itemSku : purchase.getSkus()){
                if (itemSku.equals(sku)) return purchase;
            }

        }
        return null;
    }

    public void consumePurchase(Purchase purchase) {
        if (purchase != null) {

            ConsumeParams consumeParams = ConsumeParams
                    .newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();

            mBillingClient.consumeAsync(consumeParams, (responseCode, purchaseToken) -> {

            });
        }
    }

    /**
     * Consumes all purchases.
     * Used only for testing one-time purchases
     */
    public void clearPurchases() {
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                ConsumeResponseListener listener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Handle the success of the consume operation.
                            // For example, increase the number of coins inside the user's basket.
                        }
                    }

                };
                ConsumeParams consumeParams = ConsumeParams
                        .newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                mBillingClient.consumeAsync(consumeParams, listener);
            }
        }
    }
}
