package com.zw.yanshinavi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;

import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.event.LocationEvent;
import com.zw.yanshinavi.services.LocationService;
import com.zw.yanshinavi.ui.base.BaseActivity;
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
 */
public class MainActivity extends BaseActivity {

    // 存放被拒绝的权限
    private List<String> refusePermissions = new ArrayList<>(Constant.PERMISSIONS.length);

    private static final int PERMISSION_REQUEST_CODE = 0x0001; //权限申请请求码

    private double mLat, mLon; // 获取的当前位置

    @BindView(R.id.tv_voice)
    AppCompatTextView tvVoice;
    @BindView(R.id.switch_voice)
    SwitchCompat switchVoice;

    public static Intent getLauncher(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void afterCreate() {
        EventBus.getDefault().register(this);

        initView();

        requestPermission();

    }

    private void initView() {
//                Poi start = new Poi("三元桥", new LatLng(39.96087,116.45798), "");
//                /**终点传入的是北京站坐标,但是POI的ID "B000A83M61"对应的是北京西站，所以实际算路以北京西站作为终点**/
//                Poi end = new Poi("北京站", new LatLng(39.904556, 116.427231), "B000A83M61");
//
//                AmapNaviParams params = new AmapNaviParams(start,null,end,AmapNaviType.DRIVER);
//                AmapNaviPage.getInstance()
//                        .showRouteActivity(MainActivity.this, params, MainActivity.this);


        //在Activity页面调用startActvity启动离线地图组件
//        startActivity(new Intent(App.getAppContext(),
//                OfflineMapActivity.class));

        boolean hasVoice = SPUtils.getBolean(Constant.SP_HAS_VOICE);
        switchVoice.setChecked(hasVoice);
        switchVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.putBoolean(Constant.SP_HAS_VOICE,isChecked);
            }
        });
    }

    /**
     * 查看是否申请了相关权限
     */
    private void requestPermission() {
        refusePermissions.clear();
        for (int i = 0; i < Constant.PERMISSIONS.length; i++) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Constant.PERMISSIONS[i])
                    != PackageManager.PERMISSION_GRANTED) {
                refusePermissions.add(Constant.PERMISSIONS[i]);
            }
        }
        if(!refusePermissions.isEmpty()) {
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
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refusePermissions.remove(0);
                if(!refusePermissions.isEmpty()) {
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
    protected void release() {
        super.release();
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().finishAllActivity();
    }

    /**
     * 开启定位Service
     */
    private void startLocationService(){
        showLoading();
        startService(LocationService.getLancher(this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onLocationEvent(LocationEvent event){
        mLat = event.getLat();
        mLon = event.getLon();
        Log.e("zhangwei","" + mLat + " , lon : " + mLon);
        hideLoading();
    }

    @OnClick(R.id.tv_voice)
    public void onViewClicked() {
        showLoading();
    }
}
