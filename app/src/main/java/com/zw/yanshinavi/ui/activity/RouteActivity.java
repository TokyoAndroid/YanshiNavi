package com.zw.yanshinavi.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapRestrictionInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPath;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.zw.yanshinavi.R;
import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.ui.base.BaseActivity;
import com.zw.yanshinavi.ui.views.RouteItemView;
import com.zw.yanshinavi.ui.views.TitleView;
import com.zw.yanshinavi.utils.CommonUtils;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.DisplayUtils;
import com.zw.yanshinavi.utils.SPUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 路径导航页面
 *
 * @author zhangwei
 * @since 2019-2-24
 */
public class RouteActivity extends BaseActivity implements View.OnClickListener, AMapNaviListener {

    private static final String TAG = "RouteActivity";

    @BindView(R.id.title_view)
    TitleView titleView;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tv_select_road)
    TextView tvSelectRoad;
    @BindView(R.id.tv_start_navi)
    TextView tvStartNavi;

    private AMap aMap;
    private Marker mStartMarker;
    private Marker mEndMarker;
    private AMapNavi aMapNavi;
    private List<NaviLatLng> mStartList;
    private List<NaviLatLng> mEndList;
    private boolean calculateSuccess; // 算路是否成功
    private int routeCount = 0; // 可选路线总数

    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    /**
     * 路线的权值，重合路线情况下，权值高的路线会覆盖权值低的路线
     **/
    private int zindex = 1;

    private AlertDialog mSelectDialog;
    private RouteItemView routeItemView1;
    private RouteItemView routeItemView2;
    private RouteItemView routeItemView3;
    private List<RouteItemView> routeItemViews = new ArrayList<>();

    public static Intent getLauncher(double startLat, double startLon,
                                     double endLat, double endLon) {
        Intent intent = new Intent(App.getAppContext(), RouteActivity.class);
        intent.putExtra("startLat", startLat);
        intent.putExtra("startLon", startLon);
        intent.putExtra("endLat", endLat);
        intent.putExtra("endLon", endLon);
        return intent;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_route;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        initIntent();

        initView(savedInstanceState);

        startCalculate();
    }

    /**
     * 获取起点终点坐标
     */
    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            double startLat = intent.getDoubleExtra("startLat", 0);
            double startLon = intent.getDoubleExtra("startLon", 0);
            double endLat = intent.getDoubleExtra("endLat", 0);
            double endLon = intent.getDoubleExtra("endLon", 0);

            NaviLatLng startLatLon = new NaviLatLng(startLat, startLon);
            NaviLatLng endLatLon = new NaviLatLng(endLat, endLon);
            mStartList = new ArrayList<>(1);
            mEndList = new ArrayList<>(1);
            mStartList.add(startLatLon);
            mEndList.add(endLatLon);
        }
    }

    private void initView(@Nullable Bundle savedInstanceState) {
        // 设置标题
        titleView.setTitle("导航规划");
        titleView.setRightImageVisiable(false);
        titleView.setLeftClickListener(this);

        // 地图初始化
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        mStartMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start))));
        mEndMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.end))));

        setMapCenter();

        aMapNavi = AMapNavi.getInstance(App.getAppContext());
        aMapNavi.addAMapNaviListener(this);
    }

    private void selectRoute(){
        if(mSelectDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_route_info,null);
            routeItemView1 = contentView.findViewById(R.id.item_view1);
            routeItemView2 = contentView.findViewById(R.id.item_view2);
            routeItemView3 = contentView.findViewById(R.id.item_view3);
            routeItemViews.add(routeItemView1);
            routeItemViews.add(routeItemView2);
            routeItemViews.add(routeItemView3);

            int size = routeOverlays.size() < 3 ? routeOverlays.size() : 3;
            for (int i = 0; i < size; i++) {
                aMapNavi.selectRouteId(routeOverlays.keyAt(i));
                AMapNaviPath path = aMapNavi.getNaviPath();
                String label = path.getLabels();
                String cost = getCostTime(path.getAllTime());
                String km = getKm(path.getAllLength());
                RouteItemView view = routeItemViews.get(i);
                Log.e("zhangwei", " i : " + i + " lab " + label + " , " + cost + " , " + km);
                view.setLabel(label);
                view.setTime(cost);
                view.setKm(km);
                view.setOnClickListener(this);
                view.setItemBackground(i == 0);
                view.setVisiable(true);
            }
            aMapNavi.selectRouteId(routeOverlays.keyAt(0));

            builder.setView(contentView);
            mSelectDialog = builder.create();
            mSelectDialog.setCanceledOnTouchOutside(false);
        }
        if(!mSelectDialog.isShowing()) {
            mSelectDialog.show();
        }
    }

    private void changeRoute(int index){
        if(mSelectDialog != null && mSelectDialog.isShowing()) {
            mSelectDialog.dismiss();
        }

        int routeID = routeOverlays.keyAt(index);

        //突出选择的那条路
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            routeOverlays.get(key).setTransparency(0.4f);
        }
        RouteOverLay routeOverlay = routeOverlays.get(routeID);
        if(routeOverlay != null){
            routeOverlay.setTransparency(1);
            routeOverlay.zoomToSpan(DisplayUtils.dip2px(this, 100));
            /**把用户选择的那条路的权值弄高，使路线高亮显示的同时，重合路段不会变的透明**/
            routeOverlay.setZindex(zindex++);
        }
        //必须告诉AMapNavi 你最后选择的哪条路
        aMapNavi.selectRouteId(routeID);
        /**选完路径后判断路线是否是限行路线**/
        AMapRestrictionInfo info = aMapNavi.getNaviPath().getRestrictionInfo();
        if (!TextUtils.isEmpty(info.getRestrictionTitle())) {
            CommonUtils.showToast(info.getRestrictionTitle(),true);
        }

        if(!routeItemViews.isEmpty()) {
            for (int i = 0; i < routeItemViews.size(); i++) {
                routeItemViews.get(i).setItemBackground(i == index);
            }
        }
    }

    /**
     * 获取导航耗时
     *
     * @param time 单位:秒
     */
    private String getCostTime(int time){
        int min = time / 60;
        int hour = min / 60;
        if(hour != 0) {
            int min1 = min % 60;
            return hour + "小时" + min1 + "分钟";
        }
        return min + "分钟";
    }

    /**
     * 获取总里程
     *
     * @param length
     */
    private String getKm(int length){
        int km = length / 1000;
        if(km != 0){
            float km1 = length / 1000f;
            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(km1) + "公里";
        }
        return length + "米";
    }

    /**
     * 开始规划路径
     */
    private void startCalculate() {
        if (mStartList == null || mEndList == null) {
            CommonUtils.showToast("没有获取到合法坐标！", false);
            finish();
            return;
        }

        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        boolean isNoHighway = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        boolean isHighway = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
        int strategyFlag = aMapNavi.strategyConvert(isNoCar, isNoHighway, isNoCharge, isHighway, true);
        aMapNavi.calculateDriveRoute(mStartList, mEndList, null, strategyFlag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        aMapNavi.removeAMapNaviListener(this);
        aMapNavi.destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_view1:
                changeRoute(0);
                break;
            case R.id.item_view2:
                changeRoute(1);
                break;
            case R.id.item_view3:
                changeRoute(2);
                break;

        }
    }

    @OnClick({R.id.tv_select_road, R.id.tv_start_navi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_road:
                selectRoute();
                break;
            case R.id.tv_start_navi:
                break;
        }
    }


    /**
     * 规划路径成功后绘制路径
     *
     * @param routeId
     * @param path
     */
    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    private void setMapCenter() {
        double startLat = mStartList.get(0).getLatitude();
        double startLon = mStartList.get(0).getLongitude();
        double endLat = mEndList.get(0).getLatitude();
        double endLon = mEndList.get(0).getLongitude();
        double centerLat = (startLat + endLat) / 2;
        double centerLon = (startLon + endLon) / 2;

        LatLng start = new LatLng(startLat, startLon);
        LatLng end = new LatLng(endLat, endLon);
        LatLng center = new LatLng(centerLat, centerLon);
        float distance = AMapUtils.calculateLineDistance(start, end);
//
//        int zoom = 0;
//        if (distance < 20 * 1000) {
//            zoom = 12;
//        } else if (distance < 100 * 1000) {
//            zoom = 10;
//        } else if (distance < 400 * 1000) {
//            zoom = 8;
//        } else {
//            zoom = 5;
//        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 5));
    }


    /**
     * 算路失败回调
     *
     * @param i 错误码
     */
    @Override
    public void onCalculateRouteFailure(int i) {
        calculateSuccess = false;
        CommonUtils.showCalculateRouteFail(i);
        finish();
    }

    /**
     * 算路成功回调
     *
     * @param ints
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        Log.e("zhangwei",TAG + " onCalculateRouteSuccess");
        routeOverlays.clear();
        HashMap<Integer, AMapNaviPath> paths = aMapNavi.getNaviPaths();
        routeCount = paths.size();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
            }
        }
        changeRoute(0);
    }


    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }


    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }


    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }


}
