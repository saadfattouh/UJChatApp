package com.example.ujchatapp;

import java.util.ArrayList;

public class GroupInfoModel {

    private String id;
    private String title;//done
    private String section;//done
    private String courseCode;//done
    private String instructorName;//done
    private String image;//done
    private String lastMessage;
    private String lastMessageSender;
    private String time;


    public GroupInfoModel(){}

    public GroupInfoModel(String id, String title, String section, String courseCode, String instructorName, String image) {
        this.id = id;
        this.title = title;
        this.section = section;
        this.courseCode = courseCode;
        this.instructorName = instructorName;
        this.image = image;
    }

    public GroupInfoModel(String id, String title, String image, String lastMessage, String lastMessageSender, String time, String instructorName) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.lastMessage = lastMessage;
        this.lastMessageSender = lastMessageSender;
        this.time = time;
        this.instructorName = instructorName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    public void setLastMessageSender(String lastMessageSender) {
        this.lastMessageSender = lastMessageSender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
