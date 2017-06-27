package com.uestc.magicwo.livecampus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.magicwo.com.magiclib.base.BaseActivity;
import com.pgyersdk.update.PgyUpdateManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.custom.CustomViewPager;
import com.uestc.magicwo.livecampus.index.IndexFragment;
import com.uestc.magicwo.livecampus.live.LiveFragment;
import com.uestc.magicwo.livecampus.user.UserFragment;
import com.uestc.magicwo.livecampus.user.DrawerFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends AppBaseActivity implements OnTabSelectListener, ViewPager.OnPageChangeListener, IndexFragment.OnStartDrawerListener {


    private static final int INDEX = 0;
    private static final int LIVE = 1;
    private static final int USERCENTER = 2;

    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.left_drawer)
    FrameLayout leftDrawer;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private List<Fragment> mFragmentList;
    private static boolean isExit = false;

    @Override
    protected void initVariables() {

    }
    /**
     * 6.0权限处理
     **/
    private boolean bPermission = false;
    private final int WRITE_PERMISSION_REQ_CODE = 100;

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**   6.0权限申请     **/
        bPermission = checkPublishPermission();

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setMenuFragment();
        setMainFragment();
        PgyUpdateManager.register(this,"magicwo_livecampus");


    }



    /**
     * 设置侧滑菜单Fragment
     */
    private void setMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.left_drawer, new DrawerFragment());

        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 设置主界面Fragment
     */
    private void setMainFragment() {
        mFragmentList = new ArrayList<>();

        //首页
        IndexFragment indexFragment = new IndexFragment();
        indexFragment.setOnStartDrawerListener(this);
        mFragmentList.add(indexFragment);

        //直播
        LiveFragment liveFragment = new LiveFragment();
        mFragmentList.add(liveFragment);

        //个人
        UserFragment userFragment = new UserFragment();
        mFragmentList.add(userFragment);

        bottomBar.setOnTabSelectListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setPagingEnabled(false);


        //设置一次记载Fragment数
        viewPager.setOffscreenPageLimit(mFragmentList.size());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList));
    }

    @Override
    protected void loadData() {

    }


    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bottomBar.getTabAtPosition(position).setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
            case R.id.tab_index:
                viewPager.setCurrentItem(INDEX);
                break;
            case R.id.tab_live:
                viewPager.setCurrentItem(LIVE);
                break;
            case R.id.tab_user:
                viewPager.setCurrentItem(USERCENTER);
                break;
            default:
                break;
        }
    }
    //传递退出消息
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            showToast("再按一次退出应用");
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }
}
