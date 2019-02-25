package com.zw.yanshinavi.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.amap.api.maps.offlinemap.OfflineMapActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.ui.views.EditView;
import com.zw.yanshinavi.ui.views.JumpView;
import com.zw.yanshinavi.ui.views.SwitchItemView;
import com.zw.yanshinavi.ui.views.TitleView;
import com.zw.yanshinavi.utils.CommonUtils;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 地图设置界面
 *
 * @author zhangwei
 * @since 2019-2-23
 *
 */
public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.title_view)
    TitleView titleView;
    @BindView(R.id.sw_voice)
    SwitchItemView swVoice;
    @BindView(R.id.sw_road_satus)
    SwitchItemView swRoadSatus;
    @BindView(R.id.sw_no_car)
    SwitchItemView swNoCar;
    @BindView(R.id.sw_no_charge)
    SwitchItemView swNoCharge;
    @BindView(R.id.sw_no_highway)
    SwitchItemView swNoHighway;
    @BindView(R.id.sw_hightway)
    SwitchItemView swHightway;
    @BindView(R.id.jv_offline_map)
    JumpView jvOfflineMap;
    @BindView(R.id.et_car_number)
    EditView etCarNumber;

    // 由于复用了switch，id都是一样的，因此需要手动设置Id
    private int[] switchIds = {0x0001, 0x0002, 0x0003, 0x0004
            , 0x0005, 0x0006};
    private AlertDialog mSetCarDialog;
    private AppCompatEditText carEditText;

    public static Intent getLauncher(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        titleView.setTitle(getResources().getString(R.string.map_setting));
        titleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setRightImageVisiable(false);

        // 语音提示
        swVoice.setText(getResources().getString(R.string.navi_voice));
        swVoice.setSelectId(switchIds[0]);
        boolean hasVoice = SPUtils.getBoolean(Constant.SP_HAS_VOICE);
        swVoice.setSwitchChecked(hasVoice);
        swVoice.setOnSwitchClickListener(this);

        // 实时路况
        swRoadSatus.setText(getResources().getString(R.string.road_status));
        swRoadSatus.setSelectId(switchIds[1]);
        boolean hasRoadStatus = SPUtils.getBoolean(Constant.SP_HAS_ROAD_STATUS);
        swRoadSatus.setSwitchChecked(hasRoadStatus);
        swRoadSatus.setOnSwitchClickListener(this);

        // 路线规划
        swNoCar.setText(getString(R.string.no_car));
        swNoCar.setSelectId(switchIds[2]);
        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        swNoCar.setSwitchChecked(isNoCar);
        swNoCar.setOnSwitchClickListener(this);
        swNoCar.setLineVisible(false);

        swNoCharge.setText(getString(R.string.no_charge));
        swNoCharge.setSelectId(switchIds[3]);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        swNoCharge.setSwitchChecked(isNoCharge);
        swNoCharge.setOnSwitchClickListener(this);
        swNoCharge.setLineVisible(false);

        swNoHighway.setText(getString(R.string.no_highway));
        swNoHighway.setSelectId(switchIds[4]);
        boolean isNoHightWay = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        swNoHighway.setSwitchChecked(isNoHightWay);
        swNoHighway.setOnSwitchClickListener(this);
        swNoHighway.setLineVisible(false);

        swHightway.setText(getString(R.string.first_highway));
        swHightway.setSelectId(switchIds[5]);
        boolean isHighWay = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
        swHightway.setSwitchChecked(isHighWay);
        swHightway.setOnSwitchClickListener(this);

        // 离线地图
        jvOfflineMap.setItemName(getString(R.string.offline_map));
        jvOfflineMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在Activity页面调用startActvity启动离线地图组件
                startActivity(new Intent(App.getAppContext(), OfflineMapActivity.class));
            }
        });

        // 设置车牌号
        etCarNumber.refreshCarNumber();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == swVoice.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_HAS_VOICE, isChecked);
        } else if (id == swRoadSatus.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_HAS_ROAD_STATUS, isChecked);
        } else if (id == swNoCar.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_IS_NO_CAR, isChecked);
        } else if (id == swNoCharge.getSelectId()) {
            if (isChecked) {
                boolean isHighWay = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
                if (isHighWay) {
                    swHightway.setSwitchChecked(false);
                    CommonUtils.showToast(getString(R.string.cannot_highway_and_no_charge)
                            , true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_NO_CHARGE, isChecked);
        } else if (id == swNoHighway.getSelectId()) {
            if (isChecked) {
                boolean isHighWay = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
                if (isHighWay) {
                    swHightway.setSwitchChecked(false);
                    CommonUtils.showToast(getString(R.string.cannot_highway_and_not_highway)
                            , true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_NO_HIGHWAY, isChecked);
        } else if (id == swHightway.getSelectId()) {
            if (isChecked) {
                boolean isNoHighWay = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
                boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
                if (isNoHighWay) {
                    swNoHighway.setSwitchChecked(false);
                    CommonUtils.showToast(getString(R.string.cannot_highway_and_not_highway)
                            , true);
                }
                if (isNoCharge) {
                    swNoCharge.setSwitchChecked(false);
                    CommonUtils.showToast(getString(R.string.cannot_highway_and_no_charge)
                            , true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_HIGHWAY, isChecked);
        }

    }

    @OnClick(R.id.et_car_number)
    public void onViewClicked() {
        showSetCarDialog();
    }

    private void showSetCarDialog(){
        if(mSetCarDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_edit_car_number,null);
            carEditText = contentView.findViewById(R.id.et_car_number);
            builder.setView(contentView);
            builder.setTitle(R.string.set_car_number);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSetCarDialog.dismiss();

                    String carNumberStr = carEditText.getText().toString().trim();
                    if(carNumberStr.length() > 10) {
                        CommonUtils.showToast(getString(R.string.car_number_too_long)
                                ,true);
                        return;
                    }

                    SPUtils.putString(Constant.SP_CAR_NUMBER,carNumberStr);
                    etCarNumber.refreshCarNumber();
                }
            });
            mSetCarDialog = builder.create();
        }
        if(!mSetCarDialog.isShowing()) {
            String carNumbar = SPUtils.getString(Constant.SP_CAR_NUMBER);
            carEditText.setText(carNumbar);
            carEditText.setSelection(carNumbar.length());
            mSetCarDialog.show();
        }
    }
}
