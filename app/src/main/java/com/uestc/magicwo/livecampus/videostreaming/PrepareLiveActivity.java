package com.uestc.magicwo.livecampus.videostreaming;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.netease.LSMediaCapture.lsLogUtil;
import com.netease.LSMediaCapture.util.string.StringUtil;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.user.CertificateActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 直播准备Activity
 */
public class PrepareLiveActivity extends AppBaseActivity implements View.OnClickListener {
    @BindView(R.id.cover_image)
    ImageView coverImage;
    @BindView(R.id.select_pic_button)
    Button selectPicButton;
    @BindView(R.id.edit_text_title)
    EditText editTextTitle;
    @BindView(R.id.edit_text_description)
    EditText editTextDescription;
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

    public static final int REQUEST_CODE_SELECT = 100;
    private static final long ONE_KB = 1024;
    private static final long ONE_MB = 1024 * 1024;
    private ProgressDialog mProgressDialog;
    private String rid;
    private String description;
    private String title;
    private String cover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prepare_live);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pushUrl = intent.getExtras().getString("push_url");
        rid = intent.getExtras().getString("rid");
        description = intent.getExtras().getString("description");
        title = intent.getExtras().getString("title");
        cover = intent.getExtras().getString("cover");
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LiveStreamingStopFinished");
        registerReceiver(msgReceiver, intentFilter);
        publishParam = new PublishParam();
        initInfo();//直播间信息
        initButtons();
    }

    private void initInfo() {

        selectPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("-->", "从手机相册选择");
                Intent intent = new Intent(PrepareLiveActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
            }
        });
        Glide.with(this).load(cover).into(coverImage);
        editTextTitle.setText(title);
        editTextDescription.setText(description);

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
            setRoomInfo();

        } else if (id == imageScan.getId()) {

        }
    }

    /**
     * 设置直播间信息
     */
    private void setRoomInfo() {

        final HashMap<String, String> params = new HashMap<>();
        params.put("rid", rid);
        params.put("title", editTextTitle.getText().toString());
        params.put("category", "直播");
        params.put("description", editTextDescription.getText().toString());
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.UPDATEROOMS)
                .tag(this)
                .upJson(jsonObject)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        showToast("房间信息设置完毕");
                        startLive();
                    }


                });

    }

    /**
     * 开始进入直播
     */
    private void startLive() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                Log.d("---------", "data=" + data.toString());
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ArrayList<File> imageFiles = new ArrayList<>();
                for (ImageItem imageItem : images) {
                    imageFiles.add(new File(imageItem.path));
                }
                Glide.with(this).load(imageFiles.get(0)).into(coverImage);
                upLoad(imageFiles);
            }
        }
    }

    /**
     * 上传图片
     *
     * @param imageFiles
     */
    private void upLoad(ArrayList<File> imageFiles) {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false); // 进度条是否不明确
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("正在上传图片");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 进度条为长方形
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO 设置进度条取消事件
            }
        });
        //TODO 补充onError事件
        mProgressDialog.show();

        OkGo.post(Urls.COVER)
                .tag(this)
                .isMultipart(true)//FIXME isMultipart参数有待商榷
                .params("rid", rid)
                .addFileParams("cover", imageFiles)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        int displayProgress = (int) (progress * 100);
                        mProgressDialog.setProgress(displayProgress);

                        mProgressDialog.setProgressNumberFormat(refreshSpeed(networkSpeed));
                        if (displayProgress >= 100) {
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        Log.d("----------", "图片上传完毕");
                        showToast("封面照片上传成功");

                    }
                });


    }

    private String refreshSpeed(long networkSpeed) {
        StringBuffer speed = new StringBuffer(10);
        if (networkSpeed > ONE_MB) {
            speed.append(String.format("%.2f", 1.0 * networkSpeed / ONE_MB) + "M");
        } else if (networkSpeed > ONE_KB) {
            speed.append(String.format("%.2f", 1.0 * networkSpeed / ONE_KB) + "K");
        } else {
            speed.append(String.valueOf(networkSpeed) + "B");
        }
        speed.append("/s");
        return speed.toString();
    }
}
