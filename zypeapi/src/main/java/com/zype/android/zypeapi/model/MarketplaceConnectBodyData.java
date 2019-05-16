package com.zype.android.zypeapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
/**
* Created by Evgeny Cherkasov on 25.06.2018
*/

public class MarketplaceConnectBodyData implements Serializable {

    @SerializedName("receipt")
    public String receipt;

    @SerializedName("signature")
    public String signature;

    @SerializedName("receiptId")
    public String receiptId;

    @SerializedName("userId")
    public String userId;
}