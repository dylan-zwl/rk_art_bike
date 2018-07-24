package com.tapc.platform.witget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;

public class FullScreenDialog extends RelativeLayout {
    @ViewInject(R.id.full_switch)
    private ImageView full_switch;

    private boolean isFullScreen = false;

    public FullScreenDialog(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_full_sreen, this, true);
        ViewUtils.inject(this);
    }

    /**
     * 点击全屏
     *
     * @param v
     */
    @OnClick(R.id.full_switch)
    private void fullSwitch(View v) {
        if (isFullScreen) {
            isFullScreen = false;
            TapcApp.getInstance().menuService.showMenuBar();
            full_switch.setImageResource(R.drawable.bg_full_screen);
        } else {
            isFullScreen = true;
            TapcApp.getInstance().menuService.hideMenuBar();
            full_switch.setImageResource(R.drawable.bg_un_full_screen);
        }
    }
}
