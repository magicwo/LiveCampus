package com.uestc.magicwo.livecampus.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.yunba.android.manager.YunBaManager;


/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);

            //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message from server: ")
                    .append(YunBaManager.MQTT_TOPIC)
                    .append(" = ")
                    .append(topic)
                    .append(" ")
                    .append(YunBaManager.MQTT_MSG)
                    .append(" = ")

                .append(msg);
//            DemoUtil.showNotifation(context, topic, msg);
        }

    }
//	}
}
