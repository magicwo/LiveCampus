package com.uestc.magicwo.livecampus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.magicwo.com.magiclib.base.BaseActivity;
import com.magicwo.com.magiclib.constant.DataSaveConstant;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.utils.ActivityManagement;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppBaseActivity {                 //登录界面活动

    public int pwdresetFlag = 0;
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private Button mCancleButton;                     //注销按钮
    private CheckBox mRememberCheck;

    private SharedPreferences login_sp;
    private String userNameValue, passwordValue;

    private View loginView;                           //登录
    private View loginSuccessView;
    private TextView loginSuccessShow;
    private TextView mChangepwdText;
    private UserDataManager mUserDataManager;         //用户数据管理类

    String userName = "";
    String userPwd = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //通过id找到相应的控件
        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        mCancleButton = (Button) findViewById(R.id.login_btn_cancle);
        loginView = findViewById(R.id.login_view);
        loginSuccessView = findViewById(R.id.login_success_view);
        loginSuccessShow = (TextView) findViewById(R.id.login_success_show);

        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);

        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);

//        login_sp = getSharedPreferences("userInfo", 0);
//        String name = login_sp.getString("USER_NAME", "");
//        String pwd = login_sp.getString("PASSWORD", "");
//        boolean choseRemember = login_sp.getBoolean("mRememberCheck", false);
//        boolean choseAutoLogin = login_sp.getBoolean("mAutologinCheck", false);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
//        if (choseRemember) {
//            mAccount.setText(name);
//            mPwd.setText(pwd);
//            mRememberCheck.setChecked(true);
//        }
        mAccount.setText(BaseApplication.nickName);
        mPwd.setText(BaseApplication.pwd);

        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        mCancleButton.setOnClickListener(mListener);
        mChangepwdText.setOnClickListener(mListener);

        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
        image.setImageResource(R.drawable.logo);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }
    }


    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(LoginActivity.this, RegisterActivity.class);    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    toLogin();
                    break;
                case R.id.login_btn_cancle:                             //登录界面的注销按钮
                    cancel();
                    break;
                case R.id.login_text_change_pwd:                             //登录界面的注销按钮
                    Intent intent_Login_to_reset = new Intent(LoginActivity.this, ResetpwdActivity.class);    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_reset);
                    break;
            }
        }
    };

    public void toLogin() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            userPwd = mPwd.getText().toString().trim();
            login();
//            SharedPreferences.Editor editor = login_sp.edit();
//            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd);
//            if (result == 1) {                                             //返回1说明用户名和密码均正确
//                //保存用户名和密码
//                editor.putString("USER_NAME", userName);
//                editor.putString("PASSWORD", userPwd);
//
//                //是否记住密码
//                if (mRememberCheck.isChecked()) {
//                    editor.putBoolean("mRememberCheck", true);
//                } else {
//                    editor.putBoolean("mRememberCheck", false);
//                }
//                editor.commit();
//
//                Intent intent = new Intent(LoginActivity.this, User.class);    //切换Login Activity至User Activity
//                startActivity(intent);
//                finish();
//                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();//登录成功提示
//            } else if (result == 0) {
//                Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();  //登录失败提示
//            }
        }
    }

    public void cancel() {           //注销
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();
            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd);
            if (result == 1) {                                             //返回1说明用户名和密码均正确
//                Intent intent = new Intent(Login.this,User.class) ;    //切换Login Activity至User Activity
//                startActivity(intent);
                Toast.makeText(this, getString(R.string.cancel_success), Toast.LENGTH_SHORT).show();//登录成功提示
                mPwd.setText("");
                mAccount.setText("");
                mUserDataManager.deleteUserDatabyname(userName);
            } else if (result == 0) {
                Toast.makeText(this, getString(R.string.cancel_fail), Toast.LENGTH_SHORT).show();  //登录失败提示
            }
        }

    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }

    //网络请求Get方法的示例,具体请大洋参考官方文档
    //登录请求 post
    private void login() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("username", userName);
        params.put("password", userPwd);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.LOGIN)
                .tag(this)
                .upJson(jsonObject)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        showToast("登陆成功");
                        if (simpleResponseBaseResponse.getToken() != null && !simpleResponseBaseResponse.getToken().equals("")) {
                            BaseApplication.saveUserInfo(simpleResponseBaseResponse.getToken(), simpleResponseBaseResponse.getUid(), userName, userPwd);
                            HttpHeaders headers = new HttpHeaders();
                            headers.put("Authorization", "Bearer " + BaseApplication.token);
                            OkGo.getInstance().addCommonHeaders(headers);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            ActivityManagement.getActivityManagement().finishAllActivity();


                        }
                    }
                });

    }
}
