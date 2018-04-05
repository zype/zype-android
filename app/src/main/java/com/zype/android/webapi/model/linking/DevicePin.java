package com.zype.android.webapi.model.linking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 03.04.2018.
 */

public class DevicePin {
    @SerializedName("response")
    @Expose
    public DevicePinData data;
}
