package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class EntitlementParamsBuilder extends ParamsBuilder {

    private static final String ACCESS_TOKEN = "access_token";
    public static final String VIDEO_ID = "video_id";

    public EntitlementParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public EntitlementParamsBuilder addVideoId(String videoId) {
        addPathParam(VIDEO_ID, videoId);
        return this;
    }

}
