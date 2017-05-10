package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class PlaylistParamsBuilder extends ParamsBuilder {

    private static final String PAGE = "page";
    private static final String PER_PAGE = "per_page";
    private static final String DATE_START = "published_at.gte";
    private static final String DATE_END = "published_at.lte";
    private static final String DATA_IS_ON_AIR = "on_air";
    private static final String CATEGORY_HIGHLIGHT = "category[" + CategoryParamsBuilder.CATEGORY_HIGHTLIGHT + "]";
    public static final String VIDEO_ID = "id";
    public static final String PARENT_ID = "parent_id";

//    public VideoParamsBuilder addAppKey() {
//        addGetParam(API_KEY, WebApiManager.API_KEY);
//        return this;
//    }


    public PlaylistParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

    public PlaylistParamsBuilder addPage(int page) {
        addGetParam(PAGE, String.valueOf(page));
        return this;
    }

    public PlaylistParamsBuilder addPerPage(int number) {
        addGetParam(PER_PAGE, String.valueOf(number));
        return this;
    }

    public PlaylistParamsBuilder addDateLimit(String startDate, String endDate) {
        addGetParam(DATE_START, startDate);
        addGetParam(DATE_END, endDate);
        return this;
    }

    public PlaylistParamsBuilder addOnAir(boolean isOnAir) {
        addGetParam(DATA_IS_ON_AIR, String.valueOf(isOnAir));
        return this;
    }

    public PlaylistParamsBuilder addCategoryHighlight(boolean value) {
        addGetParam(CATEGORY_HIGHLIGHT, String.valueOf(value));
        return this;
    }

    public PlaylistParamsBuilder addVideoId(String videoId) {
        addGetParam(VIDEO_ID, videoId);
        return this;
    }

    public PlaylistParamsBuilder excludeCategoryWithValue(String categoryTitleToExclude, String value) {
        addGetParam("category![" + categoryTitleToExclude + "]", value);
        return this;
    }

    public PlaylistParamsBuilder addParentId(String parentId) {
        addGetParam(PARENT_ID, parentId);
        return  this;
    }
}
