package com.terminal.gclo.Models;


public class ChatMessageModel {
    String message;
    String sentBy;
    long timestamp;
    public static String sent_by_user = "user";
    public static String sent_by_admin = "admin";


    public ChatMessageModel(String message, String sentBy, long timestamp) {
        this.message = message;
        this.sentBy = sentBy;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return this.sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}