package com.example.ujchatapp;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserModel implements Serializable {

    String name, status, image, number, uID, online, typing, token, email, profileName, studentID, major, department;
    int type;

    public UserModel() {
    }

    public UserModel(String name, String status, String image, String number, String uID, String online, String typing, String token, String email, String profileName, String studentID, String major, String department, int type) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.number = number;
        this.uID = uID;
        this.online = online;
        this.typing = typing;
        this.token = token;
        this.email = email;
        this.profileName = profileName;
        this.studentID = studentID;
        this.major = major;
        this.department = department;
        this.type = type;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(CircleImageView view, String image) {
        Glide.with(view.getContext()).load(image).timeout(6000).into(view);
    }

    @BindingAdapter("imageChat")
    public static void loadImage(ImageView view, String image) {

        Glide.with(view.getContext()).load(image).into(view);

    }
}
