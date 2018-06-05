package com.tapc.platform.witget.scancode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;

/**
 * Created by Administrator on 2017/8/28.
 */

public abstract class BaseView extends LinearLayout {
    protected Context mContext;

    protected abstract int getLayoutResID();

    public BaseView(Context context) {
        super(context);
        init(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutResID(), this, true);
        ViewUtils.inject(this);
        mContext = context;
        initView();
    }

    protected void initView() {
    }

    public void onDestroy() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
