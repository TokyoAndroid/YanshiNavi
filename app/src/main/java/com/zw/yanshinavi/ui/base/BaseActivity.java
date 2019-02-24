package com.zw.yanshinavi.ui.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.AppManager;

import butterknife.ButterKnife;

/**
 * 公共Activity的基类
 *
 * @author zhangwei
 * @since 2019-2-22
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    private AlertDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        if(savedInstanceState == null) {
            AppManager.getInstance().addToActivityStack(this);
            ButterKnife.bind(this);
            afterCreate(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
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
        if(!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * 隐藏loading对话框
     */
    protected void hideLoading(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 设置对话框是否可以手动取消
     *
     * @param cancelable
     */
    protected void setLoadingCancelable(boolean cancelable) {
        if(mLoadingDialog != null) {
            mLoadingDialog.setCancelable(cancelable);
        }
    }

    /**
     * 判断对话框是否在加载
     *
     * @return
     */
    protected boolean isLoadingShowing() {
        if(mLoadingDialog != null) {
            return mLoadingDialog.isShowing();
        }
        return false;
    }

    protected void release(){};

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract void afterCreate(@Nullable Bundle savedInstanceState);

}
