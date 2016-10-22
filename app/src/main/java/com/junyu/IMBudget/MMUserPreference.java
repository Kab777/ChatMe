package com.junyu.IMBudget;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;


/**
 * Created by Junyu on 10/9/2016.
 */

public class MMUserPreference {
    

    public static String getUserImg(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(MMConstant.PROFILE_IMAGE, null);
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(MMConstant.EMAIL, null);
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(MMConstant.NAME, null);
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(MMConstant.USER_ID, null);
    }

    public static Boolean ifUserRegistered(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.contains(MMConstant.USER_ID);
    }


    public static void updateImg(Context context, String imgUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        sharedPreferences.edit().putString(MMConstant.PROFILE_IMAGE, imgUrl).commit();
    }

    public static void cleanSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
}
