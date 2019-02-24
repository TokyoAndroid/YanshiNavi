package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zw.yanshinavi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteItemView extends FrameLayout {

    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_km)
    TextView tvKm;
    private View rootView;

    private int colorAccent;
    private int colorDivider;
    private int blackTextColor;
    private int whiteTextColor;

    public RouteItemView(@NonNull Context context) {
        this(context, null);
    }

    public RouteItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        colorDivider = ContextCompat.getColor(context, R.color.dividerColor);
        blackTextColor = ContextCompat.getColor(context,R.color.normalTextColor);
        whiteTextColor = ContextCompat.getColor(context, android.R.color.white);

        rootView = LayoutInflater.from(context).inflate(R.layout.layout_route_item, this, false);
        ButterKnife.bind(this, rootView);
        addView(rootView);
    }

    public void setLabel(String label){
        tvLabel.setText(label);
    }

    public void setTime(String time){
        tvTime.setText(time);
    }

    public void setKm(String km){
        tvKm.setText(km);
    }

    public void setItemBackground(boolean isSelect) {
        if(isSelect) {
            rootView.setBackgroundResource(R.drawable.shape_route_item_background);
            tvLabel.setBackgroundColor(colorAccent);
            tvLabel.setTextColor(whiteTextColor);
            tvTime.setTextColor(colorAccent);
            tvKm.setTextColor(colorAccent);
        } else {
            rootView.setBackgroundResource(R.drawable.shape_route_item_background_normal);
            tvLabel.setBackgroundColor(colorDivider);
            tvLabel.setTextColor(blackTextColor);
            tvTime.setTextColor(blackTextColor);
            tvKm.setTextColor(blackTextColor);
        }
    }

    public void setVisiable(boolean isVisiable){
        setVisibility(isVisiable ? VISIBLE : GONE);
    }
}
