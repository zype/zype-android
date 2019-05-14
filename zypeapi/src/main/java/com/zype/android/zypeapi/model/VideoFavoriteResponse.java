package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 04.01.2018.
 */

public class VideoFavoriteResponse {
    @SerializedName("response")
    @Expose
    public VideoFavoriteData data;
}
