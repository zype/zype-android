package com.zype.android.Billing;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.MarketplaceConnectParamsBuilder;
import com.zype.android.webapi.builder.PlanParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.marketplaceconnect.MarketplaceConnectEvent;
import com.zype.android.webapi.events.plan.PlanEvent;
import com.zype.android.webapi.model.plan.PlanData;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.MarketplaceIds;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit.RetrofitError;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class MarketplaceGateway implements BillingManager.BillingUpdatesListener {
    private Context context;
    private String appKey;
    private List<String> planIds;

    private MutableLiveData<Map<String, Subscription>> subscriptionsLiveData;
    private MutableLiveData<Boolean> subscriptionVerified = null;
    private MutableLiveData<Boolean> playlistPurchaseVerified = null;

    private BillingManager billingManager;

    private WebApiManager api;

    public MarketplaceGateway(Context context, String appKey, List<String> planIds) {
        this.context = context.getApplicationContext();
        this.appKey = appKey;
        this.planIds = planIds;

        api = WebApiManager.getInstance();
        api.subscribe(this);
    }

    public void setup() {
        // Start setup marketplace (Google Play) client
        billingManager = new BillingManager(context, this);

        // Load Zype plans
        if (ZypeConfiguration.isNativeSubscriptionEnabled(context)
            || ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(context)) {
            subscriptionsLiveData = new MutableLiveData<>();
            final Map<String, Subscription> subscriptions = new LinkedHashMap<>();
            subscriptionsLiveData.setValue(subscriptions);

            for (final String planId : planIds) {
                Log.d("BillingManager","planid: "+planId);
                loadPlan(planId);
            }
        }
    }

    public void loadPlan(String planId) {
        PlanParamsBuilder builder = new PlanParamsBuilder(planId);
        api.executeRequest(WebApiManager.Request.PLAN, builder.build());
    }

    public LiveData<Map<String, Subscription>> getSubscriptions() {
        Log.d("BillingManager","getSubscriptions: subscriptionsLiveData: "+subscriptionsLiveData);
        if (subscriptionsLiveData == null || subscriptionsLiveData.getValue().isEmpty() || !setupCompleted()) {
            setup();
        }
        return subscriptionsLiveData;
    }

    private boolean setupCompleted() {
        for (Map.Entry<String, Subscription> entry : subscriptionsLiveData.getValue().entrySet()) {
            if (entry.getValue().getZypePlan() == null || entry.getValue().getMarketplace() == null) {
                return false;
            }
        }
        return true;
    }

    public BillingManager getBillingManager() {
        return billingManager;
    }

    public Subscription findSubscriptionBySku(String sku) {
        for (Map.Entry<String, Subscription> entry : subscriptionsLiveData.getValue().entrySet()) {
            if (entry.getValue().getZypePlan().thirdPartyId.equals(sku)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public LiveData<Boolean> verifySubscription(Subscription subscription) {
        if (subscriptionVerified == null) {
            subscriptionVerified = new MutableLiveData<>();
        }
//        else {
//            Logger.w("validateSubscription(): Can't verify subscription now.");
//            return null;
//        }


        Log.d("BillingManager","verifySubscription: sku: "+subscription.getMarketplace().getSku()+", getpurchases: "+billingManager.getPurchases().size());

        String sku = subscription.getMarketplace().getSku();
        for (Purchase item : billingManager.getPurchases()) {
            Log.d("BillingManager","verifySubscription: ItemSKU: "+item.getSku()+", Google sku: "+sku);
            Log.d("BillingManager","verifySubscription: getOriginalJson: "+item.getOriginalJson());
            Log.d("BillingManager","verifySubscription: getSignature: "+item.getSignature());
            Log.d("BillingManager","verifySubscription: getPurchaseToken: "+item.getPurchaseToken());
            Log.d("BillingManager","verifySubscription: planID: "+subscription.getZypePlan().id);
            Log.d("BillingManager","verifySubscription: ConsumerID: "+SettingsProvider.getInstance().getConsumerId());
            if (item.getSku().equals(sku)) {
                SubscriptionLiveData result = new SubscriptionLiveData(subscription);
                MarketplaceConnectParamsBuilder builder = new MarketplaceConnectParamsBuilder()
                        .addConsumerId(SettingsProvider.getInstance().getConsumerId())
                        .addPlanId(subscription.getZypePlan().id)
                        .addPurchaseToken(item.getPurchaseToken())
                        .addReceipt(item.getOriginalJson())
                        .addSignature(item.getSignature());
                api.executeRequest(WebApiManager.Request.MARKETPLACE_CONNECT, builder.build());
            }
            /*for (String itemSku : item.getSkus()){
                if (itemSku.equals(sku)) {
                    SubscriptionLiveData result = new SubscriptionLiveData(subscription);
                    Logger.d("purchase originalJson=" + item.getOriginalJson());
                    Logger.d("purchase signature=" + item.getSignature());
                    MarketplaceConnectParamsBuilder builder = new MarketplaceConnectParamsBuilder()
                            .addConsumerId(SettingsProvider.getInstance().getConsumerId())
                            .addPlanId(subscription.getZypePlan().id)
                            .addPurchaseToken(item.getPurchaseToken())
                            .addReceipt(item.getOriginalJson())
                            .addSignature(item.getSignature());
                    api.executeRequest(WebApiManager.Request.MARKETPLACE_CONNECT, builder.build());
                }
            }*/
        }
        return subscriptionVerified;
    }

    public LiveData<Boolean> verifyPlaylistPurchase(@NonNull Playlist playlist, PurchaseItem purchaseItem) {
        if (playlistPurchaseVerified == null) {
            playlistPurchaseVerified = new MutableLiveData<>();
        }
//        else {
//            Logger.w("validateSubscription(): Can't verify subscription now.");
//            return null;
//        }

        MarketplaceIds marketplaceIds = new Gson().fromJson(playlist.marketplaceIds, MarketplaceIds.class);
        final String sku = marketplaceIds.googleplay;
        for (Purchase item : billingManager.getPurchases()) {
            if (item.getSku().equals(sku)) {
                Logger.d("purchase originalJson=" + item.getOriginalJson());
                Logger.d("purchase signature=" + item.getSignature());
                ZypeApi.getInstance().verifyTvodPurchaseGoogle(
                        ZypeApp.appData.id,
                        ZypeApp.appData.siteId,
                        SettingsProvider.getInstance().getConsumerId(),
                        playlist.id,
                        item.getPurchaseToken(),
                        String.valueOf(purchaseItem.product.getPriceAmountMicros() / 1000000),
                        item.getOriginalJson(),
                        item.getSignature(),
                        response -> {
                            if (response.isSuccessful) {
                                if (playlistPurchaseVerified != null) {
                                    playlistPurchaseVerified.setValue(true);
                                }
                            }
                            else {
                                if (playlistPurchaseVerified != null) {
                                    playlistPurchaseVerified.setValue(false);
                                }
                            }
                        });
//                MarketplaceConnectParamsBuilder builder = new MarketplaceConnectParamsBuilder()
//                        .addConsumerId(SettingsProvider.getInstance().getConsumerId())
//                        .addPlaylistId(playlist.id)
//                        .addPurchaseToken(item.getPurchaseToken())
//                        .addReceipt(item.getOriginalJson())
//                        .addSignature(item.getSignature());
//                api.executeRequest(WebApiManager.Request.MARKETPLACE_CONNECT, builder.build());
            }
            /*for (String itemSku : item.getSkus()){
                if (itemSku.equals(sku)) {
                    Logger.d("purchase originalJson=" + item.getOriginalJson());
                    Logger.d("purchase signature=" + item.getSignature());
                    ZypeApi.getInstance().verifyTvodPurchaseGoogle(
                            ZypeApp.appData.id,
                            ZypeApp.appData.siteId,
                            SettingsProvider.getInstance().getConsumerId(),
                            playlist.id,
                            item.getPurchaseToken(),
                            String.valueOf(purchaseItem.product.getPriceAmountMicros() / 1000000),
                            item.getOriginalJson(),
                            item.getSignature(),
                            response -> {
                                if (response.isSuccessful) {
                                    if (playlistPurchaseVerified != null) {
                                        playlistPurchaseVerified.setValue(true);
                                    }
                                }
                                else {
                                    if (playlistPurchaseVerified != null) {
                                        playlistPurchaseVerified.setValue(false);
                                    }
                                }
                            });
//                MarketplaceConnectParamsBuilder builder = new MarketplaceConnectParamsBuilder()
//                        .addConsumerId(SettingsProvider.getInstance().getConsumerId())
//                        .addPlaylistId(playlist.id)
//                        .addPurchaseToken(item.getPurchaseToken())
//                        .addReceipt(item.getOriginalJson())
//                        .addSignature(item.getSignature());
//                api.executeRequest(WebApiManager.Request.MARKETPLACE_CONNECT, builder.build());
                }
            }*/

        }
        return playlistPurchaseVerified;
    }

    public void verifyVideoPurchase(@NonNull Video video, PurchaseItem purchaseItem, IPurchaseVerifyingListener listener) {
        MarketplaceIds marketplaceIds = new Gson().fromJson(video.marketplaceIds, MarketplaceIds.class);
        final String sku = marketplaceIds.googleplay;
        for (Purchase item : billingManager.getPurchases()) {
            if (item.getSku().equals(sku)) {
                Logger.d("purchase originalJson=" + item.getOriginalJson());
                Logger.d("purchase signature=" + item.getSignature());
                ZypeApi.getInstance().verifyVideoPurchaseGoogle(
                        ZypeApp.appData.id,
                        ZypeApp.appData.siteId,
                        SettingsProvider.getInstance().getConsumerId(),
                        video.id,
                        item.getPurchaseToken(),
                        String.valueOf(purchaseItem.product.getPriceAmountMicros() / 1000000),
                        item.getOriginalJson(),
                        item.getSignature(),
                        response -> {
                            if (response.isSuccessful) {
                                billingManager.consumePurchase(item);
                            }
                            else {
                                if (response.errorBody.status == 400) {
                                    Logger.e("verifyVideoPurchase(): Error verifying purchase. It is likely because it was processed earlier. Consuming this purchase.");
                                    billingManager.consumePurchase(item);
                                }
                            }
                            if (listener != null) {
                                listener.onPurchaseVerified(response.isSuccessful);
                            }
                        });
            }
            /*for (String itemSku : item.getSkus()){
                if (itemSku.equals(sku)) {
                    Logger.d("purchase originalJson=" + item.getOriginalJson());
                    Logger.d("purchase signature=" + item.getSignature());
                    ZypeApi.getInstance().verifyVideoPurchaseGoogle(
                            ZypeApp.appData.id,
                            ZypeApp.appData.siteId,
                            SettingsProvider.getInstance().getConsumerId(),
                            video.id,
                            item.getPurchaseToken(),
                            String.valueOf(purchaseItem.product.getPriceAmountMicros() / 1000000),
                            item.getOriginalJson(),
                            item.getSignature(),
                            response -> {
                                if (response.isSuccessful) {
                                    billingManager.consumePurchase(item);
                                }
                                else {
                                    if (response.errorBody.status == 400) {
                                        Logger.e("verifyVideoPurchase(): Error verifying purchase. It is likely because it was processed earlier. Consuming this purchase.");
                                        billingManager.consumePurchase(item);
                                    }
                                }
                                if (listener != null) {
                                    listener.onPurchaseVerified(response.isSuccessful);
                                }
                            });
                }
            }*/

        }
    }

    //
    // Zype API
    //

    @Subscribe
    public void handlePlan(PlanEvent event) {
        Logger.d("handlePlan()");
        PlanData data = event.getEventData().getModelData().data;
        Subscription subscription = new Subscription();
        subscription.setZypePlan(data);

        subscriptionsLiveData.getValue().put(data.id, subscription);

        queryGooglePlayProduct(subscription);
    }

    @Subscribe
    public void handleMarketplaceConnect(MarketplaceConnectEvent event) {
        // TODO: Check response data to properly update subscription count
        SettingsProvider.getInstance().saveSubscriptionCount(1);

        if (subscriptionVerified != null) {
            subscriptionVerified.setValue(true);
        }
        if (playlistPurchaseVerified != null) {
            playlistPurchaseVerified.setValue(true);
        }
    }

    @Subscribe
    public void handleError(ErrorEvent event) {
        if (event.getEventData() == WebApiManager.Request.MARKETPLACE_CONNECT) {
            Logger.e("handleError(): Marketplace connect");
            RetrofitError error = event.getError();
            if (error != null) {
                if (subscriptionVerified != null) {
                    subscriptionVerified.setValue(false);
                }
                if (playlistPurchaseVerified != null) {
                    playlistPurchaseVerified.setValue(false);
                }
            }
        }
        // TODO: Add retrieve plan error handling
    }

    //
    // Google Play
    //
    private void queryGooglePlayProduct(final Subscription subscription) {
        // Get sku details from marketplace (Google Play) for specified sku
        Log.d("BillingManager","queryGooglePlayProduct: subscription: "+subscription);
        if (subscription.getZypePlan().marketplaceIds == null) {
            Logger.d("queryGooglePlayProduct(): marketplaceIds is empty.");
            return;
        }
        final String sku = subscription.getZypePlan().marketplaceIds.googleplay;
        Log.d("BillingManager","queryGooglePlayProduct: sku: "+sku);
        List<String> skuList = new ArrayList<>();
        skuList.add(sku);
        billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                            Logger.e("onSkuDetailsResponse(): Error retrieving sku details from Google Play");
                        }
                        else {
                            if (list != null) {
                                if (list.size() == 0) {
                                    Logger.e("onSkuDetailsResponse(): Sku is not found in Google Play, sku=" + sku);
                                }
                                else {
                                    if (list.size() > 1) {
                                        Logger.w("onSkuDetailsResponse(): Unexpected number of items (" +
                                                list.size() + ") in Google Play, sku=" + sku);
                                    }
                                    subscription.setMarketplace(list.get(0));
                                }
                            }
                        }
                    }
                });
    }

    //
    // BillingManager.BillingUpdatesListener implementation
    //
    @Override
    public void onBillingClientSetupFinished() {
    }

    @Override
    public void onConsumeFinished(String token, int result) {
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
    }

    @Override
    public void onPurchaseCancelled() {
    }
}
