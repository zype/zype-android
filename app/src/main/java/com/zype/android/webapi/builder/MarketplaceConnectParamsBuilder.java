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
    public static final String PLAN_ID = "plan_id";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String PURCHASE_TOKEN = "purchase_token";
    public static final String RECEIPT = "receipt";
    public static final String SIGNATURE = "signature";

    public static final String ACCESS_TOKEN = "access_token";

    MarketplaceConnectBody body;

    public MarketplaceConnectParamsBuilder() {
        super();
    }

    public MarketplaceConnectParamsBuilder addReceipt(String receipt) {
        addPostParam(RECEIPT, receipt);
        return this;
    }

    public MarketplaceConnectParamsBuilder addSignature(String signature) {
        addPostParam(SIGNATURE, signature);
        return this;
    }

    public MarketplaceConnectParamsBuilder addConsumerId(String consumerId) {
        addPostParam(CONSUMER_ID, consumerId);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPlanId(String planId) {
        addPostParam(PLAN_ID, planId);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPlaylistId(String playlistId) {
        addPostParam(PLAYLIST_ID, playlistId);
        return this;
    }

    public MarketplaceConnectParamsBuilder addPurchaseToken(String purchaseToken) {
        addPostParam(PURCHASE_TOKEN, purchaseToken);
        return this;
    }

}
