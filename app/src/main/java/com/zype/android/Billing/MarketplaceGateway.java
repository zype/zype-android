package com.zype.android.Billing;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.squareup.otto.Subscribe;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlanParamsBuilder;
import com.zype.android.webapi.events.plan.PlanEvent;
import com.zype.android.webapi.model.plan.PlanData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class MarketplaceGateway implements BillingManager.BillingUpdatesListener {
    private Context context;
    private String appKey;
    private List<String> planIds;

    private MutableLiveData<Map<String, Subscription>> subscriptionsLiveData;

    BillingManager billingManager;

    public MarketplaceGateway(Context context, String appKey, List<String> planIds) {
        this.context = context.getApplicationContext();
        this.appKey = appKey;
        this.planIds = planIds;
    }

    public void setup() {
        WebApiManager.getInstance().subscribe(this);

        // Start setup marketplace (Google Play) client
        List<String> skuList = new ArrayList<>();
        billingManager = new BillingManager(context, this);

        subscriptionsLiveData = new MutableLiveData<>();
        final Map<String, Subscription> subscriptions = new LinkedHashMap<>();
        subscriptionsLiveData.setValue(subscriptions);

        for (final String planId : planIds) {
            loadPlan(planId);
//            // Get Zype Plan for given plan id and add new Subscription object to the list
//            final SubscriptionLiveData subscriptionLiveData = new SubscriptionLiveData();
//            subscriptionLiveData.observe(ProcessLifecycleOwner.get(), new Observer<Subscription>() {
//                @Override
//                public void onChanged(@Nullable final Subscription subscription) {
//                    subscriptionLiveData.removeObserver(this);
//
//                    subscriptions.put(planId, subscription);
//
//                    subscriptionsLiveData.setValue(subscriptions);
//
//                    // Get sku details from marketplace (Google Play) for specified sku
//                    final String sku = subscription.getZypePlan().thirdPartyId;
//                    List<String> skuList = new ArrayList<>();
//                    skuList.add(sku);
//                    billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuList,
//                            new SkuDetailsResponseListener() {
//                                @Override
//                                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
//                                    if (responseCode != BillingClient.BillingResponse.OK) {
//                                        Logger.e("onSkuDetailsResponse(): Error retrieving sku details from Google Play");
//                                    }
//                                    else {
//                                        if (skuDetailsList != null) {
//                                            if (skuDetailsList.size() == 0) {
//                                                Logger.e("onSkuDetailsResponse(): Sku is not found in Google Play, sku=" + sku);
//                                            }
//                                            else {
//                                                if (skuDetailsList.size() > 1) {
//                                                    Logger.w("onSkuDetailsResponse(): Unexpected number of items (" +
//                                                            skuDetailsList.size() + ") in Google Play, sku=" + sku);
//                                                }
//                                                subscription.setMarketplace(skuDetailsList.get(0));
//
//                                                subscriptionsLiveData.setValue(subscriptions);
//                                            }
//                                        }
//                                    }
//                                }
//                            });
//                }
//            });
//            subscriptionLiveData.loadPlan(planId);
        }
    }

    public LiveData<Map<String, Subscription>> getSubscriptions() {
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

    //
    // Zype API
    //

    public void loadPlan(String planId) {
        PlanParamsBuilder builder = new PlanParamsBuilder(planId);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.PLAN, builder.build());
    }

    @Subscribe
    public void handlePlan(PlanEvent event) {
        Logger.d("handlePlan()");
        PlanData data = event.getEventData().getModelData().data;
        Subscription subscription = new Subscription();
        subscription.setZypePlan(data);

        subscriptionsLiveData.getValue().put(data.id, subscription);

        queryGooglePlayProduct(subscription);
    }

    //
    // Google Play
    //
    private void queryGooglePlayProduct(final Subscription subscription) {
        // Get sku details from marketplace (Google Play) for specified sku
        final String sku = subscription.getZypePlan().thirdPartyId;
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
                                    subscription.setMarketplace(skuDetailsList.get(0));
//
//                                    subscriptionsLiveData.setValue(subscriptions);
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
