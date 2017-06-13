package com.uestc.magicwo.livecampus.user;


import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.liji.circleimageview.CircleImageView;
import com.uestc.magicwo.livecampus.LoginActivity;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseFragment;
import com.uestc.magicwo.livecampus.videostreaming.CameraPreviewActivity;
import com.uestc.magicwo.livecampus.videostreaming.PublishParam;

import butterknife.BindView;


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


                Intent intent = new Intent(getActivity(), CameraPreviewActivity.class);
                intent.putExtra("data", publishParam);
                startActivity(intent);

            }
        });


    }

    @Override
    public void loadData() {

    }


}
