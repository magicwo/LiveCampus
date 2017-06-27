package com.uestc.magicwo.livecampus.models;

import com.google.gson.Gson;

/**
 * Created by Magicwo on 2017/6/27.
 */

public class UserInfoResponse {


    /**
     * userID : 6
     * username : a
     * password : a
     * email :
     * phone :
     * sex :
     * age : 0
     * studentID :
     * registerTime :
     * signTime :
     * imgAvatar :
     * imgIDCard :
     */

    private String userID;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String sex;
    private int age;
    private String studentID;
    private String registerTime;
    private String signTime;
    private String imgAvatar;
    private String imgIDCard;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(String imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public String getImgIDCard() {
        return imgIDCard;
    }

    public void setImgIDCard(String imgIDCard) {
        this.imgIDCard = imgIDCard;

    }
}
