package com.tapc.android.workouting;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.tapc.android.controller.MachineController;
import com.tapc.android.data.Enum;
import com.tapc.android.data.Enum.WorkoutGoal;
import com.tapc.android.data.Enum.WorkoutState;
import com.tapc.android.data.MessageDefine;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.data.Workout;
import com.tapc.android.data.WorkoutData;
import com.tapc.android.data.WorkoutInterval;
import com.tapc.android.data.Enum.MachineType;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.machine.Machine;
import com.tapc.android.sqlite.SQLiteHelper;
import com.tapc.android.util.AbstractFactory;
import com.tapc.android.interfaceset.RPMListener;
import com.tapc.android.interfaceset.UpdateTaskEx;
import com.tapc.android.interfaceset.WorkoutStateListener;
import com.tapc.platform.Config;
import com.tapc.platform.entity.BikeCtlType;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class Program {
	public static final String KEY_WORKOUT_REMAINING = "key_workout_remaining";
	public static final String KEY_WORKOUT_ELAPSED = "key_workout_elapsed";
	public static final String KEY_WORKOUT_STAGE = "key_workout_stage";
	public static final String KEY_WORKOUT_DATA = "key_workout_data";
	public static final String KEY_WORKOUT_TOTAL = "key_workout_time";
	
	Boolean mHaveInterval = false;
	ProgramType mProgramType;
	
	Machine mMachine;
	ProgramSetting mProgramSetting;
	Handler mUIHandler;
	
	private Timer mHRC;
	private Timer mCooldown;
	
	Workout mWorkout;
	WorkoutInterval mWorkoutInterval;
	UpdateTaskManager mTaskMgr;
	WorkoutState mState = WorkoutState.STOP;
	
	protected List<IntervalInfo> mIntervalList = null;
	
	abstract int getWarmupTime();
	abstract int getCooldownTime();
	abstract int getNormalTime();
	
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int MINUTES_PER_HOUR = 60;
	private static final int DEFUALT_WARMUP_TIME_SECOND;
	private static final int DEFUALT_COOLDOWN_TIME_SECOND;
	private static final int DEFUALT_WORKOUT_TIME_SECOND;
	private static final MachineType mMachineType;
	protected MachineController mController = MachineController.getInstance();
	
	
	static {
		DEFUALT_WARMUP_TIME_SECOND = (SystemSettingsHelper.getInt( 
				SystemSettingsHelper.KEY_WARMUP_TIME)) * SECONDS_PER_MINUTE;
		DEFUALT_COOLDOWN_TIME_SECOND = (SystemSettingsHelper.getInt( 
				SystemSettingsHelper.KEY_COOLDOWN_TIME)) * SECONDS_PER_MINUTE;
		DEFUALT_WORKOUT_TIME_SECOND = SystemSettingsHelper.getInt(
				SystemSettingsHelper.KEY_WORKOUT_TIME) * SECONDS_PER_MINUTE;
		int machineType = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MACHINE_TYPE);
		mMachineType = MachineType.values()[machineType];
	}
	
	public Program(ProgramSetting programSetting, Handler handler) {
		mProgramType = programSetting.getProgramType();
		mMachine = AbstractFactory.getInstance().getAFGFactory(mMachineType).makeMachineInterface();
		this.mProgramSetting = programSetting;
		this.mUIHandler = handler;
	}
	
	public Machine getMachine() {
		return mMachine;
	}
	
	public ProgramSetting getProgramSetting() {
		return mProgramSetting;
	}
	
	public List<IntervalInfo> getIntervaInfo() {
		if (mIntervalList!=null) {
			return mIntervalList;
		}else {
			return null;
		}
	}
	
	public void setIntervaInfo(List<IntervalInfo> intervalInfos) {
		this.mIntervalList = intervalInfos;
	}
	
	/**
	 * 
	 * @param msg
	 */
	public void translateMessage(Message msg) { 
		switch (msg.what) {
		case MessageDefine.MSG_ID_PRO_TICK: {
			mTaskMgr.trigger();
		}
		break;
		case MessageDefine.MSG_ID_PRO_SET_RIGHT_PANEL: {
			Bundle data = msg.getData();
			double value = data.getDouble(Workouting.KEY_RIGHT_PANEL_VALUE);
			mMachine.setRightPanel(value);
			mWorkout.setSpeed(value);
		}
		break;
		case MessageDefine.MSG_ID_PRO_SET_LEFT_PANEL: {
			Bundle data = msg.getData();
			double value = data.getDouble(Workouting.KEY_LEFT_PANEL_VALUE);
			mMachine.setLeftPanel(value);
			mWorkout.setIncline(value);
		}
		break;
		case MessageDefine.MSG_ID_CMD_SET_FAN_STATE: {
/*			Bundle data = msg.getData();
			int value = data.getInt(RunActivity.KEY_FAN_STATE);
			//mMachineInterface.SendRequest(MCUUARTCommand.SET_FAN_STATE, value);
			mMachine.setFanState(FanState.values()[value]);*/
		}
		break;
		case MessageDefine.MSG_ID_PRO_SEND_REQUEST: {
//			mMachine.sendRequest(msg.getData());
		}
		break;
		case MessageDefine.MSG_ID_PRO_START_WORKOUT: {
			startWorkout();
		}
		break;
		case MessageDefine.MSG_ID_PRO_STOP_WORKOUT: {
			stopWorkout();
		}
		break;
		case MessageDefine.MSG_ID_PRO_PAUSE_WORKOUT: {
			pauseWorkout();
		}
		break;
		case MessageDefine.MSG_ID_PRO_GOTO_COOLDOWN: {
			cooldownWorkout();
		}
		break;
		case MessageDefine.MSG_ID_PRO_RESUME_WORKOUT: {
			resumeWorkout();
		}
		break;
		case MessageDefine.MSG_ID_PRO_PLUS: {
			workoutTimePlus();
		}
		break;
		case MessageDefine.MSG_ID_PRO_MINUS: {
			workoutTimeMinus();
		}
		break;
		case MessageDefine.MSG_ID_PRO_CHANGE_WORKOUT_TIME: {
			Bundle data = msg.getData();
			int workoutTime = data.getInt("workout_time");
			changeWorkoutTime(workoutTime);
		}
		break;
			default: break;
		}
	}
	
	public Boolean isHaveInterval() {
		return mHaveInterval;
	}
	
	public ProgramType getProgramType() {
		return mProgramSetting.getProgramType();
	}
	
	public int getWorkoutTimeByStage(WorkoutStage stage) {
		int time = 0;
		switch (stage) {
			case WARMUP: {
				time = getWarmupTime();
				mWorkout.setTargetTime(time);
			}
			break;
			case NORMAL: {
				time = mProgramSetting.getTime();
				mWorkout.setTargetTime(time);
			}
			break;
			case COOLDOWN: {
				time = getCooldownTime();
				mWorkout.setTargetTime(time);
			}
			break;
			default:
				time = 0;
				mWorkout.setTargetTime(time);
			break;
		}
		
		return time; // seconds
	}
	
	public double getWorkoutDistanceByStage(WorkoutStage stage) {
		double distance = 0;
		switch (stage) {
			case NORMAL: {
				distance = mProgramSetting.getDistance();
				mWorkout.setTargetDistance(distance);
			}
			break;
			default:
				distance = -1;
				mWorkout.setTargetDistance(distance);
			break;
		}
		
		return distance; // seconds
	}
	
	public double getWorkoutCalorieByStage(WorkoutStage stage) {
		double calorie = 0;
		switch (stage) {
			case NORMAL: {
				calorie = mProgramSetting.getCalorie();
				mWorkout.setTargetCalorie(calorie);
			}
			break;
			default:
				calorie = -1;
				mWorkout.setTargetCalorie(calorie);
			break;
		}
		
		return calorie; // seconds
	}
	
	public int getTotalTime() {
		return mProgramSetting.getTime(); // seconds
	}
	
	public double getTotalDistance() {
		return mProgramSetting.getDistance(); // distance
	}
	
	public double getTotalCalorie() {
		return mProgramSetting.getCalorie(); // calorie
	}
	
	public int getDefaultWarmupTime() {
		return DEFUALT_WARMUP_TIME_SECOND;
	}

	public int getDefaultCooldownTime() {
		return DEFUALT_COOLDOWN_TIME_SECOND; 
	}
	
	public int getDefaultTotalTime() {
		return DEFUALT_WORKOUT_TIME_SECOND;
	}
	
	void workoutTimePlus() {
		int maxMinute = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MAX_WORKOUT_TIME);
	}
	
	void workoutTimeMinus() {
	}
	
	void changeWorkoutTime(int workoutTime) {
	}
	
	/**
	 * 
	 */
	public void startWorkout() {
		mWorkout = new Workout();
		mWorkout.setWorkoutGoal(WorkoutGoal.TIME);
		mWorkoutInterval = new WorkoutInterval();
		mTaskMgr = new UpdateTaskManager();
		mState = WorkoutState.RUNNING;
		
		// set machine
		mMachine.setRPMListener(mRPMListener);
//		mMachine.setUIHandler(mUIHandler);
		mMachine.setLeftPanel(mProgramSetting.getIncline());
		mMachine.setRightPanel(mProgramSetting.getSpeed());
		mMachine.start();
		
		mTaskMgr.clear();
		UpdateTaskEx task = null;
		
		// create task to update workout state
		task = new WorkoutStageUpdate(this);
		((WorkoutStageUpdate)task).initialization();
		mTaskMgr.add(WorkoutStageUpdate.TAG, task);
		
		// create task to update workout data
		task = new WorkoutDataUpdate(this);
		mTaskMgr.add(WorkoutDataUpdate.TAG, task);
		
		// save start timestamp 
		long timestamp = new Date().getTime();
		
		// set workout interval
		mWorkoutInterval.setStartTimestamp(timestamp);
		
		// set workout
		mWorkout.setStartTimestamp(timestamp);
		mWorkout.setProgramType(mProgramType);
		mWorkout.setLevel(mProgramSetting.getLevel());
		mWorkout.setIncline(mProgramSetting.getIncline());
		mWorkout.setSpeed(mProgramSetting.getSpeed());
		mWorkout.setTargetHeart(mProgramSetting.getTargetHeart());
		mWorkout.setWeight(mProgramSetting.getWeight());
	}
	
	public void stopWorkout() {
		if (mState == WorkoutState.STOP) {
			return;
		}
		intervalProgramChanged();
		mState = WorkoutState.STOP;
		
		WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
		mWorkout.setCurTime(task.getElapsed());
		
		IntervalUpdate intervalTask = (IntervalUpdate) mTaskMgr.get(IntervalUpdate.TAG);
		if (intervalTask!=null) {
			mWorkout.setCurTime(intervalTask.getElapsed());
		}
		mTaskMgr.clear();
		// stop machine
		mMachine.stop();
		
		processWorkoutFinish(false);
	}
	
	void processWorkoutFinish(boolean completed) {
		terminateHRCTimer();
		terminateCooldownTimer();
		sendUIMessage(MessageDefine.MSG_ID_UI_WORKOUT_FINISH);	
/*		if (mMachine.usePassport()) {
			mMachine.updatePassportStatus(RF_STATE.FINISH);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mMachine.initPassport(RF_STATE.IDLE);
			mAverageHelper.clear();
		}*/	
	}
	
	public void resumeWorkout() {
		mState = WorkoutState.RUNNING;
		
		// resume incline, speed if need
		mMachine.resume();
	}
	
	public void pauseWorkout() {
		mState = WorkoutState.PAUSE;
		
		// pause machine
		mMachine.pause();
		
	/*	if (mMachine.usePassport()) {
			mMachine.updatePassportStatus(RF_STATE.PAUSE);
		}*/
	}
	
	public void cooldownWorkout() {
/*		WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
		if (null != task) {
			if (task.mCurrentWorkoutStage==WorkoutStage.COOLDOWN) {
				return;
			}
		}
		task.mCurrentWorkoutStage = WorkoutStage.COOLDOWN;
		mMachine.setRightPanel(mMachine.getSpeed()/2);
		Log.e("COOLDOWN", " ");*/
	}
	
	public WorkoutState getWorkoutState() {
		return mState;
	}
	
	void notifyWorkoutStageChanged(WorkoutStage stage) {
		//mWorkout.clear();
/*		if (stage==WorkoutStage.COOLDOWN) {
			Log.e("COOLDOWN", " ");
			mMachine.setRightPanel(mMachine.getSpeed()/2);
		}*/
		Log.e("COOLDOWN", " ");
		if (stage==WorkoutStage.NORMAL){
			triggerHRCTimer();
		}
		if (stage == WorkoutStage.COOLDOWN) {
			triggerCooldownTimer();
		}
		Bundle data = new Bundle();
		data.putInt(KEY_WORKOUT_STAGE, stage.ordinal());

		sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_WORKOUT_STAGE, data);
		
		mWorkoutInterval.setCurrentWorkoutState(stage);
	}
	
	Boolean sendUIMessage(int msgID) {
		if (null == mUIHandler) {
			return false;
		}
		
		Message msg = mUIHandler.obtainMessage(msgID);
		return mUIHandler.sendMessage(msg);
	}
	
	Boolean sendUIMessage(int msgID, Bundle data) {
		if (null == mUIHandler) {
			return false;
		}
		
		Message msg = mUIHandler.obtainMessage(msgID);
		msg.setData(data);
		
		return mUIHandler.sendMessage(msg);
	}
	
	public WorkoutStage getWorkoutStartStage() {
		WorkoutStage stage = WorkoutStage.WARMUP;
		switch (mProgramType) {
			case MANUAL: stage = WorkoutStage.NORMAL; break;
			default: stage = WorkoutStage.WARMUP; break;
		}
		
		return stage;
	}
	
	public WorkoutStateListener getStateListener() {
		return mListener;
	}
	
/*	void onWorkoutTimeChanged(int workoutTime) {
		int totalTime[] = new int[2]; 
		totalTime[0] = (workoutTime / 60); // minutes
		totalTime[1] = (workoutTime % 60); // seconds
		
		Bundle data = new Bundle();
		data.putIntArray(KEY_WORKOUT_TOTAL, totalTime);
		
		mWorkout.setTotalTime(workoutTime);
		sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_TOTAL_TIME, data);
	}*/
	
	WorkoutStateListener mListener = new WorkoutStateListener() {
		
		@Override
		public void onStageChanged(WorkoutStage stage) {
			notifyWorkoutStageChanged(stage);
//			WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			mWorkout.stageChage(stage);
		}

		@Override
		public void onRemainingChanged(int[] remaining) {
			Bundle data = new Bundle();
			data.putIntArray(KEY_WORKOUT_REMAINING, remaining);
			
//			sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_REMAINING_TIME, data);
		}

		@Override
		public void onElapsedChanged(int elapsed) {
/*			Bundle data = new Bundle();
			data.putIntArray(KEY_WORKOUT_ELAPSED, elapsed);*/
			
//			sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_ELAPSED_TIME, data);
			mWorkout.setTime(elapsed);
//			mWorkout.setCurTime(elapsed);
		}

		@Override
		public void onWorkoutFinish() {
			// stop machine
			if (mState == WorkoutState.STOP) {
				return;
			}
			mState = WorkoutState.STOP;
			Log.e("onWorkoutFinish", " ");
			
			WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			mWorkout.setCurTime(task.getElapsed());
			
/*			IntervalUpdate intervalTask = (IntervalUpdate) mTaskMgr.get(IntervalUpdate.TAG);
			if (intervalTask!=null) {
				mWorkout.setCurTime(intervalTask.getElapsed());
			}*/
			
			
			mMachine.stop();
			
			processWorkoutFinish(true);
		}
	};
	
	class WorkoutDataUpdate implements UpdateTaskEx {
		public static final String TAG = "WorkoutDataUpdate";
		private Program mProgram;
		
		public WorkoutDataUpdate(Program prgoram) {
			mProgram = prgoram;
		}
		
		@Override
		public void run() {
			// update machine data every second
//			mMachine.update(mWorkout.getMass());
			// update workout data every second
			mWorkout.update(mProgram, mMachineType, 1000);
			
			// send data to UI
			Bundle data = new Bundle();
			data.putParcelable(KEY_WORKOUT_DATA, mWorkout);
			sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_DATA, data);
						
/*			if (mMachine.usePassport()) {
				mMachine.updatePassportStatus(RF_STATE.IN_USE);
			}*/
		}
	}
	
	RPMListener mRPMListener = new RPMListener() {
		@Override
		public void onRPMChanged(int rpm) { // if RPM changed that means the workout will going to next interval
			if (rpm > 0 || WorkoutState.STOP == mState) {
				mWorkoutInterval.addEntry(mWorkout);
				mWorkout.intervalChage();
			}
		}
	};

	public void intervalProgramChanged() {
		mWorkoutInterval.addEntry(mWorkout);
		mWorkout.intervalChage();
	}

	public WorkoutInterval getWorkoutInterval() {
		return mWorkoutInterval;
	}
	
	void terminateHRCTimer() {
		if (null != mHRC) {
			mHRC.cancel();
		}
	}
		
	void triggerHRCTimer() {
		mHRC = new Timer();
		mHRC.schedule(new HRCTimerTask(), 10000, 10000);
	}
	
	class HRCTimerTask extends TimerTask {
		@Override
		public void run() {
			// something must to do each time reach
			WorkoutStageUpdate workoutTask = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			if (workoutTask==null) {
				return;
			}
			if (mState != WorkoutState.RUNNING ) {
				return;
			}
			if (workoutTask.mCurrentWorkoutStage!=WorkoutStage.NORMAL) {
				return;
			}
			if (mWorkout.getTargetHeart()!=0) {
				if (mWorkout.getTargetHeart()>mWorkout.getHeartRate()) {
					Log.e("getTargetHeart", " "+mWorkout.getTargetHeart());
					long A = mWorkout.getTargetHeart()- mWorkout.getHeartRate();
					if (A>=10 && A<15) {
						double incline = mMachine.getIncline();
						incline += 1;
						mMachine.setIncline(incline);
					}
					if (A>=15) {
						double speed = mMachine.getSpeed();
						speed += 0.5;
						mMachine.setSpeed(speed);
					}
				}else {
					long B = mWorkout.getHeartRate() - mWorkout.getTargetHeart();
					if (B>=10 && B<15) {
						double incline = mMachine.getIncline();
						incline -= 1;
						mMachine.setIncline(incline);
					}
					if (B>=15) {
						double speed = mMachine.getSpeed();
						speed -= 0.5;
						mMachine.setSpeed(speed);
					}
				}
			}
		}
	}
	
	void terminateCooldownTimer() {
		if (null != mCooldown) {
			mCooldown.cancel();
		}
	}
		
	void triggerCooldownTimer() {
		mCooldown = new Timer();
		mCooldown.schedule(new CooldownTimerTask(), 20000, 20000);
	}
	
	class CooldownTimerTask extends TimerTask {
		@Override
		public void run() { 
			// something must to do each time reach
			WorkoutStageUpdate workoutTask = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			if (workoutTask==null) {
				return;
			}
			if (mState != WorkoutState.RUNNING ) {
				return;
			}
			if (workoutTask.mCurrentWorkoutStage!=WorkoutStage.COOLDOWN) {
				return;
			}
			if (Config.sBikeCtlType == BikeCtlType.LOAD) {
				double incline = mWorkout.getIncline();
				if ((incline / 2) >= SystemSettingsHelper.MIN_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE) {
					incline /= 2;
					if (incline % SystemSettingsHelper.STEP_INCLINE != 0) {
						int inclinetemp = (int) (incline / SystemSettingsHelper.STEP_INCLINE);
						incline = inclinetemp * SystemSettingsHelper.STEP_INCLINE;
					}
				} else {
					incline = SystemSettingsHelper.MIN_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE;
				}
				mMachine.setIncline(incline);
			} else {
				double speed = mWorkout.getSpeed();
				if ((speed / 2) >= SystemSettingsHelper.MIN_SPEED) {
					speed /= 2;
				} else {
					speed = SystemSettingsHelper.MIN_SPEED;
				}
				mMachine.setSpeed(speed);
			}
		}
	}
}
