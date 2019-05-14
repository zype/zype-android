package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 25.05.2017.
 */

public class VideosResponse {
    @SerializedName("response")
    @Expose
    public List<VideoData> videoData = new ArrayList<>();

    @Expose
    public Pagination pagination;

    @Expose
    public String message;
}
