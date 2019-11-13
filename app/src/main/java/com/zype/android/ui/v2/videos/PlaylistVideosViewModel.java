package com.zype.android.ui.v2.videos;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideosResponse;

import java.util.ArrayList;
import java.util.List;

import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_FAVORITE;
import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_UNFAVORITE;

/**
 * Created by Evgeny Cherkasov on 11.02.2019.
 */
public class PlaylistVideosViewModel extends AndroidViewModel {
    DataRepository repo;
    ZypeApi api;

    LiveData<List<Video>> videos;
    MutableLiveData<String> errorMessage;

    public PlaylistVideosViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<Video>> getVideos(String playlistId) {
        if (videos == null) {
            videos = repo.getPlaylistVideos(playlistId);
        }
        loadVideos(playlistId);
        return videos;
    }

    public LiveData<String> getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new MutableLiveData<>();
        }
        return errorMessage;
    }

    public LiveData<String> onError() {
        if (errorMessage == null) {
            errorMessage = new MutableLiveData<>();
        }
        return errorMessage;
    }

    private void loadVideos(final String playlistId) {
        final List<Video> result = new ArrayList<>();
        final IZypeApiListener listener = new IZypeApiListener() {
            @Override
            public void onCompleted(ZypeApiResponse response) {
                VideosResponse videosResponse = (VideosResponse) response.data;
                if (response.isSuccessful) {
                    for (com.zype.android.zypeapi.model.VideoData item : videosResponse.videoData) {
                        Video video = repo.getVideoSync(item.id);
                        if (video != null) {
                            result.add(DbHelper.videoUpdateEntityByApi(video, item));
                        }
                        else {
                            result.add(DbHelper.videoApiToEntity(item));
                        }
                    }
                    if (videosResponse.pagination.current == videosResponse.pagination.pages) {
                        repo.insertVideos(result);
                        repo.deletePlaylistVideos(playlistId);
                        repo.insertPlaylistVideos(DbHelper.videosToPlaylistVideos(result, playlistId));
                    }
                    else {
                        api.getPlaylistVideos(playlistId, videosResponse.pagination.next, this);
                    }
                }
                else {
                    if (videosResponse != null) {
                        errorMessage.setValue(videosResponse.message);
                    }
                    else {
                        errorMessage.setValue(getApplication().getString(R.string.videos_error));
                    }
                }
            }
        };
        api.getPlaylistVideos(playlistId, 1, listener);
    }

    public void handleVideoAction(int action, Video video, VideoActionsHelper.IVideoActionCallback listener) {
        switch (action) {
            case ACTION_FAVORITE:
                VideoActionsHelper.onFavorite(video, getApplication(), listener);
                break;
            case ACTION_UNFAVORITE:
                VideoActionsHelper.onUnfavorite(video, getApplication(), listener);
                break;
        }
    }

}
