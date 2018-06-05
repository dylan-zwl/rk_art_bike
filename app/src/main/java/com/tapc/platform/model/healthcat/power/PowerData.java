package com.tapc.platform.model.healthcat.power;

import com.tapc.platform.model.healthcat.BaseDeviceData;

/**
 * Created by Administrator on 2018/4/13.
 */

public class PowerData extends BaseDeviceData {
    private int heartTime;

    private int weight;
    private int times;

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    @Override
    public void clearData() {
        super.clearData();
        weight = 0;
        times = 0;
    }
}
