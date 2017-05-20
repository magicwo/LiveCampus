package com.magicwo.com.magiclib.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by magicwo on 2016/10/22.
 * 实时监测网络连接状态
 * 在activity中/注册网络监听
 * ConnectionChangeReceiver mNetworkStateReceiver = new ConnectionChangeReceiver();
 * IntentFilter filter = new IntentFilter();
 * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
 * registerReceiver(mNetworkStateReceiver, filter);
 * <p>
 * 在onDestroy()中销毁广播
 * unregisterReceiver(mNetworkStateReceiver); //取消监听
 * 需要网络访问权限
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            Toast.makeText(context, "当前网络已断开", Toast.LENGTH_SHORT).show();
            //改变背景或者 处理网络的全局变量
        } else {
//            Toast.makeText(context, "当前网络已连接", Toast.LENGTH_SHORT).show();
            //改变背景或者 处理网络的全局变量
        }
    }
}
