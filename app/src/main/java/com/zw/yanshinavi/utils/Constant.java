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
}
