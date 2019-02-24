package com.zw.yanshinavi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;

import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.enums.PathPlanningStrategy;

import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.event.LocationEvent;
import com.zw.yanshinavi.services.LocationService;
import com.zw.yanshinavi.ui.activity.RouteActivity;
import com.zw.yanshinavi.ui.activity.SettingActivity;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.ui.views.TitleView;
import com.zw.yanshinavi.utils.CommonUtils;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 调试 sha1  CF:8D:F6:19:CA:A0:94:EC:96:19:E3:C5:95:3E:7E:5C:9E:CA:6E:F6
 * <p>
 * taishi : 17:02:19:67:57:D4:F4:AF:3E:AE:22:1F:95:65:9A:27:FD:F7:8D:D0
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.cv_road_rout)
    CardView cvRoadRout;
    @BindView(R.id.cv_map_setting)
    CardView cvMapSetting;
    @BindView(R.id.btn_start_navi)
    AppCompatButton btnStartNavi;
    @BindView(R.id.title_view)
    TitleView titleView;

    // 存放被拒绝的权限
    private List<String> refusePermissions = new ArrayList<>(Constant.PERMISSIONS.length);

    private static final int PERMISSION_REQUEST_CODE = 0x0001; //权限申请请求码

    private double mLat, mLon; // 获取的当前位置
    private String streetName; // 获取的当前位置街道名称

    public static Intent getLauncher(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {

        initView();

        requestPermission();
    }

    private void initView() {
        // 设置标题
        titleView.setTitle("Runbo智能导航");
        titleView.setLeftImageVisiable(false);
        titleView.setRightImageVisiable(false);
    }

    /**
     * 查看是否申请了相关权限
     */
    private void requestPermission() {
        refusePermissions.clear();
        for (int i = 0; i < Constant.PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Constant.PERMISSIONS[i])
                    != PackageManager.PERMISSION_GRANTED) {
                refusePermissions.add(Constant.PERMISSIONS[i]);
            }
        }
        if (!refusePermissions.isEmpty()) {
            ActivityCompat.
                    requestPermissions(this, new String[]{refusePermissions.get(0)}, PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    /**
     * 权限申请处理回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refusePermissions.remove(0);
                if (!refusePermissions.isEmpty()) {
                    ActivityCompat.
                            requestPermissions(this, new String[]{refusePermissions.get(0)}, PERMISSION_REQUEST_CODE);
                } else {
                    startLocationService();
                }
            } else {
                AppManager.getInstance().finishAllActivity();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("zhangwei", "onConfigurationChanged");
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.e("zhangwei", "port");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("zhangwei", "land");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void release() {
        super.release();
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().finishAllActivity();
    }

    /**
     * 开启定位Service
     */
    private void startLocationService() {
        EventBus.getDefault().register(this);
        showLoading();
        setLoadingCancelable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(LocationService.getLancher(this));
        } else {
            startService(LocationService.getLancher(this));
        }
    }

    /**
     * 定位成功后的回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onLocationEvent(LocationEvent event) {
        mLat = event.getLat();
        mLon = event.getLon();
        streetName = event.getStreetName();
        Log.e("zhangwei", "" + mLat + " , lon : " + mLon);
        hideLoading();
    }

    @OnClick({R.id.cv_road_rout, R.id.cv_map_setting, R.id.btn_start_navi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_road_rout:
                // 112.24608,30.333196 模拟点位
                if (mLat == 0 || mLon == 0) {
                    CommonUtils.showToast("未获取到当前坐标，请确认定位是否已开启！", false);
                } else {
                    Poi start = new Poi(streetName, new LatLng(mLat, mLon), "");
                    Poi end = new Poi("点位", new LatLng(30.333196, 112.24608), "");

                    AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER);
                    boolean isShowRoadStatus = SPUtils.getBoolean(Constant.SP_HAS_ROAD_STATUS);
                    boolean isSpeaking = SPUtils.getBoolean(Constant.SP_HAS_VOICE);
                    int strategy = getStrategy();
                    params.setTrafficEnabled(isShowRoadStatus);
                    params.setUseInnerVoice(isSpeaking);
                    params.setRouteStrategy(strategy);
                    AmapNaviPage.getInstance()
                            .showRouteActivity(MainActivity.this, params, null);
                }
                break;
            case R.id.cv_map_setting:
                startActivity(SettingActivity.getLauncher(this));
                break;
            case R.id.btn_start_navi:
                // 112.24608,30.333196 模拟点位
                if (mLat == 0 || mLon == 0) {
                    CommonUtils.showToast("未获取到当前坐标，请确认定位是否已开启！", false);
                } else {
                    startActivity(RouteActivity.getLauncher(mLat,mLon,30.333196,112.24608));
                }
                break;
        }
    }

    /**
     * 获取出行偏好
     */
    private int getStrategy() {
        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        boolean isNoHighway = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        boolean isHighway = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);

        int strategy = 10; // 高德地图默认不选策略为10

        if(isNoCar && !isNoHighway && !isNoCharge && !isHighway) {
            // 躲避拥堵
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_CONGESTION;
        } else if(isNoCar && isNoHighway && !isNoCharge && !isHighway) {
            // 躲避拥堵&不走高速
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_CONGESTION;
        } else if(isNoCar && !isNoHighway && isNoCharge && !isHighway) {
            // 躲避拥堵&躲避收费
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_COST_CONGESTION;
        }  else if(isNoCar && !isNoHighway && isNoCharge && !isHighway) {
            // 躲避拥堵&高速优先
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED_AVOID_CONGESTION;
        } else if(isNoCar && isNoHighway && isNoCharge && !isHighway){
            // 躲避拥堵&不走高速&躲避收费
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_COST_CONGESTION;
        } else if(!isNoCar && isNoHighway && !isNoCharge && !isHighway){
            // 不走高速
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED;
        } else if(!isNoCar && isNoHighway && isNoCharge && !isHighway){
            // 不走高速&躲避收费
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_HIGHTSPEED_COST;
        } else if(!isNoCar && !isNoHighway && isNoCharge && !isHighway){
            // 躲避收费
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_AVOID_COST;
        } else if(!isNoCar && !isNoHighway && !isNoCharge && isHighway){
            // 高速优先
            strategy = PathPlanningStrategy
                    .DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED;
        }
        return strategy;
    }

    @Override
    public void onBackPressed() {
        if(isLoadingShowing()) {
            hideLoading();
        }
        AppManager.getInstance().finishAllActivity();
        super.onBackPressed();
    }
}
