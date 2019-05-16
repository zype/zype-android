package com.zype.android.ui.epg;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Kristoffer.
 */
public class EPGUtil {
  private static final String TAG = "EPGUtil";
  private static final DateTimeFormatter dtfShortTime = DateTimeFormat.forPattern("h:mm a");

  public static String getShortTime(long timeMillis) {
    return dtfShortTime.print(timeMillis);
  }

  public static String getWeekdayName(long dateMillis) {
    LocalDate date = new LocalDate(dateMillis);
    return date.dayOfWeek().getAsText();
  }

  public static String getEPGdayName(long dateMillis) {
    LocalDate date = new LocalDate(dateMillis);
    return date.monthOfYear().getAsText() + ", " + date.getDayOfMonth();

  }


}
