package com.zype.android.webapi.model.onair;

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
    private Integer start;

    /**
     * @return The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * @param end The end
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * @return The start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(Integer start) {
        this.start = start;
    }
}
