package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsumerSubscriptionPlan {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("entitlement_type")
    @Expose
    public String entitlementType;

    @SerializedName("subscription_id")
    @Expose
    public String subscriptionId;
}
