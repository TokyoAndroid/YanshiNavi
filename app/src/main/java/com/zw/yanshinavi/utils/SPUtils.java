package com.zw.yanshinavi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.zw.yanshinavi.common.App;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.UUID;

public class SPUtils {

    private static final String TAG = "SPUtils";

    private static SharedPreferences getSp(){
        return App.getAppContext()
                .getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
    }

    public static void putBoolean(String name, boolean bool) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name,bool);
        editor.commit();
    }

    public static boolean getBoolean(String name) {
        return getSp().getBoolean(name,false);
    }

    public static void putString(String key, String value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(String key) {
        return getSp().getString(key, "");
    }

    /**
     * 获取出行偏好策略值
     *
     * @return
     */
    public static int getStrategy() {
        boolean isNoCar = SPUtils.getBoolean(Constant.SP_IS_NO_CAR);
        boolean isNoHighway = SPUtils.getBoolean(Constant.SP_IS_NO_HIGHWAY);
        boolean isNoCharge = SPUtils.getBoolean(Constant.SP_IS_NO_CHARGE);
        boolean isHighway = SPUtils.getBoolean(Constant.SP_IS_HIGHWAY);
        try{
            return AMapNavi.getInstance(App.getAppContext())
                    .strategyConvert(isNoCar, isNoHighway, isNoCharge, isHighway, true);
        } catch (Exception e) {
            Log.e("zhangwei", TAG + " getStrategy error");
            return 0;
        }
    }

    /**
     * 获取设备唯一Id
     *
     * @return
     */
    public static String getDeviceId() {
        String deviceId = getString(Constant.SP_DEVICE_ID);
        if(!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        StringBuffer sb = new StringBuffer();
        try {
            deviceId = getIMEI();
            sb.append(deviceId);
            Log.e("zhangwei",TAG + " IMEI : " + deviceId);
        } catch (Exception e){
            Log.e("zhangwei",TAG + " getIMEI error : " + e.getMessage());
        }

        try {
            deviceId = getLocalMac().replace(":","");
            sb.append(deviceId);
            Log.e("zhangwei",TAG + " MAC : " + deviceId);
        } catch (Exception e){
            Log.e("zhangwei",TAG + " getMac error : " + e.getMessage());
        }

        if (sb.length() <= 0) {
            UUID uuid = UUID.randomUUID();
            Log.e("zhangwei",TAG + " uuid : " + uuid);
            deviceId = uuid.toString().replace("-", "");
            sb.append(deviceId);
        }
        if(sb.length() > 0) {
            putString(Constant.SP_DEVICE_ID,sb.toString());
        }
        return sb.toString();
    }

    /**
     * 获取设备IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(){
        TelephonyManager tm = (TelephonyManager) App.getAppContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取设备本地Mac地址
     *
     * @return
     */
    private static String getLocalMac() {
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();


            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return macAddress;
    }

    /**
     * 获取加密后的MD5值
     *
     * @param message
     * @return
     */
    public static String getMD5(String message) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] input = message.getBytes();

            byte[] buff = md.digest(input);

            md5str = bytesToHex(buff).trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        if(md5str.length() < 20) {
            int length = md5str.length();
            for (int i = length; i < 20; i++) {
                sb.append("0");
            }
        } else {
            sb.append(md5str.substring(0,20));
        }
        return sb.toString();
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toLowerCase();
    }
}
