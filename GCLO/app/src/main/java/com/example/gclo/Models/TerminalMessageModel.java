package com.example.gclo.Models;


public class TerminalMessageModel {
    String message;
    String sendtBy;
    long timestamp;
    public static String received_by_user = "user";
    public static String sent_by_admin = "admin";

    public TerminalMessageModel(String message, String sendtBy) {
        this.message = message;
        this.sendtBy = sendtBy;
    }

    public TerminalMessageModel(String message, String sendtBy, long timestamp) {
        this.message = message;
        this.sendtBy = sendtBy;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}