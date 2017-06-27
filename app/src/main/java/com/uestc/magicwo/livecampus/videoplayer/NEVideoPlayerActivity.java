package com.uestc.magicwo.livecampus.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.neliveplayer.NELivePlayer;
import com.netease.neliveplayer.NEMediaPlayer;
import com.uestc.magicwo.livecampus.R;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.yunba.android.manager.YunBaManager;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class NEVideoPlayerActivity extends Activity {
    public final static String TAG = "PlayerActivity";
    public NEVideoView mVideoView;  //用于画面显示


    private View mLoadingView; //用于指示缓冲状态
    private NEMediaController mMediaController; //用于控制播放

    public static final int NELP_LOG_UNKNOWN = 0; //!< log输出模式：输出详细
    public static final int NELP_LOG_DEFAULT = 1; //!< log输出模式：输出详细
    public static final int NELP_LOG_VERBOSE = 2; //!< log输出模式：输出详细
    public static final int NELP_LOG_DEBUG = 3; //!< log输出模式：输出调试信息
    public static final int NELP_LOG_INFO = 4; //!< log输出模式：输出标准信息
    public static final int NELP_LOG_WARN = 5; //!< log输出模式：输出警告
    public static final int NELP_LOG_ERROR = 6; //!< log输出模式：输出错误
    public static final int NELP_LOG_FATAL = 7; //!< log输出模式：一些错误信息，如头文件找不到，非法参数使用
    public static final int NELP_LOG_SILENT = 8; //!< log输出模式：不输出

    private String mVideoPath; //文件路径
    private String mDecodeType;//解码类型，硬解或软解
    private String mMediaType; //媒体类型
    private boolean mHardware = true;
    private ImageButton mPlayBack;
    private TextView mFileName; //文件名称
    private String mTitle;
    private Uri mUri;
    private boolean pauseInBackgroud = false;

    private RelativeLayout mPlayToolbar;
    private LinearLayout sendTextLayout;
    private EditText editText;
    private Button buttonConfirm;
    private String rid;

    NEMediaPlayer mMediaPlayer = new NEMediaPlayer();

    //弹幕相关
    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;
    private boolean showDanmaku = true;//是否显示弹幕
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };//弹幕解析器

    private PushMessageReceiver pushMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        //接收MainActivity传过来的参数
        mMediaType = getIntent().getStringExtra("media_type");
        mDecodeType = getIntent().getStringExtra("decode_type");
        mVideoPath = getIntent().getStringExtra("videoPath");
        rid = getIntent().getStringExtra("Rid");

        initDanmakus();//初始化弹幕相关
        registerReceiver();

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
        }

        if (mDecodeType.equals("hardware")) {
            mHardware = true;
        } else if (mDecodeType.equals("software")) {
            mHardware = false;
        }

        mPlayBack = (ImageButton) findViewById(R.id.player_exit);//退出播放
        mPlayBack.getBackground().setAlpha(0);
        mFileName = (TextView) findViewById(R.id.file_name);
        mUri = Uri.parse(mVideoPath);
        if (mUri != null) { //获取文件名，不包括地址
            List<String> paths = mUri.getPathSegments();
            String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
            setFileName(name);
        }

        mPlayToolbar = (RelativeLayout) findViewById(R.id.play_toolbar);
        sendTextLayout = (LinearLayout) findViewById(R.id.comment_layout);
        editText = (EditText) findViewById(R.id.editText);
        buttonConfirm = (Button) findViewById(R.id.button_send);
        View.OnFocusChangeListener mFocusChangedListener;
        mFocusChangedListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
            }
        };

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(this.toString(), "before");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(this.toString(), "on");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(this.toString(), "after");
                if (editText.getText().toString().length() <= 0) {
                    buttonConfirm.setVisibility(View.INVISIBLE);
                } else {
                    buttonConfirm.setVisibility(View.VISIBLE);
                }
            }
        });
        buttonConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText(editText.getText().toString());
            }
        });

        mPlayToolbar.setVisibility(View.INVISIBLE);
        sendTextLayout.setVisibility(View.INVISIBLE);

        mLoadingView = findViewById(R.id.buffering_prompt);
        mMediaController = new NEMediaController(this);

        mVideoView = (NEVideoView) findViewById(R.id.video_view);

        if (mMediaType.equals("livestream")) {
            mVideoView.setBufferStrategy(NELivePlayer.NELPLOWDELAY); //直播低延时
        } else {
            mVideoView.setBufferStrategy(NELivePlayer.NELPANTIJITTER); //点播抗抖动
        }
        mVideoView.setMediaController(mMediaController);
        mVideoView.setBufferingIndicator(mLoadingView);
        mVideoView.setMediaType(mMediaType);
        mVideoView.setHardwareDecoder(mHardware);
        mVideoView.setPauseInBackground(pauseInBackgroud);
        mVideoView.setVideoPath(mVideoPath);
        mMediaPlayer.setLogLevel(NELP_LOG_SILENT); //设置log级别
        mVideoView.requestFocus();
        mVideoView.start();

        mPlayBack.setOnClickListener(mOnClickEvent); //监听退出播放的事件响应
        mMediaController.setOnShownListener(mOnShowListener); //监听mediacontroller是否显示
        mMediaController.setOnHiddenListener(mOnHiddenListener); //监听mediacontroller是否隐藏
        mMediaController.setFileName("直播");
    }

    private void registerReceiver() {
        //订阅topic
        YunBaManager.start(getApplicationContext());
        YunBaManager.subscribe(getApplicationContext(), rid,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("-------------->", "成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "Subscribe failed with error code : " + ex.getReasonCode();
                            Log.e("-------------->", msg);
                        }

                    }
                }
        );
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("io.yunba.android.MESSAGE_RECEIVED_ACTION");
        intentFilter.addCategory(getPackageName());
        pushMessageReceiver = new PushMessageReceiver();
        registerReceiver(pushMessageReceiver, intentFilter);//注册广播监听
    }

    private void sendText(final String text) {
        YunBaManager.publish(getApplicationContext(), rid, text,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("-------------->", "发送弹幕成功");
//                        addDanmaku(text, true);
                        clearText();

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "publish failed with error code : " + ex.getReasonCode();
                            Log.e("-------------->", msg);

                        }
                    }
                }
        );


    }

    private void clearText() {
        if (editText != null)
            editText.setText("");
    }

    public class PushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

                String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
                String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
                Log.e("-------------->", msg);
                addDanmaku(msg, false);
            }

        }
    }

    //弹幕相关从此开始
    private void initDanmakus() {
        danmakuView.enableDanmakuDrawingCache(true);//设置缓存
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
//                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //弹幕相关到此结束

    OnClickListener mOnClickEvent = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_exit) {
                mVideoView.release_resource();
                onDestroy();
                finish();
            }
        }
    };

    NEMediaController.OnShownListener mOnShowListener = new NEMediaController.OnShownListener() {

        @Override
        public void onShown() {
            mPlayToolbar.setVisibility(View.VISIBLE);
            mPlayToolbar.requestLayout();
            sendTextLayout.setVisibility(View.VISIBLE);
            sendTextLayout.requestLayout();
            mVideoView.invalidate();
            mPlayToolbar.postInvalidate();
            sendTextLayout.postInvalidate();

        }
    };

    NEMediaController.OnHiddenListener mOnHiddenListener = new NEMediaController.OnHiddenListener() {

        @Override
        public void onHidden() {
            mPlayToolbar.setVisibility(View.INVISIBLE);
//            sendTextLayout.setVisibility(View.INVISIBLE);
        }
    };

    public void setFileName(String name) { //设置文件名并显示出来
        mTitle = name;
        if (mFileName != null)
            mFileName.setText(mTitle);

        mFileName.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "NEVideoPlayerActivity onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "NEVideoPlayerActivity onPause");

        if (pauseInBackgroud)
            mVideoView.pause(); //锁屏时暂停
        super.onPause();
        //弹幕暂停
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "PlayerActivity onDestroy");
        mVideoView.release_resource();
        super.onDestroy();
        showDanmaku = false;
        //释放弹幕资源
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
        if (pushMessageReceiver != null)
            unregisterReceiver(pushMessageReceiver);
        YunBaManager.unsubscribe(this, rid,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "Subscribe failed with error code : " + ex.getReasonCode();
                        }

                    }
                }
        );
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "NEVideoPlayerActivity onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "NEVideoPlayerActivity onResume");
        if (pauseInBackgroud && !mVideoView.isPaused()) {
            mVideoView.start(); //锁屏打开后恢复播放
        }
        super.onResume();
        //弹幕恢复
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "NEVideoPlayerActivity onRestart");
        super.onRestart();
    }

}