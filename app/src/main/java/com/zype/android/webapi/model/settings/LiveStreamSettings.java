package com.zype.android.webapi.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class LiveStreamSettings {
    @SerializedName("response")
    @Expose
    private List<LiveStreamSettingsData> data = new ArrayList<>();

    @Expose
    private Pagination pagination;

    /**
     *
     * @return
     * The data
     */
    public List<LiveStreamSettingsData> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setSettingsData(List<LiveStreamSettingsData> data) {
        this.data = data;
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
