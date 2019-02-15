package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 24.05.2017.
 */

public class Thumbnail {
    @SerializedName("aspect_ratio")
    @Expose
    public Float aspectRatio;

    @Expose
    public Integer height;

    @Expose
    public String name;

    @Expose
    public String url;

    @Expose
    public Integer width;
}
