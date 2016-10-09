package com.junyu.moneymanager;

import android.app.Application;

import timber.log.Timber;


/**
 * Created by Junyu on 10/8/2016.
 */

public class MoneyManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
