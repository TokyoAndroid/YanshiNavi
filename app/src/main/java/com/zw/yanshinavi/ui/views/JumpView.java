package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zw.yanshinavi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 跳转条目 公共控件
 *
 * @author zhangwei
 * @since 2019-2-23
 */
public class JumpView extends FrameLayout {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.line)
    View line;

    private View rootView;

    public JumpView(@NonNull Context context) {
        this(context, null);
    }

    public JumpView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JumpView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_jump_item, this, false);
        ButterKnife.bind(this, rootView);
        addView(rootView);
    }

    public void setItemName(String name) {
        tv.setText(name);
    }

    public void setLineVisiable(boolean isVisiable){
        line.setVisibility(isVisiable ? VISIBLE : INVISIBLE);
    }
}
