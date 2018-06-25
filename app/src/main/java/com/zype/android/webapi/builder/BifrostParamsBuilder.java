package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class BifrostParamsBuilder extends ParamsBuilder {
    public static final String CONSUMER_ID = "consumer_id";
    public static final String CONSUMER_TOKEN = "consumer_token";
    public static final String PACKAGE_NAME = "packageName";
    public static final String PLAN_ID = "plan_id";
    public static final String RECEIPT = "receipt";

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REDIRECT_URI= "redirect_uri";

    public static final String ACCESS_TOKEN = "access_token";

    public BifrostParamsBuilder() {
        super();
//        addAccessToken();
//        addClientId();
//        addClientSecret();
//        addRedirectUri();
    }

    public BifrostParamsBuilder addConsumerId(String consumerId) {
        addPostParam(CONSUMER_ID, consumerId);
        return this;
    }

    public BifrostParamsBuilder addConsumerToken(String consumerToken) {
        addPostParam(CONSUMER_TOKEN, consumerToken);
        return this;
    }

    public BifrostParamsBuilder addPackageName(String packageName) {
        addPostParam(PACKAGE_NAME, packageName);
        return this;
    }

    public BifrostParamsBuilder addPlanId(String planId) {
        addPostParam(PLAN_ID, planId);
        return this;
    }

    public BifrostParamsBuilder addPurchaseToken(String purchaseToken) {
        addPostParam(RECEIPT, purchaseToken);
        return this;
    }

    private BifrostParamsBuilder addClientId() {
        addPostParam(CLIENT_ID, ZypeSettings.GOOGLE_CLIENT_ID);
        return this;
    }

    private BifrostParamsBuilder addClientSecret() {
        addPostParam(CLIENT_SECRET, ZypeSettings.GOOGLE_CLIENT_SECRET);
        return this;
    }

    private BifrostParamsBuilder addRedirectUri() {
        addPostParam(REDIRECT_URI, ZypeSettings.GOOGLE_REDIRECT_URL);
        return this;
    }

    private BifrostParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }


}
