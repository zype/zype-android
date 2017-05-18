package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class VideoParamsBuilder extends ParamsBuilder {

    private static final String PAGE = "page";
    private static final String PER_PAGE = "per_page";
    private static final String DATE_START = "published_at.gte";
    private static final String DATE_END = "published_at.lte";
    private static final String DATA_IS_ON_AIR = "on_air";
    private static final String CATEGORY_HIGHLIGHT = "category[" + CategoryParamsBuilder.CATEGORY_HIGHTLIGHT + "]";
    public static final String VIDEO_ID = "id";
    public static final String PLAYLIST_ID = "playlist_id";

//    public VideoParamsBuilder addAppKey() {
//        addGetParam(API_KEY, WebApiManager.API_KEY);
//        return this;
//    }

    public VideoParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

    public VideoParamsBuilder addPlaylistId(String playlistId) {
        addPathParam(PLAYLIST_ID, playlistId);
        return this;
    }

    public VideoParamsBuilder addPage(int page) {
        addGetParam(PAGE, String.valueOf(page));
        return this;
    }

    public VideoParamsBuilder addPerPage(int perPage) {
        addGetParam(PER_PAGE, String.valueOf(perPage));
        return this;
    }

    public VideoParamsBuilder addDateLimit(String startDate, String endDate) {
        addGetParam(DATE_START, startDate);
        addGetParam(DATE_END, endDate);
        return this;
    }

    public VideoParamsBuilder addOnAir(boolean isOnAir) {
        addGetParam(DATA_IS_ON_AIR, String.valueOf(isOnAir));
        return this;
    }

    public VideoParamsBuilder addCategoryHighlight(boolean value) {
        addGetParam(CATEGORY_HIGHLIGHT, String.valueOf(value));
        return this;
    }

    public VideoParamsBuilder addVideoId(String videoId) {
        addGetParam(VIDEO_ID, videoId);
        return this;
    }

    public VideoParamsBuilder excludeCategoryWithValue(String categoryTitleToExclude, String value) {
        addGetParam("category![" + categoryTitleToExclude + "]", value);
        return this;
    }
}
