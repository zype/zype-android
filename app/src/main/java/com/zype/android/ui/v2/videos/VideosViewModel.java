package com.zype.android.ui.v2.videos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;

import java.util.List;

import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_FAVORITE;
import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_UNFAVORITE;

/**
 * Created by Evgeny Cherkasov
 */
public abstract class VideosViewModel extends BaseViewModel {

    private MutableLiveData<StatefulData<List<Video>>> videos;
    protected String playlistId;
    private MutableLiveData<Video> selectedVideo = new MutableLiveData<>();

    public VideosViewModel(Application application) {
        super(application);
        videos = new MutableLiveData<>();
        videos.setValue(new StatefulData<>(null, null, DataState.READY));
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public LiveData<StatefulData<List<Video>>> getVideos() {
        videos.setValue(new StatefulData<>(null, null, DataState.LOADING));
        retrieveVideos(true);
        return videos;
    }

    public LiveData<Video> getSelectedVideo() {
        return selectedVideo;
    }

    // Actions

    public void onVideoClicked(Video video) {
        if (TextUtils.isEmpty(playlistId)) {
            repo.loadVideoPlaylistIds(video.id, response -> {
                selectedVideo.setValue(repo.getVideoSync(video.id));
            });
        }
        else {
            selectedVideo.setValue(video);
        }
    }

    public void onSelectedVideoProcessed() {
        selectedVideo.setValue(null);
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

    //

    protected void updateVideos(StatefulData<List<Video>> videos) {
        this.videos.setValue(videos);
    }

    protected abstract void retrieveVideos(boolean forceLoad);

}
