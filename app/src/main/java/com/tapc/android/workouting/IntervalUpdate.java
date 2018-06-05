package com.tapc.android.workouting;

import java.util.LinkedList;
import java.util.List;

import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.interfaceset.IntervalUpdateListener;
import com.tapc.android.interfaceset.UpdateTaskEx;


import android.util.Log;


class IntervalUpdate extends StageTask implements UpdateTaskEx {
	public static final String TAG = "IntervalUpdateTask";
	
	private double mDistanceout;
	private int mTimeout = 0;
	private int mTimePassed = 0;
	private int mCurrentIndex;
	private int mFlag = -1;
	private IntervalUpdateListener mIntervalUpdateListener;
	private IntervalProgram mIntervalProgram;
	private List<IntervalInfo> mIntervalList = new LinkedList<IntervalInfo>();
	
	IntervalUpdate(IntervalProgram intervalProgram) {
		mIntervalProgram = intervalProgram;
		mIntervalUpdateListener = mIntervalProgram.mIntervalUpdateListener;
	}
	
	public void setIntervalList(List<IntervalInfo> intervalList) {
		mCurrentIndex = 0;
		mTimeout = 0;
		mDistanceout = 0;
		mIntervalList = intervalList;
	}
	
	public void initialization() {
		gotoNextStage();
	}
	
	@Override
	public void run() {
		if (mFlag == -1) {
			return;
		}
		tick();
		updateUI();
		switch (mFlag) {
		case 1:
			if (timeout()) {// timeout
				if (hasNextStage()) { // found next interval 
					gotoNextStage();
				} else { // have not any interval found
					mFlag = -1;
					mIntervalProgram.intervalProgramChanged();
					Log.d(TAG, "run::have not any interval found in interval list!");
					return;
				}
			}
			break;
		case 2:
			 if (distanceout()) {// distanceout
				if (hasNextStage()) {
					gotoNextStage();
				}else {
					mFlag = -1;
					mIntervalProgram.intervalProgramChanged();
					Log.d(TAG, "run::have not any interval found in interval list!");
					return;
				}
			 }
			break;
		default:
			break;
		}
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
					mDistanceout -= mIntervalProgram.mWorkout.getSpeed()/3.6;
				}
				break;
		default:
			break;
		}
		++mTimePassed;
	//	Log.e("interval", "time   "+mTimePassed);
	}
	
	@Override
	boolean timeout() {
		return (0 == mTimeout);
	}
	
	@Override
	boolean canChangeStage() {
		return (!isEnd()/* || !isCooldown()*/);
	}

	@Override
	boolean hasNextStage() {
		return (hasInterval() && canChangeStage());
	}
	
	@Override
	void gotoNextStage() {
		IntervalInfo interval = mIntervalList.get(mCurrentIndex);
		mTimeout = interval.getChangeTime();
		mTimePassed = 0;
		mDistanceout = interval.getChangeDistance();
		if (mTimeout!=-1) {
			mFlag = 1;
		}
		if (mDistanceout!=-1) {
			mFlag = 2;
		}
		mIntervalUpdateListener.onIntervalChanged(mCurrentIndex, interval);
		mCurrentIndex++;
	}
	
	int getElapsed() {
		return (mTimePassed);
	}
	
	void updateUI() {
		updateElapsed();
	}
	
	void updateElapsed() {
		mIntervalUpdateListener.onIntervalElapsedChanged(mTimePassed);
	}
	
	boolean isEnd() {
		return (mCurrentIndex == mIntervalList.size());
	}

	boolean hasInterval() {
		return (mIntervalList.size() > 0);
	}

	@Override
	boolean distanceout() {
		// TODO Auto-generated method stub
		return (mDistanceout<mIntervalProgram.mWorkout.getSpeed()/3600);
	}

	@Override
	boolean calorieout() {
		// TODO Auto-generated method stub
		return false;
	}
} // end class