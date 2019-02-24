package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.zw.yanshinavi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwitchItemView extends FrameLayout {

    private View rootView;
    private int selectId; // 点击事件的Id

    @BindView(R.id.tv)
    AppCompatTextView tv;
    @BindView(R.id.sw)
    SwitchCompat sw;
    @BindView(R.id.line)
    View line;


    public SwitchItemView(@NonNull Context context) {
        this(context, null);
    }

    public SwitchItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_switch_item, this, false);
        addView(rootView);
        ButterKnife.bind(this, rootView);
    }

    public void setText(String text) {
        tv.setText(text);
    }

    public void setSwitchChecked(boolean isCheck) {
        sw.setChecked(isCheck);
    }

    public void setOnSwitchClickListener(CompoundButton.OnCheckedChangeListener listener) {
        sw.setOnCheckedChangeListener(listener);
    }

    public void setLineVisible(boolean isVisibility) {
        line.setVisibility(isVisibility ? VISIBLE : GONE);
    }

    public void setSelectId(int id) {
        this.selectId = id;
        sw.setId(id);
    }

    public int getSelectId(){
        return selectId;
    }

}
