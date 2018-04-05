package com.zype.android.webapi.builder;

import com.zype.android.webapi.WebApiManager;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class DevicePinParamsBuilder extends ParamsBuilder {

    public static final String LINKED_DEVICE_ID = "linked_device_id";

    public DevicePinParamsBuilder() {
        addGetParam(APP_KEY, WebApiManager.APP_KEY);
    }

    public DevicePinParamsBuilder addDeviceId(String deviceId) {
        addGetParam(LINKED_DEVICE_ID, deviceId);
        return this;
    }

}
