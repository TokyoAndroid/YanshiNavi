package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class ForceDialog extends AlertDialog {

    private Builder builder;

    protected ForceDialog(@NonNull Context context, String title, String message) {
        this(context, 0);

    }

    private ForceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(false);
    }
}
