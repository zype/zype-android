package com.zype.android.webapi.model.marketplaceconnect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Evgeny Cherkasov on 25.06.2018
 */
public class MarketplaceConnectBody implements Serializable{
    @SerializedName("consumer_id")
    public String consumerId;

    @SerializedName("consumer_token")
    public String consumerToken;

    @SerializedName("receipt")
    public String receipt;

    @SerializedName("plan_id")
    public String planId;
}
