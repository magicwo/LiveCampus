package com.uestc.magicwo.livecampus.models;

/**
 * Created by Magicwo on 2017/6/21.
 * 房间基本信息，不带拉流地址
 */

public class RoomBaseInfoResponse {

    private String rid;
    private String title;
    private String category;
    private String description;
    private String cover;
    private String status;
    private String uid;
    private String username;

    public String getCover() {
        return cover;
    }

    public String getDescription() {
        return description;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public String getCategory() {
        return category;
    }


    public String getRid() {
        return rid;
    }


    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public void setRid(String rid) {
        this.rid = rid;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
