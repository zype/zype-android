package com.zype.android.Db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

@Entity(tableName = "video")
public class Video implements PlaylistItem {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NonNull
    public String id;

    @ColumnInfo(name = "active")
    @NonNull
    public Integer active;

    @ColumnInfo(name = "ad_video_tag")
    public String adVideoTag;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "createdAt")
    @NonNull
    public String createdAt;

    @ColumnInfo(name = "crunchyroll_id")
    public String crunchyrollId;

    @ColumnInfo(name = "data_source")
    public String dataSource;

    @ColumnInfo(name = "description")
    @NonNull
    public String description;

    @ColumnInfo(name = "download_audio_path")
    public String downloadAudioPath;

    @ColumnInfo(name = "download_audio_url")
    public String downloadAudioUrl;

    @ColumnInfo(name = "download_video_path")
    public String downloadVideoPath;

    @ColumnInfo(name = "download_video_url")
    public String downloadVideoUrl;

    @ColumnInfo(name = "discovery_url")
    public String discoveryUrl;

    @ColumnInfo(name = "duration")
    @NonNull
    public Integer duration;

    @ColumnInfo(name = "guest")
    public String guest;

    @ColumnInfo(name = "entitlement_updated_at")
    public String entitlementUpdatedAt;

    @ColumnInfo(name = "episode")
    public String episode;

    @ColumnInfo(name = "expire_at")
    public String expireAt;

    @ColumnInfo(name = "featured")
    public String featured;

    @ColumnInfo(name = "foreign_id")
    public String foreignId;

    @ColumnInfo(name = "hulu_id")
    public String huluId;

    @ColumnInfo(name = "is_downloaded_video")
    public Integer isDownloadedVideo;

    @ColumnInfo(name = "is_downloaded_audio")
    public Integer isDownloadedAudio;

    @ColumnInfo(name = "is_downloaded_video_should_be")
    public Integer isDownloadedVideoShouldBe;

    @ColumnInfo(name = "is_downloaded_audio_should_be")
    public Integer isDownloadedAudioShouldBe;

    @ColumnInfo(name = "is_entitled")
    public Integer isEntitled;

    @ColumnInfo(name = "is_favorite")
    public Integer isFavorite;

    @ColumnInfo(name = "is_highlight")
    public Integer isHighlight;

    @ColumnInfo(name = "is_play_started")
    public Integer isPlayStarted;

    @ColumnInfo(name = "is_play_finished")
    public Integer isPlayFinished;

    @ColumnInfo(name = "is_zype_live")
    public Integer isZypeLive;

    @ColumnInfo(name = "keywords")
    public String keywords;

    @ColumnInfo(name = "mature_content")
    public String matureContent;

    @ColumnInfo(name = "on_air")
    public Integer onAir;

    @ColumnInfo(name = "play_time")
    public Long playTime;

    @ColumnInfo(name = "player_audio_url")
    public String playerAudioUrl;

    @ColumnInfo(name = "player_video_url")
    public String playerVideoUrl;

    @ColumnInfo(name = "playlists")
    public String playlists;

    @ColumnInfo(name = "preview_ids")
    public String previewIds;

    @ColumnInfo(name = "published_at")
    public String publishedAt;

    @ColumnInfo(name = "PurchaseRequired")
    public String purchaseRequired;

    @ColumnInfo(name = "rating")
    public String rating;

    @ColumnInfo(name = "related_playlist_ids")
    public String relatedPlaylistIds;

    @ColumnInfo(name = "request_count")
    public String requestCount;

    @ColumnInfo(name = "season")
    public String season;

    @ColumnInfo(name = "segment")
    public Integer segment;

    @ColumnInfo(name = "segments")
    public String segments;

    @ColumnInfo(name = "serialized_playlist_ids")
    public String serializedPlaylistIds;

    @ColumnInfo(name = "short_description")
    public String shortDescription;

    @ColumnInfo(name = "site_id")
    public String siteId;

    @ColumnInfo(name = "start_at")
    public String startAt;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "subscription_required")
    public String subscriptionRequired;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "thumbnails")
    public String thumbnails;

    @ColumnInfo(name = "images")
    public String images;

    @ColumnInfo(name = "transcoded")
    public Integer transcoded;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;

    @ColumnInfo(name = "video_zobject")
    public String videoZObject;

    @ColumnInfo(name = "youtube_id")
    public String youtubeId;

    @ColumnInfo(name = "zobjectIds")
    public String zobjectIds;

    @ColumnInfo(name = "registration_required")
    public int registrationRequired;


    @Override
    public String getTitle() {
        return title;
    }

}
