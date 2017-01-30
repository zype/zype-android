package com.zype.android.webapi.model.onair;

import com.google.gson.annotations.Expose;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class OnAirData {


    @Expose
    private List<Response> response = new ArrayList<>();
    @Expose
    private Pagination pagination;

    /**
     * @return The response
     */
    public List<Response> getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    public void setResponse(List<Response> response) {
        this.response = response;
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

    @Override
    public String toString() {
        return "OnAirData{" +
                "response=" + response +
                ", pagination=" + pagination +
                '}';
    }
}
