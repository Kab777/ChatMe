package com.junyu.IMBudget;

import android.app.Application;
import android.content.Context;


import com.junyu.IMBudget.module.DaggerNetComponent;
import com.junyu.IMBudget.module.NetComponent;
import com.junyu.IMBudget.module.NetModule;


import timber.log.Timber;


/**
 * Created by Junyu on 10/8/2016.
 */

public class ChatMeApplication extends Application {
    private NetComponent netComponent;
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());


        netComponent = DaggerNetComponent.builder()
                .netModule(new NetModule("http://api.program-o.com/v2/"))
                .build();
    }

    private static ChatMeApplication getApp(Context context) {
        return (ChatMeApplication) context.getApplicationContext();
    }

    public static NetComponent getNetComponent(Context context) {
        return getApp(context).netComponent;
    }
}
