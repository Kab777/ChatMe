package com.junyu.IMBudget.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Junyu on 10/17/2016.
 */

public class Time {
    public static String getCurTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        String curTime = sdf.format(new Date());
        return curTime;
    }

    public static String formateDateFromString(String date) {
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", java.util.Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("MMM dd HH:mm", java.util.Locale.getDefault());

        try {
            parsed = input.parse(date);
            outputDate = output.format(parsed);

        } catch (ParseException e) {
            Timber.e(e.getMessage());
        }
        return outputDate;
    }

    public static String formateDateFromFriendScreen(String date) {
        Date parsed = null;
        String outputDate = "";
        Calendar currentCalendar = Calendar.getInstance();
        Calendar targetCalendar = Calendar.getInstance();
        int curWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int curDay = currentCalendar.get(Calendar.DAY_OF_YEAR);
        SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", java.util.Locale.getDefault());
        SimpleDateFormat output;

        try {
            parsed = input.parse(date);
            targetCalendar.setTime(parsed);

        } catch (ParseException e) {
            Timber.e(e.getMessage());
        }

        int msgWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int msgDay = targetCalendar.get(Calendar.DAY_OF_YEAR);

        if (curDay == msgDay) {
            output = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        } else if (curWeek == msgWeek) {
            output = new SimpleDateFormat("EEE", java.util.Locale.getDefault());
        } else {
            output = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        }
        outputDate = output.format(parsed);
        return outputDate;
    }
}
