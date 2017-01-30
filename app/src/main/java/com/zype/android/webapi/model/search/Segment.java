package com.zype.android.webapi.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class Segment {

    @SerializedName("_id")
    @Expose
    private String Id;
    @Expose
    private String description;
    @Expose
    private Integer end;
    @Expose
    private int start;

    private boolean isActive;

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
     * The end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     *
     * @param end
     * The end
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     *
     * @return
     * The start
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @param start
     * The start
     */
    public void setStart(int start) {
        this.start = start;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
