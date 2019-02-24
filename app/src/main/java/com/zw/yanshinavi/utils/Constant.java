package com.zw.yanshinavi.utils;

import android.Manifest;

public class Constant {

    // SharedPreferences名称
    public static final String SP_NAME = "config";

    // 使用权限
    public static final String HAS_USE_PERMISSION = "use_permission";

    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
    };

    public static final String SP_HAS_VOICE = "sp_has_voice";
    public static final String SP_HAS_ROAD_STATUS = "sp_has_voice";
    public static final String SP_IS_NO_CAR = "sp_is_no_car";
    public static final String SP_IS_NO_CHARGE = "sp_is_no_charge";
    public static final String SP_IS_NO_HIGHWAY = "sp_is_no_highway";
    public static final String SP_IS_HIGHWAY = "sp_is_highway";
}
