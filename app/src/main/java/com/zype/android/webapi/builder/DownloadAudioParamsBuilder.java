package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;

public class DownloadAudioParamsBuilder extends ParamsBuilder {

public static final String VIDEO_ID = "video_id";
    public static final String ACCESS_TOKEN = "access_token";

    public DownloadAudioParamsBuilder() {
        addGetParam("audio", String.valueOf(true));
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
    }

    public DownloadAudioParamsBuilder addAudioId(String videoId) {
        addPathParam(VIDEO_ID, videoId);
        return this;
    }
}
