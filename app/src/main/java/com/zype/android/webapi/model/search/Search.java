package com.zype.android.webapi.model.search;

/**
 * @author vasya
 * @version 1
 * date 6/29/15
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.List;

public class Search {

    @SerializedName("response")
    @Expose
    private List<VideoData> videoData = new ArrayList<>();
    @Expose
    private Pagination pagination;
    @Expose
    private String message;
    /**
     * @return The response
     */
    public List<VideoData> getVideoData() {
        return videoData;
    }

    /**
     * @param videoData The response
     */
    public void setVideoData(List<VideoData> videoData) {
        this.videoData = videoData;
    }

    /**
     * @return The pagination
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * @param pagination The pagination
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}