package com.zype.android.webapi.model.epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Program implements Serializable {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  @SerializedName("_id")
  @Expose
  public String id;

  @SerializedName("title")
  @Expose
  public String name;

  @SerializedName("program_guide_id")
  @Expose
  public String programGuideId;

  @SerializedName("start_time_with_offset")
  @Expose
  public String startTime;

  @SerializedName("end_time_with_offset")
  @Expose
  public String endTime;


  public long getStartTime() {
    try {
      SimpleDateFormat formatter = new
          SimpleDateFormat(DATE_FORMAT);
      Date date = formatter.parse(startTime);
      return date.getTime();
    } catch (Exception e) {
      return 0;
    }
  }

  public long getEndTime() {
    try {
      SimpleDateFormat formatter = new
          SimpleDateFormat(DATE_FORMAT);
      Date date = formatter.parse(endTime);
      return date.getTime();
    } catch (Exception e) {
      return 0;
    }
  }

}
