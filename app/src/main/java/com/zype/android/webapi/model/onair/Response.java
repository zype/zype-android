package com.zype.android.webapi.model.onair;

/**
 * @author vasya
 * @version 1
 * date 8/6/15
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Thumbnail;

import java.util.ArrayList;
import java.util.List;

public class Response {

    @SerializedName("_id")
    @Expose
    private String Id;
    @Expose
    private Boolean active;
    @Expose
    private List<Category> categories = new ArrayList<>();
    @Expose
    private String country;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @Expose
    private String description;
    @SerializedName("discovery_url")
    @Expose
    private String discoveryUrl;
    @Expose
    private Integer duration;
    @Expose
    private Integer episode;
    @SerializedName("expire_at")
    @Expose
    private String expireAt;
    @Expose
    private Boolean featured;
    @SerializedName("foreign_id")
    @Expose
    private String foreignId;
    @Expose
    private List<String> keywords = new ArrayList<>();
    @SerializedName("mature_content")
    @Expose
    private Boolean matureContent;
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @Expose
    private Double rating;
    @SerializedName("related_playlist_ids")
    @Expose
    private List<String> relatedPlaylistIds = new ArrayList<>();
    @SerializedName("request_count")
    @Expose
    private Integer requestCount;
    @Expose
    private String season;
    @Expose
    private List<Segment> segments = new ArrayList<>();
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("start_at")
    @Expose
    private String startAt;
    @Expose
    private String status;
    @SerializedName("subscription_required")
    @Expose
    private Boolean subscriptionRequired;
    @Expose
    private String title;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("zobject_ids")
    @Expose
    private List<String> zobjectIds = new ArrayList<>();
    @Expose
    private List<Thumbnail> thumbnails = new ArrayList<>();
    @SerializedName("hulu_id")
    @Expose
    private String huluId;
    @SerializedName("youtube_id")
    @Expose
    private String youtubeId;
    @SerializedName("crunchyroll_id")
    @Expose
    private String crunchyrollId;
    @Expose
    private Boolean tranascoded;

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
     * The categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     *
     * @param categories
     * The categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
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
     * The discoveryUrl
     */
    public String getDiscoveryUrl() {
        return discoveryUrl;
    }

    /**
     *
     * @param discoveryUrl
     * The discovery_url
     */
    public void setDiscoveryUrl(String discoveryUrl) {
        this.discoveryUrl = discoveryUrl;
    }

    /**
     *
     * @return
     * The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The episode
     */
    public Integer getEpisode() {
        return episode;
    }

    /**
     *
     * @param episode
     * The episode
     */
    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    /**
     *
     * @return
     * The expireAt
     */
    public String getExpireAt() {
        return expireAt;
    }

    /**
     *
     * @param expireAt
     * The expire_at
     */
    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    /**
     *
     * @return
     * The featured
     */
    public Boolean getFeatured() {
        return featured;
    }

    /**
     *
     * @param featured
     * The featured
     */
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    /**
     *
     * @return
     * The foreignId
     */
    public String getForeignId() {
        return foreignId;
    }

    /**
     *
     * @param foreignId
     * The foreign_id
     */
    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    /**
     *
     * @return
     * The keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     *
     * @param keywords
     * The keywords
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     *
     * @return
     * The matureContent
     */
    public Boolean getMatureContent() {
        return matureContent;
    }

    /**
     *
     * @param matureContent
     * The mature_content
     */
    public void setMatureContent(Boolean matureContent) {
        this.matureContent = matureContent;
    }

    /**
     *
     * @return
     * The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     *
     * @param publishedAt
     * The published_at
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     *
     * @return
     * The rating
     */
    public Double getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The relatedPlaylistIds
     */
    public List<String> getRelatedPlaylistIds() {
        return relatedPlaylistIds;
    }

    /**
     *
     * @param relatedPlaylistIds
     * The related_playlist_ids
     */
    public void setRelatedPlaylistIds(List<String> relatedPlaylistIds) {
        this.relatedPlaylistIds = relatedPlaylistIds;
    }

    /**
     *
     * @return
     * The requestCount
     */
    public Integer getRequestCount() {
        return requestCount;
    }

    /**
     *
     * @param requestCount
     * The request_count
     */
    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    /**
     *
     * @return
     * The season
     */
    public String getSeason() {
        return season;
    }

    /**
     *
     * @param season
     * The season
     */
    public void setSeason(String season) {
        this.season = season;
    }

    /**
     *
     * @return
     * The segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     *
     * @param segments
     * The segments
     */
    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    /**
     *
     * @return
     * The shortDescription
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     *
     * @param shortDescription
     * The short_description
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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
     * The startAt
     */
    public String getStartAt() {
        return startAt;
    }

    /**
     *
     * @param startAt
     * The start_at
     */
    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The subscriptionRequired
     */
    public Boolean getSubscriptionRequired() {
        return subscriptionRequired;
    }

    /**
     *
     * @param subscriptionRequired
     * The subscription_required
     */
    public void setSubscriptionRequired(Boolean subscriptionRequired) {
        this.subscriptionRequired = subscriptionRequired;
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
     * The zobjectIds
     */
    public List<String> getZobjectIds() {
        return zobjectIds;
    }

    /**
     *
     * @param zobjectIds
     * The zobject_ids
     */
    public void setZobjectIds(List<String> zobjectIds) {
        this.zobjectIds = zobjectIds;
    }

    /**
     *
     * @return
     * The thumbnails
     */
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    /**
     *
     * @param thumbnails
     * The thumbnails
     */
    public void setThumbnails(List<Thumbnail> thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     *
     * @return
     * The huluId
     */
    public String getHuluId() {
        return huluId;
    }

    /**
     *
     * @param huluId
     * The hulu_id
     */
    public void setHuluId(String huluId) {
        this.huluId = huluId;
    }

    /**
     *
     * @return
     * The youtubeId
     */
    public String getYoutubeId() {
        return youtubeId;
    }

    /**
     *
     * @param youtubeId
     * The youtube_id
     */
    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    /**
     *
     * @return
     * The crunchyrollId
     */
    public String getCrunchyrollId() {
        return crunchyrollId;
    }

    /**
     *
     * @param crunchyrollId
     * The crunchyroll_id
     */
    public void setCrunchyrollId(String crunchyrollId) {
        this.crunchyrollId = crunchyrollId;
    }

    /**
     *
     * @return
     * The tranascoded
     */
    public Boolean getTranascoded() {
        return tranascoded;
    }

    /**
     *
     * @param tranascoded
     * The tranascoded
     */
    public void setTranascoded(Boolean tranascoded) {
        this.tranascoded = tranascoded;
    }

    @Override
    public String toString() {
        return "Response{" +
                "Id='" + Id + '\'' +
                ", active=" + active +
                ", categories=" + categories +
                ", country='" + country + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", description='" + description + '\'' +
                ", discoveryUrl='" + discoveryUrl + '\'' +
                ", duration=" + duration +
                ", episode=" + episode +
                ", expireAt='" + expireAt + '\'' +
                ", featured=" + featured +
                ", foreignId='" + foreignId + '\'' +
                ", keywords=" + keywords +
                ", matureContent=" + matureContent +
                ", publishedAt='" + publishedAt + '\'' +
                ", rating=" + rating +
                ", relatedPlaylistIds=" + relatedPlaylistIds +
                ", requestCount=" + requestCount +
                ", season='" + season + '\'' +
                ", segments=" + segments +
                ", shortDescription='" + shortDescription + '\'' +
                ", siteId='" + siteId + '\'' +
                ", startAt='" + startAt + '\'' +
                ", status='" + status + '\'' +
                ", subscriptionRequired=" + subscriptionRequired +
                ", title='" + title + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", zobjectIds=" + zobjectIds +
                ", thumbnails=" + thumbnails +
                ", huluId='" + huluId + '\'' +
                ", youtubeId='" + youtubeId + '\'' +
                ", crunchyrollId='" + crunchyrollId + '\'' +
                ", tranascoded=" + tranascoded +
                '}';
    }
}
