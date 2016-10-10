package com.junyu.moneymanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Junyu on 10/9/2016.
 */

public class MMUserPreference {

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(MMConstant.USER_ID, null);
    }

    public static Boolean ifUserRegistered(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MMConstant.PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.contains(MMConstant.USER_ID);
    }
}
