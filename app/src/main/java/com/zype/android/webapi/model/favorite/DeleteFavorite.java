package com.zype.android.webapi.model.favorite;

import com.google.gson.annotations.Expose;

/**
 * @author vasya
 * @version 1
 *          date 7/15/15
 */
public class DeleteFavorite {
    @Expose
    private String status;
    @Expose
    private String error;

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The error
     */
    public String getError() {
        return error;
    }

    /**
     *
     * @param error
     * The error
     */
    public void setError(String error) {
        this.error = error;
    }
}
