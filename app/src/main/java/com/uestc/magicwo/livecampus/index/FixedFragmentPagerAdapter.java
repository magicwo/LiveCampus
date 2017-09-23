package com.uestc.magicwo.livecampus.index;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Magicwo on 2017/1/6.
 *
 */

public class FixedFragmentPagerAdapter extends FragmentStatePagerAdapter{
    private Context context;
    private String[] titles;
    private List<Fragment> fragmentlists;
    public FixedFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentlists) {
        super(fm);
        this.fragmentlists=fragmentlists;
    }


    /**
     * 设置标题
     * @param titles 标题数组String
     */
    public void setTitles(String[] titles){
        this.titles=titles;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentlists.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return fragmentlists.size();
    }
}
