package com.zype.android.webapi.model.entitlements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class VideoEntitlements {
    @SerializedName("response")
    @Expose
    public List<VideoEntitlementData> videoEntitlements = new ArrayList<>();

    @Expose
    public Pagination pagination;

    @Expose
    public String message;
}
