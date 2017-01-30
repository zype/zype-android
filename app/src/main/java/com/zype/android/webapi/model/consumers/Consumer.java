package com.zype.android.webapi.model.consumers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class Consumer {

    @SerializedName("response")
    @Expose
    private ConsumerData consumerData;

    /**
     *
     * @return
     * The response
     */
    public ConsumerData getConsumerData() {
        return consumerData;
    }

    /**
     *
     * @param consumerData
     * The response
     */
    public void setv(ConsumerData consumerData) {
        this.consumerData = consumerData;
    }

}