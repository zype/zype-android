package com.zype.android.Billing;

import com.android.billingclient.api.SkuDetails;
import com.zype.android.webapi.model.plan.PlanData;

import static com.zype.android.Billing.MarketplaceGateway.MARKETPLACE_GOOGLE;
import static com.zype.android.Billing.MarketplaceGateway.MARKETPLACE_SAMSUNG;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */

public class Subscription {
    private PlanData zypePlan;
    private SkuDetails marketplaceProduct;
    private ProductDetails marketplaceProductDetails;
    private boolean verified = false;

    public PlanData getZypePlan() {
        return zypePlan;
    }

    public void setZypePlan(PlanData data) {
        zypePlan = data;
    }

    public SkuDetails getMarketplaceProduct() {
        return marketplaceProduct;
    }

    public void setMarketplaceProduct(SkuDetails skuDetails) {
        marketplaceProduct = skuDetails;
    }

    public ProductDetails getMarketplaceProductDetails() {
        return marketplaceProductDetails;
    }

    public void setMarketplaceProductDetails(ProductDetails productDetails) {
        this.marketplaceProductDetails = productDetails;
    }

    public String getMarketplaceId(String marketplace) {
        if (marketplace.equals(MARKETPLACE_GOOGLE)) {
            return getZypePlan().marketplaceIds.googleplay;
        }
        else if (marketplace.equals(MARKETPLACE_SAMSUNG)) {
            return getZypePlan().marketplaceIds.samsung;
        }
        else {
            return null;
        }
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
