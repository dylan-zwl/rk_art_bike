package com.tapc.android.data;

import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

import com.tapc.android.data.Enum.MachineType;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.Enum.WorkoutGoal;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.workouting.Program;

public class Workout implements Parcelable {
	long mStartTimestamp;
	ProgramType mProgramType;
	int mLevel;

	WorkoutStage mWorkoutStage = WorkoutStage.WARMUP;
	WorkoutGoal mWorkoutGoal = WorkoutGoal.TIME;

	double mWeight;

	double mGoal = 0;

	// WARMUP,NORMAL,COOLDOWN WorkoutStage
	double mDistance = 0;
	double mCalorie = 0;
	long mSecond = 0;
	double mPace = 0;
	double mAltitude = 0;
	double mHeart = 0;

	// interval intervalprogram
	double mCurDistance = 0;
	double mCurCalorie = 0;
	long mCurSecond = 0;
	double mCurPace = 0;
	double mCurAltitude = 0;
	double mCurHeart = 0;

	// cur total, include all stage and interval
	double mTotalDistance = 0;
	double mTotalCalorie = 0;
	long mTotalSecond = 0;

	double mTotalPace = 0;
	double mTotalAltitude = 0;
	double mTotalHeart = 0;

	// ExpectTotal
	double mExpectTotalDistance = 0;
	double mExpectTotalCalorie = 0;
	long mExpectTotalSecond = 0;

	// remain
	double mRemainDistance = 0;
	double mRemainCalorie = 0;
	long mRemainSecond = 0;

	// goal time,distance,calorie
	double mTargetDistance = 0;
	double mTargetCalorie = 0;
	long mTargetSecond = 0;
	long mTargetHeart = 0;

	// real time
	long mHeartRate;
	double mPaceRate;
	double mPaceAvg = 0;
	double mSpeed;
	double mIncline;

	double bikeSpeed;
	double bikeRpmSpeed;
	int watt;

	int mPaceFlag = 0;

	WorkoutInfo mWorkOutInfo = new WorkoutInfo();

	public void stageChage(WorkoutStage workoutStage) { // clear current stage
		mDistance = 0;
		mCalorie = 0;
		mSecond = 0;
		mPace = 0;
		mAltitude = 0;
		mHeart = 0;
		mGoal = 0;
		mWorkoutStage = workoutStage;
	}

	public WorkoutStage getWorkoutStage() {
		return mWorkoutStage;
	}

	public void setWorkoutGoal(WorkoutGoal workoutGoal) {
		this.mWorkoutGoal = workoutGoal;
	}

	public WorkoutGoal getWorkoutGoal() {
		return mWorkoutGoal;
	}

	public double getGoal() {
		return mGoal;
	}

	public void setGoal(double goal) {
		this.mGoal = goal;
	}

	public void intervalChage() { // clear current interval
		mCurDistance = 0;
		mCurCalorie = 0;
		mCurSecond = 0;
		mCurPace = 0;
		mCurAltitude = 0;
		mCurHeart = 0;
	}

	public double getWeight() {
		return mWeight;
	}

	public void setWeight(double weight) {
		this.mWeight = weight;
	}

	public long getTime() {
		return mSecond;
	}

	public void setTime(long second) {
		this.mSecond = second;
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

	public long getCurTime() {
		return mCurSecond;
	}

	public void setCurTime(long curSecond) {
		this.mCurSecond = curSecond;
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

	public void setTotalTime(long totalTime) {
		this.mTotalSecond = totalTime;
	}

	public long getTotalTime() {
		return mTotalSecond;
	}

	public void setTotalDistance(double totalDistance) {
		this.mTotalDistance = totalDistance;
	}

	public double getTotalDistance() {
		return mTotalDistance;
	}

	public void setTotalCalorie(double totalCalorie) {
		this.mTotalCalorie = totalCalorie;
	}

	public double getTotalCalorie() {
		return mTotalCalorie;
	}

	public long getExpectTotalTime() {
		return mExpectTotalSecond;
	}

	public double getExpectTotalDistance() {
		return mExpectTotalDistance;
	}

	public double getExpectTotalCalorie() {
		return mExpectTotalCalorie;
	}

	public double getRemainDistance() {
		return mRemainDistance;
	}

	public double getRemainCalorie() {
		return mRemainCalorie;
	}

	public long getRemainTime() {
		return mRemainSecond;
	}

	public double calculateCalorie(double watt) {
		double calorie = 0;
		calorie = ((4 * watt) / 4.18) / 1000;
		return calorie;
	}

	public void calculate(Program program) {
		if (mWorkoutGoal == WorkoutGoal.TIME) {
			mRemainSecond = mTargetSecond - (int) mGoal;
		}
		if (mWorkoutGoal == WorkoutGoal.DISTANCE) {
			mRemainDistance = mTargetDistance - mGoal;
			mRemainSecond = (int) (mRemainDistance / (bikeSpeed / 3600));
		}
		if (mWorkoutGoal == WorkoutGoal.CALORIE) {
			mRemainCalorie = mTargetCalorie - mGoal;
			mRemainSecond = (int) (mRemainCalorie / calculateCalorie(program.getMachine().getWatt()));
		}
		/*
		 * if (mProgramType==ProgramType.CALORIE ||
		 * ((mProgramType==ProgramType.VIGOROUS_BURN ||
		 * mProgramType==ProgramType.MODERATE_BURN )&&
		 * program.getTotalCalorie()!=-1)) { //goal calorie mExpectTotalCalorie
		 * = program.getTotalCalorie()+
		 * calculateCalorie(getSpeed())*program.getDefaultWarmupTime()+
		 * calculateCalorie(getSpeed())*program.getDefaultCooldownTime();
		 * mRemainCalorie = mExpectTotalCalorie-mTotalCalorie; mRemainSecond=
		 * (int)(mRemainCalorie/calculateCalorie(getSpeed()));
		 * mExpectTotalSecond = mTotalSecond+mRemainSecond; mRemainDistance =
		 * mRemainSecond*getSpeed()/3600; mExpectTotalDistance =
		 * mTotalDistance+mRemainDistance; }
		 * 
		 * if (mProgramType==ProgramType.MANUAL || //goal time
		 * mProgramType==ProgramType.TIME || mProgramType==ProgramType.INTERVALS
		 * || mProgramType==ProgramType.FAT_BURN ||
		 * mProgramType==ProgramType.TAPC_PROG ||
		 * ((mProgramType==ProgramType.VIGOROUS_BURN ||
		 * mProgramType==ProgramType.MODERATE_BURN )&&
		 * program.getTotalTime()!=0)) {
		 * 
		 * mExpectTotalSecond = program.getTotalTime()+
		 * program.getDefaultWarmupTime()+ program.getDefaultCooldownTime();
		 * mRemainSecond = mExpectTotalSecond-mTotalSecond;
		 * 
		 * if
		 * (mTotalSecond<program.getDefaultWarmupTime()+program.getTotalTime()
		 * && mWorkoutStage==WorkoutStage.COOLDOWN) { mRemainSecond =
		 * program.getDefaultCooldownTime()-mSecond; } mRemainDistance =
		 * mRemainSecond*getSpeed()/3600; mExpectTotalDistance =
		 * mTotalDistance+mRemainDistance; mRemainCalorie =
		 * calculateCalorie(getSpeed())*mRemainSecond; mExpectTotalCalorie =
		 * mTotalCalorie+mRemainCalorie; }
		 * 
		 * if (mProgramType==ProgramType.DISTANCE || //goal distance
		 * mProgramType==ProgramType._5K || mProgramType==ProgramType._10K ||
		 * ((mProgramType==ProgramType.VIGOROUS_BURN ||
		 * mProgramType==ProgramType.MODERATE_BURN )&&
		 * program.getTotalDistance()!=-1)) { mExpectTotalDistance =
		 * program.getTotalDistance()+
		 * program.getDefaultWarmupTime()*getSpeed()/3600+
		 * program.getDefaultCooldownTime()*getSpeed()/3600; mRemainDistance =
		 * mExpectTotalDistance-mTotalDistance; mRemainSecond =
		 * (int)(mRemainDistance/(getSpeed()/3600)); mExpectTotalSecond =
		 * mTotalSecond+mRemainSecond; mRemainCalorie =
		 * mRemainSecond*calculateCalorie(getSpeed()); mExpectTotalCalorie =
		 * mTotalCalorie+mRemainCalorie; }
		 */
	}

	public void setPace(double pace) {
		this.mPace = pace;
	}

	public double getPace() {
		return mPace;
	}

	public double getPaceAvg() {
		return mPaceAvg;
	}

	public void setPaceAvg(double paceAvg) {
		this.mPaceAvg = paceAvg;
	}

	public void setAltitude(double altitude) {
		this.mAltitude = altitude;
	}

	public double getAltitude() {
		return mAltitude;
	}

	public void setHeart(double heart) {
		this.mHeart = heart;
	}

	public long getHeart() {
		return ((long) mHeart);
	}

	public void setCurPace(double curPace) {
		this.mCurPace = curPace;
	}

	public double getCurPace() {
		return mCurPace;
	}

	public void setCurAltitude(double curAltitude) {
		this.mCurAltitude = curAltitude;
	}

	public double getCurAltitude() {
		return mCurAltitude;
	}

	public void setCurHeart(double curHeart) {
		this.mCurHeart = curHeart;
	}

	public long getCurHeart() {
		return ((long) mCurHeart);
	}

	public void setTargetDistance(double targetDistance) {
		if (targetDistance != -1) {
			setWorkoutGoal(WorkoutGoal.DISTANCE);
		}
		this.mTargetDistance = targetDistance;
	}

	public double getTargetDistance() {
		return mTargetDistance;
	}

	public void setTargetCalorie(double targetCalorie) {
		if (targetCalorie != -1) {
			setWorkoutGoal(WorkoutGoal.CALORIE);
		}
		this.mTargetCalorie = targetCalorie;
	}

	public double getTargetCalorie() {
		return mTargetCalorie;
	}

	public void setTargetTime(long targetSecond) {
		if (targetSecond != 0) {
			setWorkoutGoal(WorkoutGoal.TIME);
		}
		this.mTargetSecond = targetSecond;
	}

	public long getTargetTime() {
		return mTargetSecond;
	}

	public void setTargetHeart(long targetHeart) {
		this.mTargetHeart = targetHeart;
	}

	public long getTargetHeart() {
		return mTargetHeart;
	}

	public void setTotalPace(double totalPace) {
		this.mTotalPace = totalPace;
	}

	public double getTotalPace() {
		return mTotalPace;
	}

	public void setTotalAltitude(double totalAltitude) {
		this.mTotalAltitude = totalAltitude;
	}

	public double getTotalAltitude() {
		return mTotalAltitude;
	}

	public void setTotalHeart(double totalHeart) {
		this.mTotalHeart = totalHeart;
	}

	public long getTotalHeart() {
		return ((long) mTotalHeart);
	}

	public long getHeartRate() {
		return mHeartRate;
	}

	public void setHeartRate(long heartRate) {
		this.mHeartRate = heartRate;
	}

	public double getPaceRate() {
		return mPaceRate;
	}

	public void setPaceRate(double paceRate) {
		this.mPaceRate = paceRate;
	}

	public int getPaceFlag() {
		return mPaceFlag;
	}

	public void setPaceFlag(int paceFlag) {
		this.mPaceFlag = paceFlag;
	}

	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double speed) {
		this.mSpeed = speed;
	}

	public double getBikeSpeed() {
		return bikeSpeed;
	}

	public void setBikeSpeed(double rpm, int diameter) {
		this.bikeSpeed = (rpm * 60) * (3.14 * (2.54 * diameter) / 100000);
	}

	public int getWatt() {
		return watt;
	}

	public void setWatt(int watt) {
		this.watt = watt;
	}

	public double getBikeRpmSpeed() {
		return bikeRpmSpeed;
	}

	public void setBikeRpmSpeed(double bikeRpmSpeed) {
		this.bikeRpmSpeed = bikeRpmSpeed;
	}

	public double getIncline() {
		return mIncline;
	}

	public void setIncline(double incline) {
		this.mIncline = incline;
	}

	public long getStartTimestamp() {
		return mStartTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.mStartTimestamp = startTimestamp;
	}

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

	public int getRandomMax() {
		String str = "0,1,2,3,4,5,6,7,8,9";
		String code[] = str.split(",");
		Random random = new Random();
		int index = 0;
		String verifyCode = "";
		for (int i = 0; i < 1; i++) {
			index = random.nextInt(code.length - 1);
			verifyCode += code[index];
		}
		return Integer.valueOf(verifyCode);
	}

	public int getRandomMin() {
		String str = "0,1,2,3,4";
		String code[] = str.split(",");
		Random random = new Random();
		int index = 0;
		String verifyCode = "";
		for (int i = 0; i < 1; i++) {
			index = random.nextInt(code.length - 1);
			verifyCode += code[index];
		}
		return Integer.valueOf(verifyCode);
	}

	public int SPEED_[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	public int DISTANCE_[] = { 1, 2, 3, 4, 5 };

	public void update(Program program, MachineType machineType, long updatePeriod) { // update
																						// workout
																						// every
																						// second
		// speed
		setSpeed(program.getMachine().getSpeed());

		setBikeRpmSpeed(program.getMachine().getRpmSpeed());
		setBikeSpeed(program.getMachine().getRpmSpeed(), 78);
		// Log.e("update", "speed  "+getSpeed());
		// incline
		setIncline(program.getMachine().getIncline());

		int watt = program.getMachine().getWatt();
		setWatt(watt);
		// goal
		if (mWorkoutGoal == WorkoutGoal.TIME) {
			mGoal += 1;
		}
		if (mWorkoutGoal == WorkoutGoal.DISTANCE) {
			mGoal += bikeSpeed / 3600;
		}
		if (mWorkoutGoal == WorkoutGoal.CALORIE) {
			mGoal += calculateCalorie(watt);
		}

		// time
		setTotalTime(getTotalTime() + 1);
		// distance

		setCurDistance(getCurDistance() + bikeSpeed / 3600);
		setDistance(getDistance() + bikeSpeed / 3600);
		setTotalDistance(getTotalDistance() + bikeSpeed / 3600);

		// heart rate
		double distance = getTotalDistance();
		double speed = distance / getTotalTime() * 3600;
		int heart = program.getMachine().getHeartRate();
		if (heart > 120) {
			int speed_ = (int) speed;
			if (speed_ >= 15) {
				speed_ = 15;
			}
			int distance_ = 0;
			if (distance_ >= 4) {
				distance_ = 4;
			}
			heart = 120 + SPEED_[speed_] + DISTANCE_[distance_] + getRandomMax();
			if (heart < getHeartRate()) {
				heart = (int) getHeartRate() - getRandomMin();
			} else {
				heart = (int) getHeartRate() + getRandomMin();
			}
		}
		setHeartRate(heart);
		double heartRate = getHeartRate() / 60;
		setCurHeart(mCurHeart + heartRate);
		setHeart(mHeart + heartRate);
		setTotalHeart(mTotalHeart + heartRate);

		// pace
		setPaceFlag(program.getMachine().getPaceFlag());
		setPaceRate(program.getMachine().getPaceRate());
		setCurPace(getCurPace() + getPaceRate());
		setPace(getPace() + getPaceRate());
		setTotalPace(getTotalPace() + getPaceRate());
		if (getPaceRate() != 0) {
			if (getPaceAvg() == 0) {
				setPaceAvg(getPaceRate());
			} else {
				double avgPace = getPaceAvg() + getPaceRate();
				avgPace = avgPace / 2;
				setPaceAvg(avgPace);
			}
		}

		// altitude
		setCurAltitude(getCurAltitude() + program.getMachine().getClimbSpeed() / 3600);
		setAltitude(getAltitude() + program.getMachine().getClimbSpeed() / 3600);
		setTotalAltitude(getTotalAltitude() + program.getMachine().getClimbSpeed() / 3600);

		// calories
		setCurCalorie(getCurCalorie() + calculateCalorie(watt));
		setCalorie(getCalorie() + calculateCalorie(watt));
		setTotalCalorie(getTotalCalorie() + calculateCalorie(watt));

		calculate(program);

		// calories per hour

		// mets

		// rpm

		// watts
	}

	public WorkoutInfo getWorkoutInfo() {
		WorkoutInfo workOutInfo = new WorkoutInfo();
		workOutInfo.setStartTimestamp(getStartTimestamp());
		workOutInfo.setTime(getTotalTime());
		workOutInfo.setDistance(getTotalDistance());
		workOutInfo.setCalorie(getTotalCalorie());
		workOutInfo.setTotalPace(getTotalPace());
		workOutInfo.setPaceAvg(getPaceAvg());
		workOutInfo.setAltitude(getTotalAltitude());
		workOutInfo.setHeart(getTotalHeart());
		if (getWorkoutStage() == WorkoutStage.NORMAL) {
			workOutInfo.setRemainTime(getRemainTime());
		} else {
			workOutInfo.setRemainTime(0);
		}
		mWorkOutInfo = workOutInfo;
		return mWorkOutInfo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stu
		parcel.writeDouble(mWeight);
		parcel.writeInt(mLevel);
		parcel.writeDouble(mDistance);
		parcel.writeDouble(mCalorie);
		parcel.writeLong(mSecond);
		parcel.writeDouble(mCurDistance);
		parcel.writeDouble(mCurCalorie);
		parcel.writeLong(mCurSecond);
		parcel.writeDouble(mTotalDistance);
		parcel.writeDouble(mTotalCalorie);
		parcel.writeLong(mTotalSecond);
		parcel.writeDouble(mRemainDistance);
		parcel.writeDouble(mRemainCalorie);
		parcel.writeLong(mRemainSecond);
		parcel.writeDouble(mPace);
		parcel.writeDouble(mAltitude);
		parcel.writeDouble(mHeart);
		parcel.writeDouble(mCurPace);
		parcel.writeDouble(mCurAltitude);
		parcel.writeDouble(mCurHeart);
		parcel.writeDouble(mTargetDistance);
		parcel.writeDouble(mTargetCalorie);
		parcel.writeLong(mTargetSecond);
		parcel.writeLong(mTargetHeart);
		parcel.writeDouble(mTotalPace);
		parcel.writeDouble(mTotalAltitude);
		parcel.writeLong(mExpectTotalSecond);
		parcel.writeDouble(mExpectTotalDistance);
		parcel.writeDouble(mExpectTotalCalorie);
		parcel.writeDouble(mTotalHeart);
		parcel.writeLong(mHeartRate);
		parcel.writeDouble(mPaceRate);
		parcel.writeInt(mPaceFlag);
		parcel.writeDouble(mPaceAvg);
		parcel.writeDouble(mSpeed);
		parcel.writeDouble(mIncline);
		parcel.writeDouble(mGoal);
		parcel.writeSerializable(mWorkoutStage);
		parcel.writeSerializable(mWorkoutGoal);
	}

	public static final Creator<Workout> CREATOR = new Creator<Workout>() {

		@Override
		public Workout createFromParcel(Parcel source) {
			return new Workout(source);
		}

		@Override
		public Workout[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public Workout() {
	}

	public Workout(Parcel source) {
		// source.readList(this.mStageList, null);
		this.mLevel = source.readInt();
		this.mWeight = source.readDouble();
		this.mDistance = source.readDouble();
		this.mCalorie = source.readDouble();
		this.mSecond = source.readLong();
		this.mCurDistance = source.readDouble();
		this.mCurCalorie = source.readDouble();
		this.mCurSecond = source.readLong();
		this.mTotalDistance = source.readDouble();
		this.mTotalCalorie = source.readDouble();
		this.mTotalSecond = source.readLong();
		this.mRemainDistance = source.readDouble();
		this.mRemainCalorie = source.readDouble();
		this.mRemainSecond = source.readLong();
		this.mPace = source.readDouble();
		this.mAltitude = source.readDouble();
		this.mHeart = source.readLong();
		this.mCurPace = source.readDouble();
		this.mCurAltitude = source.readDouble();
		this.mCurHeart = source.readLong();
		this.mTargetDistance = source.readDouble();
		this.mTargetCalorie = source.readDouble();
		this.mTargetSecond = source.readLong();
		this.mTargetHeart = source.readLong();
		this.mTotalPace = source.readDouble();
		this.mTotalAltitude = source.readDouble();
		this.mTotalHeart = source.readLong();
		this.mExpectTotalSecond = source.readLong();
		this.mExpectTotalDistance = source.readDouble();
		this.mExpectTotalCalorie = source.readDouble();
		this.mHeartRate = source.readLong();
		this.mPaceRate = source.readDouble();
		this.mPaceFlag = source.readInt();
		this.mPaceAvg = source.readDouble();
		this.mSpeed = source.readDouble();
		this.mIncline = source.readDouble();
		this.mGoal = source.readDouble();
		this.mWorkoutStage = source.readParcelable(WorkoutStage.class.getClassLoader());
		this.mWorkoutStage = source.readParcelable(WorkoutGoal.class.getClassLoader());
	}
}
