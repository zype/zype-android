package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 13.02.2018.
 */

public class AnalyticsDimensions {
    @Expose
    public String device;

    @SerializedName("player_id")
    @Expose
    public String playerId;

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @SerializedName("video_id")
    @Expose
    public String videoId;

}
