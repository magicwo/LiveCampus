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


}

