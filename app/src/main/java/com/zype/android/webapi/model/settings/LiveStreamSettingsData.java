package com.zype.android.webapi.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class LiveStreamSettingsData {
    @SerializedName("_id")
    @Expose
    private String Id;

    @Expose
    private Boolean active;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @Expose
    private String description;

    @SerializedName("friendly_title")
    @Expose
    private String friendlyTitle;

    @SerializedName("help_url")
    @Expose
    private List<Object> keywords = new ArrayList<>();

    @Expose
    private int limit;

    @Expose
    private String message;

    @SerializedName("refresh_rate")
    @Expose
    private String refreshRate;

    @SerializedName("site_id")
    @Expose
    private String siteId;

    @Expose
    private String title;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("video_ids")
    @Expose
    private List<Object> videoIds = new ArrayList<>();

    @SerializedName("zobject_type_id")
    @Expose
    private String zobjectTypeId;

    @SerializedName("zobject_type_title")
    @Expose
    private String zobjectTypeTitle;

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
     * The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(Boolean active) {
        this.active = active;
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
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The friendlyTitle
     */
    public String getFriendlyTitle() {
        return friendlyTitle;
    }

    /**
     *
     * @param friendlyTitle
     * The friendly_title
     */
    public void setFriendlyTitle(String friendlyTitle) {
        this.friendlyTitle = friendlyTitle;
    }

    /**
     *
     * @return
     * The keywords
     */
    public List<Object> getKeywords() {
        return keywords;
    }

    /**
     *
     * @param keywords
     * The keywords
     */
    public void setKeywords(List<Object> keywords) {
        this.keywords = keywords;
    }

    /**
     *
     * @return
     * The limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     *
     * @param limit
     * The limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The refreshRate
     */
    public String getRefreshRate() {
        return refreshRate;
    }

    /**
     *
     * @param refreshRate
     * The refreshRate
     */
    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
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
     * The videoIds
     */
    public List<Object> getVideoIds() {
        return videoIds;
    }

    /**
     *
     * @param videoIds
     * The video_ids
     */
    public void setVideoIds(List<Object> videoIds) {
        this.videoIds = videoIds;
    }

    /**
     *
     * @return
     * The zobjectTypeId
     */
    public String getZobjectTypeId() {
        return zobjectTypeId;
    }

    /**
     *
     * @param zobjectTypeId
     * The zobject_type_id
     */
    public void setZobjectTypeId(String zobjectTypeId) {
        this.zobjectTypeId = zobjectTypeId;
    }

    /**
     *
     * @return
     * The zobjectTypeTitle
     */
    public String getZobjectTypeTitle() {
        return zobjectTypeTitle;
    }

    /**
     *
     * @param zobjectTypeTitle
     * The zobject_type_title
     */
    public void setZobjectTypeTitle(String zobjectTypeTitle) {
        this.zobjectTypeTitle = zobjectTypeTitle;
    }
}
