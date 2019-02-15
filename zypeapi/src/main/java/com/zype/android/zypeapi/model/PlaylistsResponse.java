package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 24.05.2017.
 */

public class PlaylistsResponse {
    @SerializedName("response")
    @Expose
    public List<PlaylistData> response = new ArrayList<>();
    @SerializedName("pagination")
    @Expose
    public Pagination pagination;
}
