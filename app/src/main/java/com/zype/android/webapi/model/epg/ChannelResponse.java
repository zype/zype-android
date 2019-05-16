package com.zype.android.webapi.model.epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChannelResponse {
  @SerializedName("response")
  @Expose
  public List<Channel> response = new ArrayList<>();

  @SerializedName("pagination")
  @Expose
  public Pagination pagination;
}
