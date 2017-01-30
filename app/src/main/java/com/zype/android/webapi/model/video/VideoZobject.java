package com.zype.android.webapi.model.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class VideoZobject {

    @SerializedName("_id")
    @Expose
    private String Id;
    @Expose
    private String description;
    @Expose
    private String title;
    @SerializedName("zobject_type_title")
    @Expose
    private String zobjectTypeTitle;

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
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * The zobjectTypeTitle
     */
    public String getZobjectTypeTitle() {
        return zobjectTypeTitle;
    }

    /**
     *
     * @param zobjectTypeTitle
     * The zobject_type_title
     */
    public void setZobjectTypeTitle(String zobjectTypeTitle) {
        this.zobjectTypeTitle = zobjectTypeTitle;
    }
}
