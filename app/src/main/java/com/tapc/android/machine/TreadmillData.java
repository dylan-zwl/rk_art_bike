package com.tapc.android.machine;

public class TreadmillData extends MachineData {

	@Override
	public void setRightPanel(double value) {
		mSpeed = value;
	}

	@Override
	public double getRightPanel() {
		// TODO Auto-generated method stub
		return mSpeed;
	}

	@Override
	public void calculateWatts() {
		// TODO Auto-generated method stub
	}
}
