package com.zw.yanshinavi.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.zw.yanshinavi.MainActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.NetworkUtil;

public class WelcomeActivity extends BaseActivity {

    private static final int LOCATION_TIMEOUT = 10 * 1000; // 定位超时时间

    public double lat, lon; // 经度，纬度

    private boolean isFirstLocation = true; //是否是初次定位
    private AlertDialog mDialog;

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
        boolean hasNetwork = NetworkUtil.isNetworkConnected(App.getAppContext());
        if(!hasNetwork) {
            showDialog("没有网络","请先检查网络是否可用！");
            return;
        }

        boolean hasUsePermission = checkHasUsePermission();
        if(hasUsePermission) {
            startActivity(MainActivity.getLauncher(this));
        } else {
            // TODO 跳转到注册码界面
            startActivity(MainActivity.getLauncher(this));
            finish();
        }

    }

    private void showDialog(String title, String message) {
        if(mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppManager.getInstance().finishAllActivity();
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

}
