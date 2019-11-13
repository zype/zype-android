package com.zype.android.ui.v2.favorites;

import android.app.Application;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;
import com.zype.android.ui.v2.videos.VideosViewModel;

import java.util.List;


public class FavoriteVideosViewModel extends VideosViewModel {

    public FavoriteVideosViewModel(Application application) {
        super(application);
    }

    protected void retrieveVideos(boolean forceLoad) {
        if (forceLoad && AuthHelper.isLoggedIn()) {
            repo.loadVideoFavorites(success -> {
                updateFavoriteVideos();
            });
        }
        else {
            updateFavoriteVideos();
        }
    }

    private void updateFavoriteVideos() {
        List<Video> result = repo.getFavoriteVideosSync();
        if (result == null || result.isEmpty()) {
            updateVideos(new StatefulData<>(null, null, DataState.READY));
        }
        else {
            updateVideos(new StatefulData<>(result, null, DataState.READY));
        }
    }
}
