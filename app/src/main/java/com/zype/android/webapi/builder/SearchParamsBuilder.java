package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class SearchParamsBuilder extends ParamsBuilder {

    private static final String SEARCH_TEXT = "q";
    private static final String PAGE = "page";
    private static final String PLAYLIST_ID_EXCLUSIVE = "playlist_id.exclusive";
    private static final String PLAYLIST_ID_INCLUSIVE = "playlist_id.inclusive";

    public SearchParamsBuilder addSearchText(String searchText) {
        addGetParam(SEARCH_TEXT, searchText);
        return this;
    }

    public SearchParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

    public SearchParamsBuilder addPage(int page) {
        addGetParam(PAGE, String.valueOf(page));
        return this;
    }

    public SearchParamsBuilder addPlaylistId(String playlistId, boolean inclusive) {
        if (inclusive) {
            addGetParam(PLAYLIST_ID_INCLUSIVE, playlistId);
        }
        else {
            addGetParam(PLAYLIST_ID_EXCLUSIVE, playlistId);
        }
        return this;
    }
}
