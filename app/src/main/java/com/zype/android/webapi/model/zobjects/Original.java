package com.zype.android.webapi.model.zobjects;

import com.google.gson.annotations.Expose;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class Original {
    @Expose
    private String url;
    @Expose
    private String size;

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
     * The size
     */
    public String getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    public void setSize(String size) {
        this.size = size;
    }
}
