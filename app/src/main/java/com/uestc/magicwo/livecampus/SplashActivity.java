package com.uestc.magicwo.livecampus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.magicwo.com.magiclib.constant.DataSaveConstant;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Magicwo on 2017/2/21.
 * 启动页
 */

public class SplashActivity extends AppBaseActivity {


    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;

    final String[] danmus = {"主播快露脸", "老铁双击666", "成电人自己直播平台", "老铁双击666", "主播快露脸", "强势围观"};

    private DanmakuContext danmakuContext;
    private boolean showDanmaku = false;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };//弹幕解析器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        ButterKnife.bind(this);
        initDanmakus();
        if (BaseApplication.userId != null && !BaseApplication.userId.equals("")) {
            login();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            this.finish();
        }

    }

    //弹幕相关从此开始
    private void initDanmakus() {
        danmakuView.enableDanmakuDrawingCache(true);//设置缓存
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku=true;
                danmakuView.start();
                generateSomeDanmaku();
//
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
        danmaku.textColor = Color.BLACK;
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
                String s;
                for (int i = 0; i <danmus.length; i++) {
                    int time =4*( new Random().nextInt(300));
                    s = danmus[i];
                    if (s != null)
                        addDanmaku(s, false);
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

    private void login() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", BaseApplication.nickName);
        params.put("password", BaseApplication.pwd);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.LOGIN)
                .tag(this)
                .upJson(jsonObject)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        SplashActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        if (simpleResponseBaseResponse.getToken() != null && !simpleResponseBaseResponse.getToken().equals("")) {
                            BaseApplication.token = simpleResponseBaseResponse.getToken();
                            BaseApplication.sharedPreferencesHelper.saveString(DataSaveConstant.TOKEN, BaseApplication.token);
                            HttpHeaders headers = new HttpHeaders();
                            headers.put("Authorization", "Bearer " + BaseApplication.token);
                            OkGo.getInstance().addCommonHeaders(headers);
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            SplashActivity.this.finish();


                        }
                    }
                });
    }


}
