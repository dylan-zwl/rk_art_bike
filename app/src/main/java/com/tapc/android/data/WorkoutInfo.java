package com.tapc.android.data;

import com.tapc.android.data.Enum.ProgramType;

//save workout
public class WorkoutInfo {
	ProgramType mProgramType;
	long mStartTimestamp;
	int mLevel;

	double mDistance;
	double mCalorie;
	long mSecond;
	double mTotalPace;
	double mPaceAvg;
	double mAltitude;
	long mHeart;

	long mRemainSecond;

	double mIncline;
	double mSpeed;
	int mResistance;

	double mWatt;
	double mMet;

	public ProgramType getProgramType() {
		return mProgramType;
	}

	public void setProgramType(ProgramType programType) {
		this.mProgramType = programType;
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) {
		this.mLevel = level;
	}

	public long getStartTimestamp() {
		return mStartTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.mStartTimestamp = startTimestamp;
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

	public void setRemainTime(long remainSecond) {
		mRemainSecond = remainSecond;
	}

	public long getRemainTime() {
		return mRemainSecond;
	}

	public void setTotalPace(double totalPace) {
		this.mTotalPace = totalPace;
	}

	public double getTotalPace() {
		return mTotalPace;
	}

	public double getPaceAvg() {
		return mPaceAvg;
	}

	public void setPaceAvg(double paceAvg) {
		this.mPaceAvg = paceAvg;
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

	public double getIncline() {
		return mIncline;
	}

	public double setIncline(double incline) {
		return mIncline;
	}

	public double getSpeed() {
		if (mSecond != 0) {
			mSpeed = (mDistance * 3600) / mSecond;
		}
		return mSpeed;
	}

	public void setSpeed(double speed) {
		this.mSpeed = speed;
	}

	public int getResistance() {
		return mResistance;
	}

	public void setResistance(int resistance) {
		this.mResistance = resistance;
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
}
