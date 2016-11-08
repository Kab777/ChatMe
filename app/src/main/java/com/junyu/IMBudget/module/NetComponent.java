package com.junyu.IMBudget.module;

import com.junyu.IMBudget.activity.ActivityFriendChat;


import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Junyu on 11/7/2016.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(ActivityFriendChat activityFriendChat);
}