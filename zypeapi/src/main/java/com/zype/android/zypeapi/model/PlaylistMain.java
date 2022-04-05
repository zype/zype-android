package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistMain {
    @SerializedName("response")
    @Expose
    private PlaylistMainResponse response;

    public PlaylistMainResponse getResponse() {
        return response;
    }

    public void setResponse(PlaylistMainResponse response) {
        this.response = response;
    }
}
