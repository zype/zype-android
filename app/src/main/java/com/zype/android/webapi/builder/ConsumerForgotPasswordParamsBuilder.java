package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.WebApiManager;

/**
 * Created by Evgeny Cherkasov on 07.10.2018.
 */

public class ConsumerForgotPasswordParamsBuilder extends ParamsBuilder {

    private static final String EMAIL = "email";

    public ConsumerForgotPasswordParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

    public ConsumerForgotPasswordParamsBuilder addEmail(String email) {
        addPostParam(EMAIL, email);
        return this;
    }
}
