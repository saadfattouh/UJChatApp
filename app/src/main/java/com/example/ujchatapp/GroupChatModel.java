package com.example.ujchatapp;

public class GroupChatModel {

    GroupInfoModel info;
    GroupMessageModel[] messages;

    public GroupChatModel() {
    }

    public GroupChatModel(GroupInfoModel info, GroupMessageModel[] messages) {
        this.info = info;
        this.messages = messages;
    }

    public GroupInfoModel getInfo() {
        return info;
    }

    public void setInfo(GroupInfoModel info) {
        this.info = info;
    }

    public GroupMessageModel[] getMessages() {
        return messages;
    }

    public void setMessages(GroupMessageModel[] messages) {
        this.messages = messages;
    }
}
