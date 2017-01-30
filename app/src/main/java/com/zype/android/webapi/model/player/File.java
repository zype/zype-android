package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;

/**
 * @author vasya
 * @version 1
 *          date 7/10/15
 */
public class File {

    @Expose
    private String url;
    @Expose
    private String name;

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }
}
