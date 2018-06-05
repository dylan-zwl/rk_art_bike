package com.tapc.android.machine;

import android.util.Log;

import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.workouting.IntervalInfo;

public class Treadmill extends Machine {

	static Treadmill mInst = null;

	public static Treadmill getIntance() {
		if (null == mInst) {
			mInst = new Treadmill();
		}

		return mInst;
	}

	@Override
	public void setLeftPanel(double value) {
		super.setIncline(value);
	}

	@Override
	public void setRightPanel(double value) {
		super.setSpeed(value);
	}

	@Override
	public void loadInterval(IntervalInfo interval) {
		Log.e("loadInterval", "IntervalInfo");
		if (interval.getSpeed() != -1) {
			super.setSpeed(interval.getSpeed());
		} else {
			super.setSpeed(getSpeed());
		}
		if (interval.getIncline() != -1) {
			super.setIncline(interval.getIncline());
		}
	}

	@Override
	public void resume() {
		// resume speed
		mController.startMachine((int) (getSpeed() * 10), (int) (getIncline()));
		// setRightPanel(getSpeed());
	}

	@Override
	public void pause() {
		// set RPM To 0
//		setRPM(0);
		// mController.setSpeed(0);
		mController.stopMachine((int) (getIncline()));
	}

	@Override
	public void stop() {
		// set RPM To 0
		mController.stopMachine((int) (MIN_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE));
		// setRPM(0);
		// set incline To min value
		// setIncline(MIN_INCLINE+SystemSettingsHelper.ORIGIN_INCLINE);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		mController.startMachine((int) (getSpeed() * 10), (int) (getIncline()));
	}
}
