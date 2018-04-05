package com.zype.android.webapi.model.linking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 03.04.2018.
 */

public class DevicePinData {

    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("consumer_id")
    @Expose
    public Object consumerId;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("deleted_at")
    @Expose
    public Object deletedAt;

    @SerializedName("device_id")
    @Expose
    public Object deviceId;

    @SerializedName("linked_device_id")
    @Expose
    public String linkedDeviceId;

    @SerializedName("pin")
    @Expose
    public String pin;

    @SerializedName("pin_expiration")
    @Expose
    public String pinExpiration;

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("linked")
    @Expose
    public Boolean linked;

    @SerializedName("subscription_count")
    @Expose
    public Object subscriptionCount;
}
