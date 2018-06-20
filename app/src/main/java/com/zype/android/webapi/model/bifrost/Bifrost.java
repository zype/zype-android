package com.zype.android.webapi.model.bifrost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.consumers.ConsumerData;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class Bifrost {
    @SerializedName("response")
    @Expose
    public BifrostData data;
}