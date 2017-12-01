package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class BifrostParamsBuilder extends ParamsBuilder {
    public static final String PACKAGE_NAME = "packageName";
    public static final String SUBSCRIPTION_ID = "subscriptionId";
    public static final String PURCHASE_TOKEN = "purchaseToken";

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REDIRECT_URI= "redirect_uri";

    public static final String ACCESS_TOKEN = "access_token";

    public BifrostParamsBuilder() {
        super();
        addAccessToken();
        addClientId();
        addClientSecret();
        addRedirectUri();
    }
    public BifrostParamsBuilder addPackageName(String packageName) {
        addPostParam(PACKAGE_NAME, packageName);
        return this;
    }

    public BifrostParamsBuilder addSubscriptionId(String subscriptionId) {
        addPostParam(SUBSCRIPTION_ID, subscriptionId);
        return this;
    }

    public BifrostParamsBuilder addPurchaseToken(String purchaseToken) {
        addPostParam(PURCHASE_TOKEN, purchaseToken);
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
