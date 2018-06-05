package com.tapc.android.workouting;

import com.tapc.android.data.MessageDefine;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.Enum.WorkoutStage;

import android.os.Handler;
import android.os.Message;

public class ManualProgram extends Program {
	final static String TAG = "ManualProgram";
	
	public ManualProgram(ProgramSetting programSetting, Handler handler) {
		super(programSetting, handler);
	}
	
	
	@Override
	public void translateMessage(Message msg) {
		super.translateMessage(msg);
		switch (msg.what) {
		case MessageDefine.MSG_ID_PRO_GOTO_COOLDOWN: {
			WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			if (null != task) {
				/*if (task.mCurrentWorkoutStage!=WorkoutStage.COOLDOWN) {
					task.setWorkoutStage(WorkoutStage.COOLDOWN);
				}*/
				task.gotoCooldown();
			}
		}
		break;
		default: break;
		}
	}

	public void startWorkout() {
		super.startWorkout();
	}
	
	@Override
	int getWarmupTime() {
		return (super.getDefaultWarmupTime()); // seconds
	}

	@Override
	int getCooldownTime() {
		return (super.getDefaultCooldownTime());
	}

	@Override
	int getNormalTime() {
		int ret = super.getTotalTime();
		if (0 == ret) {
			ret = super.getDefaultTotalTime();
		}
		
		return ret;
	}
}
