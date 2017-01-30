package com.zype.android.webapi.model.zobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class Picture {

    @SerializedName("_id")
    @Expose
    private String Id;
    @SerializedName("content_type")
    @Expose
    private String contentType;
    @Expose
    private String caption;
    @Expose
    private String title;
    @Expose
    private String url;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private Styles styles;

    /**
     *
     * @return
     * The Id
     */
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     * The contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     *
     * @param contentType
     * The content_type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @return
     * The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     *
     * @param caption
     * The caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The styles
     */
    public Styles getStyles() {
        return styles;
    }

    /**
     *
     * @param styles
     * The styles
     */
    public void setStyles(Styles styles) {
        this.styles = styles;
    }
}
