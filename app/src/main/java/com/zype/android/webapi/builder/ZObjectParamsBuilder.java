package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class ZObjectParamsBuilder extends ParamsBuilder {

    private static final String ZOBJECT_TYPE = "zobject_type";

    public static final String TYPE_GUEST = "guest";
    public static final String TYPE_NOTIFICATIONS = "notifications";
    public static final String TYPE_CONTENT = "content";
    public static final String VIDEO_ID = "video_id";

//    public ZObjectParamsBuilder addAppKey() {
//        addGetParam(API_KEY, WebApiManager.API_KEY);
//        return this;
//    }

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

//    public ZObjectParamsBuilder addDateLimit(String startDate, String endDate) {
//        addGetParam(DATE_START, startDate);
//        addGetParam(DATE_END, endDate);
//        return this;
//    }
//
//    public ZObjectParamsBuilder addOnAir(boolean isOnAir) {
//        addGetParam(DATA_IS_ON_AIR, String.valueOf(isOnAir));
//        return this;
//    }
}
