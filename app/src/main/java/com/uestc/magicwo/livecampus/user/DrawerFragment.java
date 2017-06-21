package com.uestc.magicwo.livecampus.user;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import android.widget.Toast;

import com.liji.circleimageview.CircleImageView;
import com.lzy.okgo.OkGo;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.LoginActivity;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseFragment;
import com.uestc.magicwo.livecampus.models.RoomInfoResponse;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.JsonUploadCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.videostreaming.CameraPreviewActivity;
import com.uestc.magicwo.livecampus.videostreaming.PublishParam;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;


public class DrawerFragment extends AppBaseFragment {


    @BindView(R.id.drawer_head_img)
    CircleImageView drawerHeadImg;
    @BindView(R.id.drawer_name)
    TextView drawerName;
    @BindView(R.id.drawer_content)
    TextView drawerContent;
    @BindView(R.id.drawer_live_tv)
    TextView drawerLiveTv;
    @BindView(R.id.drawer_wallet_tv)
    TextView drawerWalletTv;
    @BindView(R.id.drawer_setting_tv)
    TextView drawerSettingTv;
    @BindView(R.id.drawer_recommended_tv)
    TextView drawerRecommendedTv;

    @Override
    public int setContentLayout() {
        return R.layout.fragment_drawer;
    }

    @Override
    public void initView() {

        drawerHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });
        drawerName.setText(BaseApplication.nickName);
        drawerLiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                publishParam.watermark = false;


                publishParam.flashEnable = false;


                publishParam.qosEnable = false;


                publishParam.graffitiOn = false;


                publishParam.useFilter = false;


                publishParam.zoomed = false;


                publishParam.pushUrl = "rtmp://p4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2?wsSecret=13d710866cbff6ef72e01f071c5d626f&wsTime=1497339630";
                publishParam.bitrate = 600;//码率
                publishParam.fps = 20;//帧率
//
                getRoom(BaseApplication.userId);

//                Intent intent = new Intent(getActivity(), CameraPreviewActivity.class);
//                intent.putExtra("data", publishParam);
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
                        Log.e("-----------直播间信息", roomInfoResponseBaseResponse.getRet().getRtmpPullUrl());
                    }
                });

    }

    private void createRoom(final String uid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("title", BaseApplication.nickName);
        params.put("category", "直播");
        params.put("description", "我是描述");
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.CREATEROOMS).tag(this).upJson(jsonObject)
                .execute(new JsonUploadCallback<BaseResponse<SimpleResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        getRoom(uid);
                    }
                });
    }

    @Override
    public void loadData() {

    }


}
