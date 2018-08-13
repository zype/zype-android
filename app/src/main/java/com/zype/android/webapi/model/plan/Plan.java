package com.zype.android.webapi.model.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class Plan {
    @SerializedName("response")
    @Expose
    public PlanData data;
}
