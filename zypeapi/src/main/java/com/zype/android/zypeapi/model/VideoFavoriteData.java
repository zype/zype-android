package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EvgenyCherkasov on 30.12.2017.
 */

public class VideoFavoriteData {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("consumer_id")
    @Expose
    public String consumerId;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("deleted_at")
    @Expose
    public String expireAt;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("video_id")
    @Expose
    public String videoId;
}
