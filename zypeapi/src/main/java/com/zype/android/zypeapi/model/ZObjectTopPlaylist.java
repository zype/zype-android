package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ZObjectTopPlaylist {
    @SerializedName("_id")
    @Expose
    public String id;

    @Expose
    public Boolean active;

    @Expose
    public Boolean autoplay;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @Expose
    public String description;

    @SerializedName("friendly_title")
    @Expose
    public String friendlyTitle;

    @Expose
    public int limit;

    @Expose
    public String message;

    @SerializedName("refresh_rate")
    @Expose
    public String refreshRate;

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @Expose
    public String title;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("zobject_type_id")
    @Expose
    public String zobjectTypeId;

    @SerializedName("zobject_type_title")
    @Expose
    public String zobjectTypeTitle;

    @SerializedName("playlistid")
    @Expose
    public String playlistId;

    @SerializedName("pictures")
    @Expose
    public List<Image> images = new ArrayList<>();

    @SerializedName("videoid")
    @Expose
    public String videoId;

    @SerializedName("priority")
    @Expose
    public Integer priority;

}
