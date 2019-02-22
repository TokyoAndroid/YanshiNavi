package com.zw.yanshinavi.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zw.yanshinavi.common.AppManager;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addToActivityStack(this);
        setContentView(getLayoutRes());
        afterCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        AppManager.getInstance().finishActivity(this);
    }

    protected void release(){};

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract void afterCreate();
}
