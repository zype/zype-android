package com.zype.android.ui.Gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.video.VideoListEvent;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 12.06.2018.
 */
public class GalleryViewModel extends AndroidViewModel {
    private MediatorLiveData<List<GalleryRow>> data;
    private LiveData<List<Playlist>> liveDataPlaylists;
    private Map<String, LiveData<List<Video>>> liveDataVideos;
    private Map<String, LiveData<List<Playlist>>> liveDataNestedPlaylists;

    private String parentPlaylistId;

    DataRepository repo;
    WebApiManager api;

    public GalleryViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = WebApiManager.getInstance();
        api.subscribe(this);
    }

    @Override
    protected void onCleared() {
        api.unsubscribe(this);
        super.onCleared();
    }

    public LiveData<List<GalleryRow>> getGalleryRows(String parentPlaylistId) {
        if (data == null) {
            this.parentPlaylistId = parentPlaylistId;

            data = new MediatorLiveData<>();
            liveDataPlaylists = getPlaylists(parentPlaylistId);
            liveDataVideos = new HashMap<>();
            liveDataNestedPlaylists = new HashMap<>();

            data.addSource(liveDataPlaylists, new Observer<List<Playlist>>() {
                @Override
                public void onChanged(@Nullable List<Playlist> playlists) {
                    Logger.d("onChanged(): Playlists, size=" + playlists.size());

                    if (!playlists.isEmpty()) {
                        data.removeSource(liveDataPlaylists);
                    }
                    final List<GalleryRow> galleryRows = new ArrayList<>();

                    for (final Playlist playlist : playlists) {
                        final GalleryRow row = new GalleryRow(playlist);

                        // Add video items to the row if playlist contains any video
                        if (playlist.playlistItemCount > 0) {
                            LiveData<List<Video>> playlistVideos = liveDataVideos.get(playlist.id);
                            if (playlistVideos == null) {
                                playlistVideos = getPlaylistVideos(playlist.id);
                                liveDataVideos.put(playlist.id, playlistVideos);
                                data.addSource(playlistVideos, new Observer<List<Video>>() {
                                    @Override
                                    public void onChanged(@Nullable List<Video> videos) {
                                        Logger.d("onChanged(): Videos, size=" + videos.size());
                                        row.videos = videos;
                                        if (allDataLoaded()) {
                                            data.setValue(galleryRows);
                                            ZypeApp.needToLoadData = false;
                                        }
                                    }
                                });
                            }
                        }
                        // Otherwise add nested playlists
                        else {
                            LiveData<List<Playlist>> nestedPlaylists = liveDataNestedPlaylists.get(playlist.id);
                            if (nestedPlaylists == null) {
                                nestedPlaylists = getPlaylists(playlist.id);
                                liveDataNestedPlaylists.put(playlist.id, nestedPlaylists);
                                data.addSource(nestedPlaylists, new Observer<List<Playlist>>() {
                                    @Override
                                    public void onChanged(@Nullable List<Playlist> playlists) {
                                        Logger.d("onChanged(): Nested playlists, size=" + playlists.size());
                                        if (playlists.isEmpty()) {
                                            galleryRows.remove(row);
                                        }
                                        else {
                                            row.nestedPlaylists = playlists;
                                        }
                                        if (allDataLoaded()) {
                                            data.setValue(galleryRows);
                                            ZypeApp.needToLoadData = false;
                                        }
                                    }
                                });
                            }
                        }

                        galleryRows.add(row);
                    }
                    data.setValue(galleryRows);
                }
            });
        }
        return data;
    }

    public boolean allDataLoaded() {
        boolean result = true;
        if (data.getValue() == null || data.getValue().isEmpty()) {
            result = false;
        }
        else {
            for (GalleryRow item : data.getValue()) {
                if ((item.videos == null || item.videos.isEmpty())
                        && (item.nestedPlaylists == null || item.nestedPlaylists.isEmpty())) {
                    result = false;
                    break;
                }
            }
        }
        Logger.d("allDataLoaded(): " + result);
        return result;
    }

    /**
     * Return observable list of plalists with specified parent playlist id from the local database.
     * If result is empty, then start loading playlist from the server.
     *
     * @param parentPlaylistId Parent playlist id
     * @return LiveData<List<Playlist>> object
     */
    private LiveData<List<Playlist>> getPlaylists(String parentPlaylistId) {
        Logger.d("getPlaylists(): parentPlaylistId=" + parentPlaylistId);
        LiveData<List<Playlist>> result = repo.getPlaylists(parentPlaylistId);
        if (result.getValue() == null || result.getValue().isEmpty()
                || ZypeApp.needToLoadData) {
            loadPlaylists(parentPlaylistId);
        }
        return result;
    }

    /**
     * Make API request for playlists with specified id
     *
     * @param parentPlaylistId Parent playlist id
     */
    private void loadPlaylists(String parentPlaylistId) {
        Logger.d("loadPlaylists(): parentPlaylistId=" + parentPlaylistId);
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addParentId(parentPlaylistId)
                .addPerPage(100);
        api.executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
    }

    /**
     * Handles API request for playlists
     *
     * @param event Response event
     */
    @Subscribe
    public void handleRetrievePlaylist(PlaylistEvent event) {
        Logger.d("handlePlaylistEvent size=" + event.getEventData().getModelData().getResponse().size());
        List<PlaylistData> playlists = event.getEventData().getModelData().getResponse();
        if (playlists.size() > 0) {
            repo.insertPlaylists(DbHelper.playlistDataToEntity(playlists));

            for (PlaylistData playlistData : playlists) {
                if (!TextUtils.isEmpty(playlistData.getParentId())
                        && playlistData.getParentId().equals(parentPlaylistId)) {
                    // Load playlist videos
                    if (playlistData.getPlaylistItemCount() > 0) {
                        loadPlaylistVideos(playlistData.getId(), 1);
                    }
                    // Load child playlists
                    else {
                        loadPlaylists(playlistData.getId());
                    }
                }
            }
        }
    }

    private LiveData<List<Video>> getPlaylistVideos(String playlistId) {
        // Get videos for specified 'playlistId'
        return DataRepository.getInstance(getApplication()).getPlaylistVideos(playlistId);
    }

    private void loadPlaylistVideos(String playlistId, int page) {
        Logger.d("loadPlaylistVideos(): playlistId=" + playlistId + ", page=" + page);

        VideoParamsBuilder builder = new VideoParamsBuilder();
        builder.addPlaylistId(playlistId);
        builder.addPage(page);
        builder.addPerPage(100);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO_FROM_PLAYLIST, builder.build());
    }

    @Subscribe
    public void handleRetrieveVideo(VideoListEvent event) {
        List<VideoData> videoData = event.getEventData().getModelData().getVideoData();
        if (videoData != null) {
            Logger.d("handleRetrieveVideo(): size=" + videoData.size());
            if (videoData.size() > 0) {
                repo.insertVideos(DbHelper.videoDataToVideoEntity(videoData));
                repo.deletePlaylistVideos(event.getPlaylistId());
                repo.insertPlaylistVideos(DbHelper.videoDataToPlaylistVideoEntity(videoData, event.getPlaylistId()));
            }
        }
    }


}

