package com.tapc.android.workouting;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.android.data.Enum.MachineType;
import com.tapc.android.data.Enum.Unit;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.data.Enum.WorkoutState;
import com.tapc.android.data.MessageDefine;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.data.Workout;
import com.tapc.android.data.WorkoutInfo;
import com.tapc.android.data.WorkoutInterval;
import com.tapc.android.data.WorkoutIntervalInfo;
import com.tapc.android.util.AbstractFactory;

public class Workouting {
	public final static String TAG = "Workouting";
	public final static String KEY_RIGHT_PANEL_VALUE = "key_right_panel_value";
	public final static String KEY_LEFT_PANEL_VALUE = "key_right_panel_value";
	private final static String KEY_WORKOUT = "key_workout";
	private final static MachineType mMachineType;

	protected List<IntervalInfo> mIntervalList = null;

	// the workout whether mIsRunning
	public boolean mIsRunning = false;
	public boolean mIsPausing = false;
	// run timer task
	private Timer mTimer; // triggered each 1000 millsecond
	ProgramSetting mProgramSetting;
	Program mProgram;
	Unit mUnit = Unit.STANDARD;
	private Workout mWorkout = new Workout();// the workout data
	private WorkoutInfo mWorkoutInfo;// save the workout
	private WorkoutInterval mWorkoutInterval;// save the workout interval data
	private UIHandler mUIHandler = new UIHandler(); // for ui thread
	private ProgramHandler mProgramHandler = new ProgramHandler(); // for
																	// program
																	// thread
	// private ProgramThread mProgramThread = new ProgramThread(); // to
	// dispatch program message
	Context mContext;

	static {
		// get machine type
		int machineType = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MACHINE_TYPE,
				MachineType.TREADMILL.ordinal());
		mMachineType = MachineType.values()[machineType];

		// mPassportActived = SystemSettingsHelper.getBoolean(context,
		// "key_passport_active", false);
	}

	public void setBroadcast(Context context) {
		mContext = context;
	}

	public Workouting(ProgramSetting programSetting) {
		if (programSetting != null) {
			mProgramSetting = programSetting;
		} else {
			mProgramSetting = new ProgramSetting();
		}
	}

	public void onStart() {
		if (mIsRunning) {
			return;
		}
		mIsRunning = true;
		mProgram = AbstractFactory.getInstance().getAFGFactory(mMachineType).makeProgram(mProgramSetting, mUIHandler);
		if (mIntervalList != null) {
			mProgram.setIntervaInfo(mIntervalList);
		}
		sendProgramMessage(MessageDefine.MSG_ID_PRO_START_WORKOUT);
		// trigger timer
		triggerTimer();
	}

	public void setIntervaInfo(List<IntervalInfo> intervalInfos) {
		this.mIntervalList = intervalInfos;
	}

	public List<IntervalInfo> getIntervaInfo() {
		if (mIntervalList != null) {
			return mIntervalList;
		} else {
			return null;
		}
	}

	public void onStop() {
		if (!mIsRunning && getWorkoutState() == WorkoutState.STOP) {
			return;
		}
		if (mIntervalList != null) {
			mIntervalList.clear();
			mIntervalList = null;
		}
		mIsRunning = false;
		// notify program that workout has stop
		sendProgramMessage(MessageDefine.MSG_ID_PRO_STOP_WORKOUT);
	}

	public void onPause() {
		if (!mIsRunning) {
			return;
		}
		mIsRunning = false;
		// goto pause
		// notify program that workout has pause
		sendProgramMessage(MessageDefine.MSG_ID_PRO_PAUSE_WORKOUT);
		// terminate timer
		terminateTimer();
	}

	public void onResume() {
		if (mIsRunning) {
			return;
		}
		mIsRunning = true;
		// notify program that workout has resume
		sendProgramMessage(MessageDefine.MSG_ID_PRO_RESUME_WORKOUT);

		// trigger timer
		triggerTimer();
	}

	// Pause or Resume button clicked
	public void onPauseOrResume() {
		pauseOrResume();
	}

	void pauseOrResume() {
		if (mIsRunning) { // if current workout is in running
			// pause
			onPause();
		} else { // if current workout is in pausing
			// resume running
			onResume();
		}
	}

	public void onCooldown() {
		if (!mIsRunning) {
			return;
		}

		sendProgramMessage(MessageDefine.MSG_ID_PRO_GOTO_COOLDOWN);
	}

	// process left(incline) add
	public double onLeftKeypadAdd() {
		if (mIsRunning == false) {
			return 0;
		}
		double incline = mWorkout.getIncline();
		if (Math.abs(SystemSettingsHelper.MAX_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE - incline) > SystemSettingsHelper.STEP_INCLINE) {
			incline += SystemSettingsHelper.STEP_INCLINE;
			return onLeftKeypadEvent(incline);
		} else {
			incline = SystemSettingsHelper.MAX_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE;
			return onLeftKeypadEvent(incline);
		}
	}

	// process left(incline) sub
	public double onLeftKeypadSub() {
		if (mIsRunning == false) {
			return 0;
		}
		double incline = mWorkout.getIncline();
		if (Math.abs(incline - SystemSettingsHelper.MIN_INCLINE) > SystemSettingsHelper.STEP_INCLINE) {
			incline -= SystemSettingsHelper.STEP_INCLINE;
			return onLeftKeypadEvent(incline);
		} else {
			incline = SystemSettingsHelper.MIN_INCLINE;
			return onLeftKeypadEvent(incline);
		}
	}

	// process right(speed) add
	public double onRightKeypadAdd() {
		if (mIsRunning == false) {
			return 0;
		}
		double speed = mWorkout.getSpeed();
		if (Math.abs(SystemSettingsHelper.MAX_SPEED - speed) > SystemSettingsHelper.STEP_SPEED) {
			speed += SystemSettingsHelper.STEP_SPEED;
			return onRightKeypadEvent(speed);
		} else {
			speed = SystemSettingsHelper.MAX_SPEED;
			return onRightKeypadEvent(speed);
		}
	}

	// process right(speed) sub
	public double onRightKeypadSub() {
		if (mIsRunning == false) {
			return 0;
		}
		double speed = mWorkout.getSpeed();
		if (Math.abs(speed - SystemSettingsHelper.MIN_SPEED) > SystemSettingsHelper.STEP_SPEED) {
			speed -= SystemSettingsHelper.STEP_SPEED;
			return onRightKeypadEvent(speed);
		} else {
			speed = SystemSettingsHelper.MIN_SPEED;
			return onRightKeypadEvent(speed);
		}
	}

	// process left(incline) changed
	public double onLeftKeypadEvent(double value) {
		Log.d(TAG, "onLeftKeypadEvent:" + value);
		if (value >= SystemSettingsHelper.MAX_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE) {
			value = SystemSettingsHelper.MAX_INCLINE + SystemSettingsHelper.ORIGIN_INCLINE;
		}
		if (value <= SystemSettingsHelper.MIN_INCLINE) {
			value = SystemSettingsHelper.MIN_INCLINE;
		}
		// send the new value to program
		//if (mIsRunning) {
			Bundle data = new Bundle();
			data.putDouble(KEY_LEFT_PANEL_VALUE, value);
			sendProgramMessage(MessageDefine.MSG_ID_PRO_SET_LEFT_PANEL, data);
		//}
		return value;
	}

	// process right(speed or resistance) changed
	public double onRightKeypadEvent(double value) {
		Log.d(TAG, "onRightKeypadEvent:" + value);
		if (value >= SystemSettingsHelper.MAX_SPEED) {
			value = SystemSettingsHelper.MAX_SPEED;
		}
		if (value <= SystemSettingsHelper.MIN_SPEED) {
			value = SystemSettingsHelper.MIN_SPEED;
		}
		// send the new value to program
		//if (mIsRunning) {
			Bundle data = new Bundle();
			data.putDouble(KEY_RIGHT_PANEL_VALUE, value);
			sendProgramMessage(MessageDefine.MSG_ID_PRO_SET_RIGHT_PANEL, data);
		//}
		return value;
	}

	void terminateTimer() {
		if (null != mTimer) {
			mTimer.cancel();
		}
	}

	void triggerTimer() {
		mTimer = new Timer();
		mTimer.schedule(new WorkoutTimerTask(), 0, 1000);
	}

	Boolean sendProgramMessage(int msgID) {
		if (null == mProgramHandler) {
			return false;
		}

		Message msg = mProgramHandler.obtainMessage(msgID);
		return mProgramHandler.sendMessage(msg);
	}

	Boolean sendProgramMessage(int msgID, Bundle data) {
		return sendProgramMessage(msgID, data, 0);
	}

	Boolean sendProgramMessage(int msgID, Bundle data, long delay) {
		if (null == mProgramHandler) {
			return false;
		}

		Message msg = mProgramHandler.obtainMessage(msgID);
		msg.setData(data);

		return mProgramHandler.sendMessageDelayed(msg, delay);
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

	class WorkoutTimerTask extends TimerTask {
		@Override
		public void run() {
			// something must to do each time reach
			sendProgramMessage(MessageDefine.MSG_ID_PRO_TICK);
			/*
			 * if (mProgram!=null) { if
			 * (Math.abs((int)mProgram.mMachine.getRealRPM()-
			 * mProgram.mMachine.getRPM())>20) {
			 * mProgram.mMachine.setRealRPM(mProgram.mMachine.getRPM()); } };
			 */
			// sendUIMessage(MessageDefine.MSG_ID_UI_UPDATE_CLOCK);
		}
	}

	void updateWorkout(Workout workout) {
		mWorkout = workout;
	}

	public Workout getWorkout() {
		return mWorkout;
	}

	public WorkoutInfo getWorkoutInfo() {
		return mWorkoutInfo;
	}

	public WorkoutInterval getWorkoutInterval() {
		return mWorkoutInterval;
	}

	public WorkoutState getWorkoutState() {
		return mProgram.getWorkoutState();
	}

	void workoutFinish() {
		if (mIsRunning != false) {
			mIsRunning = false;
		}
		// terminate timer
		// terminateTimer();
		saveWorkoutData();
		exit();
		terminateTimer();
	}

	void saveWorkoutData() {
		// save workout record
		Log.e("saveWorkoutData() ", "Workou");
		mWorkoutInfo = mWorkout.getWorkoutInfo();
		// save workout interval
		mWorkoutInterval = mProgram.getWorkoutInterval();
	}

	void exit() {
		/*
		 * Bundle bundle = new Bundle(); bundle.putParcelable(KEY_WORKOUT,
		 * mWorkout);
		 * 
		 * Intent data = new Intent(); data.putExtras(bundle);
		 */
		Log.e("result-WorkoutInfo", "Time  " + mWorkoutInfo.getTime());
		Log.e("result-WorkoutInfo", "Calorie  " + mWorkoutInfo.getCalorie());
		Log.e("result-WorkoutInfo", "Distance  " + mWorkoutInfo.getDistance());

		for (int i = 0; i < mWorkoutInterval.mIntervalList.size(); i++) {
			WorkoutIntervalInfo workoutIntervalInfo = mWorkoutInterval.mIntervalList.get(i);
			Log.e("result-WorkoutIntervalInfo", "Time  " + workoutIntervalInfo.getTime() + "  index  " + (i + 1));
			Log.e("result-WorkoutIntervalInfo", "Calorie  " + workoutIntervalInfo.getCalorie() + "  index  " + (i + 1));
			Log.e("result-WorkoutIntervalInfo", "Distance  " + workoutIntervalInfo.getDistance() + "  index  "
					+ (i + 1));
		}
		/*
		 * for (WorkoutIntervalInfo workoutIntervalInfo :
		 * mWorkoutInterval.mIntervalList) { Log.e("result-WorkoutIntervalInfo",
		 * "Time  "+workoutIntervalInfo.getTime());
		 * Log.e("result-WorkoutIntervalInfo",
		 * "Calorie  "+workoutIntervalInfo.getCalorie());
		 * Log.e("result-WorkoutIntervalInfo",
		 * "Distance  "+workoutIntervalInfo.getDistance()); }
		 */
		Intent intent = new Intent(); // ���ڴ�����ݵ�intent
		intent.setAction(MessageDefine.MSG_WORKOUT_FINISH); // Ϊ���intent����action�����ڹ㲥����������
		mContext.sendBroadcast(intent); // ʹ��sendBroadcast���͹㲥
	}

	class ProgramHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// Log.e("ProgramHandler", "msg:"+msg.what);
			mProgram.translateMessage(msg);
		}
	}

	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageDefine.MSG_ID_UI_UPDATE_DATA: { // update every second
				// save workout data
				Bundle data = msg.getData();
				Workout workout = data.getParcelable(Program.KEY_WORKOUT_DATA);
				// update
				updateWorkout(workout);
			}
				break;
			case MessageDefine.MSG_ID_UI_WORKOUT_FINISH: { // workout finish
				workoutFinish();
			}
				break;
			case MessageDefine.MSG_ID_UI_UPDATE_WORKOUT_STAGE: {
				Bundle data = msg.getData();
				WorkoutStage stage = WorkoutStage.values()[data.getInt(Program.KEY_WORKOUT_STAGE)];
				/*
				 * if (stage==WorkoutStage.COOLDOWN) {
				 * sendProgramMessage(MessageDefine.MSG_ID_PRO_GOTO_COOLDOWN); }
				 */
			}
				break;
			default:
				break;
			}

		}
	}
}
