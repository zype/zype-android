package com.zype.android.zypeapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Evgeny Cherkasov on 29.01.2019
 */
public class MarketplaceConnectBody implements Serializable {
    @SerializedName("amount")
    public String amount;

    @SerializedName("app_id")
    public String appId;

    @SerializedName("consumer_id")
    public String consumerId;

    @SerializedName("data")
    public MarketplaceConnectBodyData data;

    @SerializedName("plan_id")
    public String planId;

    @SerializedName("playlist_id")
    public String playlistId;

    @SerializedName("purchase_id")
    public String purchaseId;

    @SerializedName("site_id")
    public String siteId;

    @SerializedName("purchase_token")
    public String purchaseToken;

    @SerializedName("transaction_type")
    public String transactionType;

    @SerializedName("video_id")
    public String videoId;
}