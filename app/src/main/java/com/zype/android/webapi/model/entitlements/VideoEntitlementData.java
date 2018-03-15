package com.zype.android.webapi.model.entitlements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
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
