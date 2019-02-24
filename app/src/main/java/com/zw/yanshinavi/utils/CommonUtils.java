package com.zw.yanshinavi.utils;

import android.widget.Toast;

import com.zw.yanshinavi.common.App;


public class CommonUtils {

    /**
     * 弹提示
     *
     * @param message 提示消息
     * @param isDurationShort 弹出时间
     */
    public static void showToast(String message, boolean isDurationShort) {
        Toast.makeText(App.getAppContext(),message,
                isDurationShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void showCalculateRouteFail(int errCode){
        String errMsg = "";
        switch (errCode) {
            case 2:
                errMsg = "网络超时，请检查网络是否通畅，稍候再试";
                break;
            case 3:
                errMsg = "经纬度不合法，高德地图仅支持中国境内路径规划";
                break;
            case 6:
                errMsg = "经纬度不合法，高德地图仅支持中国境内路径规划";
                break;
            case 20:
                errMsg = "路径规划失败，起点/终点的距离过长";
                break;
            case 21:
                errMsg = "经纬度不合法，高德地图仅支持中国境内路径规划";
                break;
            case 25:
                errMsg = "经纬度不合法，高德地图仅支持中国境内路径规划";
                break;
            case 26:
                errMsg = "径规划失败，起点/终点的距离过长";
                break;
            default:
                errMsg = "路径规划失败，请稍后再试";
        }
        showToast(errMsg,true);
    }

}
