package com.tapc.android.workouting;

import com.tapc.android.data.Enum.WorkoutStage;


public abstract class StageTask {
	
	WorkoutStage mCurrentWorkoutStage;
	
	public void setWorkoutStage(WorkoutStage stage) {
		mCurrentWorkoutStage = stage;
	}
	
	abstract void tick();
	abstract boolean timeout();
	abstract boolean distanceout();
	abstract boolean calorieout();
	abstract boolean canChangeStage();
	abstract boolean hasNextStage();
	abstract void gotoNextStage();

	boolean isCooldown() {
		return (mCurrentWorkoutStage == WorkoutStage.COOLDOWN);
	}
}
