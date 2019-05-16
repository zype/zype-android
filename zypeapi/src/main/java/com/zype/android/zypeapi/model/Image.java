package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 24.10.2017.
 */

public class Image {
    @SerializedName("_id")
    @Expose
    public String id;

    @Expose
    public String caption;

    @Expose
    public String layout;

    @Expose
    public String title;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @Expose
    public String url;
}
