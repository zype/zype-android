package com.zype.android.Db;

import com.google.gson.Gson;
import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.video.Image;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.zypeapi.model.AdvertisingSchedule;
import com.zype.android.zypeapi.model.Analytics;
import com.zype.android.zypeapi.model.AnalyticsDimensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 07.07.2018
 */
public class DbHelper {

    public static List<AdSchedule> adScheduleApiToEntity(List<AdvertisingSchedule> schedule, String videoId) {
        List<AdSchedule> result = new ArrayList<>();
        for (AdvertisingSchedule item : schedule) {
            AdSchedule entity = new AdSchedule();
            entity.videoId = videoId;
            entity.offset = item.offset;
            entity.tag = item.tag;
            result.add(entity);
        }
        return result;
    }

    public static AnalyticBeacon analyticsApiToEntity(Analytics analytics) {
        AnalyticBeacon result = new AnalyticBeacon();
        result.beacon = analytics.beacon;
        AnalyticsDimensions dimensions = analytics.dimensions;
        result.device = dimensions.device;
        result.playerId = dimensions.playerId;
        result.siteId = dimensions.siteId;
        result.videoId = dimensions.videoId;
        return result;
    }

    // Playlist

    public static Playlist playlistApiToEntity(com.zype.android.zypeapi.model.PlaylistData playlist) {
        return playlistUpdateEntityByApi(new Playlist(), playlist);
    }

    public static Playlist playlistUpdateEntityByApi(Playlist entity, com.zype.android.zypeapi.model.PlaylistData data) {
        entity.id = data.id;
        entity.active = data.active ? 1 : 0;
        entity.createdAt = data.createdAt;
        entity.deletedAt = data.deletedAt;
        entity.images = new Gson().toJson(data.images);
        entity.marketplaceIds = new Gson().toJson(data.marketplaceIds);
        entity.parentId = data.parentId;
        entity.playlistItemCount = data.playlistItemCount;
        entity.priority = data.priority;
        entity.purchasePrice = data.purchasePrice;
        entity.purchaseRequired = data.purchaseRequired ? 1 : 0;
        entity.thumbnails = new Gson().toJson(data.thumbnails);
        entity.thumbnailLayout = data.thumbnailLayout;
        entity.title = data.title;
        entity.updatedAt = data.updatedAt;
        return entity;
    }

    public static List<Playlist> playlistDataToEntity(List<PlaylistData> playlists) {
        List<Playlist> result = new ArrayList<>(playlists.size());
        for (PlaylistData item : playlists) {
            Playlist playlistEntity = new Playlist();
            playlistEntity.id = item.getId();
            playlistEntity.active = item.active ? 1 : 0;
            playlistEntity.createdAt = item.getCreatedAt();
            playlistEntity.deletedAt = item.getDeletedAt();
            playlistEntity.images = new Gson().toJson(item.getImages());
            playlistEntity.marketplaceIds = new Gson().toJson(item.marketplaceIds);
            playlistEntity.purchasePrice = item.purchasePrice;
            playlistEntity.purchaseRequired = item.purchaseRequired ? 1 : 0;
            playlistEntity.parentId = item.getParentId();
            playlistEntity.playlistItemCount = item.getPlaylistItemCount();
            playlistEntity.priority = item.getPriority();
            playlistEntity.thumbnails = new Gson().toJson(item.getThumbnails());
            playlistEntity.thumbnailLayout = item.getThumbnailLayout();
            playlistEntity.title = item.getTitle();
            playlistEntity.updatedAt = item.getUpdatedAt();
            result.add(playlistEntity);
        }
        return result;
    }

    public static List<PlaylistVideo> videoDataToPlaylistVideoEntity(List<VideoData> videoData, String playlistId) {
        List<PlaylistVideo> result = new ArrayList<>(videoData.size());
        int number = 1;
        for (VideoData item : videoData) {
            PlaylistVideo entity = new PlaylistVideo();
            entity.number = number;
            entity.playlistId = playlistId;
            entity.videoId = item.getId();
            result.add(entity);
            number++;
        }
        return result;
    }

    public static Video videoDataToVideoEntity(VideoData videoData) {
        return updateVideoEntityByVideoData(new Video(), videoData);
    }

    public static Video updateVideoEntityByVideoData(Video entity, VideoData videoData) {
        entity.id = videoData.getId();
        entity.active = videoData.isActive() ? 1 : 0;
        entity.category = new Gson().toJson(videoData.getCategories());
        entity.country = videoData.getCountry();
        entity.createdAt = videoData.getCreatedAt();
        entity.crunchyrollId = videoData.getCrunchyrollId();
        entity.description = (videoData.getDescription() == null) ? "" : videoData.getDescription();
        entity.discoveryUrl = videoData.getDiscoveryUrl();
        entity.duration = videoData.getDuration();
        entity.episode = (videoData.getEpisode() == null) ? "" : String.valueOf(videoData.getEpisode());
        entity.expireAt = videoData.getExpireAt();
        entity.featured = String.valueOf(videoData.isFeatured() ? 1 : 0);
        entity.foreignId = videoData.getForeignId();
        entity.huluId = videoData.getHuluId();
        entity.isZypeLive = videoData.isZypeLive ? 1 : 0;
        entity.keywords = new Gson().toJson(videoData.getKeywords());
        entity.matureContent = String.valueOf(videoData.isMatureContent() ? 1 : 0);
        entity.onAir = videoData.isOnAir() ? 1 : 0;
        entity.previewIds = new Gson().toJson(videoData.previewIds);
        entity.publishedAt = videoData.getPublishedAt();
        entity.purchaseRequired = String.valueOf(videoData.isPurchaseRequired() ? 1 : 0);
        entity.rating = String.valueOf(videoData.getRating());
        entity.relatedPlaylistIds = new Gson().toJson(videoData.getRelatedPlaylistIds());
        entity.requestCount = String.valueOf(videoData.getRequestCount());
        entity.season = videoData.getSeason();
        entity.segments = new Gson().toJson(videoData.getSegments());
        entity.shortDescription = videoData.getShortDescription();
        entity.siteId = videoData.getSiteId();
        entity.startAt = videoData.getStartAt();
        entity.status = videoData.getStatus();
        entity.subscriptionRequired = String.valueOf(videoData.isSubscriptionRequired() ? 1 : 0);
        entity.thumbnails = new Gson().toJson(videoData.getThumbnails());
        entity.images = new Gson().toJson(videoData.getImages());
        entity.title = videoData.getTitle();
        entity.transcoded = videoData.isTranscoded() ? 1 : 0;
        entity.updatedAt = videoData.getUpdatedAt();
        entity.videoZObject = new Gson().toJson(videoData.getVideoZobjects());
        entity.youtubeId = videoData.getYoutubeId();
        entity.zobjectIds = new Gson().toJson(videoData.getZobjectIds());
        entity.registrationRequired = videoData.isRegistrationRequired() ? 1 : 0;
        return entity;
    }

    public static List<Video> videoDataToVideoEntity(List<VideoData> videoData) {
        List<Video> result = new ArrayList<>(videoData.size());
        for (VideoData item : videoData) {
            Video entity = videoDataToVideoEntity(item);
            result.add(entity);
        }
        return result;
    }

    public static List<PlaylistVideo> videosToPlaylistVideos(List<Video> videos, String playlistId) {
        List<PlaylistVideo> result = new ArrayList<>(videos.size());
        int number = 1;
        for (Video item : videos) {
            PlaylistVideo entity = new PlaylistVideo();
            entity.number = number;
            entity.playlistId = playlistId;
            entity.videoId = item.id;
            result.add(entity);
            number++;
        }
        return result;
    }

    // Video

    public static Video videoApiToEntity(com.zype.android.zypeapi.model.VideoData videoData) {
        return videoUpdateEntityByApi(new Video(), videoData);
    }

    public static Video videoUpdateEntityByApi(Video entity, com.zype.android.zypeapi.model.VideoData videoData) {
        entity.id = videoData.id;
        entity.active = videoData.active ? 1 : 0;
        entity.category = new Gson().toJson(videoData.categories);
        entity.country = videoData.country;
        entity.createdAt = videoData.createdAt;
        entity.crunchyrollId = videoData.crunchyrollId;
        entity.description = (videoData.description == null) ? "" : videoData.description;
        entity.discoveryUrl = videoData.discoveryUrl;
        entity.duration = videoData.duration;
        entity.episode = (videoData.episode == null) ? "" : String.valueOf(videoData.episode);
        entity.expireAt = videoData.expireAt;
        entity.featured = String.valueOf(videoData.featured ? 1 : 0);
        entity.foreignId = videoData.foreignId;
        entity.huluId = videoData.huluId;
        entity.isZypeLive = videoData.isZypeLive ? 1 : 0;
        entity.keywords = new Gson().toJson(videoData.keywords);
        entity.matureContent = String.valueOf(videoData.matureContent ? 1 : 0);
        entity.onAir = videoData.onAir ? 1 : 0;
        entity.previewIds = new Gson().toJson(videoData.previewIds);
        entity.publishedAt = videoData.publishedAt;
        entity.purchaseRequired = String.valueOf(videoData.purchaseRequired ? 1 : 0);
        entity.rating = String.valueOf(videoData.rating);
        entity.registrationRequired = videoData.registrationRequired ? 1 : 0;
        entity.relatedPlaylistIds = new Gson().toJson(videoData.relatedPlaylistIds);
        entity.requestCount = String.valueOf(videoData.requestCount);
        entity.season = videoData.season;
        entity.segments = new Gson().toJson(videoData.segments);
        entity.serializedPlaylistIds = new Gson().toJson(videoData.serializablePlaylistIds);
        entity.shortDescription = videoData.shortDescription;
        entity.siteId = videoData.siteId;
        entity.startAt = videoData.startAt;
        entity.status = videoData.status;
        entity.subscriptionRequired = String.valueOf(videoData.subscriptionRequired ? 1 : 0);
        entity.thumbnails = new Gson().toJson(videoData.thumbnails);
        entity.images = new Gson().toJson(videoData.images);
        entity.title = videoData.title;
        entity.transcoded = videoData.transcoded ? 1 : 0;
        entity.updatedAt = videoData.updatedAt;
        entity.videoZObject = new Gson().toJson(videoData.videoZobjects);
        entity.youtubeId = videoData.youtubeId;
        entity.zobjectIds = new Gson().toJson(videoData.zobjectIds);
        return entity;
    }

    public static List<Video> apiVideosToVideoEntities(List<com.zype.android.zypeapi.model.VideoData> videoData) {
        List<Video> result = new ArrayList<>(videoData.size());
        for (com.zype.android.zypeapi.model.VideoData item : videoData) {
            Video entity = videoApiToEntity(item);
            result.add(entity);
        }
        return result;
    }
}
