package com.uestc.magicwo.livecampus;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.magicwo.com.magiclib.constant.DataSaveConstant;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;
import com.uestc.magicwo.livecampus.sp.SharedPreferencesHelper;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.logging.Level;

import io.yunba.android.manager.YunBaManager;
import okhttp3.OkHttpClient;


/**
 * Created by Magicwo on 2017/1/2.
 */
public class BaseApplication extends Application {
    public static String decodeType = "software";  //解码类型，默认软件解码
    public static String mediaType = "livestream";//媒体类型，默认网络直播

    public static SharedPreferences sharedPreferences;
    public static SharedPreferencesHelper sharedPreferencesHelper;
    public static String userId = "";
    public static String nickName = "";
    public static String headUrl = "";
    public static String token = "";
    public static String pwd = "";

    @Override
    public void onCreate() {
        super.onCreate();
        PgyCrashManager.register(this);

        setupSharedPreferences();

        setOkGo();//网络请求库的初始化
//
        setYunBa();//云吧推送
        initImagePicker();


    }

    private void setYunBa() {
//        //订阅topic
//        YunBaManager.start(getApplicationContext());
//        YunBaManager.subscribe(getApplicationContext(), "wujinwo",
//                new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//                        Log.e("-------------->", "成功");
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        if (exception instanceof MqttException) {
//                            MqttException ex = (MqttException) exception;
//                            String msg = "Subscribe failed with error code : " + ex.getReasonCode();
//                            Log.e("-------------->", msg);
//                        }
//
//                    }
//                }
//        );
//        YunBaManager.subscribe(getApplicationContext(), new String[]{"t1"}, new IMqttActionListener() {
//
//            @Override
//            public void onSuccess(IMqttToken arg0) {
//                Log.d("订阅", "Subscribe topic succeed");
//            }
//
//            @Override
//            public void onFailure(IMqttToken arg0, Throwable arg1) {
//                Log.d("订阅", "Subscribe topic failed");
//            }
//        });
    }

    /**
     * OkGo的全局初始化
     */
    private void setOkGo() {
//        try {
//            ProviderInstaller.installIfNeeded(this);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
//        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//
        //必须调用初始化
        OkGo.init(this);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                              //方法一：信任所有证书,不安全有风险
//                    .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//                    .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//                    .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                    .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

            //这两行同上，不需要就不要加入
            // .addCommonHeaders(new HttpHeaders("Authorization", "Bearer " + BaseApplication.token));  //设置全局公共头
//                    .addCommonParams(params);   //设置全局公共参数


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化SharedPreferences
     */
    private void setupSharedPreferences() {
        //MODE_PRIVATE 表示只有当前的应用程序才可以对这个SharedPreferences文件进行读写
        sharedPreferences = getSharedPreferences(DataSaveConstant.SP_NAME, MODE_PRIVATE);
        sharedPreferencesHelper = SharedPreferencesHelper.getSharedPreferencesHelperInstance(this);
        userId = sharedPreferencesHelper.getString(DataSaveConstant.USERID);
        headUrl = sharedPreferencesHelper.getString(DataSaveConstant.HEADURL);
        nickName = sharedPreferencesHelper.getString(DataSaveConstant.NICKNAME);
        token = sharedPreferencesHelper.getString(DataSaveConstant.TOKEN);
        pwd = sharedPreferencesHelper.getString(DataSaveConstant.PWD);

    }

    //存储用户信息
    public static void saveUserInfo(String token, String userId, String userName, String pwd) {
        BaseApplication.token = token;
        BaseApplication.userId = userId;
        BaseApplication.nickName = userName;
        BaseApplication.pwd = pwd;
        sharedPreferencesHelper.saveString(DataSaveConstant.TOKEN, token);
        sharedPreferencesHelper.saveString(DataSaveConstant.USERID, userId);
        sharedPreferencesHelper.saveString(DataSaveConstant.NICKNAME, userName);
        sharedPreferencesHelper.saveString(DataSaveConstant.PWD, pwd);

    }

    /**
     * 图片选择器必须要有的配置，用来配置ImagePicker.这里由于该第三方库都是用的ImageView，无法用fresco，所以采用了Glide图片加载库来代替
     * ps：整个图片选择器用到的都是Glide
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

}
