package com.example.ujchatapp;

public class GroupMessageModel {
    String sender, message, date, type;


    public GroupMessageModel() {
    }

    public GroupMessageModel(String sender, String message, String date, String type) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
