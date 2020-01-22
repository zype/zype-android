package com.zype.android.ui.v2.library;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.util.Log;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Auth.AuthLiveData;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;
import com.zype.android.ui.v2.videos.VideoActionsHelper;
import com.zype.android.ui.v2.videos.VideosViewModel;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideosResponse;

import java.util.ArrayList;
import java.util.List;

import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_FAVORITE;
import static com.zype.android.ui.v2.videos.VideoActionsHelper.ACTION_UNFAVORITE;

/**
 * Created by Evgeny Cherkasov on 11.01.2020.
 */
public class LibraryViewModel extends VideosViewModel {
    private static final String TAG = LibraryViewModel.class.getSimpleName();

    Observer<Boolean> observerLoggedIn;

    public LibraryViewModel(Application application) {
        super(application);
        observerLoggedIn = isLoggedIn -> {
            Log.d(TAG, "observerLoggedIn: " + isLoggedIn);
            retrieveVideos(isLoggedIn);
        };
        AuthHelper.onLoggedIn(observerLoggedIn);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AuthLiveData.getInstance().removeObserver(observerLoggedIn);
    }

    protected void retrieveVideos(boolean forceLoad) {
        if (AuthHelper.isLoggedIn()) {
            if (forceLoad) {
                repo.loadVideoEntitlements(success -> {
                    if (success) {
                        getEntitledVideos();
                    }
                    else {
                        updateVideos(new StatefulData<>(null, getApplication().getString(R.string.videos_error), DataState.ERROR));
                    }
                });
            }
            else {
                getEntitledVideos();
            }
        }
        else {
            updateVideos(new StatefulData<>(null, null, DataState.READY));
        }
    }

    private void getEntitledVideos() {
        List<Video> result = repo.getEntitledVideosSync();
        if (result == null || result.isEmpty()) {
            updateVideos(new StatefulData<>(null, null, DataState.READY));
        }
        else {
            updateVideos(new StatefulData<>(result, null, DataState.READY));
        }
    }
}
