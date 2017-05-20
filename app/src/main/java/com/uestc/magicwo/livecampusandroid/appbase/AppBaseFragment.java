package com.uestc.magicwo.livecampusandroid.appbase;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uestc.magicwo.livecampusandroid.utils.AllFragmentManagement;

import java.util.logging.Logger;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by hasee on 2016/10/18.
 */
public abstract class AppBaseFragment extends Fragment {

    protected Context mContext;
    private View view;

    protected Bundle savedInstanceState;
    protected Unbinder unbinder;

    public abstract int setContentLayout();
    public abstract void initView();
    public abstract void loadData();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        this.savedInstanceState=savedInstanceState;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            if (view==null){
                view = inflater.inflate(setContentLayout(), container, false);
                unbinder=ButterKnife.bind(this, view);
                initView();
                loadData();
            }
        }catch (Exception e){
            Log.e("TAG","Fragment资源没有找到");
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
