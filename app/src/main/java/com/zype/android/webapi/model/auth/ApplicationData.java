package com.zype.android.webapi.model.auth;

import com.google.gson.annotations.Expose;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class ApplicationData {
    @Expose
    private String uid;

    /**
     *
     * @return
     * The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     * The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}
