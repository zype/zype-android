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
}
