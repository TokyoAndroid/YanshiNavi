package com.zw.yanshinavi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.event.LocationEvent;

import org.greenrobot.eventbus.EventBus;

public class LocationService extends Service implements AMapLocationListener {

    private static final int LOCATION_TIMEOUT = 10 * 1000; // 定位超时时间

    public double lat, lon; // 经度，纬度

    private boolean isFirstLocation = true; //是否是初次定位

    public static Intent getLancher(Context context) {
        Intent intent = new Intent(context,LocationService.class);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        if( isFirstLocation ) {
            EventBus.getDefault().post(new LocationEvent(lat,lon));
            isFirstLocation = false;
        }
    }

}
