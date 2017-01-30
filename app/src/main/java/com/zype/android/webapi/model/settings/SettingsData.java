package com.zype.android.webapi.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.zobjects.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/13/15
 */
public class SettingsData {
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
    private String helpUrl;
    @Expose
    private List<Object> keywords = new ArrayList<>();
    @SerializedName("no_downloads_message")
    @Expose
    private String noDownloadsMessage;
    @SerializedName("no_favorites_message")
    @Expose
    private String noFavoritesMessage;
    @SerializedName("no_favorites_message_not_logged_in")
    @Expose
    private String noFavoritesMessageNotLoggedIn;
    @Expose
    private List<Picture> pictures = new ArrayList<>();
    @SerializedName("share_message")
    @Expose
    private String shareMessage;
    @SerializedName("share_subject")
    @Expose
    private String shareSubject;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("subscribe_url")
    @Expose
    private String subscribeUrl;
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
     * The helpUrl
     */
    public String getHelpUrl() {
        return helpUrl;
    }

    /**
     *
     * @param helpUrl
     * The help_url
     */
    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
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
     * The noDownloadsMessage
     */
    public String getNoDownloadsMessage() {
        return noDownloadsMessage;
    }

    /**
     *
     * @param noDownloadsMessage
     * The no_downloads_message
     */
    public void setNoDownloadsMessage(String noDownloadsMessage) {
        this.noDownloadsMessage = noDownloadsMessage;
    }

    /**
     *
     * @return
     * The noFavoritesMessage
     */
    public String getNoFavoritesMessage() {
        return noFavoritesMessage;
    }

    /**
     *
     * @param noFavoritesMessage
     * The no_favorites_message
     */
    public void setNoFavoritesMessage(String noFavoritesMessage) {
        this.noFavoritesMessage = noFavoritesMessage;
    }

    /**
     *
     * @return
     * The noFavoritesMessageNotLoggedIn
     */
    public String getNoFavoritesMessageNotLoggedIn() {
        return noFavoritesMessageNotLoggedIn;
    }

    /**
     *
     * @param noFavoritesMessageNotLoggedIn
     * The no_favorites_message_not_logged_in
     */
    public void setNoFavoritesMessageNotLoggedIn(String noFavoritesMessageNotLoggedIn) {
        this.noFavoritesMessageNotLoggedIn = noFavoritesMessageNotLoggedIn;
    }

    /**
     *
     * @return
     * The pictures
     */
    public List<Picture> getPictures() {
        return pictures;
    }

    /**
     *
     * @param pictures
     * The pictures
     */
    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    /**
     *
     * @return
     * The shareMessage
     */
    public String getShareMessage() {
        return shareMessage;
    }

    /**
     *
     * @param shareMessage
     * The share_message
     */
    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
    }

    /**
     *
     * @return
     * The shareSubject
     */
    public String getShareSubject() {
        return shareSubject;
    }

    /**
     *
     * @param shareSubject
     * The share_subject
     */
    public void setShareSubject(String shareSubject) {
        this.shareSubject = shareSubject;
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
     * The subscribeUrl
     */
    public String getSubscribeUrl() {
        return subscribeUrl;
    }

    /**
     *
     * @param subscribeUrl
     * The subscribe_url
     */
    public void setSubscribeUrl(String subscribeUrl) {
        this.subscribeUrl = subscribeUrl;
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
