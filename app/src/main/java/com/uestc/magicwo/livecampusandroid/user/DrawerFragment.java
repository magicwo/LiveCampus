package com.uestc.magicwo.livecampusandroid.user;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liji.circleimageview.CircleImageView;
import com.uestc.magicwo.livecampusandroid.Login;
import com.uestc.magicwo.livecampusandroid.R;
import com.uestc.magicwo.livecampusandroid.appbase.AppBaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DrawerFragment extends AppBaseFragment {


    @BindView(R.id.drawer_head_img)
    CircleImageView drawerHeadImg;
    @BindView(R.id.drawer_name)
    TextView drawerName;
    @BindView(R.id.drawer_content)
    TextView drawerContent;
    @BindView(R.id.drawer_like_tv)
    TextView drawerLikeTv;
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
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void loadData() {

    }


}
