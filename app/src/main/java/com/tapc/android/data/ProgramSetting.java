package com.tapc.android.data;

import com.tapc.android.data.Enum.ProgramType;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProgramSetting implements Parcelable {
	public static final String KEY_WORKOUT_SETTING = "key_workout_setting";
	private ProgramType programType = ProgramType.MANUAL;
	
	private double mIncline;	
	private int mResistance;
	private double mSpeed;
	private int mLevel;
	private long mTargetHeart;
	
	private int mMinutes = 0;
	private double mDistance = -1;
	private double mCalorie = -1;
	
	private double mWeight;
	private int mAge;

	private boolean mCanCooldown = true;
	private boolean mIsIntervalProgram = true;

	public ProgramSetting(Context context) {
		this.mMinutes = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_WORKOUT_TIME);
		this.mAge = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_AGE);
		this.mWeight = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_WEIGHT);
		this.mIncline = SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_DEFAULT_INCLINE); 
		this.mSpeed = SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_DEFAULT_SPEED); 
		this.mResistance = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MIN_RESISTANCE); 
		this.mTargetHeart = SystemSettingsHelper.getLong(SystemSettingsHelper.KEY_TARGET_HEART_RATE); 
	}
	
	public int getAge() {
		return mAge;
	}

	public void setAge(int age) {
		this.mAge = age;
	}
	
	public int getResistance() {
		return mResistance;
	}

	public void setResistance(int resistance) {
		this.mResistance = resistance;
	}
	
	public boolean isIntervalProgram() {
		return mIsIntervalProgram;
	}

	public void setIntervalProgram(boolean isIntervalProgram) {
		this.mIsIntervalProgram = isIntervalProgram;
	}

	public boolean canCooldown() {
		return mCanCooldown;
	}

	public void setCanCooldown(boolean canCooldown) {
		this.mCanCooldown = canCooldown;
	}

	public ProgramType getProgramType() {
		return programType;
	}
	
	public void setProgramType(ProgramType programType) {
		this.programType = programType;
		if (programType==ProgramType._5K) {
			SetDistance(5.0);
		}
		if (programType==ProgramType._10K) {
			SetDistance(10.0);
		}
	}
	
	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) {
		this.mLevel = level;
	}

	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double speed) {
		this.mSpeed = speed;
	}
	
	public int getMinutes() {
		return mMinutes;
	}

	public void setMinutes(int minutes) {
		this.mMinutes = minutes;
	}
	
	public int getTime() {
		return (mMinutes*60);
	}
	
	public void SetDistance(double distance) {
		this.mDistance = distance;
	}
	
	public double getDistance() {
		return mDistance;
	}
	
	public void SetCalorie(double calorie) {
		this.mCalorie = calorie;
	}
	
	public double getCalorie() {
		return mCalorie;
	}
	
	public double getIncline() {
		return mIncline;
	}

	public void setIncline(double incline) {
		this.mIncline = incline;
	}
	
	public long getTargetHeart() {
		return mTargetHeart;
	}

	public void setTargetHeart(long targetHeart) {
		this.mTargetHeart = targetHeart;
	}
	
	public double getWeight() {
		return this.mWeight;
	}

	public void setWeight(double weight) {
		this.mWeight = weight;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(this.programType.ordinal());
		parcel.writeDouble(this.mIncline);
		parcel.writeDouble(this.mSpeed);
		parcel.writeInt(this.mLevel);
		parcel.writeInt(this.mMinutes);
		parcel.writeDouble(this.mWeight);
		parcel.writeInt(this.mAge);
		parcel.writeLong(this.mTargetHeart);
		parcel.writeDouble(this.mDistance);
		parcel.writeDouble(this.mCalorie);
		
		boolean[] boolArray = { this.mCanCooldown, this.mIsIntervalProgram};
		parcel.writeBooleanArray(boolArray);
	}
	
	public static final Parcelable.Creator<ProgramSetting> CREATOR = 
			new Parcelable.Creator<ProgramSetting>() {
				public ProgramSetting createFromParcel(Parcel in) {
					return new ProgramSetting(in);
				}
		
				public ProgramSetting[] newArray(int size) {
					return new ProgramSetting[size];
				}
			};

	public ProgramSetting() {}
	private ProgramSetting(Parcel in) {
		this.programType = ProgramType.values()[in.readInt()];
		this.mIncline = in.readDouble();
		this.mSpeed = in.readDouble();
		this.mLevel = in.readInt();
		this.mMinutes = in.readInt();
		this.mWeight = in.readDouble();
		this.mAge = in.readInt();
		this.mDistance = in.readDouble();
		this.mCalorie = in.readDouble();
		this.mTargetHeart = in.readLong();
		
		boolean boolArray[] = new boolean[2];
		in.readBooleanArray(boolArray);
		this.mCanCooldown = boolArray[0];
		this.mIsIntervalProgram = boolArray[1];
	}
}
