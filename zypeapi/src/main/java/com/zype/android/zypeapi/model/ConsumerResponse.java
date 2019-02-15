package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 13.04.2017.
 */

public class ConsumerResponse {
    @SerializedName("response")
    @Expose
    public ConsumerData consumerData;
}
