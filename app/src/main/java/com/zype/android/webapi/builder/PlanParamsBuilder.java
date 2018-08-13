package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

/**
 * Created by Evgeny Cherkasov on 22.06.2018.
 */

public class PlanParamsBuilder extends ParamsBuilder {
    public static final String PLAN_ID = "plan_id";

    public PlanParamsBuilder(String planId) {
        super();
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
        addPathParam(PLAN_ID, planId);
    }
}
