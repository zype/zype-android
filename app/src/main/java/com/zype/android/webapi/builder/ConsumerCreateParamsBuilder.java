package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;
import com.zype.android.ui.Consumer.Model.Consumer;

/**
 * Created by Evgeny Cherkasov on 27.06.2017.
 */
public class ConsumerCreateParamsBuilder extends ParamsBuilder {
    private static final String EMAIL = "consumer[email]";
    private static final String PASSWORD = "consumer[password]";

    public ConsumerCreateParamsBuilder addAppKey() {
        addGetParam(APP_KEY, ZypeSettings.APP_KEY);
        return this;
    }

    public ConsumerCreateParamsBuilder addConsumerParams(Consumer consumer) {
        addPostParam(EMAIL, consumer.email);
        addPostParam(PASSWORD, consumer.password);
        return this;
    }
}
