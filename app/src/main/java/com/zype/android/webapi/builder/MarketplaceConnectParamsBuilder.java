package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.model.marketplaceconnect.MarketplaceConnect;
import com.zype.android.webapi.model.marketplaceconnect.MarketplaceConnectBody;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class MarketplaceConnectParamsBuilder extends ParamsBuilder {
    public static final String CONSUMER_ID = "consumer_id";
    public static final String CONSUMER_TOKEN = "consumer_token";
    public static final String PACKAGE_NAME = "packageName";
    public static final String PLAN_ID = "plan_id";
    public static final String PURCHASE_TOKEN = "purchase_token";

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REDIRECT_URI= "redirect_uri";

    public static final String ACCESS_TOKEN = "access_token";

    MarketplaceConnectBody body;

    public MarketplaceConnectParamsBuilder() {
        super();
//        addAccessToken();
//        addClientId();
//        addClientSecret();
//        addRedirectUri();
    }

    public MarketplaceConnectParamsBuilder addReceipt(String receipt) {
        addPostParam("receipt", receipt);
        return this;
    }

    public MarketplaceConnectParamsBuilder addSignature(String signature) {
        addPostParam("signature", signature);
        return this;
    }

    public MarketplaceConnectParamsBuilder addConsumerId(String consumerId) {
        addPostParam(CONSUMER_ID, consumerId);
        return this;
    }

    public MarketplaceConnectParamsBuilder addConsumerToken(String consumerToken) {
        addPostParam(CONSUMER_TOKEN, consumerToken);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPackageName(String packageName) {
        addPostParam(PACKAGE_NAME, packageName);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPlanId(String planId) {
        addPostParam(PLAN_ID, planId);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPurchaseToken(String purchaseToken) {
        addPostParam(PURCHASE_TOKEN, purchaseToken);
        return this;
    }

    private MarketplaceConnectParamsBuilder addClientId() {
        addPostParam(CLIENT_ID, ZypeSettings.GOOGLE_CLIENT_ID);
        return this;
    }

    private MarketplaceConnectParamsBuilder addClientSecret() {
        addPostParam(CLIENT_SECRET, ZypeSettings.GOOGLE_CLIENT_SECRET);
        return this;
    }

    private MarketplaceConnectParamsBuilder addRedirectUri() {
        addPostParam(REDIRECT_URI, ZypeSettings.GOOGLE_REDIRECT_URL);
        return this;
    }

    private MarketplaceConnectParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }


}
