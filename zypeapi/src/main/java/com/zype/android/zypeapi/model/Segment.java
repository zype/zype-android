package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 12.02.2019.
 */
public class Segment {
    @SerializedName("_id")
    @Expose
    private String id;

    @Expose
    private String description;

    @Expose
    private Integer end;

    @Expose
    private int start;

    private boolean isActive;
}
