package com.zw.yanshinavi.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zw.yanshinavi.R;
import com.zw.yanshinavi.utils.Constant;
import com.zw.yanshinavi.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 编辑类公共控件
 *
 * @author zhangwei
 * @since 2019-2-25
 */
public class EditView extends FrameLayout {

    @BindView(R.id.tv_set_number)
    TextView tvSetNumber;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;
    @BindView(R.id.line)
    View line;
    private View rootView;

    public EditView(@NonNull Context context) {
        this(context, null);
    }

    public EditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
        int[] attribute = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = context.getTheme().obtainStyledAttributes(tv.resourceId, attribute);
        setBackground(ta.getDrawable(0));

        rootView = LayoutInflater.from(context).inflate(R.layout.layout_setting_car_number,
                this, false);
        ButterKnife.bind(this, rootView);
        addView(rootView);
    }

    public void refreshCarNumber() {
        String carNumberStr = SPUtils.getString(Constant.SP_CAR_NUMBER).trim();
        if(TextUtils.isEmpty(carNumberStr)) {
            tvCarNumber.setText(R.string.not_setting);
        } else {
            tvCarNumber.setText(carNumberStr);
        }
    }
}
