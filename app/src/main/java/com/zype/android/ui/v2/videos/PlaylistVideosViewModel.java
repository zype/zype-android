package com.zype.android.ui.v2.videos;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;
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
public class PlaylistVideosViewModel extends VideosViewModel {

    public PlaylistVideosViewModel(Application application) {
        super(application);
    }

    protected void retrieveVideos(boolean forceLoad) {
        updateVideos(new StatefulData<>(repo.getPlaylistVideosSync(playlistId), null, DataState.READY));
        if (forceLoad) {
            loadVideos(playlistId);
        }
    }

    private void loadVideos(final String playlistId) {
        updateVideos(new StatefulData<>(null, null, DataState.LOADING));
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
                        updateVideos(new StatefulData<>(repo.getPlaylistVideosSync(playlistId), null, DataState.READY));
                    }
                    else {
                        api.getPlaylistVideos(playlistId, videosResponse.pagination.next, this);
                    }
                }
                else {
                    if (videosResponse != null) {
                        updateVideos(new StatefulData<>(null, videosResponse.message, DataState.ERROR));
//                        errorMessage.setValue(videosResponse.message);
                    }
                    else {
                        updateVideos(new StatefulData<>(null, getApplication().getString(R.string.videos_error), DataState.ERROR));
//                        errorMessage.setValue(getApplication().getString(R.string.videos_error));
                    }
                }
            }
        };
        api.getPlaylistVideos(playlistId, 1, listener);
    }

}
