package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

/**
 * Created by Evgeny Cherkasov on 07.12.2018.
 */

public class AppParamsBuilder extends ParamsBuilder {

    public AppParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }


}
