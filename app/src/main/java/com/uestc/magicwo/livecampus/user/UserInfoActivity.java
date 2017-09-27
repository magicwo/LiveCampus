package com.uestc.magicwo.livecampus.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.magicwo.com.magiclib.constant.DataSaveConstant;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.event.MessageEvent;
import com.uestc.magicwo.livecampus.models.UserInfoResponse;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.utils.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Magicwo on 2017/9/22.
 * 个人资料页面
 */

public class UserInfoActivity extends AppBaseActivity {
    @BindView(R.id.head_image_imageView)
    ImageView headImageImageView;
    @BindView(R.id.head_image_layout)
    LinearLayout headImageLayout;
    @BindView(R.id.nick_name_text_view)
    TextView nickNameTextView;
    @BindView(R.id.nick_name_layout)
    LinearLayout nickNameLayout;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    @BindView(R.id.email_layout)
    LinearLayout emailLayout;
    @BindView(R.id.phone_text_view)
    TextView phoneTextView;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.sex_text_view)
    TextView sexTextView;
    @BindView(R.id.sex_layout)
    LinearLayout sexLayout;
    @BindView(R.id.age_text_view)
    TextView ageTextView;
    @BindView(R.id.age_layout)
    LinearLayout ageLayout;
    @BindView(R.id.student_id_text_view)
    TextView studentIdTextView;
    @BindView(R.id.student_id_layout)
    LinearLayout studentIdLayout;
    @BindView(R.id.register_time_text_view)
    TextView registerTimeTextView;
    @BindView(R.id.register_time_layout)
    LinearLayout registerTimeLayout;
    @BindView(R.id.last_login_time_text_view)
    TextView lastLoginTimeTextView;
    @BindView(R.id.last_login_time_layout)
    LinearLayout lastLoginTimeLayout;
    @BindView(R.id.student_image_imageView)
    ImageView studentImageImageView;
    @BindView(R.id.student_image_layout)
    LinearLayout studentImageLayout;

    @Override
    protected void initVariables() {
        super.initVariables();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        nickNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.runActivity(UserInfoActivity.this, 1, nickNameTextView.getText().toString());
            }
        });
    }

    //传递回来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MessageEvent messageEvent) {
        nickNameTextView.setText(messageEvent.getMessage());
        BaseApplication.saveUserInfo(BaseApplication.token, BaseApplication.userId, messageEvent.getMessage(), BaseApplication.pwd);
    }

    @Override
    protected void loadData() {
        super.loadData();
        getUserInfo(BaseApplication.userId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获得用户信息
     */
    private void getUserInfo(String uid) {
        OkGo.get(Urls.USERINFO + "?uid=" + uid)
                .tag(this)
                .execute(new JsonCallback<BaseResponse<UserInfoResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfoResponse> userInfoResponseBaseResponse, Call call, Response response) {
                        //保存用户信息
                        BaseApplication.saveUserInfo(BaseApplication.token, userInfoResponseBaseResponse.getRet().getUserID(), userInfoResponseBaseResponse.getRet().getUsername(), BaseApplication.pwd);
                        BaseApplication.headUrl = userInfoResponseBaseResponse.getRet().getImgAvatar();
                        BaseApplication.sharedPreferencesHelper.saveString(DataSaveConstant.HEADURL, BaseApplication.headUrl);
                        ImageLoader.load(UserInfoActivity.this, headImageImageView, userInfoResponseBaseResponse.getRet().getImgAvatar());
                        nickNameTextView.setText(userInfoResponseBaseResponse.getRet().getUsername());
                        emailTextView.setText(userInfoResponseBaseResponse.getRet().getEmail());
                        phoneTextView.setText(userInfoResponseBaseResponse.getRet().getPhone());
                        sexTextView.setText(userInfoResponseBaseResponse.getRet().getSex());
                        ageTextView.setText(userInfoResponseBaseResponse.getRet().getAge() + "");
                        studentIdTextView.setText(userInfoResponseBaseResponse.getRet().getStudentID());
                        registerTimeTextView.setText(userInfoResponseBaseResponse.getRet().getRegisterTime());
                        lastLoginTimeTextView.setText(userInfoResponseBaseResponse.getRet().getSignTime());
                        ImageLoader.load(UserInfoActivity.this, headImageImageView, userInfoResponseBaseResponse.getRet().getImgAvatar());
                    }
                });

    }
}
