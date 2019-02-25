package com.zw.yanshinavi.common;

import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class StaticHandler<T extends Context> extends Handler {

    private WeakReference<T> mContext;

    public StaticHandler(T context){
        mContext = new WeakReference<>(context);
    }

}
