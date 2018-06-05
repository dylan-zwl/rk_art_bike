package com.tapc.android.data;

import com.tapc.android.data.Enum.WorkoutStage;

//save current workout interval
public class WorkoutIntervalInfo {

	long mStartTimeStamp; // milliseconds
	int mIntervalIndex;

	double mDistance;
	double mCalorie;
	long mSecond;
	double mPace;
	double mAltitude;
	long mHeart; // total heart

	double mSpeed;
	double mIncline;
	int mResistance;
	double mPaceRate;
	long mHeartRate;

	double mWatt;
	double mMet;

	WorkoutStage workoutStage;

	public long getStartTime() {
		return mStartTimeStamp;
	}

	public void setStartTime(long startTimestamp) {
		this.mStartTimeStamp = startTimestamp;
	}

	public int getIntervalIndex() {
		return mIntervalIndex;
	}

	public void setIntervalIndex(int intervalIndex) {
		this.mIntervalIndex = intervalIndex;
	}

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double distance) {
		this.mDistance = distance;
	}

	public double getCalorie() {
		return mCalorie;
	}

	public void setCalorie(double calorie) {
		this.mCalorie = calorie;
	}

	public long getTime() {
		return mSecond;
	}

	public void setTime(long second) {
		this.mSecond = second;
	}

	public double getPace() {
		return mPace;
	}

	public void setPace(double pace) {
		this.mPace = pace;
	}

	public double getAltitude() {
		return mAltitude;
	}

	public void setAltitude(double altitude) {
		this.mAltitude = altitude;
	}

	public long getHeart() {
		return mHeart;
	}

	public void setHeart(long heart) {
		this.mHeart = heart;
	}

	public double getWatt() {
		if (mSecond != 0) {
			mWatt = (mCalorie * 4.18 * 1000 / 4) / mSecond;
		}
		return mWatt;
	}

	public void setWatt(double watt) {
		this.mWatt = watt;
	}

	public double getMet() {
		return mMet;
	}

	public void setMet(double met) {
		this.mMet = met;
	}

	public double getSpeed() { // km/h
		if (mSecond != 0) {
			mSpeed = (mDistance * 3600) / mSecond;
		}
		return mSpeed;
	}

	public double getIncline() {
		return mIncline;
	}

	public int resistance() {
		return mResistance;
	}

	public double paceRate() { // pace/s
		if (mSecond != 0) {
			mPaceRate = mPace / mSecond;
		}
		return mPaceRate;
	}

	public double getHeartRate() {
		if (mSecond != 0) {
			mHeartRate = mHeart / mSecond;
		}
		return mHeartRate;
	}

	public WorkoutStage getWorkoutStage() {
		return workoutStage;
	}

	public void setWorkoutStage(WorkoutStage workoutStage) {
		this.workoutStage = workoutStage;
	}
}
