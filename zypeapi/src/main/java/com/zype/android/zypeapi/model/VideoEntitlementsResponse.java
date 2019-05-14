package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 11.10.2017.
 */

public class VideoEntitlementsResponse {
    @SerializedName("response")
    @Expose
    public List<VideoEntitlementData> videoEntitlements = new ArrayList<>();

    @Expose
    public Pagination pagination;

    @Expose
    public String message;
}
