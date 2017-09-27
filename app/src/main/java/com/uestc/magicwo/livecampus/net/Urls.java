package com.uestc.magicwo.livecampus.net;


/**
 * 用于存储app的api接口
 */
public class Urls {
    public static final String SERVER = "http://120.24.54.60:8080";

    /**
     * 用户登录
     */
    public static final String LOGIN = SERVER + "/pass/login";
    /**
     * 用户注册
     */
    public static final String SIGN = SERVER + "/pass/sign";
    /**
     * 获取房间的信息
     */
    public static final String ROOMINFO = SERVER + "/room/roominfo";
    /**
     * 创建房间
     */
    public static final String CREATEROOMS = SERVER + "/room/create";
    /**
     * 改变房间信息
     */
    public static final String UPDATEROOMS = SERVER + "/room/update";
    /**
     * 获取房间列表
     */
    public static final String ROOMLIST = SERVER + "/room/roomlist";

    /**
     * 上传学生证
     */
    public static final String CERTIFICATION = SERVER + "/user/certification/upload";

    /**
     * 直播间封面照片
     */
    public static final String COVER = SERVER + "/room/cover/upload";

    /**
     * 获取用户的信息
     */
    public static final String USERINFO = SERVER + "/user/userinfo";
    /**
     * 获取正在直播的房间列表
     */
    public static final String ONLINEROOMS = SERVER + "/room/onlineroomlist";
    /**
     * 修改用户信息
     */
    public static final String UPDATEINFO = SERVER + "/user/info/update";
}

