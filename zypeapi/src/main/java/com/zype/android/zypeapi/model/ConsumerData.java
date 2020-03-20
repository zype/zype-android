package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 13.04.2017.
 */

public class ConsumerData {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @Expose
    public String email;

    @Expose
    public String name;

    @SerializedName("password_token")
    @Expose
    public String passwordToken;

    @SerializedName("remember_token")
    @Expose
    public String rememberToken;

    @SerializedName("rss_token")
    @Expose
    public String rssToken;

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @SerializedName("subscription_count")
    @Expose
    public int subscriptionCount;

    @SerializedName("subscription_plans")
    @Expose
    public List<ConsumerSubscriptionPlan> subscriptionPlans;

    @SerializedName("linked_devices")
    @Expose
    public List<String> linkedDevices = new ArrayList<>();

}
