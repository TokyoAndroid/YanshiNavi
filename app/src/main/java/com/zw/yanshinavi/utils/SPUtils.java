package com.zw.yanshinavi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.zw.yanshinavi.common.App;

public class SPUtils {

    private static final String TAG = "SPUtils";

    private static SharedPreferences getSp(){
        return App.getAppContext()
                .getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
    }

    public static void putBoolean(String name, boolean bool) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name,bool);
        editor.commit();
    }

    public static boolean getBoolean(String name) {
        return getSp().getBoolean(name,false);
    }

    public static void putString(String key, String value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(String key) {
        return getSp().getString(key, "");
    }

    /**
     * 获取出行偏好策略值
     *
     * @return
     */
    public static int getStrategy() {
        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        boolean isNoHighway = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        boolean isHighway = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
        try{
            return AMapNavi.getInstance(App.getAppContext())
                    .strategyConvert(isNoCar, isNoHighway, isNoCharge, isHighway, true);
        } catch (Exception e) {
            Log.e("zhangwei", TAG + " getStrategy error");
            return 0;
        }
    }
}
