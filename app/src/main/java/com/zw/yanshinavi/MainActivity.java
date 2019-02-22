package com.zw.yanshinavi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.common.AppManager;
import com.zw.yanshinavi.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements INaviInfoCallback, AMapLocationListener {

    private static final int LOCATION_TIMEOUT = 10 * 1000; // 定位超时时间

    public double lat, lon; // 经度，纬度

    private boolean isFirstLocation = true; //是否是初次定位

    /**
     * 调试 sha1  CF:8D:F6:19:CA:A0:94:EC:96:19:E3:C5:95:3E:7E:5C:9E:CA:6E:F6
     *
     *
     */

    public static Intent getLauncher(Context context) {
        Intent intent = new Intent(context,MainActivity.class);
        return intent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void afterCreate() {
        initView();

    }

    private void initView(){
        findViewById(R.id.helloworld).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Poi start = new Poi("三元桥", new LatLng(39.96087,116.45798), "");
//                /**终点传入的是北京站坐标,但是POI的ID "B000A83M61"对应的是北京西站，所以实际算路以北京西站作为终点**/
//                Poi end = new Poi("北京站", new LatLng(39.904556, 116.427231), "B000A83M61");
//
//                AmapNaviParams params = new AmapNaviParams(start,null,end,AmapNaviType.DRIVER);
//                AmapNaviPage.getInstance()
//                        .showRouteActivity(MainActivity.this, params, MainActivity.this);


                //在Activity页面调用startActvity启动离线地图组件
                startActivity(new Intent(App.getAppContext(),
                        com.amap.api.maps.offlinemap.OfflineMapActivity.class));
            }
        });
    }

    @Override
    protected void release() {
        super.release();
        AppManager.getInstance().finishAllActivity();
    }

    private void initLocation() {
        AMapLocationClient mLocationClient = new AMapLocationClient(App.getAppContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        option.setHttpTimeOut(LOCATION_TIMEOUT);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(option);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        lat = aMapLocation.getLatitude(); // 获取纬度
        lon = aMapLocation.getLongitude(); // 获取经度

    }

    @Override
    public void onInitNaviFailure() {
        Log.e("zhangwei","onInitNaviFailure");
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }
}
