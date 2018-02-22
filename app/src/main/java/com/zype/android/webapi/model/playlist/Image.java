package com.zype.android.webapi.model.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andy Zheng on 2/22/18.
 */

public class Image {
    @SerializedName("_id")
    @Expose
    private String id;

    @Expose
    private String caption;

    @Expose
    private String layout;

    @Expose
    private String title;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @Expose
    private String url;

    public Image() {
    }

    /**
     * @return
     * The id
     */
    public String getId() { return id; }

    /**
     * @param id
     * The id
     */
    public void setId(String id) { this.id = id; }

    /**
     * @return
     * The caption
     */
    public String getCaption() { return caption; }

    /**
     * @param caption
     * The caption
     */
    public void setCaption(String caption) { this.caption = caption; }

    /**
     * @return
     * The layout
     */
    public String getLayout() { return layout; }

    /**
     * @param layout
     * The layout
     */
    public void setLayout(String layout) { this.layout = layout; }

    /**
     * @return
     * The title
     */
    public String getTitle() { return title; }

    /**
     * @param title
     * The title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * @return
     * Updated at
     */
    public String getUpdatedAt() { return updatedAt; }

    /**
     * @param updatedAt
     * Updated at
     */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /**
     * @return
     * The url
     */
    public String getUrl() { return url; }

    /**
     * @param url
     * The url
     */
    public void setUrl(String url) { this.url = url; }
}
