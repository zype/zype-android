package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class PlayerParamsBuilder extends ParamsBuilder {

    public static final String AUDIO = "audio";
    public static final String VIDEO_ID = "video_id";
    private static final String ACCESS_TOKEN = "access_token";


    public PlayerParamsBuilder() {
    }

    public PlayerParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public PlayerParamsBuilder addAppKey() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
        return this;
    }

    public PlayerParamsBuilder addAudio() {
        addGetParam(AUDIO, String.valueOf(true));
        return this;
    }

    public PlayerParamsBuilder addVideoId(String videoId) {
        addGetParam(VIDEO_ID, videoId);
        return this;
    }

}
