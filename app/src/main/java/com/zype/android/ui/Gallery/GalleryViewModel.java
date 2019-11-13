package com.zype.android.ui.Gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.video.VideoListEvent;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.PlaylistsResponse;
import com.zype.android.zypeapi.model.VideosResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zype.android.core.settings.SettingsProvider.CONSUMER_SUBSCRIPTION_COUNT;
import static com.zype.android.ui.Gallery.Model.GalleryRow.State.CREATED;
import static com.zype.android.ui.Gallery.Model.GalleryRow.State.LOADING;
import static com.zype.android.ui.Gallery.Model.GalleryRow.State.READY;
import static com.zype.android.ui.Gallery.Model.GalleryRow.State.UPDATED;

/**
 * Created by Evgeny Cherkasov on 12.06.2018.
 */
public class GalleryViewModel extends AndroidViewModel {
    private MediatorLiveData<List<GalleryRow>> data;
    private LiveData<List<Playlist>> liveDataPlaylists;
    private Map<String, LiveData<List<Video>>> liveDataVideos;
    private Map<String, LiveData<List<Playlist>>> liveDataNestedPlaylists;

    private Map<String, Boolean> playlistApiRequests = new HashMap<>();

    private String parentPlaylistId;

    private MutableLiveData<List<GalleryRow>> galleryRows;
    private GalleryRow.State galleryRowsState;

    DataRepository repo;
    ZypeApi api;
    WebApiManager oldApi;
    SharedPreferences.OnSharedPreferenceChangeListener listenerPreferences;

    public GalleryViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
        oldApi = WebApiManager.getInstance();
        oldApi.subscribe(this);
        listenerPreferences = (sharedPreferences, key) -> {
            // Update gallery rows when subscription count is changed
            if (key.equals(CONSUMER_SUBSCRIPTION_COUNT)) {
                if (galleryRows != null) {
                    galleryRows.setValue(galleryRows.getValue());
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(application)
                .registerOnSharedPreferenceChangeListener(listenerPreferences);
    }

    @Override
    protected void onCleared() {
        oldApi.unsubscribe(this);
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .unregisterOnSharedPreferenceChangeListener(listenerPreferences);
        super.onCleared();
    }

    public GalleryViewModel setPlaylistId(String parentPlaylistId) {
        this.parentPlaylistId = parentPlaylistId;
        return this;
    }

    public GalleryRow.State getGalleryRowsState() {
        return galleryRowsState;
    }

    public LiveData<List<GalleryRow>> getGalleryRows() {
        if (galleryRows == null) {
            galleryRows = new MutableLiveData<>();
        }
        if (galleryRowsState != UPDATED) {
            loadRootPlaylists();
        }
        else {
            galleryRows.setValue(galleryRows.getValue());
        }
        return galleryRows;
    }

    private void createGalleryRows() {
        List<GalleryRow> rows = new ArrayList<>();
        List<Playlist> rootPlaylists = repo.getPlaylistsSync(parentPlaylistId);
        for (Playlist playlist : rootPlaylists) {
            GalleryRow row = new GalleryRow(playlist);
            if (playlist.playlistItemCount > 0) {
                row.videos = repo.getPlaylistVideosSync(playlist.id);
                if (row.videos.isEmpty()) {
                    row.state = LOADING;
                }
                else {
                    row.state = READY;
                }
                loadRowVideos(row);
            } else {
                row.nestedPlaylists = repo.getPlaylistsSync(playlist.id);
                if (row.nestedPlaylists.isEmpty()) {
                    row.state = LOADING;
                }
                else {
                    row.state = READY;
                }
                loadRowPlaylists(row);
            }
            rows.add(row);
        }
        galleryRowsState = CREATED;
        updateGalleryRows(rows);
    }

    private void updateGalleryRows(List<GalleryRow> rows) {
        List<GalleryRow> newRows;
        if (rows != null) {
            newRows = rows;
        }
        else {
            newRows = galleryRows.getValue();
        }
        switch (galleryRowsState) {
            case CREATED:
                galleryRowsState = LOADING;
                Logger.d("updateGalleryRows(): state=LOADING");
                galleryRows.setValue(newRows);
                return;
            case LOADING:
                if (allRowsReady(newRows)) {
                    galleryRowsState = READY;
                    Logger.d("updateGalleryRows(): state=READY");
                    galleryRows.setValue(newRows);
                    return;
                }
            case READY:
                if (allRowsUpdated(newRows)) {
                    galleryRowsState = UPDATED;
                    Logger.d("updateGalleryRows(): state=UPDATED");
                    galleryRows.setValue(newRows);
                    return;
                }
            case UPDATED:
                return;
        }
    }

    private void loadRootPlaylists() {
        Logger.d("loadRootPlaylists(): parentPlaylistId=" + parentPlaylistId);
        final List<Playlist> result = new ArrayList<>();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ZypeApi.PER_PAGE, String.valueOf(100));
        final IZypeApiListener listener = new IZypeApiListener() {
            @Override
            public void onCompleted(ZypeApiResponse response) {
                PlaylistsResponse playlistsResponse = (PlaylistsResponse) response.data;
                if (response.isSuccessful) {
                    for (com.zype.android.zypeapi.model.PlaylistData item : playlistsResponse.response) {
                        Playlist playlist = repo.getPlaylistSync(item.id);
                        if (playlist != null) {
                            result.add(DbHelper.playlistUpdateEntityByApi(playlist, item));
                        }
                        else {
                            result.add(DbHelper.playlistApiToEntity(item));
                        }
                    }
                    if (playlistsResponse.pagination.current >= playlistsResponse.pagination.pages) {
                        repo.insertPlaylists(result);
                        createGalleryRows();
                        Logger.d("loadRootPlaylists(): size=" + result.size());
                    }
                    else {
                        api.getPlaylists(parentPlaylistId, playlistsResponse.pagination.next, parameters, this);
                        Logger.d("loadRootPlaylists(): page=" + playlistsResponse.pagination.next);
                    }
                }
                else {
                    // TODO: Handle api response error
                    if (playlistsResponse != null) {
//                        errorMessage.setValue(videosResponse.message);
                    }
                    else {
//                        errorMessage.setValue(getApplication().getString(R.string.videos_error));
                    }
                }
            }
        };
        api.getPlaylists(parentPlaylistId, 1, parameters, listener);
        Logger.d("loadRootPlaylists(): page=1");
    }

    private void loadRowPlaylists(GalleryRow row) {
        row.pageToLoad = 1;
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ZypeApi.PER_PAGE, String.valueOf(10));
        final IZypeApiListener listener = new IZypeApiListener() {
            @Override
            public void onCompleted(ZypeApiResponse response) {
                PlaylistsResponse playlistsResponse = (PlaylistsResponse) response.data;
                if (response.isSuccessful) {
                    List<Playlist> result = new ArrayList<>();
                    for (com.zype.android.zypeapi.model.PlaylistData item : playlistsResponse.response) {
                        Playlist playlist = repo.getPlaylistSync(item.id);
                        if (playlist != null) {
                            result.add(DbHelper.playlistUpdateEntityByApi(playlist, item));
                        }
                        else {
                            result.add(DbHelper.playlistApiToEntity(item));
                        }
                    }
                    repo.insertPlaylists(result);
                    row.nestedPlaylists = repo.getPlaylistsSync(row.playlist.id);
                    if (playlistsResponse.pagination.current >= playlistsResponse.pagination.pages) {
                        row.pageToLoad = -1;
                        row.state = UPDATED;
                    }
                    else {
                        row.pageToLoad = playlistsResponse.pagination.next;
                        row.state = READY;
                        api.getPlaylists(row.playlist.id, row.pageToLoad, parameters, this);
                        Logger.d("loadRowPlaylists(): page=" + playlistsResponse.pagination.next);
                    }
                    updateGalleryRows(null);
                }
                else {
                    // TODO: Handle api response error
                    if (playlistsResponse != null) {
//                        errorMessage.setValue(videosResponse.message);
                    }
                    else {
//                        errorMessage.setValue(getApplication().getString(R.string.videos_error));
                    }
                }
            }
        };
        api.getPlaylists(row.playlist.id, row.pageToLoad, parameters, listener);
        Logger.d("loadRowPlaylists(): page=" + row.pageToLoad);
    }

    private void loadRowVideos(GalleryRow row) {
        row.pageToLoad = 1;
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ZypeApi.PER_PAGE, String.valueOf(10));
        final IZypeApiListener listener = new IZypeApiListener() {
            @Override
            public void onCompleted(ZypeApiResponse response) {
                VideosResponse videosResponse = (VideosResponse) response.data;
                if (response.isSuccessful) {
                    List<Video> result = new ArrayList<>();
                    for (com.zype.android.zypeapi.model.VideoData item : videosResponse.videoData) {
                        Video video = repo.getVideoSync(item.id);
                        if (video != null) {
                            result.add(DbHelper.videoUpdateEntityByApi(video, item));
                        }
                        else {
                            result.add(DbHelper.videoApiToEntity(item));
                        }
                    }
                    repo.insertVideos(result);
                    if (videosResponse.pagination.current == 1) {
                        repo.deletePlaylistVideos(row.playlist.id);
                    }
                    repo.insertPlaylistVideos(result, row.playlist);
                    row.videos = repo.getPlaylistVideosSync(row.playlist.id);
                    if (videosResponse.pagination.current >= videosResponse.pagination.pages) {
                        row.pageToLoad = -1;
                        row.state = UPDATED;
                    }
                    else {
                        row.pageToLoad = videosResponse.pagination.next;
                        row.state = READY;
                        api.getPlaylistVideos(row.playlist.id, row.pageToLoad, parameters, this);
                        Logger.d("loadRowVideos(): page=" + videosResponse.pagination.next);
                    }
                    updateGalleryRows(null);
                }
                else {
                    // TODO: Handle api response error
                    if (videosResponse != null) {
//                        errorMessage.setValue(videosResponse.message);
                    }
                    else {
//                        errorMessage.setValue(getApplication().getString(R.string.videos_error));
                    }
                }
            }
        };
        api.getPlaylistVideos(row.playlist.id, row.pageToLoad, parameters, listener);
        Logger.d("loadRowVideos(): page=" + row.pageToLoad);
    }

    public boolean allRowsReady(List<GalleryRow> rows) {
        if (rows == null || rows.isEmpty()) {
            Logger.d("allDataReady(): rows empty");
            return false;
        }

        for (GalleryRow row : rows) {
            if (row.state == LOADING) {
                Logger.d("allDataReady(): false, playlist " + row.playlist.title + " is still loading");
                return false;
            }
        }
        Logger.d("allDataReady(): true");
        return true;
    }

    public boolean allRowsUpdated(List<GalleryRow> rows) {
        if (rows == null || rows.isEmpty()) {
            Logger.d("allDataUpdated(): rows empty");
            return false;
        }

        for (GalleryRow row : rows) {
            if (row.state == LOADING) {
                Logger.d("allDataUpdated(): false, playlist " + row.playlist.title + " is still loading");
                return false;
            }
            else if (row.state == READY) {
                Logger.d("allDataUpdated(): false, playlist " + row.playlist.title + " is not updated");
                return false;
            }
        }
        Logger.d("allDataUpdated(): true");
        return true;
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
                                        Logger.d("onChanged(): Videos (" + playlist.title + "), size=" + videos.size());
                                        row.videos = videos;
                                        if (allDataLoaded(galleryRows)) {
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
                                        Logger.d("onChanged(): Nested playlists (" + playlist.title + "), size=" + playlists.size());
                                        if (playlists.isEmpty()) {
                                            if (playlistApiRequests.containsKey(playlist.id)) {
                                                if (playlistApiRequests.get(playlist.id)) {
                                                    Logger.d("onChanged(): Nested playlists (" + playlist.title + "), wait for API response");
                                                }
                                                else {
                                                    Logger.d("onChanged(): Nested playlists (" + playlist.title + "), row removed");
                                                    galleryRows.remove(row);
                                                }
                                            }
                                            else {
                                                Logger.d("onChanged(): Nested playlists (" + playlist.title + "), row removed");
                                                galleryRows.remove(row);
                                            }
                                        }
                                        else {
                                            row.nestedPlaylists = playlists;
                                            playlistApiRequests.remove(playlist.id);
                                        }
                                        if (allDataLoaded(galleryRows)) {
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

    private boolean allDataLoaded(List<GalleryRow> rows) {
        boolean result = true;

        List<GalleryRow> rowsToDelete = new ArrayList<>();

        if (data.getValue() == null || data.getValue().isEmpty()) {
            result = false;
        }
        else {
            for (GalleryRow item : rows) {
                boolean videosEmpty = item.videos == null || item.videos.isEmpty();
                if (videosEmpty) {
                    boolean nestedPlaylistsEmpty = item.nestedPlaylists == null || item.nestedPlaylists.isEmpty();
                    if (nestedPlaylistsEmpty) {
                        if (playlistApiRequests.containsKey(item.playlist.id)) {
                            if (playlistApiRequests.get(item.playlist.id)) {
                                result = false;
                                break;
                            }
                            else {
                                rowsToDelete.add(item);
                            }
                        }
                        else {
                             result = false;
                             break;
                        }
                    }
                }
            }
        }
        Logger.d("allDataLoaded(): " + result);
        if (result) {
            for (GalleryRow item : rowsToDelete) {
                Logger.d("allDataLoaded(): (" + item.playlist.title + "), row removed");
                rows.remove(item);
            }
        }
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
//        if (result.getValue() == null || result.getValue().isEmpty() || ZypeApp.needToLoadData) {
//            loadPlaylists(parentPlaylistId);
//        }
        return result;
    }

//    /**
//     * Make API request for playlists with specified id
//     *
//     * @param parentPlaylistId Parent playlist id
//     */
//    private void loadPlaylists(String parentPlaylistId) {
//        boolean playlistRequested = playlistApiRequests.containsKey(parentPlaylistId);
//        if (playlistRequested) {
//            return;
//        }
//        else {
//            playlistApiRequests.put(parentPlaylistId, true);
//        }
//
//        Logger.d("loadPlaylists(): parentPlaylistId=" + parentPlaylistId);
//        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
//                .addParentId(parentPlaylistId)
//                .addPerPage(100);
//        oldApi.executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
//    }

//    /**
//     * Handles API request for playlists
//     *
//     * @param event Response event
//     */
//    @Subscribe
//    public void handleRetrievePlaylist(PlaylistEvent event) {
//        String parentId = event.parentId;
//        if (TextUtils.isEmpty(parentId)) {
//            Logger.d("handleRetrievePlaylist(): Not handled, empty 'parentId'");
//            return;
//        }
//
//        Logger.d("handleRetrievePlaylist(): parentId=" + parentId + ", size=" + event.getEventData().getModelData().getResponse().size());
//
//        playlistApiRequests.put(parentId, false);
//
//        List<PlaylistData> playlists = event.getEventData().getModelData().getResponse();
//        if (!playlists.isEmpty()) {
//            repo.insertPlaylists(DbHelper.playlistDataToEntity(playlists));
//
//            for (PlaylistData playlistData : playlists) {
//                if (!TextUtils.isEmpty(playlistData.getParentId())
//                        && playlistData.getParentId().equals(parentPlaylistId)) {
//                    // Load playlist videos
//                    if (playlistData.getPlaylistItemCount() > 0) {
//                        loadPlaylistVideos(playlistData.getId(), 1);
//                    }
//                    // Load child playlists
//                    else {
////                        loadPlaylists(playlistData.getId());
//                    }
//                }
//            }
//        }
//    }
//

    // Playlist videos

    private LiveData<List<Video>> getPlaylistVideos(String playlistId) {
        // Get videos for specified 'playlistId'
        return DataRepository.getInstance(getApplication()).getPlaylistVideos(playlistId);
    }

//    private void loadPlaylistVideos(String playlistId, int page) {
//        Logger.d("loadPlaylistVideos(): playlistId=" + playlistId + ", page=" + page);
//
//        VideoParamsBuilder builder = new VideoParamsBuilder();
//        builder.addPlaylistId(playlistId);
//        builder.addPage(page);
//        builder.addPerPage(100);
//        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO_FROM_PLAYLIST, builder.build());
//    }
//
//    @Subscribe
//    public void handleRetrieveVideo(VideoListEvent event) {
//        List<VideoData> videoData = event.getEventData().getModelData().getVideoData();
//        if (videoData != null) {
//            Logger.d("handleRetrieveVideo(): size=" + videoData.size());
//            if (videoData.size() > 0) {
//                List<Video> videos = new ArrayList<>(videoData.size());
//                for (VideoData item : videoData) {
//                    Video video = repo.getVideoSync(item.getId());
//                    if (video != null) {
//                        video = DbHelper.updateVideoEntityByVideoData(video, item);
//                    }
//                    else {
//                        video = DbHelper.videoDataToVideoEntity(item);
//                    }
//                    videos.add(video);
//                }
//                repo.insertVideos(videos);
//                repo.deletePlaylistVideos(event.getPlaylistId());
//                repo.insertPlaylistVideos(DbHelper.videoDataToPlaylistVideoEntity(videoData, event.getPlaylistId()));
//            }
//        }
//    }


}

