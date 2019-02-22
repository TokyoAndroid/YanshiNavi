package com.zw.yanshinavi.common;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
    }

    public static final Context getAppContext() {
        return mAppContext;
    }
}
