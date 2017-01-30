package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class CategoryParamsBuilder extends ParamsBuilder {

    public static final String CATEGORY_BEST_OF = "Best Of";
    public static final String CATEGORY_HIGHTLIGHT = "Highlight";

    public CategoryParamsBuilder addApiKey() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);

        return this;
    }
}
