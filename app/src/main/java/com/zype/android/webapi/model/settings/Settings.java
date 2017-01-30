package com.zype.android.webapi.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/13/15
 */
public class Settings {

    @SerializedName("response")
    @Expose
    private List<SettingsData> settingsData = new ArrayList<>();
    @Expose
    private Pagination pagination;

    /**
     *
     * @return
     * The settingsData
     */
    public List<SettingsData> getSettingsData() {
        return settingsData;
    }

    /**
     *
     * @param settingsData
     * The settingsData
     */
    public void setSettingsData(List<SettingsData> settingsData) {
        this.settingsData = this.settingsData;
    }

    /**
     *
     * @return
     * The pagination
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     *
     * @param pagination
     * The pagination
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
