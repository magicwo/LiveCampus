package com.uestc.magicwo.livecampus.appbase;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.magicwo.com.magiclib.BroadcastReceiver.ConnectionChangeReceiver;
import com.magicwo.com.magiclib.base.BaseActivity;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.uestc.magicwo.livecampus.utils.ActivityManagement;


/**
 * Created by Magicwo on 2017/1/3.
 */

public class AppBaseActivity extends BaseActivity {
    public ActivityManagement activityManagement;
    private ConnectionChangeReceiver mNetworkStateReceiver;

    @Override
    protected void initVariables() {
//        ActivityManagement.getActivityManagement();


    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        activityManagement = ActivityManagement.getActivityManagement();
        activityManagement.addActivity(this);
        //注册网络状态的监听
        mNetworkStateReceiver = new ConnectionChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
    }

    @Override
    protected void loadData() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听
        unregisterReceiver(mNetworkStateReceiver);
        activityManagement.finishActivity(this);

    }

    /**
     * 初始化 Toolbar
     */
    public void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, String title) {
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }

    /**
     * @param backlayout 返回图标
     * @param canback    是否可返回
     */
    public void initToolBarCanBack(LinearLayout backlayout, boolean canback) {
        if (canback) {
            backlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else {
            backlayout.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(900);

        // 以对话框的形式弹出
        // PgyFeedbackShakeManager.register(this);
        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        FeedbackActivity.setBarImmersive(true);
        PgyFeedbackShakeManager.register(this, false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        PgyFeedbackShakeManager.unregister();

    }
}
