package com.zype.android.webapi.model.epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProgramResponse {
  @SerializedName("response")
  @Expose
  public List<Program> response = new ArrayList<>();

  @SerializedName("pagination")
  @Expose
  public Pagination pagination;
}
