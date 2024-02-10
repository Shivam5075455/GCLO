package com.example.gclo.Models;


public class TerminalMessageModel {
    String message;
    String sendtBy;
    public static String sent_by_user = "user";
    public static String sent_by_admin = "admin";

    public TerminalMessageModel(String message, String sendtBy) {
        this.message = message;
        this.sendtBy = sendtBy;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendtBy() {
        return this.sendtBy;
    }

    public void setSendtBy(String sendtBy) {
        this.sendtBy = sendtBy;
    }
}