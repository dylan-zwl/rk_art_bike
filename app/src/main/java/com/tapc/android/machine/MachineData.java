package com.tapc.android.machine;

public abstract class MachineData {
	
	int mResistance;
	double mSpeed;
	double mCalorie;
	double mPaceRate;
	int mHeartRate;
	double mIncline;	

	public int getResistance() {
		return mResistance;
	}

	public void setResistance(int resistance) {
		this.mResistance = resistance;
	}

	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double speed) {
		this.mSpeed = speed;
	}

	
/*	public double getCalorie(double weight) {
		return (mCalorie=calculateCalorie(weight));
	}

	public void setCalorie(double calorie) {
		this.mCalorie = calorie;
	}
*/
	public int getHeartRate() {
		return mHeartRate;
	}

	public void setHeartRate(int heartRate) {
		this.mHeartRate = heartRate;
	}
	
	public double getPaceRate() {
		return mPaceRate;
	}

	public void setPaceRate(double paceRate) {
		this.mPaceRate = paceRate;
	}
	
	public double getIncline() {
		return mIncline;
	}

	public void setIncline(double incline) {
		this.mIncline = incline;
	}
	
	public double calculateCalorie(double weight) {
		double calorie = 0;
		double speed = mSpeed;
		double incline = mIncline;
		speed *= 1000;				
		speed /= 1609;

		if (speed>4)
		{
			calorie  = speed*53600;
			calorie += incline*speed*2412;
		}
		else
		{
			calorie  = speed*26800;
			calorie += incline*speed*4824;
		}
		calorie += 35000;
		calorie *= weight;
		calorie /= 12000;
		calorie /= 10000;
		
		return calorie;
	}

	public void calculateMets() {
	}
	
	public abstract void calculateWatts();
	public abstract void setRightPanel(double value);
	public abstract double getRightPanel();
}


