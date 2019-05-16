package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 30.12.2017.
 */

public class VideoFavoritesResponse {
    @SerializedName("response")
    @Expose
    public List<VideoFavoriteData> videoFavorites = new ArrayList<>();

    @Expose
    public Pagination pagination;

    @Expose
    public String message;
}
