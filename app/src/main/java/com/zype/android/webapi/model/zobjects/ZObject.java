package com.zype.android.webapi.model.zobjects;

/**
 * @author vasya
 * @version 1
 * date 6/30/15
 */


import com.google.gson.annotations.Expose;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

public class ZObject {

    @Expose
    private List<ZobjectData> response = new ArrayList<>();
    @Expose
    private Pagination pagination;

    /**
     *
     * @return
     * The response
     */
    public List<ZobjectData> getResponse() {
        return response;
    }

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(List<ZobjectData> response) {
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
