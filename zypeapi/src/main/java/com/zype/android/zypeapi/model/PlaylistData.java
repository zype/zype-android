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

    @Expose
    public boolean active;

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

    @SerializedName("_keywords")
    @Expose
    public List<String> keywords = new ArrayList<>();

    @SerializedName("marketplace_ids")
    @Expose
    public MarketplaceIds marketplaceIds;

    @SerializedName("purchase_price")
    @Expose
    public String purchasePrice;

    @SerializedName("purchase_required")
    @Expose
    public boolean purchaseRequired;

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

    @SerializedName("parent_id")
    @Expose
    public String parentId;

    @SerializedName("priority")
    @Expose
    public int priority;

    @SerializedName("playlist_item_count")
    @Expose
    public int playlistItemCount;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @Expose
    public List<String> values = new ArrayList<>();

}
