package com.zype.android.webapi.builder;

import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class EntitlementsParamsBuilder extends ParamsBuilder {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String PAGE = "page";

    public EntitlementsParamsBuilder addAccessToken() {
        addGetParam(ACCESS_TOKEN, SettingsProvider.getInstance().getAccessToken());
        return this;
    }

    public EntitlementsParamsBuilder addPage(int page) {
        addGetParam(PAGE, String.valueOf(page));
        return this;
    }

}
