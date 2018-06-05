package com.tapc.platform.witget.scancode;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ScanCodeEvent {
    private boolean isVisibility = false;

    public boolean isVisibility() {
        return isVisibility;
    }

    public ScanCodeEvent(boolean visibility) {
        this.isVisibility = visibility;
    }
}
