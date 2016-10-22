package com.junyu.IMBudget.model;

/**
 * Created by Junyu on 10/17/2016.
 */

public class Friend {

    public Boolean online;
    public String userId;
    public String chatId;
    public String name;
    public String lastMsg;
    public String lastMsgTime;
    public String imgUrl = "";


    public Friend() {

    }

    public Friend(String name, Boolean online) {

        this.name = name;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String id) {
        this.chatId = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }


    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Friend UserId is " + userId + " Friend Name " +
                name + " status " + online + " img is " + imgUrl;
    }
}
