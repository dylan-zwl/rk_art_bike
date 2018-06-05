package com.tapc.android.data;

import android.Manifest.permission;
import android.os.Parcel;
import android.os.Parcelable;

public class WorkoutData  implements Parcelable {
	// These fields as below for update UI and will showing on the feedbacks of run screen 
	double mCurDistance=0;	  
	double mCurCalorie=0;	   
	long mCurSecond=0;
	
	//goal
	double mTotalDistance=0;	  
	double mTotalCalorie=0;	   
	long mTotalSecond=0;		  
	
	//total
	double mPace=0;			  
	double mAltitude=0;
	long mHeart=0;
	
	double mSpeed;
	double mIncline;
	int mResistance;
	double mPaceRate;
	long   mHeartRate;
	
	double mMets; 
	double mRPM;
	double mWatts;

	double mMPH;
	double mKPH;
	int mMilePace;
	int mKmPace;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeDouble(this.mCurDistance);
		parcel.writeDouble(this.mCurCalorie);
		parcel.writeLong(this.mCurSecond);
		parcel.writeDouble(this.mTotalDistance);
		parcel.writeDouble(this.mTotalCalorie);
		parcel.writeLong(this.mTotalSecond);
		parcel.writeDouble(this.mPace);
		parcel.writeDouble(this.mAltitude);
		parcel.writeLong(this.mHeart);
		parcel.writeDouble(this.mSpeed);
		parcel.writeDouble(this.mIncline);
		parcel.writeInt(this.mResistance);
		parcel.writeDouble(this.mPaceRate);
		parcel.writeLong(this.mHeartRate);
			
		parcel.writeDouble(this.mMets);
		parcel.writeDouble(this.mRPM);
		parcel.writeDouble(this.mWatts);
		parcel.writeDouble(this.mMPH);
		parcel.writeDouble(this.mKPH);
		parcel.writeInt(this.mMilePace);
		parcel.writeInt(this.mKmPace);
	}
	
	public static final Creator<WorkoutData> CREATOR = new Creator<WorkoutData>() {

		@Override
		public WorkoutData createFromParcel(Parcel source) {
			return new WorkoutData(source);
		}

		@Override
		public WorkoutData[] newArray(int size) {
			return new WorkoutData[size];
		}
	};
	
	public WorkoutData() {}
	public WorkoutData(Parcel source) {
		this.mCurDistance = source.readDouble();
		this.mCurCalorie = source.readDouble();
		this.mCurSecond = source.readLong();
		this.mTotalDistance = source.readDouble();
		this.mTotalCalorie = source.readDouble();
		this.mTotalSecond = source.readLong();
		this.mPace = source.readDouble();
		this.mAltitude = source.readDouble();
		this.mHeart = source.readLong();
		this.mSpeed = source.readDouble();
		this.mIncline = source.readDouble();
		this.mResistance = source.readInt();
		
		this.mMets = source.readDouble();
		this.mRPM = source.readDouble();
		this.mWatts = source.readDouble();
		this.mMPH = source.readDouble();
		this.mKPH = source.readDouble();
		this.mMilePace = source.readInt();
		this.mKmPace = source.readInt();
	}
	
	public double getCurDistance() {
		return mCurDistance;
	}
	public void setCurDistance(double curDistance) {
		this.mCurDistance = curDistance;
	}
	
	public double getCurCalorie() {
		return mCurCalorie;
	}
	public void setCurCalorie(double curCalorie) {
		this.mCurCalorie = curCalorie;
	}
	
	public long getCurTime() {
		return mCurSecond;
	}
	public void setCurTime(long curSecond) {
		this.mCurSecond = curSecond;
	}
	
	public double getTotalDistance() {
		return mTotalDistance;
	}
	public void setTotalDistance(double totalDistance) {
		this.mTotalDistance = totalDistance;
	}
	
	public double getTotalCalorie() {
		return mTotalCalorie;
	}
	public void setTotalCalorie(double totalCalorie) {
		this.mTotalCalorie = totalCalorie;
	}
	
	public long getTotalTime() {
		return mTotalSecond;
	}
	public void setTotalTime(long totalSecond) {
		this.mTotalSecond = totalSecond;
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
	
	public double getSpeed() {
		return mSpeed;
	}
	public void setSpeed(double speed) {
		this.mSpeed = speed;
	}
	
	public double getIncline() {
		return mIncline;
	}
	public void setIncline(double incline) {
		this.mIncline = incline;
	}	

	public int getResistance() {
		return mResistance;
	}
	public void setResistance(int resistance) {
		this.mResistance = resistance;
	}

	public double getMets() {
		return mMets;
	}

	public void setMets(double met) {
		this.mMets = met;
	}

	public void setHeartRate(int heartRate) {
		this.mHeartRate = heartRate;
	}

	public double getRPM() {
		return mRPM;
	}

	public void setRPM(double rpm) {
		this.mRPM = rpm;
	}

	public double getWatts() {
		return mWatts;
	}

	public void setWatts(double watts) {
		this.mWatts = watts;
	}

	public double getMPH() {
		return mMPH;
	}

	public void setMPH(double mph) {
		this.mMPH = mph;
	}

	public double getKPH() {
		return mKPH;
	}

	public void setKPH(double kph) {
		this.mKPH = kph;
	}

	public int getmMilePace() {
		return mMilePace;
	}

	public void setMilePace(int milePace) {
		this.mMilePace = milePace;
	}

	public int getKmPace() {
		return mKmPace;
	}

	public void setKmPace(int kmPace) {
		this.mKmPace = kmPace;
	}
}
