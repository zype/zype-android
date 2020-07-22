package com.zype.android.zypeapi.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 25.05.2017.
 */

public class VideoData {
    @SerializedName("_id")
    @Expose
    public String id;

    @Expose
    public boolean active;

    @Nullable
    @Expose
    public List<Category> categories = new ArrayList<>();

    @Expose
    public String country;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @Expose
    public String description;

    @SerializedName("discovery_url")
    @Expose
    public String discoveryUrl;

    @Expose
    public int duration;

    @Expose
    public Integer episode;

    @SerializedName("expire_at")
    @Expose
    public String expireAt;

    @Expose
    public boolean featured;

    @SerializedName("foreign_id")
    @Expose
    public String foreignId;

    @Expose
    public List<Image> images = new ArrayList<>();

    @SerializedName("is_zype_live")
    @Expose
    public boolean isZypeLive;

    @Expose
    public List<String> keywords = new ArrayList<>();

    @SerializedName("on_air")
    @Expose
    public boolean onAir;

    @SerializedName("preview_ids")
    @Expose
    public List<String> previewIds = new ArrayList<>();

    @SerializedName("published_at")
    @Expose
    public String publishedAt;

    @Expose
    public int rating;

    @SerializedName("registration_required")
    @Expose
    public boolean registrationRequired;

    @SerializedName("related_playlist_ids")
    @Expose
    public List<String> relatedPlaylistIds = new ArrayList<>();

    @SerializedName("request_count")
    @Expose
    public int requestCount;

    @Expose
    public String season;

    @Expose
    public List<Segment> segments = new ArrayList<>();

    @SerializedName("serializable_playlist_ids")
    @Expose
    public List<String> serializablePlaylistIds = new ArrayList<>();

    @SerializedName("short_description")
    @Expose
    public String shortDescription;

    @SerializedName("site_id")
    @Expose
    public String siteId;

    @SerializedName("start_at")
    @Expose
    public String startAt;

    @Expose
    public String status;

    @Expose
    public String title;

    @Expose
    public boolean transcoded;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("video_zobjects")
    @Expose
    public List<VideoZobject> videoZobjects = new ArrayList<>();

    @SerializedName("zobject_ids")
    @Expose
    public List<String> zobjectIds = new ArrayList<>();

    @Expose
    public List<Thumbnail> thumbnails = new ArrayList<>();

    @SerializedName("hulu_id")
    @Expose
    public String huluId;

    @SerializedName("youtube_id")
    @Expose
    public String youtubeId;

    @SerializedName("crunchyroll_id")
    @Expose
    public String crunchyrollId;

    @SerializedName("vimeo_id")
    @Expose
    public String vimeoId;

    @SerializedName("subscription_required")
    @Expose
    public boolean subscriptionRequired;

    @SerializedName("pass_required")
    @Expose
    public boolean passRequired;

    @SerializedName("purchase_price")
    @Expose
    public float purchasePrice;

    @SerializedName("purchase_required")
    @Expose
    public boolean purchaseRequired;

    @SerializedName("rental_duration")
    @Expose
    public int rentalDuration;

    @SerializedName("rental_price")
    @Expose
    public float rentalPrice;

    @SerializedName("rental_required")
    @Expose
    public boolean rentalRequired;

    @SerializedName("mature_content")
    @Expose
    public boolean matureContent;

}
