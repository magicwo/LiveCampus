package com.uestc.magicwo.livecampus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Magicwo on 2017/2/21.
 * 启动页
 */

public class SplashActivity extends AppBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BaseApplication.userId != null && !BaseApplication.userId.equals("")) {
            login();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            this.finish();
        }

    }

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
                            headers.put("authorization", BaseApplication.token);
                            OkGo.getInstance().addCommonHeaders(headers);
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            SplashActivity.this.finish();


                        }
                    }
                });
    }


}
