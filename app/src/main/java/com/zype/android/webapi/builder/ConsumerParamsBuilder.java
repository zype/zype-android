package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class ConsumerParamsBuilder extends ParamsBuilder {

    public static final String VIDEO_ID = "video_id";
    //    public static final String CONSUMER_ID = "consumer_id";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String PAGE = "page";

//    public ConsumerParamsBuilder addConsumerId(String consumerId) {
//        addGetParam(CONSUMER_ID, consumerId);
//        return this;
//    }

    public ConsumerParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public ConsumerParamsBuilder addPage(int page) {
        addGetParam(PAGE, String.valueOf(page));
        return this;
    }


    public ConsumerParamsBuilder addVideoId(String videoId) {
        addPathParam(VIDEO_ID, videoId);
        return this;
    }
}
