package com.zype.android.webapi.model.video;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Video {

    @SerializedName("response")
    @Expose
    private VideoData videoData;

    /**
     * @return The response
     */
    public VideoData getVideoData() {
        return videoData;
    }

    /**
     * @param videoData The response
     */
    public void setVideoData(VideoData videoData) {
        this.videoData = videoData;
    }

}