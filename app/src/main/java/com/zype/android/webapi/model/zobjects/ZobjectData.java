package com.zype.android.webapi.model.zobjects;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class ZobjectData {

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
    @Expose
    private String facebook;
    @SerializedName("friendly_title")
    @Expose
    private String friendlyTitle;
    @Expose
    private String message;
    @Expose
    private List<Object> keywords = new ArrayList<>();
    @Expose
    private List<Picture> pictures = new ArrayList<>();
    @SerializedName("short_bio")
    @Expose
    private String shortBio;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @Expose
    private String title;
    @Expose
    private String time;
    @Expose
    private String twitter;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("video_ids")
    @Expose
    private List<String> videoIds = new ArrayList<>();
    @Expose
    private String youtube;
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
    @Nullable
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
    @Nullable
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
    @Nullable
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
    @Nullable
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
     * The facebook
     */
    @Nullable
    public String getFacebook() {
        return facebook;
    }

    /**
     *
     * @param facebook
     * The facebook
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    /**
     *
     * @return
     * The friendlyTitle
     */
    @Nullable
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
    @Nullable
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
     * The pictures
     */
    @Nullable
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
     * The shortBio
     */
    @Nullable
    public String getShortBio() {
        return shortBio;
    }

    /**
     *
     * @param shortBio
     * The short_bio
     */
    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    /**
     *
     * @return
     * The siteId
     */
    @Nullable
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
    @Nullable
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
     * The twitter
     */
    @Nullable
    public String getTwitter() {
        return twitter;
    }

    /**
     *
     * @param twitter
     * The twitter
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    @Nullable
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
    @Nullable
    public List<String> getVideoIds() {
        return videoIds;
    }

    /**
     *
     * @param videoIds
     * The video_ids
     */
    public void setVideoIds(List<String> videoIds) {
        this.videoIds = videoIds;
    }

    /**
     *
     * @return
     * The youtube
     */
    @Nullable
    public String getYoutube() {
        return youtube;
    }

    /**
     *
     * @param youtube
     * The youtube
     */
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    /**
     *
     * @return
     * The zobjectTypeId
     */
    @Nullable
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
    @Nullable
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
