package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EvgenyCherkasov on 11.10.2017.
 */

public class VideoEntitlementData {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("consumer_id")
    @Expose
    public String consumerId;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("expire_at")
    @Expose
    public String expireAt;

    @SerializedName("transaction_id")
    @Expose
    public String transactionId;

    @SerializedName("transaction_type")
    @Expose
    public String transactionType;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("video_id")
    @Expose
    public String videoId;

    @SerializedName("video_title")
    @Expose
    public String videoTitle;

}
