package com.example.gclo.Models;


public class ChatMessageModel {
    String message;
    String sentBy;
    public static String sent_by_user = "user";
    public static String sent_by_admin = "admin";


    public ChatMessageModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
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

}