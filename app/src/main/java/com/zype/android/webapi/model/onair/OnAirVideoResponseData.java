package com.zype.android.webapi.model.onair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.player.PlayerDataResponse;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class OnAirVideoResponseData {

    @SerializedName("response")
    @Expose
    private PlayerDataResponse response;

    /**
     * @return The response
     */
    public PlayerDataResponse getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    public void setResponse(PlayerDataResponse response) {
        this.response = response;
    }
}
