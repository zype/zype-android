package com.zype.android.ui.v2.videos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;

import java.util.List;

import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_FAVORITE;
import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_UNFAVORITE;

public abstract class VideosViewModel extends BaseViewModel {

    private MutableLiveData<StatefulData<List<Video>>> videos;

    public VideosViewModel(Application application) {
        super(application);
        videos = new MutableLiveData<>();
        videos.setValue(new StatefulData<>(null, null, DataState.READY));
    }

    public LiveData<StatefulData<List<Video>>> getVideos() {
        retrieveVideos(true);
        return videos;
    }

    protected void updateVideos(StatefulData<List<Video>> videos) {
        this.videos.setValue(videos);
    }

    protected abstract void retrieveVideos(boolean forceLoad);

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
