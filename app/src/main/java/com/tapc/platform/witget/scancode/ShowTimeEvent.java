package com.tapc.platform.witget.scancode;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ShowTimeEvent {
    private String time;

    public ShowTimeEvent(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
