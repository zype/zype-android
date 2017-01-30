package com.zype.android.webapi.model.zobjects;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */

import com.google.gson.annotations.Expose;

public class Styles {

    @Expose
    private Original original;

    /**
     *
     * @return
     * The original
     */
    public Original getOriginal() {
        return original;
    }

    /**
     *
     * @param original
     * The original
     */
    public void setOriginal(Original original) {
        this.original = original;
    }

}