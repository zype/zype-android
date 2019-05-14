package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 28.03.2018.
 */

public class DevicePinResponse {
    @SerializedName("response")
    @Expose
    public DevicePinData data;
}
