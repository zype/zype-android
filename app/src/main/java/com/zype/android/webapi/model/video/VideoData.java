package com.zype.android.webapi.model.video;

/**
 * @author vasya
 * @version 1
 * date 6/29/15
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.player.AdvertisingSchedule;
import com.zype.android.webapi.model.search.Segment;
import com.zype.android.webapi.model.zobjects.ZObject;

import java.util.ArrayList;
import java.util.List;

public class VideoData {

    @NonNull
    @SerializedName("_id")
    @Expose
    private String Id;

    @Expose
    private boolean active;

    @Nullable
    @Expose
    private List<Category> categories = new ArrayList<>();
    @Expose
    private String country;

    @Nullable
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @Nullable
    @Expose
    private String description;

    @Nullable
    @SerializedName("discovery_url")
    @Expose
    private String discoveryUrl;

    @Expose
    private int duration;

    @Nullable
    @Expose
    private Integer episode;

    @Nullable
    @SerializedName("expire_at")
    @Expose
    private String expireAt;

    @Expose
    private boolean featured;

    @Nullable
    @SerializedName("foreign_id")
    @Expose
    private String foreignId;

    @SerializedName("is_zype_live")
    @Expose
    public boolean isZypeLive;

    @Nullable
    @Expose
    private List<String> keywords = new ArrayList<>();

    @SerializedName("mature_content")
    @Expose
    private boolean matureContent;

    @SerializedName("on_air")
    @Expose
    private boolean onAir;

    @SerializedName("preview_ids")
    @Expose
    public List<String> previewIds = new ArrayList<>();

    @Nullable
    @SerializedName("published_at")
    @Expose
    private String publishedAt;

    @Nullable
    @SerializedName("purchase_required")
    @Expose
    private boolean purchaseRequired;

    @Expose
    private int rating;

    @Nullable
    @SerializedName("related_playlist_ids")
    @Expose
    private List<String> relatedPlaylistIds = new ArrayList<>();

    @SerializedName("request_count")
    @Expose
    private int requestCount;

    @Nullable
    @Expose
    private String season;

    @Nullable
    @SerializedName("short_description")
    @Expose
    private String shortDescription;

    @Nullable
    @SerializedName("site_id")
    @Expose
    private String siteId;

    @Nullable
    @SerializedName("start_at")
    @Expose
    private String startAt;

    @Nullable
    @Expose
    private String status;

    @SerializedName("subscription_required")
    @Expose
    private boolean subscriptionRequired;

    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }

    @SerializedName("registration_required")
    @Expose
    private boolean registrationRequired;

    @Nullable
    @Expose
    private String title;

    @Expose
    private boolean transcoded;

    @Nullable
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("video_zobjects")
    @Expose
    private List<VideoZobject> videoZobjects = new ArrayList<>();

    @Nullable
    @SerializedName("zobject_ids")
    @Expose
    private List<String> zobjectIds = new ArrayList<>();

    @Nullable
    @Expose
    private List<Thumbnail> thumbnails = new ArrayList<>();

    @Nullable
    @Expose
    private List<Image> images = new ArrayList<>();

    @Nullable
    @SerializedName("hulu_id")
    @Expose
    private String huluId;

    @Nullable
    @SerializedName("youtube_id")
    @Expose
    private String youtubeId;

    @Nullable
    @SerializedName("crunchyroll_id")
    @Expose
    private String crunchyrollId;

    @Expose
    private List<Segment> segments = new ArrayList<>();

    @Nullable
    @Expose
    private List<String> playlists = new ArrayList<>();


    private int mPlayingPosition;
    private String mPlayerAudioUrl;
    private String mPlayerVideoUrl;
    private String downloadAudioPath;
    private String downloadAudioUrl;
    private String downloadVideoPath;
    private String downloadVideoUrl;
    private boolean isVideoDownloaded;
    private boolean isAudioDownloaded;
    private List<ZObject> guests;
    private String adVideoTag;

    public List<AdvertisingSchedule> adSchedule;

    public VideoData(@NonNull String id, boolean isActive, String country) {
        this.Id = id;
        this.active = isActive;
        this.country = country;
    }

    public VideoData(@Nullable String title, String urlImageThumbnail) {
        this.title = title;
        Thumbnail thumbnail = new Thumbnail(urlImageThumbnail);
        thumbnails.add(thumbnail);
    }

    public static VideoData newVideo(String id, boolean isActive, String country) {
        return new VideoData(id, isActive, country);
    }

    /**
     * @return The Id
     */
    @NonNull
    public String getId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(@NonNull String Id) {
        this.Id = Id;
    }

    /**
     * @return The active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active The active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return The categories
     */
    @Nullable
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories
     */
    public void setCategories(@Nullable List<Category> categories) {
        this.categories = categories;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The createdAt
     */
    @Nullable
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(@Nullable String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * @return The discoveryUrl
     */
    @Nullable
    public String getDiscoveryUrl() {
        return discoveryUrl;
    }

    /**
     * @param discoveryUrl The discovery_url
     */
    public void setDiscoveryUrl(@Nullable String discoveryUrl) {
        this.discoveryUrl = discoveryUrl;
    }

    /**
     * @return The duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return The episode
     */
    @Nullable
    public Integer getEpisode() {
        return episode;
    }

    /**
     * @param episode The episode
     */
    public void setEpisode(@Nullable Integer episode) {
        this.episode = episode;
    }

    /**
     * @return The expireAt
     */
    @Nullable
    public String getExpireAt() {
        return expireAt;
    }

    /**
     * @param expireAt The expire_at
     */
    public void setExpireAt(@Nullable String expireAt) {
        this.expireAt = expireAt;
    }

    /**
     * @return The featured
     */
    public boolean isFeatured() {
        return featured;
    }

    /**
     * @param featured The featured
     */
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    /**
     * @return The foreignId
     */
    @Nullable
    public String getForeignId() {
        return foreignId;
    }

    /**
     * @param foreignId The foreign_id
     */
    public void setForeignId(@Nullable String foreignId) {
        this.foreignId = foreignId;
    }

    /**
     * @return The keywords
     */
    @Nullable
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords The keywords
     */
    public void setKeywords(@Nullable List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * @return The matureContent
     */
    public boolean isMatureContent() {
        return matureContent;
    }

    /**
     * @param matureContent The mature_content
     */
    public void setMatureContent(boolean matureContent) {
        this.matureContent = matureContent;
    }

    /**
     * @return The onAir
     */
    public boolean isOnAir() {
        return onAir;
    }

    /**
     * @param onAir The onAir
     */
    public void setOnAir(boolean onAir) {
        this.onAir = onAir;
    }

    /**
     * @return The publishedAt
     */
    @Nullable
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt The published_at
     */
    public void setPublishedAt(@Nullable String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * @return The rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @return The relatedPlaylistIds
     */
    @Nullable
    public List<String> getRelatedPlaylistIds() {
        return relatedPlaylistIds;
    }

    /**
     * @param relatedPlaylistIds The related_playlist_ids
     */
    public void setRelatedPlaylistIds(@Nullable List<String> relatedPlaylistIds) {
        this.relatedPlaylistIds = relatedPlaylistIds;
    }

    /**
     * @return The requestCount
     */
    public int getRequestCount() {
        return requestCount;
    }

    /**
     * @param requestCount The request_count
     */
    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    /**
     * @return The season
     */
    @Nullable
    public String getSeason() {
        return season;
    }

    /**
     * @param season The season
     */
    public void setSeason(@Nullable String season) {
        this.season = season;
    }

    /**
     * @return The shortDescription
     */
    @Nullable
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * @param shortDescription The short_description
     */
    public void setShortDescription(@Nullable String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * @return The siteId
     */
    @Nullable
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId The site_id
     */
    public void setSiteId(@Nullable String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return The startAt
     */
    @Nullable
    public String getStartAt() {
        return startAt;
    }

    /**
     * @param startAt The start_at
     */
    public void setStartAt(@Nullable String startAt) {
        this.startAt = startAt;
    }

    /**
     * @return The status
     */
    @Nullable
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    /**
     * @return The subscriptionRequired
     */
    public boolean isSubscriptionRequired() {
        return subscriptionRequired;
    }

    /**
     * @param subscriptionRequired The subscription_required
     */
    public void setSubscriptionRequired(boolean subscriptionRequired) {
        this.subscriptionRequired = subscriptionRequired;
    }

    /**
     * @return The title
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public boolean isTranscoded() {
        return transcoded;
    }

    public void setTranscoded(boolean transcoded) {
        this.transcoded = transcoded;
    }

    /**
     * @return The updatedAt
     */
    @Nullable
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(@Nullable String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The videoZobjects
     */
    public List<VideoZobject> getVideoZobjects() {
        return videoZobjects;
    }

    /**
     * @param videoZobjects The video_zobjects
     */
    public void setVideoZobjects(List<VideoZobject> videoZobjects) {
        this.videoZobjects = videoZobjects;
    }

    /**
     * @return The zobjectIds
     */
    @Nullable
    public List<String> getZobjectIds() {
        return zobjectIds;
    }

    /**
     * @param zobjectIds The zobject_ids
     */
    public void setZobjectIds(@Nullable List<String> zobjectIds) {
        this.zobjectIds = zobjectIds;
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
     * @return The video images
     */
    @Nullable
    public List<Image> getImages() {
        return images;
    }

    /**
     * @param images The video images
     */
    public void setImages(@Nullable List<Image> images) {
        this.images = images;
    }

    /**
     * @return The huluId
     */
    @Nullable
    public String getHuluId() {
        return huluId;
    }

    /**
     * @param huluId The hulu_id
     */
    public void setHuluId(@Nullable String huluId) {
        this.huluId = huluId;
    }

    /**
     * @return The youtubeId
     */
    @Nullable
    public String getYoutubeId() {
        return youtubeId;
    }

    /**
     * @param youtubeId The youtube_id
     */
    public void setYoutubeId(@Nullable String youtubeId) {
        this.youtubeId = youtubeId;
    }

    /**
     * @return The crunchyrollId
     */
    @Nullable
    public String getCrunchyrollId() {
        return crunchyrollId;
    }

    /**
     * @param crunchyrollId The crunchyroll_id
     */
    public void setCrunchyrollId(@Nullable String crunchyrollId) {
        this.crunchyrollId = crunchyrollId;
    }

    /**
     * @return The segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * @param segments The segments
     */
    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public List<String> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<String> playlists) { this.playlists = playlists;}

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void setPlayingPosition(int playingPosition) {
        mPlayingPosition = playingPosition;
    }

    public String getPlayerVideoUrl() {
        return mPlayerVideoUrl;
    }

    public void setPlayerVideoUrl(String playerVideoUrl) {
        mPlayerVideoUrl = playerVideoUrl;
    }

    public String getPlayerAudioUrl() {
        return mPlayerAudioUrl;
    }

    public void setPlayerAudioUrl(String playerAudioUrl) {
        mPlayerAudioUrl = playerAudioUrl;
    }

//    public void setDownloaded(boolean downloaded) {
//        this.dowloaded = downloaded;
//    }

    public String getDownloadAudioPath() {
        return downloadAudioPath;
    }

    public void setDownloadAudioPath(String downloadAudioPath) {
        this.downloadAudioPath = downloadAudioPath;
    }

    public String getDownloadAudioUrl() {
        return downloadAudioUrl;
    }

    public void setDownloadAudioUrl(String downloadAudioUrl) {
        this.downloadAudioUrl = downloadAudioUrl;
    }

    public String getDownloadVideoPath() {
        return downloadVideoPath;
    }

    public void setDownloadVideoPath(String downloadVideoPath) {
        this.downloadVideoPath = downloadVideoPath;
    }

    public String getDownloadVideoUrl() {
        return downloadVideoUrl;
    }

    public void setDownloadVideoUrl(String downloadVideoUrl) {
        this.downloadVideoUrl = downloadVideoUrl;
    }

    public void setVideoDownloaded(boolean videoDownloaded) {
        this.isVideoDownloaded = videoDownloaded;
    }

    public void setAudioDownloaded(boolean audioDownloaded) {
        this.isAudioDownloaded = audioDownloaded;
    }

    public boolean isVideoDownloaded() {
        return isVideoDownloaded;
    }

    public void setIsVideoDownloaded(boolean isVideoDownloaded) {
        this.isVideoDownloaded = isVideoDownloaded;
    }

    public boolean isAudioDownloaded() {
        return isAudioDownloaded;
    }

    public void setIsAudioDownloaded(boolean isAudioDownloaded) {
        this.isAudioDownloaded = isAudioDownloaded;
    }

    public void setGuests(List<ZObject> guests) {
        this.guests = guests;
    }

    public List<ZObject> getGuests() {
        return guests;
    }

    //    public boolean isDowloaded() {
//        return dowloaded;
//    }

    public String getAdVideoTag() {
        return adVideoTag;
    }

    public void setAdVideoTag(String adVideoTag) {
        this.adVideoTag = adVideoTag;
    }

    @Nullable
    public boolean isPurchaseRequired() {
        return purchaseRequired;
    }

    public void setPurchaseRequired(@Nullable boolean purchaseRequired) {
        this.purchaseRequired = purchaseRequired;
    }
}