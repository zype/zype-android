package com.zype.android.Billing;

import com.android.billingclient.api.SkuDetails;
import com.zype.android.webapi.model.plan.PlanData;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class Subscription {
    private PlanData zypePlan;
    private SkuDetails marketplace;
    private boolean verified = false;

    public PlanData getZypePlan() {
        return zypePlan;
    }

    public void setZypePlan(PlanData data) {
        zypePlan = data;
    }

    public SkuDetails getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(SkuDetails skuDetails) {
        marketplace = skuDetails;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
