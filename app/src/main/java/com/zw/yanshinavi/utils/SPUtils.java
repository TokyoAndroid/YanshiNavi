package com.zw.yanshinavi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zw.yanshinavi.common.App;

public class SPUtils {

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
}
