package com.uestc.magicwo.livecampusandroid.index;

/**
 * Created by Magicwo on 2017/5/20.
 * 直播信息类
 */

public class IndexResponse {

    private String uId;
    private String imageUrl;
    private String nickname;
    private String title;
    private String videoUrl;
    private int audience_num;

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuId() {
        return uId;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNickname() {
        return nickname;

    }

    public int getAudience_num() {
        return audience_num;
    }

    public String getTitle() {
        return title;
    }


    public void setAudience_num(int audience_num) {
        this.audience_num = audience_num;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
