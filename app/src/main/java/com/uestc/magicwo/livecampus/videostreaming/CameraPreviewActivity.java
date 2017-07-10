package com.uestc.magicwo.livecampus.videostreaming;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.LSMediaCapture.Statistics;
import com.netease.LSMediaCapture.lsLogUtil;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.LSMediaCapture.view.NeteaseGLSurfaceView;
import com.netease.LSMediaCapture.view.NeteaseSurfaceView;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.utils.ScreenUtil;
import com.uestc.magicwo.livecampus.videoplayer.NEVideoPlayerActivity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.yunba.android.manager.YunBaManager;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;


public class CameraPreviewActivity extends Activity implements OnClickListener, OnSeekBarChangeListener, lsMessageHandler {

    private ImageView btnNetInfo = null;
    private ImageView btnFlash = null;
    private ImageView btnCancel = null;
    private SeekBar seekBar = null;
    private ImageView btnRestart = null;
    private ImageView btnAudio = null;
    private ImageView btnVideo = null;
    private ImageView btnScreen = null;
    private ImageView btnMirror = null;
    private ImageView btnStart = null;
    private ImageView btnCamSwitch = null;
    private ImageView btnFilter = null;
    private ImageView btnMusic = null;
    private ImageView btnSpeedCalc = null;
    private TextView txtResult = null;

    private LinearLayout layoutUp = null;

    private PublishParam mPublishParam = null;

    private lsMediaCapture mLSMediaCapture = null;
    private String mliveStreamingURL = null;
    private String mMixAudioFilePath = null;
    private File mMP3AppFileDirectory = null;
    private String mVideoResolution = null;
    private boolean mUseFilter = false;

    //SDK统计相关变量
    private LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
    private Statistics mStatistics = null;

    //普通模式的view
    private NeteaseSurfaceView mVideoView;

    //滤镜模式的view
    private NeteaseGLSurfaceView mSurfaceView;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_GET_STATICS_INFO: {
                    Bundle bundle = message.getData();
                    int videoFrameRate = bundle.getInt("FR");
                    int videoBitrate = bundle.getInt("VBR");
                    int audioBitrate = bundle.getInt("ABR");
                    int totalRealBitrate = bundle.getInt("TBR");

                    try {
                        if (mNetInfoIntent != null) {
                            mNetInfoIntent.putExtra("videoFrameRate", videoFrameRate);
                            mNetInfoIntent.putExtra("videoBitRate", videoBitrate);
                            mNetInfoIntent.putExtra("audioBitRate", audioBitrate);
                            mNetInfoIntent.putExtra("totalRealBitrate", totalRealBitrate);

                            if (mLSLiveStreamingParaCtx.sLSVideoParaCtx.width == 1280 && mLSLiveStreamingParaCtx.sLSVideoParaCtx.height == 720) {
                                mNetInfoIntent.putExtra("resolution", 1);
                            } else if (mLSLiveStreamingParaCtx.sLSVideoParaCtx.width == 640 && mLSLiveStreamingParaCtx.sLSVideoParaCtx.height == 480) {
                                mNetInfoIntent.putExtra("resolution", 2);
                            } else if (mLSLiveStreamingParaCtx.sLSVideoParaCtx.width == 320 && mLSLiveStreamingParaCtx.sLSVideoParaCtx.height == 240) {
                                mNetInfoIntent.putExtra("resolution", 3);
                            }
                        }
                    } catch (IllegalStateException e) {

                    }

                    sendBroadcast(mNetInfoIntent);
                }
                break;
                case MSG_SPEED_CALC_FAIL: {
                    showToast("测速失败");
                    speedCalcOn = false;
                }
                break;
                case MSG_SPEED_CALC_SUCCESS: {
                    String txt = (String) message.obj;
                    txtResult.setText(txt);
                    showToast("测速成功");
                    speedCalcOn = false;
                }
                case MSG_BAD_NETWORK_DETECT: {
                    showToast("网络较差");
                }
                break;
                case MSG_URL_FORMAT_NOT_RIGHT: {
                    showToast("推流url格式不正确");
                }
                break;
            }
            return false;
        }
    });
    //状态变量
    private boolean m_liveStreamingOn = false;
    private boolean m_liveStreamingInit = false;
    private boolean m_liveStreamingInitFinished = false;
    private boolean m_tryToStopLivestreaming = false;
    private boolean m_startVideoCamera = false;


    //伴音相关
    private AudioManager mAudioManager;
    private float m_mixAudioVolume = 0.2f;

    //Activity环境变量
    private Context mContext;

    private int mVideoPreviewWidth, mVideoPreviewHeight;

    private Intent mNetInfoIntent = new Intent(NetWorkInfoDialog.NETINFO_ACTION);
    private Intent mIntentLiveStreamingStopFinished = new Intent("LiveStreamingStopFinished");


    private long mLastVideoProcessErrorAlertTime = 0;
    private long mLastAudioProcessErrorAlertTime = 0;

    private boolean mHardWareEncEnable = false;

    //视频涂鸦相关变量
    private boolean mGraffitiOn = false;//视频涂鸦开关，默认关闭，需要视频涂鸦的用户可以开启此开关
    private String mGraffitiFilePath;//视频涂鸦文件路径
    private String mGraffitiFileName = "vcloud1.bmp";//视频涂鸦文件名
    private File mGraffitiAppFileDirectory = null;
    private int mGraffitiPosX = 0;
    private int mGraffitiPosY = 0;

    //视频水印相关变量
    private Bitmap mBitmap;
    private boolean mWaterMarkOn = false;//视频水印开关，默认关闭，需要视频水印的用户可以开启此开关
    private String mWaterMarkFilePath;//视频水印文件路径
    private String mWaterMarkFileName = "logo.png";//视频水印文件名
    private File mWaterMarkAppFileDirectory = null;
    private int mWaterMarkPosX = 10;//视频水印坐标(X)
    private int mWaterMarkPosY = 10;//视频水印坐标(Y)

    //视频截图相关变量
    private String mScreenShotFilePath = "/sdcard/";//视频截图文件路径
    private String mScreenShotFileName = "test.jpg";//视频截图文件名

    //查询摄像头支持的采集分辨率信息相关变量
    private Thread mCameraThread;
    private Looper mCameraLooper;
    private int mCameraID = CAMERA_POSITION_BACK;//默认查询的是后置摄像头
    private Camera mCamera;

    private float mCurrentDistance;
    private float mLastDistance = -1;

    public static final int CAMERA_POSITION_BACK = 0;
    public static final int CAMERA_POSITION_FRONT = 1;

    public static final int LS_VIDEO_CODEC_AVC = 0;
    public static final int LS_VIDEO_CODEC_VP9 = 1;
    public static final int LS_VIDEO_CODEC_H265 = 2;

    public static final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
    public static final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;

    public static final int LS_AUDIO_CODEC_AAC = 0;
    public static final int LS_AUDIO_CODEC_SPEEX = 1;
    public static final int LS_AUDIO_CODEC_MP3 = 2;
    public static final int LS_AUDIO_CODEC_G711A = 3;
    public static final int LS_AUDIO_CODEC_G711U = 4;

    public static final int FLV = 0;
    public static final int RTMP = 1;
    public static final int RTMP_AND_FLV = 2;


    public static final int HAVE_AUDIO = 0;
    public static final int HAVE_VIDEO = 1;
    public static final int HAVE_AV = 2;

    public static final int OpenQoS = 0;
    public static final int CloseQoS = 1;

    private boolean flashOn = false;
    private boolean audioRunning = true;
    private boolean videoRunning = true;
    private boolean mirrorOn = false;
    private boolean speedCalcOn = false;


    public String BC_ACTION_AUDIOMIX = "AudioMix";
    public String BC_ACTION_AUDIOMIXVOLUME = "AudioMixVolume";
    private BroadcastMsgReceiver broadcastMsgReceiver = new BroadcastMsgReceiver();
    private static final String TAG = "NeteaseLiveStream";

    private String rid = "";

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

    private PushMessageReceiver2 pushMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPublishParam = (PublishParam) getIntent().getSerializableExtra("data");
        //  注册广播
        broadcastMsgReceiver.register();

//        ScreenUtil.getInstance(this).setEffetSysSetting(true);
        //  应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //从直播设置页面获取推流URL和分辨率信息
        //alert1用于检测设备SDK版本是否符合开启滤镜要求，alert2用于检测设备的硬件编码模块（与滤镜相关）是否正常
        mliveStreamingURL = mPublishParam.pushUrl;
        mVideoResolution = mPublishParam.definition;
        mUseFilter = mPublishParam.useFilter;
        mWaterMarkOn = mPublishParam.watermark;
        mGraffitiOn = mPublishParam.graffitiOn;
        //获取摄像头支持的分辨率信息，根据这个信息，用户可以选择合适的视频preview size和encode size
        //!!!!注意，用户在设置视频采集分辨率和编码分辨率的时候必须预先获取摄像头支持的采集分辨率，并设置摄像头支持的采集分辨率，否则会造成不可预知的错误，导致直播推流失败和SDK崩溃
        //用户可以采用下面的方法(getCameraSupportSize)，获取摄像头支持的采集分辨率列表
        //获取分辨率之后，用户可以按照下表选择性设置采集分辨率和编码分辨率(注意：一定要Android设备支持该种采集分辨率才可以设置，否则会造成不可预知的错误，导致直播推流失败和SDK崩溃)

        //采集分辨率     编码分辨率     建议码率
        //1280x720     1280x720     1500kbps
        //1280x720     960x540      800kbps
        //960x720      960x720      1000kbps
        //960x720      960x540      800kbps
        //960x540      960x540      800kbps
        //640x480      640x480      600kbps
        //640x480      640x360      500kbps
        //320x240      320x240      250kbps
        //320x240      320x180      200kbps


        //用户需要分别检查前后摄像头支持的分辨率，设置前后摄像头都支持的分辨率，保障在切换摄像头的过程中不会出错
//        List<Camera.Size> backCameraSupportSize = getCameraSupportSize(CAMERA_POSITION_BACK);
//        List<Camera.Size> frontCameraSupportSize = getCameraSupportSize(CAMERA_POSITION_FRONT);
//
//        if (backCameraSupportSize != null && backCameraSupportSize.size() != 0) {
//			for (Size size : backCameraSupportSize) {
//				Log.i(TAG, "backCameraSupportSize " + " is " + size.width + "x" + size.height);
//			}
//		}
//
//        if (frontCameraSupportSize != null && frontCameraSupportSize.size() != 0) {
//			for (Size size : frontCameraSupportSize) {
//				Log.i(TAG, "frontCameraSupportSize " + " is " + size.width + "x" + size.height);
//			}
//		}
        if (mVideoResolution.equals("HD")) {
            mVideoPreviewWidth = 1280;
            mVideoPreviewHeight = 720;
        } else if (mVideoResolution.equals("SD")) {
            mVideoPreviewWidth = 640;
            mVideoPreviewHeight = 480;
        } else {
            mVideoPreviewWidth = 320;
            mVideoPreviewHeight = 240;
        }

        m_liveStreamingOn = false;
        m_tryToStopLivestreaming = false;

        mContext = this;

        //伴音相关操作，获取设备音频播放service
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //拷贝MP3文件到APP目录
        handleMP3();

        //以下为SDK调用主要步骤，请用户参考使用
        //1、创建直播实例
        lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
        lsMediaCapturePara.Context = getApplicationContext();
        lsMediaCapturePara.lsMessageHandler = this;
        lsMediaCapturePara.videoPreviewWidth = mVideoPreviewWidth;
        lsMediaCapturePara.videoPreviewHeight = mVideoPreviewHeight;
        lsMediaCapturePara.useFilter = mUseFilter;
        lsMediaCapturePara.logLevel = lsLogUtil.LogLevel.INFO;  //设置日志级别
        lsMediaCapturePara.uploadLog = true; //是否上传SDK日志
        mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara, mliveStreamingURL);
        mLSMediaCapture.setCameraAutoFocus(true);

        maxZoomValue = mLSMediaCapture.getCameraMaxZoomValue();
        //2、设置直播参数
        paraSet();

        mLSLiveStreamingParaCtx.sLSQoSParaCtx.qosType = (mPublishParam.qosEnable ? OpenQoS : CloseQoS);

        setContentView(R.layout.activity_livestreaming);
        ButterKnife.bind(this);

        initDanmakus();//初始化弹幕相关
        registerReceiver();

        //3、根据是否使用滤镜模式和推流方式（音视频或者音频或者视频）选择surfaceview类型
        if (mUseFilter && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
            if (mVideoView != null) {
                mVideoView.setVisibility(View.GONE);
            }
            mSurfaceView = (NeteaseGLSurfaceView) findViewById(R.id.live_filter_view);
            mSurfaceView.setVisibility(View.VISIBLE);
        } else {
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(View.GONE);
            }
            mVideoView = (NeteaseSurfaceView) findViewById(R.id.live_normal_view);
            mVideoView.setVisibility(View.VISIBLE);
        }

        //5、设置视频预览参数
        if (mUseFilter && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
            mSurfaceView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        } else {
            mVideoView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        }

        if (mLSMediaCapture != null) {
            //6、开启视频预览
            if (mUseFilter && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
                mLSMediaCapture.startVideoPreviewOpenGL(mSurfaceView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
                m_startVideoCamera = true;
            } else {
                mLSMediaCapture.startVideoPreview(mVideoView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
                m_startVideoCamera = true;
            }

            //【示例代码】设置自定义视频采集类型
//			mLSMediaCapture.setSourceType(lsMediaCapture.SourceType.CustomVideo);
//			//如果是文件输入宽大于高，则设置成LANDSCAPE，反之为PORTRAIT（自定义摄像头输入根据摄像头方向自定义设置）
//			mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = CAMERA_ORIENTATION_LANDSCAPE;
            //【示例代码】结束

            //8、初始化直播推流
//			resize();
        }

        //9、Demo控件的初始化（Demo层实现，用户不需要添加该操作）
        buttonInit();
        //【示例代码 customVideo】设置自定义视频采集逻辑 （自定义视频采集逻辑不要调用startPreview，也不用初始化surfaceView）
//		m_startVideoCamera = true;
//		startAV();
//		startPauseResumeBtn.setImageResource(R.drawable.pause);
//		new Thread() {  //视频采集线程
//			@Override
//			public void run() {
//				while (m_liveStreamingInit) {
//					try {
//						int width = 1024;
//						int height = 768;
//						FileInputStream in = new FileInputStream("/sdcard/dump_1024_768.yuv");
//						int len = width * height * 3 / 2;
//						byte buffer[] = new byte[len];
//						int count;
//						while ((count = in.read(buffer)) != -1) {
//							if (len == count) {
//								mLSMediaCapture.sendCustomYUVData(buffer,width,height);
//							} else {
//								break;
//							}
//							sleep(50, 0);
//						}
//						in.close();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
        //【示例代码】结束

        //【示例代码2】设置自定义音频采集逻辑（音频采样位宽必须是16）
//		m_startVideoCamera = true;
//		startAV();
//		startPauseResumeBtn.setImageResource(R.drawable.pause);
//		new Thread() {  //音频采集线程
//            @Override
//            public void run() {
//                while (m_liveStreamingInit) {
//                    try {
//                        FileInputStream in = new FileInputStream("/sdcard/dump.pcm");
//                        int len = 2048;
//                        byte buffer[] = new byte[len];
//                        int count;
//                        while ((count = in.read(buffer)) != -1) {
//                            if (len == count) {
//                                mLSMediaCapture.sendCustomPCMData(buffer);
//                            } else {
//                                break;
//                            }
//                            sleep(20, 0);
//                        }
//                        in.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
        //【示例代码】结束


        //10、给广播绑定响应的过滤器（Demo层实现，用户不需要添加该操作）

        //11、如果音视频直播或者视频直播，需要等待preview finish之后才能开始直播；如果音频直播，则无需等待preview finish，可以立即开始直播（Demo层实现，用户不需要添加该操作）
        if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
            btnStart.setClickable(false);
        } else if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AUDIO) {
            btnStart.setClickable(true);
        }

        if (!mPublishParam.zoomed) {
            seekBar.setVisibility(View.GONE);
        }

        btnRestart.setVisibility(View.GONE);
        layoutUp.setVisibility(View.GONE);
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
        pushMessageReceiver = new PushMessageReceiver2();
        registerReceiver(pushMessageReceiver, intentFilter);//注册广播监听
    }

    public class PushMessageReceiver2 extends BroadcastReceiver {

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

    @Override
    protected void onDestroy() {

        if (mSurfaceView != null) {
            mSurfaceView.setVisibility(View.GONE);
        }
        if (mVideoView != null) {
            mVideoView.setVisibility(View.GONE);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (m_liveStreamingInit) {
            m_liveStreamingInit = false;
        }

        broadcastMsgReceiver.unregister();

        //停止直播调用相关API接口
        if (mLSMediaCapture != null && m_liveStreamingOn) {

            //停止直播，释放资源
            mLSMediaCapture.stopLiveStreaming();

            //如果音视频或者单独视频直播，需要关闭视频预览
            if (m_startVideoCamera) {
                mLSMediaCapture.stopVideoPreview();
                mLSMediaCapture.destroyVideoPreview();
            }

            //反初始化推流实例，当它与stopLiveStreaming连续调用时，参数为false
            mLSMediaCapture.uninitLsMediaCapture(false);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 2);
            sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (mLSMediaCapture != null && m_startVideoCamera) {
            mLSMediaCapture.stopVideoPreview();
            mLSMediaCapture.destroyVideoPreview();

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (!m_liveStreamingInitFinished) {
            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            sendBroadcast(mIntentLiveStreamingStopFinished);

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
        }

        if (m_liveStreamingOn) {
            m_liveStreamingOn = false;
        }
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

        super.onDestroy();
    }

    protected void onPause() {
        if (mLSMediaCapture != null) {
            if (!m_tryToStopLivestreaming && m_liveStreamingOn) {
                if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
                    //推最后一帧图像
                    mLSMediaCapture.backgroundVideoEncode();
                } else if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AUDIO) {
                    //释放音频采集资源
                    mLSMediaCapture.backgroundAudioEncode();
                }
            }
        }
        //弹幕暂停
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
        super.onPause();
        ScreenUtil.getInstance(this).stop();
    }

    protected void onResume() {
        super.onResume();
        if (mLSMediaCapture != null) {
            if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
                //关闭推流固定图像，正常推流
                mLSMediaCapture.resumeVideoEncode();
            } else if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AUDIO) {
                //关闭推流静音帧
                mLSMediaCapture.resumeAudioEncode();
            }
        }
        //弹幕恢复
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
        ScreenUtil.getInstance(this).start(this);
    }

    //开始直播
    private void startAV() {
        if (mLSMediaCapture != null && m_liveStreamingInitFinished) {

            //7、设置视频水印参数（可选）
            if (mWaterMarkOn && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
                mLSMediaCapture.setWaterMarkPara(mWaterMarkFilePath, mWaterMarkPosX, mWaterMarkPosY);
            }

            //8、开始直播
            mLSMediaCapture.startLiveStreaming();
            m_liveStreamingOn = true;

            //9、设置视频涂鸦参数（可选）
            if (mGraffitiOn && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(mGraffitiFilePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                int mWidth = bitmap.getWidth();
                int mHeight = bitmap.getHeight();
                int[] mIntArray = new int[mWidth * mHeight];

                // Copy pixel data from the Bitmap into the 'intArray' array
                bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "error : ", e);
                }

                mLSMediaCapture.setGraffitiPara(mIntArray, mWidth, mHeight, mGraffitiPosX, mGraffitiPosY, mGraffitiOn);
            }
        }
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setFilterType(filter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_net_info: {
                showNetworkInfoDialog();
                break;
            }
            case R.id.live_flash: {
                flashOn = !flashOn;
                liveFlash(flashOn);
                break;
            }
            case R.id.live_cancel: {
                liveCancel();
                break;
            }
            case R.id.live_restart_btn: {
                liveRestart();
                break;
            }
            case R.id.live_audio_btn: {
                audioRunning = !audioRunning;
                livePauseAudio(audioRunning);
                if (audioRunning) {
                    showToast("继续音频");
                } else {
                    showToast("暂停音频");
                }

                break;
            }
            case R.id.live_video_btn: {
                videoRunning = !videoRunning;
                livePauseVideo(videoRunning);
                if (videoRunning) {
                    showToast("继续视频");
                } else {
                    showToast("暂停视频");
                }

                break;
            }
            case R.id.live_screen_btn: {
                liveScreenCapture();
                break;
            }
            case R.id.live_mirror_btn: {
                mirrorOn = !mirrorOn;
                liveMirror(mirrorOn);
                break;
            }
            case R.id.live_start_btn: {
                startStopLive();
                break;
            }
            case R.id.live_camera_btn: {
                switchCamera();
                break;
            }
            case R.id.live_filter_btn: {
                liveFilter();
                break;
            }
            case R.id.live_music_btn: {
                showMixAudioDialog();
                break;
            }
            case R.id.live_speedcalc_btn: {
                speedCalcOn = !speedCalcOn;
                liveSpeedCalc(speedCalcOn);
                break;
            }
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setCameraFocus();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    private int lastProgress = 0;
    private int maxZoomValue = 0;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (seekBar.getId() == this.seekBar.getId()) {
            if (maxZoomValue == 0) {
                maxZoomValue = mLSMediaCapture.getCameraMaxZoomValue();
            }
            int piece = 100 / maxZoomValue;
            if (progress - lastProgress >= piece) {    //  放大
                mLSMediaCapture.setCameraZoomPara(true);
                lastProgress = progress;
            } else if (lastProgress - progress >= piece) {
                mLSMediaCapture.setCameraZoomPara(false);
                lastProgress = progress;
            }
        } else {
            if (mLSMediaCapture != null) {
                mLSMediaCapture.setFilterStrength(progress);
            }
        }

    }

    //  显示网络信息
    private void showNetworkInfoDialog() {
        NetWorkInfoDialog dialog = new NetWorkInfoDialog(this);
        dialog.showAsDropDown(btnNetInfo);
    }

    //  开关闪光灯
    private void liveFlash(boolean on) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setCameraFlashPara(on);
        }
    }

    //  退出activity
    private void liveCancel() {
        CameraPreviewActivity.this.finish();
    }

    private void liveRestart() {

    }

    //  暂停/继续音频
    private void livePauseAudio(boolean on) {
        if (mLSMediaCapture != null) {
            if (on) {
                mLSMediaCapture.resumeAudioLiveStream();
            } else {
                mLSMediaCapture.pauseAudioLiveStream();
            }
        }
    }

    //  暂停/继续视频
    private void livePauseVideo(boolean on) {
        if (mLSMediaCapture != null) {
            if (on) {
                mLSMediaCapture.resumeVideoLiveStream();
            } else {
                mLSMediaCapture.pauseVideoLiveStream();
            }
        }
    }

    //  截屏
    private void liveScreenCapture() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.enableScreenShot();
        }
    }

    //  镜像
    private void liveMirror(boolean on) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setVideoMirror(on);
            String text = "打开镜像成功";
            if (!on) {
                text = "关闭镜像成功";
            }
            showToast(text);
        }
    }

    long clickTime = 0l;

    //  开始/停止直播
    private void startStopLive() {

        long time = System.currentTimeMillis();
        if (time - clickTime < 1000) {
            return;
        }

        btnStart.setClickable(false);
        if (!m_liveStreamingOn) {
            //8、初始化直播推流
            showToast("初始化直播中，请稍后。。。");
            m_liveStreamingInitFinished = mLSMediaCapture.initLiveStream(mLSLiveStreamingParaCtx);
            if (m_liveStreamingInitFinished) {
                startAV();
            } else {
                showToast("初始化直播失败,正在退出当前界面。。。");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraPreviewActivity.this.finish();
                    }
                }, 3000);
            }
            btnStart.setImageResource(R.drawable.pause);
        } else {
            showToast("停止直播中，请稍等。。。");
            mLSMediaCapture.stopLiveStreaming();
            btnStart.setImageResource(R.drawable.play);
        }
    }

    //切换前后摄像头
    private void switchCamera() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.switchCamera();
        }
    }

    //  滤镜
    private void liveFilter() {
        GPUImageFilterTools.showDialog(this, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {
            @Override
            public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                switchFilterTo(filter);
            }
        });
    }

    //  开始/停止测速
    private void liveSpeedCalc(boolean on) {
        if (mLSMediaCapture != null) {
            if (on) {
                mLSMediaCapture.startSpeedCalc(mPublishParam.pushUrl, 500 * 1024);
                showToast("开始测速");
            } else {
                mLSMediaCapture.stopSpeedCalc();
                showToast("停止测速");
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    //  伴音
    private void showMixAudioDialog() {
        MixAudioDialog dialog = new MixAudioDialog(this);
        dialog.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    //处理SDK抛上来的异常和事件，用户需要在这里监听各种消息，进行相应的处理。
    //例如监听断网消息，用户根据断网消息进行直播重连
    //注意：直播重连请使用restartLiveStream，在网络带宽较低导致发送码率帧率降低时，可以调用这个接口重启直播，改善直播质量
    //在网络断掉的时候（用户可以监听 MSG_RTMP_ URL_ERROR 和 MSG_BAD_NETWORK_DETECT ），用户不可以立即调用改接口，而是应该在网络重新连接以后，主动调用这个接口。
    //如果在网络没有重新连接便调用这个接口，直播将不会重启
    @Override
    public void handleMessage(int msg, Object object) {
        switch (msg) {
            case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
            case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:
            case MSG_INIT_LIVESTREAMING_AUDIO_ERROR: {
                showToast("初始化直播出错");
                break;
            }
            case MSG_START_LIVESTREAMING_ERROR://开始直播出错
            {
                showToast("开始直播出错：" + object);
                break;
            }
            case MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
            {
                if (m_liveStreamingOn) {
                    showToast("MSG_STOP_LIVESTREAMING_ERROR  停止直播出错");
                }
                break;
            }
            case MSG_AUDIO_PROCESS_ERROR://音频处理出错
            {
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime >= 10000) {
                    showToast("音频处理出错");
                    mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
                }

                break;
            }
            case MSG_VIDEO_PROCESS_ERROR://视频处理出错
            {
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000) {
                    showToast("视频处理出错");
                    mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
                }
                break;
            }
            case MSG_START_PREVIEW_ERROR://视频预览出错，可能是获取不到camera的使用权限
            {
                Log.i(TAG, "test: in handleMessage, MSG_START_PREVIEW_ERROR");
                showToast("无法打开相机，可能没有相关的权限");
                break;
            }
            case MSG_AUDIO_RECORD_ERROR://音频采集出错，获取不到麦克风的使用权限
            {
                showToast("无法开启；录音，可能没有相关的权限");
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_RECORD_ERROR");
                break;
            }
            case MSG_RTMP_URL_ERROR://断网消息
            {
                Log.i(TAG, "test: in handleMessage, MSG_RTMP_URL_ERROR");
                showToast("RTMP_URL_ERROR 推流已断开");
                break;
            }
            case MSG_URL_NOT_AUTH://直播URL非法，URL格式不符合视频云要求
            {
                showToast("MSG_URL_NOT_AUTH  直播地址不合法");
                break;
            }
            case MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_ERROR");
                break;
            }
            case MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_HEARTBEAT_LOG_ERROR");
                break;
            }
            case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
                break;
            }
            case MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
            {
                Log.i(TAG, "test: in handleMessage, MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
                break;
            }
            case MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_START_RECORDING_ERROR");
                break;
            }
            case MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，视频码率档次降到最低
            {
                showToast("MSG_QOS_TO_STOP_LIVESTREAMING");
                Log.i(TAG, "test: in handleMessage, MSG_QOS_TO_STOP_LIVESTREAMING");
                break;
            }
            case MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错反馈消息
            {
                showToast("视频硬件编码出错");
                break;
            }
            case MSG_WATERMARK_INIT_ERROR://视频水印操作初始化出错
            {
                break;
            }
            case MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR://视频水印图像超出原始视频出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR");
                break;
            }
            case MSG_WATERMARK_PARA_ERROR://视频水印参数设置出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PARA_ERROR");
                break;
            }
            case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
            {
                //Log.i(TAG, "test: in handleMessage: MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_START_PREVIEW_FINISHED://camera采集预览完成
            {
                Log.i(TAG, "test: MSG_START_PREVIEW_FINISHED");

                //如果是音视频直播或者视频直播，视频preview之后才允许开始直播
                if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
                    btnStart.setClickable(true);
                }
                break;
            }
            case MSG_START_LIVESTREAMING_FINISHED://开始直播完成
            {
                Log.i(TAG, "test: MSG_START_LIVESTREAMING_FINISHED");
                showToast("开始直播");
                m_liveStreamingOn = true;
                btnStart.setClickable(true);
                btnRestart.setVisibility(View.VISIBLE);
                layoutUp.setVisibility(View.VISIBLE);
                break;
            }
            case MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
            {
                Log.i(TAG, "test: MSG_STOP_LIVESTREAMING_FINISHED");
                showToast("停止直播已完成");
                m_liveStreamingOn = false;
                btnStart.setClickable(true);
                btnRestart.setVisibility(View.GONE);
                layoutUp.setVisibility(View.GONE);
                {
                    mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
                    sendBroadcast(mIntentLiveStreamingStopFinished);
                }

//				  mIntentLiveStreamingStopFinished = null;
//				  mStatistics = null;
//				  mHandler.removeCallbacksAndMessages(null);
//				  mHandler = null;

                break;
            }
            case MSG_STOP_VIDEO_CAPTURE_FINISHED: {
                Log.i(TAG, "test: in handleMessage: MSG_STOP_VIDEO_CAPTURE_FINISHED");
                break;
            }
            case MSG_STOP_AUDIO_CAPTURE_FINISHED: {
                Log.i(TAG, "test: in handleMessage: MSG_STOP_AUDIO_CAPTURE_FINISHED");
                break;
            }
            case MSG_SWITCH_CAMERA_FINISHED://切换摄像头完成
            {
                int cameraId = (Integer) object;//切换之后的camera id
                break;
            }
            case MSG_SEND_STATICS_LOG_FINISHED://发送统计信息完成
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_FINISHED");
                break;
            }
            case MSG_SERVER_COMMAND_STOP_LIVESTREAMING://服务器下发停止直播的消息反馈，暂时不使用
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SERVER_COMMAND_STOP_LIVESTREAMING");
                break;
            }
            case MSG_GET_STATICS_INFO://获取统计信息的反馈消息
            {
                //Log.i(TAG, "test: in handleMessage, MSG_GET_STATICS_INFO");

                Message message = Message.obtain(mHandler, MSG_GET_STATICS_INFO);
                mStatistics = (Statistics) object;

                Bundle bundle = new Bundle();
                bundle.putInt("FR", mStatistics.videoEncodeFrameRate);
                bundle.putInt("VBR", mStatistics.videoEncodeBitRate);
                bundle.putInt("ABR", mStatistics.audioEncodeBitRate);
                bundle.putInt("TBR", mStatistics.totalRealSendBitRate);
                message.setData(bundle);

                if (mHandler != null) {
                    mHandler.sendMessage(message);
                }
                break;
            }
            case MSG_BAD_NETWORK_DETECT://如果连续一段时间（10s）实际推流数据为0，会反馈这个错误消息
            {
                Log.e(TAG, "MSG_BAD_NETWORK_DETECT");
                Message m = Message.obtain(mHandler, MSG_BAD_NETWORK_DETECT);
                mHandler.sendMessage(m);
                break;
            }
            case MSG_SCREENSHOT_FINISHED://视频截图完成后的消息反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SCREENSHOT_FINISHED, buffer is " + (byte[]) object);
                getScreenShotByteBuffer((byte[]) object);

                break;
            }
            case MSG_SET_CAMERA_ID_ERROR://设置camera出错（对于只有一个摄像头的设备，如果调用了不存在的摄像头，会反馈这个错误消息）
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_CAMERA_ID_ERROR");
                break;
            }
            case MSG_SET_GRAFFITI_ERROR://设置涂鸦出错消息反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_GRAFFITI_ERROR");
                break;
            }
            case MSG_MIX_AUDIO_FINISHED://伴音一首MP3歌曲结束后的反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_MIX_AUDIO_FINISHED");
                break;
            }
            case MSG_URL_FORMAT_NOT_RIGHT://推流url格式不正确
            {
                Log.e(TAG, "MSG_URL_FORMAT_NOT_RIGHT");
                Message m = Message.obtain(mHandler, MSG_URL_FORMAT_NOT_RIGHT);
                mHandler.sendMessage(m);
                break;
            }
            case MSG_URL_IS_EMPTY://推流url为空
            {
                //Log.i(TAG, "test: in handleMessage, MSG_URL_IS_EMPTY");
                break;
            }
            case MSG_SPEED_CALC_SUCCESS: {
                Log.e(TAG, "MSG_SPEED_CALC_SUCCESS");
                Message m = Message.obtain(mHandler, MSG_SPEED_CALC_SUCCESS);
                m.obj = object;
                mHandler.sendMessage(m);
                break;
            }
            case MSG_SPEED_CALC_FAIL: {
                Log.e(TAG, "MSG_SPEED_CALC_FAIL");
                Message m = Message.obtain(mHandler, MSG_SPEED_CALC_FAIL);
                mHandler.sendMessage(m);
                break;
            }
        }
    }

    private class BroadcastMsgReceiver extends BroadcastReceiver {

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BC_ACTION_AUDIOMIX);
            intentFilter.addAction(BC_ACTION_AUDIOMIXVOLUME);
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(this, intentFilter);
        }

        public void unregister() {
            unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BC_ACTION_AUDIOMIX)) {
                int audioMixMsg = intent.getIntExtra("AudioMixMSG", 0);
                mMixAudioFilePath = mMP3AppFileDirectory.toString() + "/" + intent.getStringExtra("AudioMixFilePathMSG");

                //伴音开关的控制
                if (audioMixMsg == 1) {
                    if (mMixAudioFilePath.isEmpty())
                        return;

                    if (mLSMediaCapture != null) {
                        mLSMediaCapture.startPlayMusic(mMixAudioFilePath, false);
                    }
                } else if (audioMixMsg == 2) {
                    if (mLSMediaCapture != null) {
                        mLSMediaCapture.resumePlayMusic();
                    }
                } else if (audioMixMsg == 3) {
                    if (mLSMediaCapture != null) {
                        mLSMediaCapture.pausePlayMusic();
                    }
                } else if (audioMixMsg == 4) {
                    if (mLSMediaCapture != null) {
                        mLSMediaCapture.stopPlayMusic();
                    }
                }
            } else if (action.equals(BC_ACTION_AUDIOMIXVOLUME)) {   //    伴音音量
                int audioMixVolumeMsg = intent.getIntExtra("AudioMixVolumeMSG", 0);

                //伴音音量的控制
                int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                streamVolume = audioMixVolumeMsg * maxStreamVolume / 10;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 1);
            }


        }
    }

    //获取截屏图像的数据
    public void getScreenShotByteBuffer(byte[] screenShotByteBuffer) {
        FileOutputStream outStream = null;
        String screenShotFilePath = mScreenShotFilePath + mScreenShotFileName;
        if (screenShotFilePath != null) {
            try {
                if (screenShotFilePath != null) {

                    outStream = new FileOutputStream(String.format(screenShotFilePath));
                    outStream.write(screenShotByteBuffer);
                    outStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                showToast("截屏成功, 路径: " + screenShotFilePath);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        m_tryToStopLivestreaming = true;
    }

    //音视频参数设置
    public void paraSet() {

        //创建参数实例
        mLSLiveStreamingParaCtx = mLSMediaCapture.new LSLiveStreamingParaCtx();
        mLSLiveStreamingParaCtx.eHaraWareEncType = mLSLiveStreamingParaCtx.new HardWareEncEnable();
        mLSLiveStreamingParaCtx.eOutFormatType = mLSLiveStreamingParaCtx.new OutputFormatType();
        mLSLiveStreamingParaCtx.eOutStreamType = mLSLiveStreamingParaCtx.new OutputStreamType();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx = mLSLiveStreamingParaCtx.new LSAudioParaCtx();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec = mLSLiveStreamingParaCtx.sLSAudioParaCtx.new LSAudioCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx = mLSLiveStreamingParaCtx.new LSVideoParaCtx();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new LSVideoCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraPosition();
        mLSLiveStreamingParaCtx.sLSQoSParaCtx = mLSLiveStreamingParaCtx.new LSQoSParaCtx();

        if (mWaterMarkOn) {
            waterMark();
        }

        if (mGraffitiOn) {
            Graffiti();
        }

        //设置摄像头信息，并开始本地视频预览
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_BACK;//默认后置摄像头，用户可以根据需要调整

        //输出格式：视频、音频和音视频
        mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = HAVE_AV;//默认音视频推流

        //输出封装格式
        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = RTMP;//默认RTMP推流

        //输出FLV文件的路径和名称
        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatFileName = "/sdcard/media.flv";//当outputFormatType为FLV或者RTMP_AND_FLV时有效

        //音频编码参数配置
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = LS_AUDIO_CODEC_AAC;

        //硬件编码参数设置
        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = mHardWareEncEnable;//默认关闭硬件编码

        //网络QoS开关
        mLSLiveStreamingParaCtx.sLSQoSParaCtx.qosType = OpenQoS;//默认打开QoS

        //视频编码参数配置，视频码率可以由用户任意设置，视频分辨率按照如下表格设置
        //采集分辨率     编码分辨率     建议码率
        //1280x720     1280x720     1500kbps
        //1280x720     960x540      800kbps
        //960x720      960x720      1000kbps
        //960x720      960x540      800kbps
        //960x540      960x540      800kbps
        //640x480      640x480      600kbps
        //640x480      640x360      500kbps
        //320x240      320x240      250kbps
        //320x240      320x180      200kbps

        //如下是编码分辨率等信息的设置
        if (mVideoResolution.equals("HD")) {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 1500000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 1280;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 720;
        } else if (mVideoResolution.equals("SD")) {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 480;
        } else {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 15;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 250000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 320;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 240;
        }
    }

    //按钮初始化
    public void buttonInit() {

        (btnNetInfo = (ImageView) findViewById(R.id.live_net_info)).setOnClickListener(this);
        (btnFlash = (ImageView) findViewById(R.id.live_flash)).setOnClickListener(this);
        (btnCancel = (ImageView) findViewById(R.id.live_cancel)).setOnClickListener(this);
        (btnRestart = (ImageView) findViewById(R.id.live_restart_btn)).setOnClickListener(this);
        (btnAudio = (ImageView) findViewById(R.id.live_audio_btn)).setOnClickListener(this);
        (btnVideo = (ImageView) findViewById(R.id.live_video_btn)).setOnClickListener(this);
        (btnScreen = (ImageView) findViewById(R.id.live_screen_btn)).setOnClickListener(this);
        (btnMirror = (ImageView) findViewById(R.id.live_mirror_btn)).setOnClickListener(this);
        (btnStart = (ImageView) findViewById(R.id.live_start_btn)).setOnClickListener(this);
        (btnCamSwitch = (ImageView) findViewById(R.id.live_camera_btn)).setOnClickListener(this);
        (btnSpeedCalc = (ImageView) findViewById(R.id.live_speedcalc_btn)).setOnClickListener(this);
        txtResult = (TextView) findViewById(R.id.textResult);
        layoutUp = (LinearLayout) findViewById(R.id.live_btn_layout2);

        (btnMusic = (ImageView) findViewById(R.id.live_music_btn)).setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.live_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        if (mUseFilter && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
            (btnFilter = (ImageView) findViewById(R.id.live_filter_btn)).setOnClickListener(this);
        }
    }

    //查询Android摄像头支持的采样分辨率相关方法（1）
    public void openCamera(final int cameraID) {
        final Semaphore lock = new Semaphore(0);
        final RuntimeException[] exception = new RuntimeException[1];
        mCameraThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mCameraLooper = Looper.myLooper();
                try {
                    mCamera = Camera.open(cameraID);
                } catch (RuntimeException e) {
                    exception[0] = e;
                } finally {
                    lock.release();
                    Looper.loop();
                }
            }
        });
        mCameraThread.start();
        lock.acquireUninterruptibly();
    }

    //查询Android摄像头支持的采样分辨率相关方法（2）
    public void lockCamera() {
        try {
            mCamera.reconnect();
        } catch (Exception e) {
        }
    }

    //查询Android摄像头支持的采样分辨率相关方法（3）
    public void releaseCamera() {
        if (mCamera != null) {
            lockCamera();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //查询Android摄像头支持的采样分辨率相关方法（4）
    public List<Camera.Size> getCameraSupportSize(int cameraID) {
        openCamera(cameraID);
        if (mCamera != null) {
            Parameters param = mCamera.getParameters();
            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            releaseCamera();
            return previewSizes;
        }
        return null;
    }

    //处理伴音MP3文件
    public void handleMP3() {

        AssetManager assetManager = getAssets();

        String[] files = null;
        try {
            files = assetManager.list("mixAudio");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        mMP3AppFileDirectory = getExternalFilesDir(null);
        if (mMP3AppFileDirectory == null) {
            mMP3AppFileDirectory = getFilesDir();
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("mixAudio/" + filename);
                File outFile = new File(mMP3AppFileDirectory, filename);
                mMixAudioFilePath = outFile.toString();
                if (!outFile.exists()) {
                    FileOutputStream out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                Log.e("tag", "Failed to copy MP3 file", e);
            }
        }
    }

    //视频水印相关方法(1)
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //视频水印相关方法(2)
    public void waterMark() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mWaterMarkAppFileDirectory = getExternalFilesDir(null);
        } else {
            mWaterMarkAppFileDirectory = getFilesDir();
        }

        AssetManager assetManager = getAssets();

        //拷贝水印文件到APP目录
        String[] files = null;
        File fileDirectory;

        try {
            files = assetManager.list("waterMark");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        if (mWaterMarkAppFileDirectory != null) {
            fileDirectory = mWaterMarkAppFileDirectory;
        } else {
            fileDirectory = Environment.getExternalStorageDirectory();
            mWaterMarkFilePath = fileDirectory + "/" + mWaterMarkFileName;
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("waterMark/" + filename);
                File outFile = new File(fileDirectory, filename);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                mWaterMarkFilePath = outFile.toString();
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file", e);
            }
        }
    }

    //视频涂鸦相关方法
    public void Graffiti() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mGraffitiAppFileDirectory = getExternalFilesDir(null);
        } else {
            mGraffitiAppFileDirectory = getFilesDir();
        }

        AssetManager assetManager = getAssets();

        //拷贝涂鸦文件到APP目录
        String[] files = null;
        File fileDirectory;

        try {
            files = assetManager.list("graffiti");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        if (mGraffitiAppFileDirectory != null) {
            fileDirectory = mGraffitiAppFileDirectory;
        } else {
            fileDirectory = Environment.getExternalStorageDirectory();
            mGraffitiFilePath = fileDirectory + "/" + mGraffitiFileName;
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("graffiti/" + filename);
                File outFile = new File(fileDirectory, filename);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                mGraffitiFilePath = outFile.toString();
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file", e);
            }
        }
    }


}
