package com.zype.android.Billing;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.squareup.otto.Subscribe;
import com.zype.android.AppConfiguration;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.MarketplaceConnectParamsBuilder;
import com.zype.android.webapi.builder.PlanParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.marketplaceconnect.MarketplaceConnectEvent;
import com.zype.android.webapi.events.plan.PlanEvent;
import com.zype.android.webapi.model.plan.PlanData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;

import static com.zype.android.Billing.BillingManager.KEY_PURCHASE_TOKEN;
import static com.zype.android.Billing.BillingManager.KEY_SIGNATURE;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class MarketplaceGateway implements BillingManager.BillingUpdatesListener {
    public static final String MARKETPLACE_GOOGLE = "Google";
    public static final String MARKETPLACE_SAMSUNG = "Samsung";

    private Context context;
    private AppConfiguration appConfiguration;
    private String appKey;
    private List<String> planIds;

    private MutableLiveData<Map<String, Subscription>> subscriptionsLiveData;
    private MutableLiveData<Boolean> subscriptionVerified = null;

    private BillingManager billingManager;
    private MarketplaceManager marketplaceManager;

    private WebApiManager api;

    public MarketplaceGateway(Context context, @NonNull AppConfiguration appConfiguration,
                              String appKey, List<String> planIds) {
        this.context = context.getApplicationContext();
        this.appConfiguration = appConfiguration;
        this.appKey = appKey;
        this.planIds = planIds;

        api = WebApiManager.getInstance();
        api.subscribe(this);
    }

    public void setup() {
        // Setup marketplace client
        if (appConfiguration.marketplace.equals(MARKETPLACE_GOOGLE)) {
            // TODO: Use subclass of MarketplaceManager instead of BillingManager
            billingManager = new BillingManager(context, this);
        }
        else if (appConfiguration.marketplace.equals(MARKETPLACE_SAMSUNG)) {
            marketplaceManager = new SamsungMarketplaceManager(context);
        }
        else {
            Logger.e("setup(): Invalid marketplace: " + appConfiguration.marketplace);
            throw new IllegalArgumentException();
        }

        // Load Zype plans
        subscriptionsLiveData = new MutableLiveData<>();
        subscriptionsLiveData.setValue(new LinkedHashMap<String, Subscription>());

        for (final String planId : planIds) {
            loadPlan(planId);
        }
    }

    private void loadPlan(String planId) {
        PlanParamsBuilder builder = new PlanParamsBuilder(planId);
        api.executeRequest(WebApiManager.Request.PLAN, builder.build());
    }

    public LiveData<Map<String, Subscription>> getSubscriptions() {
        if (subscriptionsLiveData == null || subscriptionsLiveData.getValue().isEmpty() || !setupCompleted()) {
            setup();
        }
        return subscriptionsLiveData;
    }

    private boolean setupCompleted() {
        for (Map.Entry<String, Subscription> entry : subscriptionsLiveData.getValue().entrySet()) {
            Subscription subscription = entry.getValue();
            if (subscription.getZypePlan() == null
                    || (subscription.getMarketplaceProduct() == null && subscription.getMarketplaceProductDetails() == null)) {
                return false;
            }
        }
        return true;
    }

    public MarketplaceManager getMarketplaceManager() {
        if (appConfiguration.marketplace.equals(MARKETPLACE_GOOGLE)) {
            // TODO: Use subclass of MarketplaceManager instead of BillingManager
            return billingManager;
        }
        else if (appConfiguration.marketplace.equals(MARKETPLACE_SAMSUNG)) {
            return marketplaceManager;
        }
        else {
            Logger.e("getMarketplaceManager(): Invalid marketplace: " + appConfiguration.marketplace);
            throw new IllegalArgumentException();
        }
    }


    // Subscriptions

    public Subscription findSubscriptionBySku(String sku) {
        for (Map.Entry<String, Subscription> entry : subscriptionsLiveData.getValue().entrySet()) {
            if (appConfiguration.marketplace.equals(MARKETPLACE_GOOGLE)) {
                if (entry.getValue().getZypePlan().marketplaceIds.googleplay.equals(sku)) {
                    return entry.getValue();
                }
            }
            else if (appConfiguration.marketplace.equals(MARKETPLACE_SAMSUNG)) {
                if (entry.getValue().getZypePlan().marketplaceIds.samsung.equals(sku)) {
                    return entry.getValue();
                }
            }
            else {
                Logger.e("findSubscriptionBySku(): Invalid marketplace: " + appConfiguration.marketplace);
                throw new IllegalArgumentException();
            }
        }
        return null;
    }

    public LiveData<Boolean> verifySubscription(final Subscription subscription) {
        if (subscriptionVerified == null) {
            subscriptionVerified = new MutableLiveData<>();
        }
        else {
            Logger.w("validateSubscription(): Can't verify subscription now.");
            return null;
        }

        final String sku = subscription.getMarketplaceProduct().getSku();
        getMarketplaceManager().getPurchases(MarketplaceManager.PRODUCT_TYPE_SUBSCRIPTION,
                new MarketplaceManager.PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(MarketplaceManager.PurchasesUpdatedResponse response) {
                        if (response.isSuccessful()) {
                            List<PurchaseDetails> purchases = response.getPurchases();
                            boolean purchaseFound = false;
                            for (PurchaseDetails item : purchases) {
                                if (item.getSku().equals(sku)) {
                                    purchaseFound = true;
                                    Logger.d("purchase originalJson=" + item.getOriginalData());
                                    Logger.d("purchase signature=" + item.getString(KEY_SIGNATURE));
                                    MarketplaceConnectParamsBuilder builder = new MarketplaceConnectParamsBuilder()
                                            .addConsumerId(SettingsProvider.getInstance().getConsumerId())
                                            .addPlanId(subscription.getZypePlan().id)
                                            .addPurchaseToken(item.getString(KEY_PURCHASE_TOKEN))
                                            .addReceipt(item.getOriginalData())
                                            .addSignature(item.getString(KEY_SIGNATURE));
                                    api.executeRequest(WebApiManager.Request.MARKETPLACE_CONNECT, builder.build());
                                }
                            }
                            if (!purchaseFound) {
                                Logger.e("verifySubscription(): Error get purchases.");
                                subscriptionVerified.setValue(false);
                            }
                        }
                        else {
                            Logger.e("verifySubscription(): Error get purchases.");
                            subscriptionVerified.setValue(false);
                        }
                    }
                });
        return subscriptionVerified;
    }


    // Zype API

    @Subscribe
    public void handlePlan(PlanEvent event) {
        Logger.d("handlePlan()");
        PlanData data = event.getEventData().getModelData().data;
        Subscription subscription = new Subscription();
        subscription.setZypePlan(data);

        subscriptionsLiveData.getValue().put(data.id, subscription);

        // Remove 'appConfiguration.marketplace' checking after refactoring Google marketplace
        if (appConfiguration.marketplace.equals(MARKETPLACE_GOOGLE)) {
            queryGooglePlayProduct(subscription);
        }
        else if (appConfiguration.marketplace.equals(MARKETPLACE_SAMSUNG)) {
            marketplaceManager.getProductDetails(subscription, new MarketplaceManager.ProductDetailsListener() {
                @Override
                public void onProductDetails(Object zypeProduct, MarketplaceManager.ProductDetailsResponse response) {
                    if (response.isSuccessful()) {
                        if (response.getProductDetails() != null) {
                            if (zypeProduct instanceof Subscription) {
                                ((Subscription) zypeProduct).setMarketplaceProductDetails(response.getProductDetails());
                            }
                            else if (zypeProduct instanceof Video) {

                            }
                        }
                        else {
                            Logger.e("onProductDetails(): Error retrieving sku details for " + zypeProduct.toString());
                        }
                    }
                    else {
                        Logger.e("onProductDetails(): Error retrieving sku details for " + zypeProduct.toString()
                                + ", errorMessage=" + response.getErrorMessage());
                    }
                }
            });
        }
    }

    @Subscribe
    public void handleMarketplaceConnect(MarketplaceConnectEvent event) {
        // TODO: Check response data to properly update subscription count
        SettingsProvider.getInstance().saveSubscriptionCount(1);

        if (subscriptionVerified != null) {
            subscriptionVerified.setValue(true);
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
            }
        }
        // TODO: Add retrieve plan error handling
    }


    //
    // Google Play
    //
    private void queryGooglePlayProduct(final Subscription subscription) {
        // Get sku details from marketplace (Google Play) for specified sku
        if (subscription.getZypePlan().marketplaceIds == null) {
            Logger.d("queryGooglePlayProduct(): marketplaceIds is empty.");
            return;
        }
        final String sku = subscription.getZypePlan().marketplaceIds.googleplay;
        List<String> skuList = new ArrayList<>();
        skuList.add(sku);
        billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode != BillingClient.BillingResponse.OK) {
                            Logger.e("onSkuDetailsResponse(): Error retrieving sku details from Google Play");
                        }
                        else {
                            if (skuDetailsList != null) {
                                if (skuDetailsList.size() == 0) {
                                    Logger.e("onSkuDetailsResponse(): Sku is not found in Google Play, sku=" + sku);
                                }
                                else {
                                    if (skuDetailsList.size() > 1) {
                                        Logger.w("onSkuDetailsResponse(): Unexpected number of items (" +
                                                skuDetailsList.size() + ") in Google Play, sku=" + sku);
                                    }
                                    subscription.setMarketplaceProduct(skuDetailsList.get(0));
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

}
