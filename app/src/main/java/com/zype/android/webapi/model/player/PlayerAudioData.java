package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class PlayerAudioData {

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
