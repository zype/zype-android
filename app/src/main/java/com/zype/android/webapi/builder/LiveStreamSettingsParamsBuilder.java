package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

/**
 * Created by Evgeny Cherkasov on 22.11.2016.
 */

public class LiveStreamSettingsParamsBuilder extends ParamsBuilder {
    public LiveStreamSettingsParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }
}
