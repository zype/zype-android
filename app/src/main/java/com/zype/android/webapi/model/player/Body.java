package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/10/15
 */
public class Body {
    @Expose
    private Advertising advertising;

    @Expose
    private List<File> files = new ArrayList<>();

    /**
     *
     * @return
     * The advertising
     */
    public Advertising getAdvertising() {
        return advertising;
    }

    /**
     *
     * @param advertising
     * The advertising
     */
    public void setAdvertising(Advertising advertising) {
        this.advertising = advertising;
    }

    /**
     *
     * @return
     * The files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     *
     * @param files
     * The files
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }
}
