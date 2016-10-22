package com.junyu.IMBudget.model;

/**
 * Created by Junyu on 10/17/2016.
 */

public class MessageChatModel {
    public String content;
    public String name;
    public String timestamp;
    public String userId;

    private int recipientOrSenderStatus;

    public MessageChatModel(String content, String name, String timestamp, String userId) {
        this.content = content;
        this.name = name;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public MessageChatModel() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String time) {
        this.timestamp = time;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getRecipientOrSenderStatus() {
        return recipientOrSenderStatus;
    }

    public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
        this.recipientOrSenderStatus = recipientOrSenderStatus;
    }

    @Override
    public String toString() {
        return "User id is " + userId +
                " Sender name is " + name +
                " Content is " + content +
                " Time is " + timestamp;
    }
}
