package com.uestc.magicwo.livecampusandroid;

import android.app.Application;



/**
 * Created by Magicwo on 2017/1/2.
 */
public class BaseApplication extends Application {
    public static String decodeType = "software";  //解码类型，默认软件解码
    public static String mediaType = "livestream";//媒体类型，默认网络直播

    @Override
    public void onCreate() {
        super.onCreate();
//


    }


}
