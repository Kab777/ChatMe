package com.junyu.IMBudget.model;

/**
 * Created by Junyu on 10/17/2016.
 */

public class FriendRequest {
    public String senderId;
    public Boolean accepted;
    public String chatId;
    public String name;
    public String imgUrl;
    public String requestMsg;


    public FriendRequest() {

    }

    public FriendRequest(Boolean accepted, String chatId, String name, String requestMsg) {
        this.accepted = accepted;
        this.chatId = chatId;
        this.name = name;
        this.requestMsg = requestMsg;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public String getRequestMsg() {
        return requestMsg;
    }

    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }

}
