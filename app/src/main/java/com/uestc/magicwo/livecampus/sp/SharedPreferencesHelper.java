package com.uestc.magicwo.livecampus.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.magicwo.com.magiclib.constant.CommonConstant;
import com.uestc.magicwo.livecampus.BaseApplication;


/**
 * Created by Magicwo on 2017/1/2.
 * 帮助处理SharedPreferences
 */
public class SharedPreferencesHelper {

    private static SharedPreferencesHelper sharedPreferencesHelper; //单例
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static SharedPreferencesHelper getSharedPreferencesHelperInstance(Context context) {
        if (sharedPreferencesHelper == null) {
            sharedPreferencesHelper = new SharedPreferencesHelper();
            sharedPreferencesHelper.sharedPreferences = BaseApplication.sharedPreferences;
            sharedPreferencesHelper.editor = sharedPreferencesHelper.sharedPreferences.edit();
        }
        return sharedPreferencesHelper;
    }

    /**
     * 清空sharedPreferences
     */

    public void clearSharedPrefrences() {
        editor.clear();
        editor.commit();
    }

    /**
     * 清空某一个值
     *
     * @param key
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();

    }

    /**
     * 保存String类型的值
     *
     * @param key
     * @param value
     */
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * 保存int类型的值
     *
     * @param key
     * @param value
     */
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();

    }

    /**
     * 保存Float类型的值
     *
     * @param key
     * @param value
     */
    public void saveFloat(String key, int value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 保存Long类型的值
     *
     * @param key
     * @param value
     */
    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();

    }

    /**
     * 保存Boolean类型的值
     *
     * @param key
     * @param value
     */
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 从SharedPreferences中获得数据
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, CommonConstant.DEFAULT_STR);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, CommonConstant.DEFAULT_INT);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, CommonConstant.DEFAULT_FLOAT);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, CommonConstant.DEFAULT_LONG);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, CommonConstant.DEFAULT_BOOLEAN);
    }


}
