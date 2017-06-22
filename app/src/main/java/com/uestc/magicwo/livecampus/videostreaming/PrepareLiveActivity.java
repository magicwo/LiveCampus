package com.uestc.magicwo.livecampus.videostreaming;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.LSMediaCapture.lsLogUtil;
import com.netease.LSMediaCapture.util.string.StringUtil;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播准备Activity
 */
public class PrepareLiveActivity extends AppBaseActivity implements View.OnClickListener {
    private PublishParam publishParam = null;
    private TextView editMainPushUrl = null;
    private EditText editBitrate = null;
    private EditText editFps = null;
    private RadioGroup resoulationGroup = null, filterGroup = null;
    private RadioButton radioBtnHD, radioBtnSD, radioBtnLD;
    private RadioButton radioNormal = null, radioFacebeauty = null;
    private TextView txtZoom = null, txtUsingFilter = null, txtWatermark = null, txtFlash = null, txtQos = null, txtGraffiti = null;
    private Button mainStartBtn = null;
    private ImageView imageScan = null;


    private MsgReceiver msgReceiver = null;
    private String pushUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prepare_live);
        Intent intent = getIntent();
        pushUrl = intent.getExtras().getString("push_url");
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LiveStreamingStopFinished");
        registerReceiver(msgReceiver, intentFilter);
        publishParam = new PublishParam();
        initButtons();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        msgReceiver = null;
        super.onDestroy();
    }

    private void switchTo(TextView view, boolean on) {
        view.setTag(((Boolean) on).toString());
        if (on) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == txtWatermark.getId()) {
            if (Boolean.parseBoolean(txtWatermark.getTag().toString())) {
                switchTo(txtWatermark, false);
                publishParam.watermark = false;
            } else {
                switchTo(txtWatermark, true);
                publishParam.watermark = true;
            }
        } else if (id == txtFlash.getId()) {
            if (Boolean.parseBoolean(txtFlash.getTag().toString())) {
                switchTo(txtFlash, false);
                publishParam.flashEnable = false;
            } else {
                switchTo(txtFlash, true);
                publishParam.flashEnable = true;
            }
        } else if (id == txtQos.getId()) {
            if (Boolean.parseBoolean(txtQos.getTag().toString())) {
                switchTo(txtQos, false);
                publishParam.qosEnable = false;
            } else {
                switchTo(txtQos, true);
                publishParam.qosEnable = true;
            }
        } else if (id == txtGraffiti.getId()) {
            if (Boolean.parseBoolean(txtGraffiti.getTag().toString())) {
                switchTo(txtGraffiti, false);
                publishParam.graffitiOn = false;
            } else {
                switchTo(txtGraffiti, true);
                publishParam.graffitiOn = true;
            }
        } else if (id == txtUsingFilter.getId()) {
            if (Boolean.parseBoolean(txtUsingFilter.getTag().toString())) {
                switchTo(txtUsingFilter, false);
                filterGroup.setVisibility(View.GONE);
                publishParam.useFilter = false;
            } else {
                switchTo(txtUsingFilter, true);
                filterGroup.setVisibility(View.VISIBLE);
                publishParam.useFilter = true;
            }
        } else if (id == txtZoom.getId()) {
            if (Boolean.parseBoolean(txtZoom.getTag().toString())) {
                switchTo(txtZoom, false);
                publishParam.zoomed = false;
            } else {
                switchTo(txtZoom, true);
                publishParam.zoomed = true;
            }
        } else if (id == mainStartBtn.getId()) {
            publishParam.pushUrl = ((TextView) findViewById(R.id.main_push_url)).getText().toString();
            publishParam.bitrate = Long.parseLong(((EditText) findViewById(R.id.main_bitrate)).getText().toString());
            publishParam.fps = Long.parseLong(((EditText) findViewById(R.id.main_fps)).getText().toString());


            if (StringUtil.isEmpty(publishParam.pushUrl) || !publishParam.pushUrl.contains(".live.126.net")) {
                Toast.makeText(getApplication(), "请先设置正确的推流地址", Toast.LENGTH_LONG).show();
            } else {
                Log.d("PrepareLiveActivity", "publish param : " + publishParam);
                Intent intent = new Intent(PrepareLiveActivity.this, CameraPreviewActivity.class);
                intent.putExtra("data", publishParam);
                startActivity(intent);
            }
        } else if (id == imageScan.getId()) {

        }
    }

    private void initButtons() {
        editMainPushUrl = (TextView) findViewById(R.id.main_push_url);
        //FIXME:发布时删除测试
        lsLogUtil.debug = true;
        editMainPushUrl.setText(pushUrl);

        editBitrate = (EditText) findViewById(R.id.main_bitrate);
        editFps = (EditText) findViewById(R.id.main_fps);
        radioBtnHD = (RadioButton) findViewById(R.id.main_resolution_high);
        radioBtnSD = (RadioButton) findViewById(R.id.main_resolution_stand);
        radioBtnLD = (RadioButton) findViewById(R.id.main_resolution_low);
        radioNormal = (RadioButton) findViewById(R.id.main_filter_noraml);
        radioFacebeauty = (RadioButton) findViewById(R.id.main_filter_beautiful);
        resoulationGroup = (RadioGroup) findViewById(R.id.main_radiogroup_resolution);
        filterGroup = (RadioGroup) findViewById(R.id.main_filter_radiogroup);
        filterGroup.setVisibility(View.GONE);

        (txtWatermark = (TextView) findViewById(R.id.main_water)).setOnClickListener(this);
        (txtFlash = (TextView) findViewById(R.id.main_flash)).setOnClickListener(this);
        (txtQos = (TextView) findViewById(R.id.main_qos)).setOnClickListener(this);
        (txtGraffiti = (TextView) findViewById(R.id.main_graffiti)).setOnClickListener(this);
        (mainStartBtn = (Button) findViewById(R.id.main_start)).setOnClickListener(this);
        (txtZoom = (TextView) findViewById(R.id.main_focus)).setOnClickListener(this);
        (txtUsingFilter = (TextView) findViewById(R.id.main_use_filter)).setOnClickListener(this);
        resoulationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == radioBtnHD.getId()) {
                    publishParam.definition = "HD";
                } else if (radioButtonId == radioBtnSD.getId()) {
                    publishParam.definition = "SD";
                } else if (radioButtonId == radioBtnLD.getId()) {
                    publishParam.definition = "LD";
                }
            }
        });

        filterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == radioNormal.getId()) {
                    publishParam.faceBeauty = false;
                } else if (radioButtonId == radioFacebeauty.getId()) {
                    publishParam.faceBeauty = true;
                }
            }
        });


    }

    //用于接收Service发送的消息
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int value = intent.getIntExtra("LiveStreamingStopFinished", 0);
            if (value == 1)//finished
            {
                mainStartBtn.setEnabled(true);
                mainStartBtn.setText("进入直播");
            } else//not yet finished
            {
                mainStartBtn.setEnabled(false);
                mainStartBtn.setText("直播停止中...");
            }
        }
    }


}
