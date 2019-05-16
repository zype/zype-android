package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 14.10.2017.
 */

public class VideoResponse {
    @SerializedName("response")
    @Expose
    public VideoData videoData;
}
