package com.tapc.platform.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.witget.MenuBar;

public class MenuService extends Service {
    private WindowManager.LayoutParams mMenuBarParams;
    private MenuBar mMenuBar;
    private WindowManager mWindowManager;
    private boolean menubarShown = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
    }

    public void onStart(Intent intent, int startId) {
        TapcApp.getInstance().menuService = this;
        mWindowManager = (WindowManager) getSystemService("window");
        createMenuBar();
        showMenuBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("stop sevice", "MenuService");
        hideMenuBar();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressLint("InlinedApi")
    private void createMenuBar() {
        mMenuBarParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, (int) getResources()
                .getDimension(R.dimen.menuBar),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT);
        mMenuBarParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mMenuBarParams.x = 0;
        mMenuBarParams.y = 0;
        mMenuBar = new MenuBar(this);

        TapcApp.getInstance().menuBar = mMenuBar;
    }

    public void showMenuBar() {
        if (menubarShown)
            return;
        mWindowManager.addView(mMenuBar, mMenuBarParams);
        menubarShown = true;
    }

    public void hideMenuBar() {
        if (!menubarShown)
            return;
        mWindowManager.removeView(mMenuBar);
        menubarShown = false;
    }
}
