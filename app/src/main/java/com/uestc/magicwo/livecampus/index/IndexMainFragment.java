package com.uestc.magicwo.livecampus.index;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseFragment;
import com.uestc.magicwo.livecampus.constants.LiveTypeConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Magicwo on 2017/9/23.
 */

public class IndexMainFragment extends AppBaseFragment {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    //填充的fragment列表
    private List<Fragment> mfragments;
    //标题
    private String[] titles = LiveTypeConstant.categoryName;

    private FixedFragmentPagerAdapter fixedFragmentPagerAdapter;


    @Override
    public int setContentLayout() {
        return R.layout.fragment_index;
    }

    @Override
    public void initView() {

    }

    @Override
    public void loadData() {
        initdata();

    }
    public void newPages(){
        if (viewpager!=null)
            viewpager.setCurrentItem(0);
    }

    private void initdata() {
        mfragments = new ArrayList<>();
        for (int i=0;i<titles.length;i++)//不再显示其他类
            mfragments.add(IndexFragment.newInstance(i));
        fixedFragmentPagerAdapter = new FixedFragmentPagerAdapter(getChildFragmentManager(), mfragments);
        fixedFragmentPagerAdapter.setTitles(titles);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewpager.setAdapter(fixedFragmentPagerAdapter);
        //设置页面缓存个数
        viewpager.setOffscreenPageLimit(5);
        tablayout.setupWithViewPager(viewpager);
    }


}
