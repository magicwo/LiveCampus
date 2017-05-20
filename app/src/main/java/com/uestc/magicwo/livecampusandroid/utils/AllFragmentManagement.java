package com.uestc.magicwo.livecampusandroid.utils;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import java.util.ArrayList;
import java.util.List;


/**
 * Fragment的管理类
 */
public class AllFragmentManagement extends Application {


    public static List<Fragment> fragmentList = new ArrayList<>();
    public static List<Fragment> eventFragmentList = new ArrayList<>();

    public AllFragmentManagement() {

    }

    /**
     * @param activity    要填充的activity
     * @param containerId 置换fragment的空间ID
     * @param fragment    需要添加的fragment
     */
    public static void ChangeFragment(int type, FragmentActivity activity, int containerId, Fragment fragment, boolean isAdd) {

        FragmentManager fg = activity.getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        switch (type) {
            case 1:
                HideAllFragment(ft);
                break;
            case 2:
                HideAllEventFragment(ft);
                break;
            default:
                break;
        }
        if (isAdd) {
            ft.add(containerId, fragment);
        } else {
            ft.show(fragment);
        }

        ft.commit();
    }

    public static void ChangeFragment(FragmentActivity activity, int containerId, Fragment fragment) {

        FragmentManager fg = activity.getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        ft.replace(containerId, fragment);
        ft.commit();

    }

    /**
     * 主Activity中隐藏所有在list中的Fragment
     *
     * @param ft
     */
    public static void HideAllFragment(FragmentTransaction ft) {
        for (Fragment mfragment : fragmentList) {
            ft.hide(mfragment);
        }
    }

    /**
     * 主Activity中隐藏所有在list中的Fragment
     *
     * @param ft
     */
    public static void HideAllEventFragment(FragmentTransaction ft) {
        for (Fragment mfragment : eventFragmentList) {
            ft.hide(mfragment);
        }
    }


}

