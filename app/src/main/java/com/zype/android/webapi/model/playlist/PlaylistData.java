package com.zype.android.webapi.model.playlist;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.zypeapi.model.MarketplaceIds;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class PlaylistData {

    @SerializedName("_id")
    @Expose
    private String Id;

    @SerializedName("_keywords")
    @Expose
    private List<String> Keywords = new ArrayList<>();

    @Expose
    public boolean active;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;

    @SerializedName("site_id")
    @Expose
    private String siteId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("marketplace_ids")
    @Expose
    public MarketplaceIds marketplaceIds;

    @SerializedName("parent_id")
    @Expose
    private String parentId;

    @SerializedName("priority")
    @Expose
    private int priority;

    @SerializedName("playlist_item_count")
    @Expose
    private int playlistItemCount;

    @SerializedName("purchase_price")
    @Expose
    public String purchasePrice;

    @SerializedName("purchase_required")
    @Expose
    public boolean purchaseRequired;

    @SerializedName("thumbnail_layout")
    @Expose
    private String thumbnailLayout;

    @Expose
    private List<String> values = new ArrayList<>();

    @Nullable
    @Expose
    private List<Thumbnail> thumbnails = new ArrayList<>();

    @Nullable
    @Expose
    private List<Image> images = new ArrayList<>();

    /**
     *
     * @return
     * The Id
     */
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     * The Keywords
     */
    public List<String> getKeywords() {
        return Keywords;
    }

    /**
     *
     * @param Keywords
     * The _keywords
     */
    public void setKeywords(List<String> Keywords) {
        this.Keywords = Keywords;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The deletedAt
     */
    public String getDeletedAt() {
        return deletedAt;
    }

    /**
     *
     * @param deletedAt
     * The deleted_at
     */
    public void setDeletedAt(String  deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     *
     * @return
     * The siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     *
     * @param siteId
     * The site_id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     * The priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     *
     * @return
     * The parentId
     */
    public String getParentId() {
        return parentId;
    }


    /**
     *
     * @param parentId
     * The parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return The playlist_item_count
     */
    public int getPlaylistItemCount() {
        return playlistItemCount;
    }

    /**
     * @param playlistItemCount The playlistItemCount
     */
    public void setPlaylistItemCount(int playlistItemCount) {
        this.playlistItemCount = playlistItemCount;
    }

    /**
     * @return The thumbnail_layout
     */
    public String getThumbnailLayout() { return thumbnailLayout; }

    /**
     * @param thumbnailLayout
     * The thumbnail layout
     */
    public void setThumbnailLayout(String thumbnailLayout) { this.thumbnailLayout = thumbnailLayout; }

    /**
     *
     * @return
     * The values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     *
     * @param values
     * The values
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * @return The thumbnails
    */
    @Nullable
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    /**
     * @param thumbnails The thumbnails
     */
    public void setThumbnails(@Nullable List<Thumbnail> thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     * @return The playlist images
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * @param images The playlist images
     */
    public void setImages(@Nullable List<Image> images) {
        this.images = images;
    }
}
