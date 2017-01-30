package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;

/**
 * @author vasya
 * @version 1
 *          date 7/15/15
 */
public class FavoriteParamsBuilder extends ParamsBuilder {

    public static final String FAVORITE_ID = "favorite_id";
    public static final String VIDEO_ID = "video_id";
//    public static final String CONSUMER_ID = "consumer_id";
    private static final String ACCESS_TOKEN = "access_token";

//    public FavoriteParamsBuilder addConsumerId(String consumerId) {
//        addPathParam(CONSUMER_ID, consumerId);
//        return this;
//    }

    public FavoriteParamsBuilder addAccessToken() {
        addPostParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public FavoriteParamsBuilder addVideoId(String videoId) {
        addPostParam(VIDEO_ID, videoId);
        return this;
    }

    public FavoriteParamsBuilder addPathVideoId(String videoId) {
        addPathParam(VIDEO_ID,videoId);
        return this;
    }

    public FavoriteParamsBuilder addPathFavoriteId(String favoriteId) {
        addPathParam(FAVORITE_ID, favoriteId);
        return this;
    }
}
