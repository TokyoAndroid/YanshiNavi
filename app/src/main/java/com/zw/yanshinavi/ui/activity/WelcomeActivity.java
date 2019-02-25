package com.zw.yanshinavi.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.zw.yanshinavi.MainActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.NetworkUtil;
import com.zw.yanshinavi.utils.SPUtils;

/**
 * 程序入口界面
 *
 * @author zhangwei
 * @since 2019-2-22
 */
public class WelcomeActivity extends BaseActivity {

    private AlertDialog mDialog;

    private static final int LOCATION_REQUEST = 0x999; // 请求系统打开位置服务的请求码
    private static final int NETWORK_REQUEST = 0x998; // 请求系统打开网络的请求码

    private static final int ACTION_NETWORK = 1; // 当前请求类型是网络
    private static final int ACTION_LOCATION = 2; // 当前请求类型是位置
    private static final int ACTION_FINISH = 3; // 当前请求类型是结束

    private int clickAction;


    /**
     * 检查是否有使用本App权限
     *
     * @return 是否有权限
     */
    private boolean checkHasUsePermission() {
        return SPUtils.getBoolean(Constant.HAS_USE_PERMISSION);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_welcome;
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        //检查网络是否可用
        boolean hasNetwork = NetworkUtil.isNetworkConnected(App.getAppContext());
        if(!hasNetwork) {
            clickAction = ACTION_NETWORK;
            showPosDialog(getString(R.string.setting_network)
                    ,getString(R.string.please_open_network));
            return;
        }

        // 检查位置服务是否开启
        isLocationEnable();
    }

    /**
     * 检查位置服务是否开启
     */
    private void isLocationEnable() {
        boolean isLocationEnable = NetworkUtil.isLocationEnabled();
        if(!isLocationEnable) {
            clickAction = ACTION_LOCATION;
            showPosDialog(getString(R.string.open_location_service)
                    ,getString(R.string.please_open_location_service));
        } else {
            jump();
        }
    }

    /**
     * 根据是否有使用权限跳转相对于界面
     */
    private void jump() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean hasUsePermission = checkHasUsePermission();
                if(hasUsePermission) {
                    startActivity(MainActivity.getLauncher(WelcomeActivity.this));
                } else {
                    // TODO 跳转到注册码界面
                    startActivity(MainActivity.getLauncher(WelcomeActivity.this));
                }
                finish();
            }
        },2 * 1000);


    }

    private void showPosDialog(String title, String message) {
        if(mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDialog.dismiss();
                    switch (clickAction) {
                        case ACTION_NETWORK:
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivityForResult(intent, NETWORK_REQUEST);
                            break;
                        case ACTION_LOCATION:
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent1, LOCATION_REQUEST);
                            break;
                        case ACTION_FINISH:
                            AppManager.getInstance().finishCurrentActivity();
                            break;
                    }
                }
            });
            mDialog = builder.create();

            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        } else {
            mDialog.setTitle(title);
            mDialog.setMessage(message);
        }
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOCATION_REQUEST) {
            boolean isLocationEnable = NetworkUtil.isLocationEnabled();
            if(!isLocationEnable) {
                clickAction = ACTION_FINISH;
                showPosDialog(getString(R.string.location_service_not_open)
                        ,getString(R.string.no_location_please_open));
            } else {
                jump();
            }
        } else if(requestCode == NETWORK_REQUEST) {
            boolean hasNetwork = NetworkUtil.isNetworkConnected(App.getAppContext());
            if(!hasNetwork) {
                clickAction = ACTION_FINISH;
                showPosDialog(getString(R.string.no_internet)
                        ,getString(R.string.please_check_network));
            } else {
                isLocationEnable();
            }
        }
    }
}
