package com.zw.yanshinavi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.amap.api.maps.offlinemap.OfflineMapActivity;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.ui.views.JumpView;
import com.zw.yanshinavi.ui.views.SwitchItemView;
import com.zw.yanshinavi.ui.views.TitleView;
import com.zw.yanshinavi.utils.CommonUtils;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    // 由于复用了switch，id都是一样的，因此需要手动设置Id
    private int[] switchIds = {0x0001, 0x0002, 0x0003, 0x0004
            , 0x0005, 0x0006};

    public static Intent getLauncher(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
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
        titleView.setTitle("地图设置");
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
        swNoCar.setText("躲避拥堵");
        swNoCar.setSelectId(switchIds[2]);
        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        swNoCar.setSwitchChecked(isNoCar);
        swNoCar.setOnSwitchClickListener(this);
        swNoCar.setLineVisible(false);

        swNoCharge.setText("避免收费");
        swNoCharge.setSelectId(switchIds[3]);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        swNoCharge.setSwitchChecked(isNoCharge);
        swNoCharge.setOnSwitchClickListener(this);
        swNoCharge.setLineVisible(false);

        swNoHighway.setText("不走高速");
        swNoHighway.setSelectId(switchIds[4]);
        boolean isNoHightWay = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        swNoHighway.setSwitchChecked(isNoHightWay);
        swNoHighway.setOnSwitchClickListener(this);
        swNoHighway.setLineVisible(false);

        swHightway.setText("高速优先");
        swHightway.setSelectId(switchIds[5]);
        boolean isHighWay = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
        swHightway.setSwitchChecked(isHighWay);
        swHightway.setOnSwitchClickListener(this);

        // 离线地图
        jvOfflineMap.setItemName("离线地图");
        jvOfflineMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在Activity页面调用startActvity启动离线地图组件
                startActivity(new Intent(App.getAppContext(),OfflineMapActivity.class));
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Log.e("zhangwei", " id : " + id);
        if (id == swVoice.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_HAS_VOICE, isChecked);
        } else if (id == swRoadSatus.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_HAS_ROAD_STATUS, isChecked);
        }
        //            不走高速与高速优先不能同时为true
        //            高速优先与避免收费不能同时为true
        else if (id == swNoCar.getSelectId()) {
            SPUtils.putBoolean(Constant.SP_IS_NO_CAR, isChecked);
        } else if (id == swNoCharge.getSelectId()) {
            if (isChecked) {
                boolean isHighWay = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
                if (isHighWay) {
                    swHightway.setSwitchChecked(false);
                    CommonUtils.showToast("高速优先与避免收费不能同时选择", true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_NO_CHARGE, isChecked);
        } else if (id == swNoHighway.getSelectId()) {
            if (isChecked) {
                boolean isHighWay1 = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
                if (isHighWay1) {
                    swHightway.setSwitchChecked(false);
                    CommonUtils.showToast("高速优先与不走高速不能同时选择", true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_NO_HIGHWAY, isChecked);
        } else if (id == swHightway.getSelectId()) {
            if (isChecked) {
                boolean isNoHighWay = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
                boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
                if (isNoHighWay) {
                    swNoHighway.setSwitchChecked(false);
                    CommonUtils.showToast("高速优先与不走高速不能同时选择", true);
                }
                if (isNoCharge) {
                    swNoCharge.setSwitchChecked(false);
                    CommonUtils.showToast("高速优先与避免收费不能同时选择", true);
                }
            }
            SPUtils.putBoolean(Constant.SP_IS_HIGHWAY, isChecked);
        }

    }
}
