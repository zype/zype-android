package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

public class DownloadVideoParamsBuilder extends ParamsBuilder {

    public static final String VIDEO_ID = "video_id";
    public static final String ACCESS_TOKEN = "access_token";

    public DownloadVideoParamsBuilder() {
        addGetParam("download", String.valueOf(true));
//        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
    }

    public DownloadVideoParamsBuilder addVideoId(String videoId) {
        addPathParam(VIDEO_ID, videoId);
        return this;
    }

    public DownloadVideoParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public DownloadVideoParamsBuilder addAppKey() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
        return this;
    }

//    public DownloadVideoParamsBuilder addResultReceiver(ResultReceiver resultReceiver) {
//        addReceiver(resultReceiver);
//        return this;
//    }
}
