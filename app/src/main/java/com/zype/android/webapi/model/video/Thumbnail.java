package com.zype.android.webapi.model.video;

/**
 * @author vasya
 * @version 1
 *          date 6/29/15
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnail {

    @SerializedName("aspect_ratio")
    @Expose
    private Float aspectRatio;
    @Expose
    private Integer height;
    @Expose
    private String name;
    @Expose
    private String url;
    @Expose
    private Integer width;

    public Thumbnail() {
    }

    public Thumbnail(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The aspectRatio
     */
    public Float getAspectRatio() {
        return aspectRatio;
    }

    /**
     *
     * @param aspectRatio
     * The aspect_ratio
     */
    public void setAspectRatio(Float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     *
     * @return
     * The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     *
     * @param height
     * The height
     */
    public void setHeight(Integer height) {
        this.height = height;
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
     * The width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     *
     * @param width
     * The width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

}