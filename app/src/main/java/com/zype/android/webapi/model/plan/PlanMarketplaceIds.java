package com.zype.android.webapi.model.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 13.08.2018
 */
public class PlanMarketplaceIds {
    @SerializedName("itunes")
    @Expose
    public String itunes;

    @SerializedName("googleplay")
    @Expose
    public String googleplay;
}
