package com.zype.android.ui.Gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    WebApiManager api;

    public HeroImagesViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = WebApiManager.getInstance();
        api.subscribe(this);
    }

    @Override
    protected void onCleared() {
        api.unsubscribe(this);
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
                if (currentPage.getValue() == data.getValue().size() - 1) {
                    currentPage.postValue(0);
                }
                else {
                    currentPage.postValue(currentPage.getValue() + 1);
                }
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
        ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
                .addType(ZObjectParamsBuilder.TYPE_TOP_PLAYLISTS);
        api.executeRequest(WebApiManager.Request.Z_OBJECT, builder.build());
    }

    @Subscribe
    public void handleZObject(ZObjectEvent event) {
        Logger.d("handleZObject()");
        List<ZobjectData> zobjectData = event.getEventData().getModelData().getResponse();
        List<HeroImage> heroImages = new ArrayList<>();
        for (ZobjectData item : zobjectData) {
            HeroImage heroImage = new HeroImage();
            heroImage.playlistId = item.playlistId;
            if (item.getPictures() != null && item.getPictures().size() > 0) {
                heroImage.imageUrl = item.getPictures().get(0).getUrl();
            }
            heroImages.add(heroImage);

            loadPlaylist(item.playlistId);
        }
        data.setValue(heroImages);
    }

    /**
     * Make API request for playlist with specified id
     *
     * @param playlistId Playlist id to load
     */
    private void loadPlaylist(String playlistId) {
        Logger.d("loadPlaylists): playlistId=" + playlistId);
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addPlaylistId(playlistId)
                .addPerPage(1);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
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

}
