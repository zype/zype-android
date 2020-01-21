package com.zype.android.Billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
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
        void onConsumeFinished(String token, @BillingResponse int result);
        void onPurchasesUpdated(List<Purchase> purchases);
    }

    public BillingManager(Context context, final BillingUpdatesListener updatesListener) {
        Log.d(TAG, "Creating Billing client.");

        this.context = context;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(context).setListener(this).build();

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
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);
                if (billingResponseCode == BillingResponse.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
//                mBillingClientResponseCode = billingResponseCode;
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
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
        int responseCode = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingResponse.OK;
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
                    if (subscriptionResult.getResponseCode() == BillingResponse.OK) {
                        purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    }
                    else {
                        Log.e(TAG, "Got an error response trying to query subscription purchases");
                    }
                }
                else if (purchasesResult.getResponseCode() == BillingResponse.OK) {
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
        if (mBillingClient == null || result.getResponseCode() != BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode() + ") was bad - quitting");
            return;
        }
        Log.d(TAG, "Query inventory was successful.");
        // Update the UI and purchases inventory with new list of purchases
//        mPurchases.clear();
        onPurchasesUpdated(BillingResponse.OK, result.getPurchasesList());
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
                        public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                            listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                        }
                    });
            }
        };

        executeServiceRequest(queryRequest);
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(Activity activity, final String skuId, final @SkuType String billingType) {
        initiatePurchaseFlow(activity, skuId, null, billingType);
    }

    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final Activity activity, final String skuId, final ArrayList<String> oldSkus,
                                     final @SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSku(skuId).setType(billingType)
                        .setOldSkus(oldSkus)
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
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingResponse.OK) {
            this.purchases = purchases;
            mBillingUpdatesListener.onPurchasesUpdated(purchases);
        }
        else if (responseCode == BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        }
        else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + responseCode);
        }
    }

    public List<Purchase> getPurchases() {
        return this.purchases;
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
                    public void onConsumeResponse(int result, String outToken) {
                        if (result == BillingResponse.OK) {
                            // Handle the success of the consume operation.
                            // For example, increase the number of coins inside the user's basket.
                        }
                    }
                };
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), listener);
            }
        }
    }
}
