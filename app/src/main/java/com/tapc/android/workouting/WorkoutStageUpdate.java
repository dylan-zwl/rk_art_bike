package com.tapc.android.workouting;


import java.util.LinkedList;
import java.util.List;


import android.util.Log;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.interfaceset.IntervalUpdateListener;
import com.tapc.android.interfaceset.UpdateTaskEx;
import com.tapc.android.interfaceset.WorkoutStateListener;


class WorkoutStageUpdate extends StageTask implements UpdateTaskEx {
	public static final String TAG = "WorkoutStageUpdate";
	
	private int mTimePassed = 0;
	private int mTimeout = 0;
	
	private double mDistancePassed = 0;
	private double mDistanceout = 0;
	
	private double mCaloriePassed = 0;
	private double mCalorieout = 0;
	
	private int mFlag = -1;
	
	private Program mProgram;
	private ProgramType mProgramType;
	private WorkoutStateListener mStateListener;
	
	public WorkoutStageUpdate(Program prgoram) {
		mProgram = prgoram;
		mProgramType = prgoram.getProgramType();
		mCurrentWorkoutStage = prgoram.getWorkoutStartStage();
		mStateListener = prgoram.getStateListener();
	}
	
	@Override
	public void run() {
		// Update UI
		tick(); 
		updateUI();
		switch (mFlag) {
		case 1:
			if (timeout()) { // timeout
	        	if (hasNextStage()) { // have next stage
	        		gotoNextStage();
	        	} else { // have not any stage
	            	mStateListener.onWorkoutFinish(); // notify the workout finish
	        	}
	        } 
			break;
		case 2:
			 if (distanceout()) {
				 if (hasNextStage()) { // have next stage
	        		gotoNextStage();
	        	} else { // have not any stage
	            	mStateListener.onWorkoutFinish(); // notify the workout finish
	        	}
			}
			break;
		case 3:
			if (calorieout()) {
				if (hasNextStage()) { // have next stage
	        		gotoNextStage();
	        	} else { // have not any stage
	            	mStateListener.onWorkoutFinish(); // notify the workout finish
	        	}
			}
			break;
		default:
			break;
		}
	}

	public void initialization() {
		gotoStage(mCurrentWorkoutStage);
	}
	
	public void plus(int maxMinute) {
		// Update current timeout in valid scope
		mTimeout = Math.min(mTimeout + 60, (maxMinute - 1) * 60
				+ (mTimeout % 60));

		// Update Remaining
		updateRemaining();
	}

	public void minus() {
		// Update current timeout in valid scope
		mTimeout = Math.max(mTimeout - 60, mTimeout % 60);

		// Update Remaining
		updateRemaining();
	}
	
	public void setTimeout(int second) {
		mTimeout = second;
	}
	
	public void setDistanceout(double distanceout) {
		mDistanceout = distanceout;
	}
	
	public void setCalorieout(double calorieout) {
		mCalorieout = calorieout;
	}
	
	public void gotoCooldown() {
		if (isCooldown()) {
			return;
		}
		if (mCurrentWorkoutStage==WorkoutStage.WARMUP) {
			mProgram.mWorkout.setCurTime(mTimePassed);
		}
		gotoStage(WorkoutStage.COOLDOWN);
	}

	public int getWorkoutTime() {
		return (mTimeout + mTimePassed);
	}
	
	public void gotoStage(WorkoutStage stage) {
		Log.e("WorkoutStage", " "+mTimePassed);
		if (stage!=WorkoutStage.COOLDOWN || mProgram.mHaveInterval==false)
			mProgram.mWorkout.setCurTime(mTimePassed);
		if (stage==WorkoutStage.NORMAL && mProgram.mHaveInterval==false)
			mProgram.mMachine.setSpeed(mProgram.mMachine.getSpeed());
		setWorkoutStage(stage);	
		mTimePassed = 0;
		mTimeout = mProgram.getWorkoutTimeByStage(stage);
		if (mTimeout==0 && stage==WorkoutStage.WARMUP) {
			gotoNextStage();
			return;
		}
		
		mCaloriePassed = 0;
		mCalorieout = mProgram.getWorkoutCalorieByStage(stage);
		
		mDistancePassed = 0;
		mDistanceout = mProgram.getWorkoutDistanceByStage(stage);
		
		if (mTimeout!=0) {
			mFlag = 1;
		}
		if (mDistanceout!=-1) {
			mFlag = 2;
		}
		if (mCalorieout!=-1) {
			mFlag = 3;
		}
		
		mStateListener.onStageChanged(stage); // notify the workout stage has changed
	}
	
	void updateUI() {
	//	updateRemaining();
		updateElapsed();
	}

	void updateRemaining() {
		int[] remaining = new int[2];

		// Get current remaining minute
		remaining[0] = getRemainingMinute();

		// Get current remaining seconds
		remaining[1] = getRemainingSecond();

		// Update UI
//		mStateListener.onRemainingChanged(remaining);
	}

	void updateElapsed() {
		mStateListener.onElapsedChanged(mTimePassed);
	}
	
	int getRemainingMinute() {
		return (mTimeout / 60);
	}
	
	int getRemainingSecond() {
		return (mTimeout % 60);
	}
	
	int getElapsedMinute() {
		return (mTimePassed / 60);
	}
	
	int getElapsed() {
		return (mTimePassed);
	}
	
	int getElapsedSecond() {
		return (mTimePassed % 60);
	}

	@Override
	boolean distanceout() {
		// TODO Auto-generated method stub
	//	Log.e("distanceout", "remain--"+mDistanceout);
		return (mDistanceout<mProgram.mWorkout.getBikeSpeed()/3600);
	}

	@Override
	boolean calorieout() {
		// TODO Auto-generated method stub
		return (mCalorieout<mProgram.mWorkout.calculateCalorie(mProgram.mWorkout.getWatt()));
	}

	@Override
	void tick() {
		switch (mFlag) {
		case 1:
				if (!timeout()) {
					--mTimeout;
				}
			break;
			case 2:
				if (!distanceout()) {
					mDistanceout -= mProgram.mWorkout.getBikeSpeed()/3600;
				}
				break;
			case 3:
				if (!calorieout()) {
					mCalorieout -= mProgram.mWorkout.calculateCalorie(mProgram.mWorkout.getWatt());
				}
				break;
		default:
			break;
		}
		++mTimePassed;
//		Log.e("workoutstage", "time   "+mTimePassed);
/*		mDistancePassed += mProgram.mWorkout.getSpeed()/3600;
		mCaloriePassed += mProgram.mMachine.getCalorie(mProgram.mProgramSetting.getWeight());*/
	}
	
	@Override
	void gotoNextStage() {
		WorkoutStage state = WorkoutStage.values()[mCurrentWorkoutStage.ordinal() + 1];
		gotoStage(state);
	}
	
	@Override
	boolean canChangeStage() {
		return (mProgramType != ProgramType.MANUAL);
	}
	
	@Override
	boolean timeout() {
		return (mTimeout == 0);
	}
	
	@Override
	boolean hasNextStage() {
		return (canChangeStage() && !isCooldown() && (SystemSettingsHelper.COOLDOWN_TIME!=0));
	}
}