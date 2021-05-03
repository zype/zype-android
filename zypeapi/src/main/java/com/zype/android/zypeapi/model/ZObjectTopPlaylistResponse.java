package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ZObjectTopPlaylistResponse {
    @SerializedName("response")
    @Expose
    public List<ZObjectTopPlaylist> topPlaylists = new ArrayList<>();

}
