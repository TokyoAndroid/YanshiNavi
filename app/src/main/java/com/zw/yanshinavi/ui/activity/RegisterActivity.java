package com.zw.yanshinavi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.zw.yanshinavi.MainActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.ui.views.TitleView;
import com.zw.yanshinavi.utils.CommonUtils;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 授权使用界面
 *
 * @auther zhangwei
 * @since 2019-3-1
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.title_view)
    TitleView titleView;
    @BindView(R.id.tv_device_code)
    TextView tvDeviceCode;
    @BindView(R.id.et)
    AppCompatEditText et;
    @BindView(R.id.tv_check)
    AppCompatTextView tvCheck;

    private String machineCode; // 机器码

    public static Intent getLauncher() {
        return new Intent(App.getAppContext(),RegisterActivity.class);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        titleView.setLeftImageVisiable(false);
        titleView.setRightImageVisiable(false);
        titleView.setTitle(getString(R.string.register));
        titleView.requestFocus();

        machineCode = SPUtils.getDeviceId();
        tvDeviceCode.setText(getString(R.string.machine_code) + machineCode);

    }

    @OnClick(R.id.tv_check)
    public void onViewClicked() {
        String etText = et.getText().toString().trim();
        if(TextUtils.isEmpty(etText)) {
            CommonUtils.showToast(getString(R.string.wrong_register_code),true);
            return;
        }

        String realPassword = SPUtils.getMD5(machineCode);
        Log.e("zhangwei"," password : " + realPassword + " ,machine : " + etText);
        if(realPassword.equals(etText)) {
            SPUtils.putBoolean(Constant.HAS_USE_PERMISSION,true);
            startActivity(MainActivity.getLauncher(this));
            finish();
        } else {
            CommonUtils.showToast(getString(R.string.wrong_register_code),true);
        }
    }
}
