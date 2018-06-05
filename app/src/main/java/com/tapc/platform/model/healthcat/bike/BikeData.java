package com.tapc.platform.model.healthcat.bike;

import com.tapc.platform.model.healthcat.BaseDeviceData;

/**
 * Created by Administrator on 2018/4/13.
 */

public class BikeData extends BaseDeviceData {
    //直径
    private int diameter = 78;

    private float speed;
    private int rounds;
    private int heart;
    private int resistance;

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    @Override
    public void clearData() {
        super.clearData();
        speed = 0;
        rounds = 0;
        resistance = 0;
    }
}
