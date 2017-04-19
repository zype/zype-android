package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class PlayerParamsBuilder extends ParamsBuilder {

    public static final String VIDEO_ID = "video_id";
    private static final String ACCESS_TOKEN = "access_token";


    public PlayerParamsBuilder() {
//        addAccessToken();
//        addGetParam(API_KEY, WebApiManager.API_KEY);
//        addGetParam(DEVICE_ID, WebApiManager.DEVICE_ID);
    }

//    public PlayerParamsBuilder(boolean isLive) {
//        if (isLive) {
//            addGetParam(API_KEY, WebApiManager.API_LIVE_KEY);
//        } else {
//            addGetParam(API_KEY, WebApiManager.API_KEY);
//        }
//    }

    public PlayerParamsBuilder addVideoId(String videoId) {
        addGetParam(VIDEO_ID, videoId);
        return this;
    }

    public PlayerParamsBuilder addAudio() {
        addGetParam("audio", String.valueOf(true));
        return this;
    }

    public PlayerParamsBuilder addTest() {
//        addGetParam("api_key", "pDzPC_0UsN2H-I0jgJneEQ");
        return this;
    }

//    public void addLiveKey() {
//        addGetParam(API_KEY, WebApiManager.API_LIVE_KEY);
//    }

    public PlayerParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public PlayerParamsBuilder addAppKey() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
        return this;
    }
}
