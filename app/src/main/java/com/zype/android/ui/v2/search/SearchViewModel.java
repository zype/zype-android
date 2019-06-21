package com.zype.android.ui.v2.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideosResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 21.05.2019.
 */
public class SearchViewModel  extends BaseViewModel {
    private MutableLiveData<StatefulData<List<Video>>> videos;

    public SearchViewModel(Application application) {
        super(application);
    }

    public LiveData<StatefulData<List<Video>>> getVideos() {
        if (videos == null) {
            videos = new MutableLiveData<>();
            videos.setValue(new StatefulData<>(null, null, DataState.READY));
        }
        return videos;
    }

    public void search(String query, String playlistId) {
        if (videos == null) {
            videos = new MutableLiveData<>();
        }
        videos.setValue(new StatefulData<>(null, null, DataState.LOADING));
        loadSearchResult(query, playlistId);
    }

    public void clearSearchResults() {
        if (videos == null) {
            videos = new MutableLiveData<>();
        }
        videos.setValue(new StatefulData<>(null, null, DataState.READY));
    }

    private void loadSearchResult(String query, String playlistId) {
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
                    if (videosResponse.pagination.current == videosResponse.pagination.pages
                        || videosResponse.pagination.pages == 0) {
                        repo.insertVideos(result);
                        videos.setValue(new StatefulData<>(result, null, DataState.READY));
                    }
                    else {
                        api.searchVideos(query, playlistId, videosResponse.pagination.next, this);
                    }
                }
                else {
                    if (videosResponse != null) {
                        videos.setValue(new StatefulData<>(null, videosResponse.message, DataState.ERROR));
                    }
                    else {
                        videos.setValue(new StatefulData<>(null, getApplication().getString(R.string.videos_error), DataState.ERROR));
                    }
                }
            }
        };
        api.searchVideos(query, playlistId, 1, listener);
    }
}
