package com.zype.android.webapi.model.consumers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class ConsumerFavoriteVideo {

    @SerializedName("response")
    @Expose
    private List<ConsumerFavoriteVideoData> response = new ArrayList<>();
    @Expose
    private Pagination pagination;

    /**
     *
     * @return
     * The response
     */
    public List<ConsumerFavoriteVideoData> getResponse() {
        return response;
    }

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(List<ConsumerFavoriteVideoData> response) {
        this.response = response;
    }

    /**
     *
     * @return
     * The pagination
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     *
     * @param pagination
     * The pagination
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
