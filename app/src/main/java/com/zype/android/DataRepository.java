package com.zype.android;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.FavoriteVideo;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.Db.ZypeDb;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideoEntitlementData;
import com.zype.android.zypeapi.model.VideoEntitlementsResponse;
import com.zype.android.zypeapi.model.VideoFavoriteData;
import com.zype.android.zypeapi.model.VideoFavoritesResponse;
import com.zype.android.zypeapi.model.VideoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

public class DataRepository {
    private static DataRepository INSTANCE;

    private ZypeDb db;

    public interface IDataLoading {
        void onLoadingCompleted(boolean success);
    }

    private DataRepository(Application application) {
        db = ZypeDb.getDatabase(application);
    }

    public static DataRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    // Ad schedule

    public List<AdSchedule> getAdScheduleSync(String videoId) {
        return db.zypeDao().getAdScheduleSync(videoId);
    }

    public void deleteAdSchedule(String videoId) {
        db.zypeDao().deleteAdSchedule(videoId);
    }

    public void insertAdSchedule(List<AdSchedule> schedule) {
        db.zypeDao().insertAdSchedule(schedule);
    }

    // Analytics beacon

    public AnalyticBeacon getAnalyticsBeaconSync(String videoId) {
        return db.zypeDao().getAnalyticsBeaconSync(videoId);
    }

    public void deleteAnalyticsBeacon(String videoId) {
        db.zypeDao().deleteAnalyticsBeacon(videoId);
    }

    public void insertAnalyticsBeacon(AnalyticBeacon beacon) {
        db.zypeDao().insertAnalyticsBeacon(beacon);
    }

    // Playlist

    public LiveData<List<Playlist>> getPlaylists(String parentId) {
        return db.zypeDao().getPlaylists(parentId);
    }

    public List<Playlist> getPlaylistsSync(String parentId) {
        return db.zypeDao().getPlaylistsSync(parentId);
    }

    public Playlist getPlaylistSync(String playlistId) {
        return db.zypeDao().getPlaylistSync(playlistId);
    }

    public void insertPlaylists(List<Playlist> playlists) {
        db.zypeDao().insertPlaylists(playlists);
    }

    // Video

    public LiveData<List<Video>> getPlaylistVideos(String playlistId) {
        return db.zypeDao().getPlaylistVideos(playlistId);
    }

    public List<Video> getPlaylistVideosSync(String playlistId) {
        return db.zypeDao().getPlaylistVideosSync(playlistId);
    }

    public List<Video> getFavoriteVideosSync() {
        return db.zypeDao().getFavoriteVideosSync();
    }

    public void insertPlaylistVideos(List<PlaylistVideo> playlistVideos) {
        db.zypeDao().insertPlaylistVideos(playlistVideos);
    }

    public void deletePlaylistVideos(String playlistId) {
        db.zypeDao().deletePlaylistVideos(playlistId);
    }

    public void insertPlaylistVideos(List<Video> videos, Playlist playlist) {
        List<PlaylistVideo> playlistVideos = new ArrayList<>();
        int number = db.zypeDao().getPlaylistVideosSync(playlist.id).size() + 1;
        for (Video video : videos) {
            PlaylistVideo playlistVideo = new PlaylistVideo();
            playlistVideo.number = number;
            playlistVideo.playlistId = playlist.id;
            playlistVideo.videoId = video.id;
            playlistVideos.add(playlistVideo);
            number++;
        }
        db.zypeDao().insertPlaylistVideos(playlistVideos);
    }

    public void deletePlaylistVideos(Playlist playlist, List<Video> videos) {
        for (Video video : videos) {
            db.zypeDao().deletePlaylistVideo(playlist.id, video.id);
        }
    }

    public Video getVideoSync(String videoId) {
        Video video = db.zypeDao().getVideoSync(videoId);
        if (video != null) {
            if (video.isDownloadedAudio == null) video.isDownloadedAudio = 0;
            if (video.isDownloadedVideo == null) video.isDownloadedVideo = 0;
            if (video.isZypeLive == null) video.isZypeLive = 0;
            if (video.onAir == null) video.onAir = 0;
            if (video.playTime == null) video.playTime = 0L;
        }
        return video;
    }

    public void updateVideo(Video video) {
        db.zypeDao().updateVideo(video);
    }

    public void insertVideos(List<Video> videos) {
        db.zypeDao().insertVideos(videos);
    }

    public void loadVideo(String videoId, IZypeApiListener listener) {
        ZypeApi.getInstance().getVideo(videoId, false,
                (IZypeApiListener<VideoResponse>) response -> {
                    if (response.isSuccessful) {
                        if (response.data != null) {
                            Video video = getVideoSync(response.data.videoData.id);
                            List<Video> videoList = new ArrayList<>();
                            if (video != null) {
                                videoList.add(DbHelper.videoUpdateEntityByApi(video, response.data.videoData));
                            } else {
                                videoList.add(DbHelper.videoApiToEntity(response.data.videoData));
                            }
                            insertVideos(videoList);

                            if (listener != null) {
                                listener.onCompleted(response);
                            }
                        }
                    }
                });
    }

    public void loadVideoPlaylistIds(String videoId, IZypeApiListener listener) {
        ZypeApi.getInstance().getVideo(videoId, true,
                (IZypeApiListener<VideoResponse>) response -> {
                    if (response.isSuccessful) {
                        if (response.data != null) {
                            Video video = getVideoSync(response.data.videoData.id);
                            List<Video> videoList = new ArrayList<>();
                            if (video != null) {
                                videoList.add(DbHelper.videoUpdateEntityByApi(video, response.data.videoData));
                            } else {
                                videoList.add(DbHelper.videoApiToEntity(response.data.videoData));
                            }
                            insertVideos(videoList);

                            if (listener != null) {
                                listener.onCompleted(response);
                            }
                        }
                    }
                });
    }

    // Video entitlements

    public List<Video> getEntitledVideosSync() {
        return db.zypeDao().getEntitledVideosSync();
    }

    public void loadVideoEntitlements(IDataLoading listener) {
        String accessToken = AuthHelper.getAccessToken();
        final List<VideoEntitlementData> entitlements = new ArrayList<>();
        final IZypeApiListener<VideoEntitlementsResponse> apiListener = new IZypeApiListener<VideoEntitlementsResponse>() {
            @Override
            public void onCompleted(ZypeApiResponse<VideoEntitlementsResponse> response) {
                if (response.isSuccessful) {
                    entitlements.addAll(response.data.videoEntitlements);
                    if (response.data.pagination.current == response.data.pagination.pages
                            || response.data.pagination.pages == 0) {
                        clearVideoEntitlements();
                        updateVideoEntitlements(entitlements);
                        if (listener != null) {
                            listener.onLoadingCompleted(true);
                        }
                    }
                    else {
                        ZypeApi.getInstance().getVideoEntitlements(accessToken,
                                response.data.pagination.next, this);
                    }
                }
                else {
                    if (listener != null) {
                        listener.onLoadingCompleted(false);
                    }
                }
            }
        };
        ZypeApi.getInstance().getVideoEntitlements(accessToken, 1, apiListener);
    }

    private void updateVideoEntitlements(List<VideoEntitlementData> entitlements) {
        for (VideoEntitlementData item : entitlements) {
            Video video = getVideoSync(item.videoId);
            if (video == null) {
                loadVideo(item.videoId, response -> {
                    if (response.isSuccessful) {
                        Video dbVideo = getVideoSync(item.videoId);
                        if (dbVideo != null) {
                            dbVideo.isEntitled = 1;
                            dbVideo.entitlementUpdatedAt = item.createdAt;
                            updateVideo(dbVideo);
                        }
                    }
                });
            }
            else {
                video.isEntitled = 1;
                video.entitlementUpdatedAt = item.createdAt;
                updateVideo(video);
            }
        }
    }

    public void clearVideoEntitlements() {
        db.zypeDao().clearVideoEntitlements();
    }

    // Video favorites

    public void loadVideoFavorites(IDataLoading listener) {
        deleteVideoFavorites();
        String accessToken = AuthHelper.getAccessToken();
        String consumerId = SettingsProvider.getInstance().getConsumerId();
        ZypeApi.getInstance().getVideoFavorites(accessToken, consumerId,
                (IZypeApiListener<VideoFavoritesResponse>) response -> {
                    if (response.isSuccessful) {
                        for (VideoFavoriteData item : response.data.videoFavorites) {
                            Video video = getVideoSync(item.videoId);
                            if (video == null) {
                                loadVideo(item.videoId, response1 -> {
                                    if (response.isSuccessful) {
                                        Video dbVideo = getVideoSync(item.videoId);
                                        if (dbVideo != null) {
                                            dbVideo.isFavorite = 1;
                                            updateVideo(dbVideo);

                                            FavoriteVideo favoriteVideo = new FavoriteVideo();
                                            favoriteVideo.id = item.id;
                                            favoriteVideo.videoId = item.videoId;
                                            addVideoFavorite(favoriteVideo);
                                        }
                                    }
                                });
                            }
                            else {
                                video.isFavorite = 1;
                                updateVideo(video);

                                FavoriteVideo favoriteVideo = new FavoriteVideo();
                                favoriteVideo.id = item.id;
                                favoriteVideo.videoId = item.videoId;
                                addVideoFavorite(favoriteVideo);
                            }
                        }
                        if (listener != null) {
                            listener.onLoadingCompleted(true);
                        }
                    }
                    else {
                        if (listener != null) {
                            listener.onLoadingCompleted(false);
                        }
                    }
                });
    }

    public void addVideoFavorite(FavoriteVideo favoriteVideo) {
        db.zypeDao().addVideoFavorite(favoriteVideo);
    }

    public FavoriteVideo getVideoFavoriteByVideoId(String videoId) {
        return db.zypeDao().getVideoFavoriteByVideoId(videoId);
    }

    public void deleteVideoFavoriteByVideoId(String videoId) {
        db.zypeDao().deleteVideoFavoriteByVideoId(videoId);
    }

    public void deleteVideoFavorites() {
        List<FavoriteVideo> favoriteVideos = db.zypeDao().getVideoFavorites();
        for (FavoriteVideo item : favoriteVideos) {
            Video video = getVideoSync(item.videoId);
            if (video != null) {
                video.isFavorite = 0;
                updateVideo(video);
            }
        }
        db.zypeDao().deleteVideoFavorites();
    }
}
