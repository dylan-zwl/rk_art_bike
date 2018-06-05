package com.tapc.android.interfaceset;

import com.tapc.android.data.Enum.WorkoutStage;

public interface WorkoutStateListener {
	void onStageChanged(WorkoutStage stage);
	void onWorkoutFinish();
	void onRemainingChanged(int[] remaining);
	void onElapsedChanged(int elapsed);
}