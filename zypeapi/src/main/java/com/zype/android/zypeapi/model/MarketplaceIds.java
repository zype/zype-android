package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MarketplaceIds {
    @SerializedName("amazon_fire_tv")
    @Expose
    public String amazon;

    @SerializedName("googleplay")
    @Expose
    public String googleplay;
}
