package com.zype.android.ui.Gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.utils.Logger;

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

    public GalleryViewModel(Application application) {
        super(application);
    }

    public LiveData<List<GalleryRow>> getGalleryRows(String parentPlaylistId) {
        if (data == null) {
            data = new MediatorLiveData<>();
            liveDataPlaylists = loadPlaylists(parentPlaylistId);
            liveDataVideos = new HashMap<>();
            liveDataNestedPlaylists = new HashMap<>();
            data.addSource(liveDataPlaylists, new Observer<List<Playlist>>() {
                @Override
                public void onChanged(@Nullable List<Playlist> playlists) {
                    Logger.d("onChanged(): Playlists, size=" + playlists.size());

                    final List<GalleryRow> galleryRows = new ArrayList<>();

                    for (final Playlist playlist : playlists) {
                        final GalleryRow row = new GalleryRow(playlist);

                        // Add video items to the row if playlist contains any video
                        if (playlist.playlistItemCount > 0) {
                            LiveData<List<Video>> playlistVideos = liveDataVideos.get(playlist.id);
                            if (playlistVideos == null) {
                                playlistVideos = loadPlaylistVideos(playlist.id);
                                liveDataVideos.put(playlist.id, playlistVideos);
                                data.addSource(playlistVideos, new Observer<List<Video>>() {
                                    @Override
                                    public void onChanged(@Nullable List<Video> videos) {
                                        Logger.d("onChanged(): Videos, size=" + videos.size());
                                        row.videos = videos;
                                        data.setValue(galleryRows);
                                    }
                                });
                            }
                        }
                        // Otherwise add nested playlists
                        else {
                            LiveData<List<Playlist>> nestedPlaylists = liveDataNestedPlaylists.get(playlist.id);
                            if (nestedPlaylists == null) {
                                nestedPlaylists = loadPlaylists(playlist.id);
                                liveDataNestedPlaylists.put(playlist.id, nestedPlaylists);
                                data.addSource(nestedPlaylists, new Observer<List<Playlist>>() {
                                    @Override
                                    public void onChanged(@Nullable List<Playlist> playlists) {
                                        Logger.d("onChanged(): Nested playlists, size=" + playlists.size());
                                        List<Playlist> nestedPlaylists = DataRepository.getInstance(getApplication()).getPlaylistsSync(playlist.id);
                                        row.nestedPlaylists = playlists;
                                        data.setValue(galleryRows);
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

    private LiveData<List<Playlist>> loadPlaylists(String parentPlaylistId) {
        // Get playlists with specified 'parentId'
        return DataRepository.getInstance(getApplication()).getPlaylists(parentPlaylistId);
    }

    private LiveData<List<Video>> loadPlaylistVideos(String playlistId) {
        // Get videos for specified 'playlistId'
        return DataRepository.getInstance(getApplication()).getPlaylistVideos(playlistId);
    }

}

