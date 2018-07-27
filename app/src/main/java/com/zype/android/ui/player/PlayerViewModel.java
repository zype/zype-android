package com.zype.android.ui.player;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ParamsBuilder;
import com.zype.android.webapi.builder.PlayerParamsBuilder;
import com.zype.android.webapi.events.player.PlayerAudioEvent;
import com.zype.android.webapi.model.player.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 23.07.2018
 */
public class PlayerViewModel extends AndroidViewModel {

    private MutableLiveData<List<PlayerMode>> availablePlayerModes;
    private MutableLiveData<PlayerMode> playerMode;
    private MutableLiveData<String> playerUrl;

    public enum PlayerMode {
        AUDIO,
        VIDEO
    }

    DataRepository repo;
    WebApiManager api;

    public PlayerViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = WebApiManager.getInstance();
        api.subscribe(this);

        availablePlayerModes = new MutableLiveData<>();
        availablePlayerModes.setValue(new ArrayList<PlayerMode>());
        playerMode = new MutableLiveData<>();
        playerMode.setValue(PlayerMode.VIDEO);
    }

    @Override
    protected void onCleared() {
        api.unsubscribe(this);
        super.onCleared();
    }

    public MutableLiveData<String> getPlayerUrl(String videoId) {
        if (playerUrl == null) {
            playerUrl = new MutableLiveData<>();
        }
        Video video = repo.getVideoSync(videoId);
        if (TextUtils.isEmpty(video.playerAudioUrl)) {
            loadAudioPlayerUrl(videoId);
        }
        if (TextUtils.isEmpty(video.playerVideoUrl)) {
//                    loadVideoPlayerUrl(videoId);
        }
        switch (playerMode.getValue()) {
            case AUDIO:
                playerUrl.setValue(video.playerAudioUrl);
                break;
            case VIDEO:
                playerUrl.setValue(video.playerVideoUrl);
                break;
        }
        return playerUrl;
    }

    public MutableLiveData<PlayerMode> getPlayerMode() {
        return playerMode;
    }

    public void setPlayerMode(PlayerMode mode) {
        playerMode.setValue(mode);
    }

    public MutableLiveData<List<PlayerMode>> getAvailablePlayerModes() {
        return availablePlayerModes;
    }

    public List<PlayerMode> getAvailableModes(String videoId) {
        List<PlayerMode> result = new ArrayList<>();

        Video video = repo.getVideoSync(videoId);
        if (video != null) {
            if (!TextUtils.isEmpty(video.playerAudioUrl)) {
                result.add(PlayerMode.AUDIO);
            }
            if (!TextUtils.isEmpty(video.playerVideoUrl)) {
                result.add(PlayerMode.VIDEO);
            }
        }
        return result;
    }

    /**
     * Make API request for audio player
     *
     * @param videoId Video id
     */
    private void loadAudioPlayerUrl(String videoId) {
        Logger.d("loadAudioPlayerUrl(): videoId=" + videoId);
        PlayerParamsBuilder builder = new PlayerParamsBuilder();
        if (SettingsProvider.getInstance().isLoggedIn()) {
            builder.addAccessToken();
        }
        else {
            builder.addAppKey();
        }
        builder.addVideoId(videoId);
        builder.addAudio();
        api.executeRequest(WebApiManager.Request.PLAYER_AUDIO, builder.build());
    }

    /**
     * Handles API request for audio player
     *
     * @param event Response event
     */
    @Subscribe
    public void handleAudioPlayerUrl(PlayerAudioEvent event) {
        Logger.d("handleAudioPlayerUrl()");

        Bundle requestOptions = event.getOptions();
        HashMap<String, String> pathParams = (HashMap<String, String>) requestOptions.getSerializable(ParamsBuilder.GET_PARAMS);
        String videoId = pathParams.get(PlayerParamsBuilder.VIDEO_ID);

        List<File> files = event.getEventData().getModelData().getResponse().getBody().getFiles();
        if (files == null || files.isEmpty()) {
            Logger.w("handleAudioPlayerUrl(): Audio source not found");
            return;
        }

        // Take first source in the list
        String url = files.get(0).getUrl();

        // Save audio url in the local database if it was changed
        Video video = repo.getVideoSync(videoId);
        if (video.playerAudioUrl == null || !video.playerAudioUrl.equals(url)) {
            video.playerAudioUrl = url;
            repo.updateVideo(video);

            if (playerMode.getValue() == PlayerMode.AUDIO) {
                playerUrl.setValue(url);
            }
            availablePlayerModes.setValue(getAvailableModes(videoId));
        }
    }


}
