package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 24.05.2017.
 */

public class PlaylistData {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("_keywords")
    @Expose
    public List<String> keywords = new ArrayList<>();

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;

    @Expose
    public String description;

    @Expose
    public List<Image> images = new ArrayList<>();

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @SerializedName("thumbnail_layout")
    @Expose
    public String thumbnailLayout;

    @Expose
    public List<Thumbnail> thumbnails = new ArrayList<>();

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("parent_id")
    @Expose
    public String parentId;

    @SerializedName("priority")
    @Expose
    public int priority;

    @SerializedName("playlist_item_count")
    @Expose
    public int playlistItemCount;

    @Expose
    public List<String> values = new ArrayList<>();

}
