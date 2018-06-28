package com.zype.android.webapi.model.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class PlanData {

    @Expose
    public boolean active;

    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("_keywords")
    @Expose
    public List<String> keywords;

    @SerializedName("third_party_id")
    @Expose
    public String thirdPartyId;
}
