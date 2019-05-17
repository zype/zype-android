package com.zype.android.webapi.model.epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

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

  @SerializedName("end_time")
  @Expose
  public String endDateTime;

  @SerializedName("start_time")
  @Expose
  public String startDateTime;

  public long getStartTime() {
    try {
      return LocalDateTime.parse(startTime, DateTimeFormat.forPattern(DATE_FORMAT)).toDateTime().getMillis() + DateTimeZone.getDefault().getOffset(DateTime.now());
    } catch (Exception e) {
      return 0;
    }
  }

  public long getEndTime() {
    try {
      return LocalDateTime.parse(endTime, DateTimeFormat.forPattern(DATE_FORMAT)).toDateTime().getMillis() + DateTimeZone.getDefault().getOffset(DateTime.now());
    } catch (Exception e) {
      return 0;
    }
  }
}
