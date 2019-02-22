package com.zw.yanshinavi.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.zw.yanshinavi.MainActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.NetworkUtil;

import java.text.BreakIterator;

public class WelcomeActivity extends BaseActivity {

    private AlertDialog mDialog;

    private static final int LOCATION_REQUEST = 0x999;
    private static final int NETWORK_REQUEST = 0x998;

    private static final int ACTION_NETWORK = 1;
    private static final int ACTION_LOCATION = 2;
    private static final int ACTION_FINISH = 3;

    private int clickAction;

    private long startTime;

    /**
     * 检查是否有使用本App权限
     *
     * @return 是否有权限
     */
    private boolean checkHasUsePermission() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
        boolean hasPer = sp.getBoolean(Constant.HAS_USE_PERMISSION, false);
        return hasPer;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_welcome;
    }

    @Override
    public void afterCreate() {
        startTime = System.currentTimeMillis();

        //检查网络是否可用
        boolean hasNetwork = NetworkUtil.isNetworkConnected(App.getAppContext());
        if(!hasNetwork) {
            clickAction = ACTION_NETWORK;
            showPosDialog("设置网络","请先去打开网络开关！");
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
            showPosDialog("开启位置服务","请先去开启位置服务！");
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
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
                showPosDialog("位置服务未打开","无法获取当前位置，请先打开位置服务！");
            } else {
                jump();
            }
        } else if(requestCode == NETWORK_REQUEST) {
            boolean hasNetwork = NetworkUtil.isNetworkConnected(App.getAppContext());
            if(!hasNetwork) {
                clickAction = ACTION_FINISH;
                showPosDialog("没有网络","请检查网络是否可用！");
            } else {
                isLocationEnable();
            }
        }
    }
}
