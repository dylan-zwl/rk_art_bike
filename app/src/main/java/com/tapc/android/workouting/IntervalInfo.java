package com.tapc.android.workouting;

import com.tapc.android.data.Enum.WorkoutStage;

public class IntervalInfo {

	int mWorkoutLevel;

	double mSpeed = -1;
	double mIncline = -1;
	int mResistance = -1;

	double mChangeDistance = -1;
	int mChangeTime = -1;

	WorkoutStage mWorkoutStage;

	public int getWorkoutLevel() {
		return mWorkoutLevel;
	}

	public void setWorkoutLevel(int workoutLevel) {
		this.mWorkoutLevel = workoutLevel;
	}

	public double getIncline() {
		return mIncline;
	}

	public void setIncline(double incline) {
		if (mWorkoutLevel == 2) {
			this.mIncline = incline;
			return;
		}
		if (incline < 0) {
			this.mIncline = 0;
		} else {
			this.mIncline = incline * 5;
		}
	}

	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double speed) {
		if (mWorkoutLevel == 2) {
			this.mSpeed = speed;
			return;
		}
		if (speed < 0) {
			this.mSpeed = 0;
		} else {
			this.mSpeed = speed * 60;
		}
	}

	public int getResistance() {
		return mResistance;
	}

	public void setResistance(int resistance) {
		this.mResistance = resistance;
	}

	public double getChangeDistance() {
		return mChangeDistance;
	}

	public void setChangeDistance(double changeDistance) {
		this.mChangeDistance = changeDistance;
	}

	public int getChangeTime() {
		return mChangeTime;
	}

	public void setChangeTime(int changeTime) {
		this.mChangeTime = changeTime;
	}

	public WorkoutStage getWorkoutStage() {
		return mWorkoutStage;
	}

	public void setWorkoutStage(WorkoutStage workoutStage) {
		this.mWorkoutStage = workoutStage;
	}

	public String toString() {
		return mSpeed + "," + mResistance + "," + mIncline + "," + mChangeTime + "," + mChangeDistance + ","
				+ mWorkoutLevel + "," + mWorkoutStage + ",";
	}
}
