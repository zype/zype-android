package com.zype.android.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class DateUtils {
    public static long getTimeWeekAgo(long currentDate) {
        Date date = new Date(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -7);
        return c.getTimeInMillis();
    }

    public static long getCurrentDate() {
        return System.currentTimeMillis();
    }

    public static long getDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTimeInMillis();
    }

    public static long getNextWeekDay(long unixTime) {
        Date date = new Date(unixTime);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, +7);
        return c.getTimeInMillis();
    }

    @NonNull
    public static String getText(long currentDate, Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        Date date = new Date(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i);
        String startMonth = new SimpleDateFormat("MMM", currentLocale).format(c.getTime());
        String startDay = new SimpleDateFormat("dd", currentLocale).format(c.getTime());
        String startYear = new SimpleDateFormat("yyyy", currentLocale).format(c.getTime());

        c.add(Calendar.DATE, 6);
        String endMonth = new SimpleDateFormat("MMM", currentLocale).format(c.getTime());
        String endDay = new SimpleDateFormat("dd", currentLocale).format(c.getTime());
        String endYear = new SimpleDateFormat("yyyy", currentLocale).format(c.getTime());

        StringBuilder builder = new StringBuilder();
        builder.append(startMonth).append(" ").append(startDay);
        if (!TextUtils.equals(startYear, endYear)) {
            builder.append(", ").append(startYear);
        }
        builder.append(" - ");
        if (!TextUtils.equals(startMonth, endMonth)) {
            builder.append(endMonth).append(" ");
        }
        builder.append(endDay);
        builder.append(", ").append(endYear);
        return builder.toString();
    }

    @NonNull
    public static String getFormattedFirstDayOfWeek(long currentDate, Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        long timeFirstDayOfWeek = getFirstDayOfWeek(context, currentDate);
        return new SimpleDateFormat("yyyy-MM-dd", currentLocale).format(timeFirstDayOfWeek);
    }

    @NonNull
    public static String getFormattedLastDayOfWeek(long currentDate, Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        Date date = new Date(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, 7 - i);
        return new SimpleDateFormat("yyyy-MM-dd", currentLocale).format(c.getTime());
    }

    public static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Convert "2015-07-07T21:17:08.981-04:00" to "Tuesday, Jul. 9"
     *
     * @param dateString input string
     * @return formatted string
     */
    @Nullable
    public static String getConvertedText(@Nullable String dateString,@NonNull  Context context) {
        if (TextUtils.isEmpty(dateString)){
            return null;
        }
        Locale currentLocale = context.getResources().getConfiguration().locale;
        String outDateString = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", currentLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, LLL. dd", currentLocale);
        try {
            Date date = inputFormat.parse(dateString);
            outDateString = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outDateString;
    }

    public static boolean isNextWeekInFuture(Context context, long unixTime) {
        Date date = new Date(getFirstDayOfWeek(context, getNextWeekDay(unixTime)));
        Date currentDate = new Date();
        return date.after(currentDate);
    }

    private static long getFirstDayOfWeek(Context context, long unixTime) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        Date date = new Date(unixTime);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.getInstance(currentLocale).getFirstDayOfWeek());
        return c.getTimeInMillis();
    }

    public static long getLastDateOfWeek(long currentDate) {
        Date date = new Date(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTimeInMillis();
    }

    public static boolean isGreater(Context context, @Nullable String currentTimeStamp, @Nullable String greaterTimeStamp) {
        if (TextUtils.isEmpty(currentTimeStamp)) {
            return true;
        }
        if (TextUtils.isEmpty(greaterTimeStamp)) {
            return false;
        }
        Locale currentLocale = context.getResources().getConfiguration().locale;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", currentLocale);
        try {
            Date currentDate = inputFormat.parse(currentTimeStamp);
            Date greaterDate = inputFormat.parse(greaterTimeStamp);
            return (greaterDate.getTime() - currentDate.getTime()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
