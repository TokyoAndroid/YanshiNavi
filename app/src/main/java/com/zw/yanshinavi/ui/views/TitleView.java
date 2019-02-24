package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zw.yanshinavi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用标题类
 *
 * @author zhangwei
 * @since 2019-2-23
 */
public class TitleView extends FrameLayout {

    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    private View rootView;

    public TitleView(@NonNull Context context) {
        this(context, null);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_title, this, false);
        ButterKnife.bind(this, rootView);
        addView(rootView);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setLeftClickListener(OnClickListener listener){
        ivBack.setOnClickListener(listener);
    }

    public void setRightClickListener(OnClickListener listener){
        ivRight.setOnClickListener(listener);
    }

    public void setLeftImageSrc(@DrawableRes int resId) {
        ivBack.setImageResource(resId);
    }

    public void setRightImageSrc(@DrawableRes int resId){
        ivRight.setImageResource(resId);
    }

    public void setLeftImageVisiable(boolean isVisiable) {
        ivBack.setVisibility(isVisiable ? VISIBLE : INVISIBLE);
    }

    public void setRightImageVisiable(boolean isVisiable){
        ivRight.setVisibility(isVisiable ? VISIBLE : INVISIBLE);
    }

}
