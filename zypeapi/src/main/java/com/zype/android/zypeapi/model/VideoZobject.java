package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 12.02.2019.
 */
public class VideoZobject {
    @SerializedName("_id")
    @Expose
    private String id;

    @Expose
    private String description;

    @Expose
    private String title;
    @SerializedName("zobject_type_title")

    @Expose
    private String zobjectTypeTitle;
}
