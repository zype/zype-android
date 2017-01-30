package com.zype.android.webapi.model.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;

/**
 * @author vasya
 * @version 1
 *          date 7/15/15
 */
public class FavoriteVideo {

    @SerializedName("response")
    @Expose
    private ConsumerFavoriteVideoData response = new ConsumerFavoriteVideoData();

    /**
     * @return The response
     */
    public ConsumerFavoriteVideoData getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    public void setResponse(ConsumerFavoriteVideoData response) {
        this.response = response;
    }

}
