package com.tapc.platform.model.healthcat;

/**
 * Created by Administrator on 2018/4/19.
 */

public class BaseDeviceData {
    private byte status;
    private byte saveStatus;

    private float calorie;
    private int runTime;
    private int distance;

    private int delayAckTime;

    public void saveStatus() {
        saveStatus = status;
    }

    public void restoreStatus() {
        status = saveStatus;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
        if (status != DeviceRunStatus.ERROR) {
            saveStatus = status;
        }
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getDelayAckTime() {
        return delayAckTime;
    }

    public void setDelayAckTime(int delayAckTime) {
        this.delayAckTime = delayAckTime;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void clearData() {
        calorie = 0;
        runTime = 0;
        distance = 0;
    }
}
