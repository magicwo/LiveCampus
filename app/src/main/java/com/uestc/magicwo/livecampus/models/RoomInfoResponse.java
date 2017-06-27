package com.uestc.magicwo.livecampus.models;

/**
 * Created by Magicwo on 2017/6/21.
 * 房间信息，带拉流和推流地址
 */

public class RoomInfoResponse {

    private String rid;
    private String title;
    private String category;
    private String status;
    private String pushUrl;
    private String httpPullUrl;
    private String hlsPullUrl;
    private String rtmpPullUrl;
    private String uid;
    private String username;
    private String description;
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
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

    public String getHlsPullUrl() {
        return hlsPullUrl;
    }

    public String getHttpPullUrl() {
        return httpPullUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public String getRid() {
        return rid;
    }

    public String getRtmpPullUrl() {
        return rtmpPullUrl;
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

    public void setHlsPullUrl(String hlsPullUrl) {
        this.hlsPullUrl = hlsPullUrl;
    }

    public void setHttpPullUrl(String httpPullUrl) {
        this.httpPullUrl = httpPullUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setRtmpPullUrl(String rtmpPullUrl) {
        this.rtmpPullUrl = rtmpPullUrl;
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
