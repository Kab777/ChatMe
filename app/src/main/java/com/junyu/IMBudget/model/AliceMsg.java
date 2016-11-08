package com.junyu.IMBudget.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Junyu on 11/7/2016.
 */

public class AliceMsg {
    private String convoId;
    private String usersay;
    private String botsay;


    /**
     *
     * @return
     * The convoId
     */
    public String getConvoId() {
        return convoId;
    }

    /**
     *
     * @param convoId
     * The convo_id
     */
    public void setConvoId(String convoId) {
        this.convoId = convoId;
    }

    /**
     *
     * @return
     * The usersay
     */
    public String getUsersay() {
        return usersay;
    }

    /**
     *
     * @param usersay
     * The usersay
     */
    public void setUsersay(String usersay) {
        this.usersay = usersay;
    }

    /**
     *
     * @return
     * The botsay
     */
    public String getBotsay() {
        return botsay;
    }

    /**
     *
     * @param botsay
     * The botsay
     */
    public void setBotsay(String botsay) {
        this.botsay = botsay;
    }
}
