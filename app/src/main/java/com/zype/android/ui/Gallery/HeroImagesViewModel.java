package com.zype.android.ui.Gallery;

import android.app.Application;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.video.VideoEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.model.PlaylistsResponse;
import com.zype.android.zypeapi.model.ZObjectTopPlaylist;
import com.zype.android.zypeapi.model.ZObjectTopPlaylistResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImagesViewModel extends AndroidViewModel {
    private MutableLiveData<List<HeroImage>> data;
    private MutableLiveData<Integer> currentPage;
    private Timer timer;
    private TimerTask timerTask;
    private long TIMER_PERIOD = 7000;

    DataRepository repo;
    ZypeApi api;
    WebApiManager oldApi;

    public HeroImagesViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
        oldApi = WebApiManager.getInstance();
        oldApi.subscribe(this);
    }

    @Override
    protected void onCleared() {
        oldApi.unsubscribe(this);
        super.onCleared();
    }

    public MutableLiveData<List<HeroImage>> getHeroImages() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadHeroImages();
        }
        return data;
    }

    public LiveData<Integer> startTimer(int startPage) {
        currentPage = new MutableLiveData<>();
        currentPage.setValue(startPage);

        if (timer == null) {
            timer = new Timer();
        }
        else {
            timer.cancel();
            timer.purge();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
//                if (currentPage.getValue() == data.getValue().size() - 1) {
//                    currentPage.postValue(0);
//                }
//                else {
                    currentPage.postValue(currentPage.getValue() + 1);
//                }
            }
        };
        timer.schedule(timerTask, 0, TIMER_PERIOD);

        return currentPage;
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void loadHeroImages() {
        api.getZObjectTopPlayLists(response -> {
            if (response.isSuccessful) {
                List<HeroImage> heroImages = new ArrayList<>();
                List<ZObjectTopPlaylist> topPlaylists = response.data.topPlaylists;
                Collections.sort(topPlaylists, (o1, o2) -> o1.priority.compareTo(o2.priority));
                for (ZObjectTopPlaylist item : topPlaylists) {
                    HeroImage heroImage = new HeroImage();
                    heroImage.playlistId = item.playlistId;
                    heroImage.videoId = item.videoId;
                    if (item.images != null && item.images.size() > 0) {
                        heroImage.imageUrl = item.images.get(0).url;
                    }
                    heroImages.add(heroImage);
                    if (!TextUtils.isEmpty(item.playlistId)) {
                        loadPlaylist(item.playlistId);
                    }
                    loadVideo(item.videoId);
                }
                data.setValue(heroImages);
            }
        });
//        ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
//                .addType(ZObjectParamsBuilder.TYPE_TOP_PLAYLISTS);
//        oldApi.executeRequest(WebApiManager.Request.Z_OBJECT, builder.build());
    }

//    @Subscribe
//    public void handleZObject(ZObjectEvent event) {
//        Logger.d("handleZObject()");
//        List<ZobjectData> zobjectData = event.getEventData().getModelData().getResponse();
//        List<HeroImage> heroImages = new ArrayList<>();
//        for (ZobjectData item : zobjectData) {
//            HeroImage heroImage = new HeroImage();
//            heroImage.playlistId = item.playlistId;
//            heroImage.videoId = item.videoId;
//            if (item.getPictures() != null && item.getPictures().size() > 0) {
//                heroImage.imageUrl = item.getPictures().get(0).getUrl();
//            }
//            heroImages.add(heroImage);
//            if (!TextUtils.isEmpty(item.playlistId)) {
//                loadPlaylist(item.playlistId);
//            }
//            loadVideo(item.videoId);
//        }
//        data.setValue(heroImages);
//    }

    /**
     * Make API request for playlist with specified id
     *
     * @param playlistId Playlist id to load
     */
    private void loadPlaylist(String playlistId) {
        Logger.d("loadPlaylists(): playlistId=" + playlistId);
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addPlaylistId(playlistId)
                .addPerPage(1);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
    }

    /**
     * Make API request for video with specified id
     *
     * @param videoId Video id to load
     */
    private void loadVideo(String videoId) {
        Logger.d("loadVideo(): videoId=" + videoId);
        VideoParamsBuilder builder = new VideoParamsBuilder()
                .addVideoId(videoId)
                .addPerPage(1);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO, builder.build());
    }

    /**
     * Handles API request for playlists
     *
     * @param event Response event
     */
    @Subscribe
    public void handlePlaylists(PlaylistEvent event) {
        Logger.d("handlePlaylists size=" + event.getEventData().getModelData().getResponse().size());
        List<PlaylistData> playlists = event.getEventData().getModelData().getResponse();
        if (playlists.size() > 0) {
            repo.insertPlaylists(DbHelper.playlistDataToEntity(playlists));
        }
    }

    /**
     * Handles API request for playlist
     *
     * @param event Response event
     */
    @Subscribe
    public void handleVideo(VideoEvent event) {
        Logger.d("handleVideo()");
        VideoData data = event.getEventData().getModelData().getVideoData();
        Video video = DbHelper.videoDataToVideoEntity(data);

        if (video != null) {
            List<Video> videosList = Arrays.asList(video);
            repo.insertVideos(videosList);
        }
    }

}
