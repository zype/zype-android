package com.zype.android.webapi.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.video.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class ContentSettings {
    @SerializedName("response")
    @Expose
    private List<ContentSettingsData> data = new ArrayList<>();

    @Expose
    private Pagination pagination;

    /**
     *
     * @return
     * The data
     */
    public List<ContentSettingsData> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<ContentSettingsData> data) {
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
