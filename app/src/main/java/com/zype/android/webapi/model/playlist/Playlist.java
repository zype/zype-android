package com.zype.android.webapi.model.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class Playlist {



    @SerializedName("response")
    @Expose
    private List<PlaylistData> response = new ArrayList<>();
    @Expose
    private Pagination pagination;

    public List<PlaylistData> getPlaylistData() {
        return response;
    }

    /**
     *
     * @return
     * The response
     */
    public List<PlaylistData> getResponse() {
        return response;
    }

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(List<PlaylistData> response) {
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
