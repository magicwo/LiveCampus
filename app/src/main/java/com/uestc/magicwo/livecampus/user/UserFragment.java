package com.uestc.magicwo.livecampus.user;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liji.circleimageview.CircleImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.magicwo.com.magiclib.constant.DataSaveConstant;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.LoginActivity;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseFragment;
import com.uestc.magicwo.livecampus.models.RoomInfoResponse;
import com.uestc.magicwo.livecampus.models.UserInfoResponse;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.utils.ImageLoader;
import com.uestc.magicwo.livecampus.videostreaming.PrepareLiveActivity;
import com.uestc.magicwo.livecampus.videostreaming.PublishParam;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class UserFragment extends AppBaseFragment {


    @BindView(R.id.drawer_head_img)
    CircleImageView drawerHeadImg;
    @BindView(R.id.drawer_name)
    TextView drawerName;
    @BindView(R.id.drawer_content)
    TextView drawerContent;
    @BindView(R.id.drawer_live_tv)
    TextView drawerLiveTv;
    @BindView(R.id.edit_data_layout)
    LinearLayout editDataLayout;
    @BindView(R.id.message_tv)
    TextView messageTv;
    @BindView(R.id.message_layout)
    LinearLayout messageLayout;
    @BindView(R.id.follow_tv)
    TextView followTv;
    @BindView(R.id.guanzhu_layout)
    LinearLayout guanzhuLayout;
    @BindView(R.id.aixin_tv)
    TextView aixinTv;
    @BindView(R.id.aixin_layout)
    LinearLayout aixinLayout;
    @BindView(R.id.drawer_live_layout)
    LinearLayout drawerLiveLayout;
    Unbinder unbinder;


    @Override
    public int setContentLayout() {
        return R.layout.fragment_drawer;
    }

    @Override
    public void initView() {
        getUserInfo(BaseApplication.userId);//获取用户信息
        drawerHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });
        editDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
        drawerName.setText(BaseApplication.nickName);
        drawerLiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApplication.userId == null || BaseApplication.userId.equals("")) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
//                String mUrlMedia = "rtmp://p4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2?wsSecret=13d710866cbff6ef72e01f071c5d626f&wsTime=1497339630";
//                Intent intent = new Intent(getActivity(), MediaPreviewActivity.class);
//                intent.putExtra("mediaPath", mUrlMedia);
//                intent.putExtra("videoResolution", "SD");//SD超清，HD高清，Fluncy标清
//              //滤镜选择
//                if (mFilterMode.equals("NormalMode")) {
//                    intent.putExtra("filter", false);
//                } else if (mFilterMode.equals("BeautyMode")) {
//                    intent.putExtra("filter", true);
//                }
                //申请权限
//                if(!bPermission){
//                    Toast.makeText(getApplication(),"请先允许app所需要的权限",Toast.LENGTH_LONG).show();
//                    bPermission = checkPublishPermission();
//                    return;
//                }

//                if (StringUtil.isEmpty(mUrlMedia) || !mUrlMedia.contains(".live.126.net")) {
//                    Toast.makeText(getActivity(), "请先设置正确的推流地址", Toast.LENGTH_LONG).show();
//                } else {
//                    startActivity(intent);
//                }


                //以下为全新版的推流设置
                PublishParam publishParam = new PublishParam();
//                publishParam.watermark = false;
//
//
//                publishParam.flashEnable = false;
//
//
//                publishParam.qosEnable = false;
//
//
//                publishParam.graffitiOn = false;
//
//
//                publishParam.useFilter = false;
//
//
//                publishParam.zoomed = false;

//                if (BaseApplication.nickName.equals("alice")) {
//                    publishParam.pushUrl = "rtmp://p4622ebf4.live.126.net/live/0209abf2449a4fe5ba9fafa90298f4e5?wsSecret=e3af3010487005961fe933cd01b230c8&wsTime=1498148950";
//                } else if (BaseApplication.nickName.equals("bob")) {
//                    publishParam.pushUrl = "rtmp://p4622ebf4.live.126.net/live/29528fa6d9714d12b4edab590efd51c0?wsSecret=5de3c1e118e2e8f33888c5e535c72f61&wsTime=1498149046";
//
//                } else if (BaseApplication.nickName.equals("root")) {
//                    publishParam.pushUrl = "rtmp://p4622ebf4.live.126.net/live/1c33a2fbfac74978b10caa7e9ef250f5?wsSecret=3a66174b01a3471a698dcfe560969b7c&wsTime=1498149062";
//
//                } else {
//                    publishParam.pushUrl = "rtmp://p4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2?wsSecret=13d710866cbff6ef72e01f071c5d626f&wsTime=1497339630";
//                }
//                publishParam.bitrate = 600;//码率
//                publishParam.fps = 20;//帧率
//
                getRoom(BaseApplication.userId);
//                createRoom(BaseApplication.userId);

//                Intent intent = new Intent(getActivity(), PrepareLiveActivity.class);
//                intent.putExtra("push_url", publishParam.pushUrl);
//                startActivity(intent);

            }
        });


    }

    /**
     * 获取主播的房间
     */
    private void getRoom(final String uid) {
        OkGo.get(Urls.ROOMINFO + "?id=" + uid + "&type=uid")
                .tag(this)
                .execute(new JsonCallback<BaseResponse<RoomInfoResponse>>() {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (e.getMessage().equals("房间不存在或查询失败")) {
                            createRoom(uid);
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponse<RoomInfoResponse> roomInfoResponseBaseResponse, Call call, Response response) {
                        Log.e("-----------直播间信息", roomInfoResponseBaseResponse.getRet().getPushUrl());
                        Intent intent = new Intent(getActivity(), PrepareLiveActivity.class);
                        intent.putExtra("push_url", roomInfoResponseBaseResponse.getRet().getPushUrl());
                        intent.putExtra("rid", roomInfoResponseBaseResponse.getRet().getRid());
                        intent.putExtra("title", roomInfoResponseBaseResponse.getRet().getTitle());
                        intent.putExtra("description", roomInfoResponseBaseResponse.getRet().getDescription());
                        intent.putExtra("cover", roomInfoResponseBaseResponse.getRet().getCover());
                        startActivity(intent);
                    }
                });

    }

    private void createRoom(final String uid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("title", BaseApplication.nickName);
        params.put("category", "zhibo");
//        params.put("room_cover", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498155090963&di=da346ad65f4f125d9d2976c2829243aa&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fa%2F568cd27741af5.jpg");
        params.put("description", "uestc");
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.CREATEROOMS).tag(this).upJson(jsonObject)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Toast.makeText(getActivity(), "正在创建房间", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (e.getMessage().equals("用户未通过认证无法创建直播间")) {
                            startActivity(new Intent(getActivity(), CertificateActivity.class));
                        }

                    }

                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        getRoom(uid);
                    }
                });
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
                        ImageLoader.load(getActivity(), drawerHeadImg, BaseApplication.headUrl);
                    }
                });

    }

    @Override
    public void loadData() {

    }

}
