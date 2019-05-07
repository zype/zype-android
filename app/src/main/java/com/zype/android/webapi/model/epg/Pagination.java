package com.zype.android.webapi.model.epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

  @Expose
  public Integer current;

  @Expose
  public Integer next;

  @Expose
  public Integer previous;

  /**
   * The number of records which was returned. Example: 10.
   */
  @SerializedName("per_page")
  @Expose
  public Integer perPage;

  /**
   * All amount of pages. Example: 10.
   */
  @Expose
  public Integer pages;
}
