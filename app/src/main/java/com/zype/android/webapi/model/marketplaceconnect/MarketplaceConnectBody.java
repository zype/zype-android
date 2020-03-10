package com.zype.android.webapi.model.marketplaceconnect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Evgeny Cherkasov on 25.06.2018
 */
public class MarketplaceConnectBody implements Serializable{
    @SerializedName("app_id")
    public String appId;

    @SerializedName("consumer_id")
    public String consumerId;

//    @SerializedName("consumer_token")
//    public String consumerToken;

    @SerializedName("plan_id")
    public String planId;

    @SerializedName("playlist_id")
    public String playlistId;

    //    @SerializedName("receipt")
//    public String receipt;

    @SerializedName("site_id")
    public String siteId;

    @SerializedName("purchase_token")
    public String purchaseToken;

    @SerializedName("data")
    public MarketplaceConnectBodyData data;

}
