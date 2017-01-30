package com.zype.android.webapi.model.video;

/**
 * @author vasya
 * @version 1
 *          date 6/29/15
 */

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @Expose
    private String title;
    @Expose
    private List<String> value = new ArrayList<>();

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
     * The value
     */
    public List<String> getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

}