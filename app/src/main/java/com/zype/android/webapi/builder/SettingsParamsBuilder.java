package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

public class SettingsParamsBuilder extends ParamsBuilder {

    public SettingsParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

}
