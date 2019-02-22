package com.zw.yanshinavi.ui.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.utils.DisplayUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private AlertDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addToActivityStack(this);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        afterCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        AppManager.getInstance().finishActivity(this);
    }

    /**
     * 显示loading对话框
     */
    protected void showLoading() {
        if(mLoadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.loading_alert);
            mLoadingDialog = builder.create();
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        mLoadingDialog.show();

    }

    /**
     * 隐藏loading对话框
     */
    protected void hideLoading(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void release(){};

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract void afterCreate();
}
