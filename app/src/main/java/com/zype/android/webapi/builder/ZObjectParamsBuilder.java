package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class ZObjectParamsBuilder extends ParamsBuilder {

    private static final String ZOBJECT_TYPE = "zobject_type";

    public static final String TYPE_GUEST = "guest";
    public static final String TYPE_NOTIFICATIONS = "notifications";
    public static final String TYPE_CONTENT = "content";
    public static final String TYPE_TOP_PLAYLISTS = "top_playlists";
    public static final String VIDEO_ID = "video_id";

    public ZObjectParamsBuilder addType(String type) {
        addGetParam(ZOBJECT_TYPE, type);
        return this;
    }

    public ZObjectParamsBuilder addVideoId(String videoId) {
        addGetParam(VIDEO_ID, videoId);
        return this;
    }

    public ZObjectParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }
}
