package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistMainResponse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("auto_remove_video_entitlements")
    @Expose
    private Boolean autoRemoveVideoEntitlements;
    @SerializedName("auto_update_video_entitlements")
    @Expose
    private Boolean autoUpdateVideoEntitlements;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("children_video_ids")
    @Expose
    private List<String> childrenVideoIds = null;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discovery_url")
    @Expose
    private Object discoveryUrl;
    @SerializedName("exclude_match_type")
    @Expose
    private String excludeMatchType;
    @SerializedName("friendly_title")
    @Expose
    private String friendlyTitle;
    @SerializedName("marketplace_ids")
    @Expose
    private MarketplaceIds marketplaceIds;
    @SerializedName("match_type")
    @Expose
    private String matchType;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("playlist_type")
    @Expose
    private String playlistType;
    @SerializedName("precise_duration")
    @Expose
    private Double preciseDuration;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("purchase_price")
    @Expose
    private Object purchasePrice;
    @SerializedName("purchase_required")
    @Expose
    private Boolean purchaseRequired;
    @SerializedName("related_video_ids")
    @Expose
    private List<Object> relatedVideoIds = null;
    @SerializedName("rental_duration")
    @Expose
    private Object rentalDuration;
    @SerializedName("rental_price")
    @Expose
    private Object rentalPrice;
    @SerializedName("rental_required")
    @Expose
    private Boolean rentalRequired;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("thumbnail_layout")
    @Expose
    private String thumbnailLayout;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("total_duration")
    @Expose
    private Double totalDuration;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("playlist_item_count")
    @Expose
    private Integer playlistItemCount;
    @SerializedName("images")
    @Expose
    private List<Object> images = null;
    @SerializedName("thumbnails")
    @Expose
    private List<Object> thumbnails = null;
    @SerializedName("content_rules")
    @Expose
    private List<Object> contentRules = null;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("video_ids")
    @Expose
    private List<Object> videoIds = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAutoRemoveVideoEntitlements() {
        return autoRemoveVideoEntitlements;
    }

    public void setAutoRemoveVideoEntitlements(Boolean autoRemoveVideoEntitlements) {
        this.autoRemoveVideoEntitlements = autoRemoveVideoEntitlements;
    }

    public Boolean getAutoUpdateVideoEntitlements() {
        return autoUpdateVideoEntitlements;
    }

    public void setAutoUpdateVideoEntitlements(Boolean autoUpdateVideoEntitlements) {
        this.autoUpdateVideoEntitlements = autoUpdateVideoEntitlements;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<String> getChildrenVideoIds() {
        return childrenVideoIds;
    }

    public void setChildrenVideoIds(List<String> childrenVideoIds) {
        this.childrenVideoIds = childrenVideoIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDiscoveryUrl() {
        return discoveryUrl;
    }

    public void setDiscoveryUrl(Object discoveryUrl) {
        this.discoveryUrl = discoveryUrl;
    }

    public String getExcludeMatchType() {
        return excludeMatchType;
    }

    public void setExcludeMatchType(String excludeMatchType) {
        this.excludeMatchType = excludeMatchType;
    }

    public String getFriendlyTitle() {
        return friendlyTitle;
    }

    public void setFriendlyTitle(String friendlyTitle) {
        this.friendlyTitle = friendlyTitle;
    }

    public MarketplaceIds getMarketplaceIds() {
        return marketplaceIds;
    }

    public void setMarketplaceIds(MarketplaceIds marketplaceIds) {
        this.marketplaceIds = marketplaceIds;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

    public Double getPreciseDuration() {
        return preciseDuration;
    }

    public void setPreciseDuration(Double preciseDuration) {
        this.preciseDuration = preciseDuration;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Object getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Object purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Boolean getPurchaseRequired() {
        return purchaseRequired;
    }

    public void setPurchaseRequired(Boolean purchaseRequired) {
        this.purchaseRequired = purchaseRequired;
    }

    public List<Object> getRelatedVideoIds() {
        return relatedVideoIds;
    }

    public void setRelatedVideoIds(List<Object> relatedVideoIds) {
        this.relatedVideoIds = relatedVideoIds;
    }

    public Object getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Object rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public Object getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(Object rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public Boolean getRentalRequired() {
        return rentalRequired;
    }

    public void setRentalRequired(Boolean rentalRequired) {
        this.rentalRequired = rentalRequired;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getThumbnailLayout() {
        return thumbnailLayout;
    }

    public void setThumbnailLayout(String thumbnailLayout) {
        this.thumbnailLayout = thumbnailLayout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getPlaylistItemCount() {
        return playlistItemCount;
    }

    public void setPlaylistItemCount(Integer playlistItemCount) {
        this.playlistItemCount = playlistItemCount;
    }

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
    }

    public List<Object> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<Object> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public List<Object> getContentRules() {
        return contentRules;
    }

    public void setContentRules(List<Object> contentRules) {
        this.contentRules = contentRules;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Object> getVideoIds() {
        return videoIds;
    }

    public void setVideoIds(List<Object> videoIds) {
        this.videoIds = videoIds;
    }

}
