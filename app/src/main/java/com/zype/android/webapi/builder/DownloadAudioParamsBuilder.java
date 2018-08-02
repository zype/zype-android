package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

public class DownloadAudioParamsBuilder extends ParamsBuilder {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String DOWNLOAD = "download";
    public static final String VIDEO_ID = "video_id";

    public DownloadAudioParamsBuilder() {
        addGetParam(DOWNLOAD, String.valueOf(true));
    }

    public DownloadAudioParamsBuilder addAudioId(String videoId) {
        addPathParam(VIDEO_ID, videoId);
        return this;
    }

    public DownloadAudioParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public DownloadAudioParamsBuilder addAppKey() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
        return this;
    }

}
