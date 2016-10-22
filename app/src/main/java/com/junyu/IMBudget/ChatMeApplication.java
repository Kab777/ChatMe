package com.junyu.IMBudget;

import android.app.Application;

import timber.log.Timber;


/**
 * Created by Junyu on 10/8/2016.
 */

public class ChatMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
