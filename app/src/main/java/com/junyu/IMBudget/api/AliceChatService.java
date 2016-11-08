package com.junyu.IMBudget.api;

import com.junyu.IMBudget.model.AliceMsg;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Junyu on 11/7/2016.
 */


public interface AliceChatService {
    @GET("chatbot")
    Observable<AliceMsg> getMsg(
            @Query("bot_id") String botId,
            @Query("say") String input,
            @Query("convo_id") String userId,
            @Query("format") String format
    );
}
