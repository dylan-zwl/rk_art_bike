package com.tapc.android.workouting;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.data.MessageDefine;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.interfaceset.IntervalUpdateListener;
import com.tapc.android.sqlite.DBManager;

public class IntervalProgram extends Program {
	final static String TAG = "IntervalProgram";
	public static final String DB_NAME = "premierprograms.db"; // �������ݿ��ļ���
	public static final String PACKAGE_NAME = "com.example.workout";
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME; // ���ֻ�������ݿ��λ��(/data/data/com.cssystem.activity/cssystem.db)

	protected List<IntervalInfo> mIntervalList = new LinkedList<IntervalInfo>();

	public IntervalProgram(ProgramSetting programSetting, Handler handler) {
		super(programSetting, handler);
		mHaveInterval = true;
	}

	@Override
	public void translateMessage(Message msg) {
		super.translateMessage(msg);
		switch (msg.what) {
		case MessageDefine.MSG_ID_PRO_GOTO_COOLDOWN: {
			WorkoutStageUpdate task = (WorkoutStageUpdate) mTaskMgr.get(WorkoutStageUpdate.TAG);
			if (null != task) {
				task.gotoCooldown();
			}
		}
			break;
		default:
			break;
		}
	}

	public void startWorkout() {
		mIntervalList.clear();
		if (super.getIntervaInfo() != null) {
			mIntervalList = super.getIntervaInfo();
		} else {
			loadIntervalFromDB();
			MakeChangeTime();
		}
		super.startWorkout();
	}

	void notifyWorkoutStageChanged(WorkoutStage stage) {
		super.notifyWorkoutStageChanged(stage);
		// create task to update interval
		IntervalUpdate task = (IntervalUpdate) mTaskMgr.get(IntervalUpdate.TAG);
		if (null == task) {
			task = new IntervalUpdate(this);
			mTaskMgr.add(IntervalUpdate.TAG, task);
		}

		// set new workout state
		task.setWorkoutStage(stage);

		// set new intervals from DB
		List<IntervalInfo> intervalList = getIntervalByWorkoutStage(stage);
		task.setIntervalList(intervalList);
		if (intervalList.size() > 0 && task != null) {
			((IntervalUpdate) task).initialization();
		}
	}

	List<IntervalInfo> getIntervalByWorkoutStage(WorkoutStage stage) {
		List<IntervalInfo> intervalList = new LinkedList<IntervalInfo>();
		for (IntervalInfo interval : mIntervalList) {
			if (stage == interval.getWorkoutStage()) {
				intervalList.add(interval);
			}
		}

		return intervalList;
	}

	IntervalUpdateListener mIntervalUpdateListener = new IntervalUpdateListener() {
		@Override
		public void onIntervalChanged(int index, IntervalInfo interval) {
			Log.e("IntervalChanged", "index  " + index);
			if (!SystemSettingsHelper.getIntervalSound() && index != 0) {
				SystemSettingsHelper.setIntervalSound(true);
				intervalProgramChanged();
			}
			mMachine.loadInterval(interval);
		}

		@Override
		public void onIntervalElapsedChanged(int elapsed) {
			// TODO Auto-generated method stub
			// Log.e("ElapsedChanged", " "+elapsed);
			mWorkout.setCurTime(elapsed);
		}
	};

	void MakeChangeTime() {
		if (mProgramSetting.getProgramType() == ProgramType.INTERVALS) {
			int elapse = 0, time = mProgramSetting.getTime();
			int interval = mIntervalList.size();
			if (interval > 0) {
				elapse = time / interval;
			}
			for (int i = 0; i < interval; i++) {
				mIntervalList.get(i).setChangeTime(elapse);
			}
			int changetime = (time % interval == 0 ? time / interval : time - time / interval * (interval - 1));
			mIntervalList.get(interval - 1).setChangeTime(changetime);
		}

		if (mProgramSetting.getProgramType() == ProgramType.TAPC_PROG) {
			int elapse = 0, time = mProgramSetting.getTime();
			int interval = mIntervalList.size();
			if (interval > 0) {
				elapse = time / interval;
			}
			for (int i = 0; i < interval; i++) {
				mIntervalList.get(i).setChangeTime(elapse);
			}
			int changetime = (time % interval == 0 ? time / interval : time - time / interval * (interval - 1));
			mIntervalList.get(interval - 1).setChangeTime(changetime);
		}

		if (mProgramSetting.getProgramType() == ProgramType.FAT_BURN) {
			int elapse = 0, time = mProgramSetting.getTime();
			int interval = mIntervalList.size();
			if (interval > 0) {
				elapse = (time % (interval - 1) == 0 ? time / (interval - 1) - 1 : time / (interval - 1));
			}
			for (int i = 0; i < interval; i++) {
				mIntervalList.get(i).setChangeTime(elapse);
			}
			int changetime = time - elapse * (interval - 1);
			mIntervalList.get(interval - 1).setChangeTime(changetime);
		}

		for (IntervalInfo info : mIntervalList) {
			Log.d("interval", "speed=" + info.getSpeed() + "--" + "resistance=" + info.getResistance() + "--"
					+ "incline=" + info.getIncline() + "--" + "change_time=" + info.getChangeTime() + "--"
					+ "change_distance=" + info.getChangeDistance());
		}
	}

	void loadIntervalFromDB() {
		// set query field
		DBManager dbManager = new DBManager();

		SQLiteDatabase db = dbManager.openDatabase();
		String sql;
		int type = 0;
		// sql ="select * from workout where workout_name='INTERVALS'";
		sql = "select * from workout where workout_name=" + "'" + mProgramSetting.getProgramType() + "'";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			type = cursor.getInt(cursor.getColumnIndex("id"));
			Log.d("workout", "workout_id=" + type);
		}
		sql = "select * from interval where workout_id=" + type + " and workout_level=" + mProgramSetting.getLevel();
		cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			IntervalInfo info = new IntervalInfo();
			info.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
			info.setResistance(cursor.getInt(cursor.getColumnIndex("resistance")));
			double incline = cursor.getDouble(cursor.getColumnIndex("incline"));
			if (incline != -1) {
				incline += Math.abs(SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_ORIGIN_INCLINE));
			}
			info.setIncline(incline);
			// info.setIncline(cursor.getDouble(cursor.getColumnIndex("incline")));
			info.setChangeTime(cursor.getInt(cursor.getColumnIndex("change_time")));
			info.setChangeDistance(cursor.getDouble(cursor.getColumnIndex("change_distance")));
			info.setWorkoutStage(WorkoutStage.NORMAL);
			mIntervalList.add(info);
			/*
			 * Log.d("interval","speed="+info.getSpeed()+
			 * "--"+"resistance="+info.getResistance()+
			 * "--"+"incline="+info.getIncline()+
			 * "--"+"change_time="+info.getChangeTime()+
			 * "--"+"change_distance="+info.getChangeDistance());
			 */
		}
		db.close();
	}

	int getWarmupTime() {
		int ret = getWorkoutTimeByStateFromInterval(WorkoutStage.WARMUP);
		if (0 == ret) {
			ret = super.getDefaultWarmupTime();
		}

		return ret; // seconds
	}

	int getNormalTime() {
		int ret = getTotalTime()/* - (getWarmupTime() + getCooldownTime()) */;
		if (ret < 0) {
			ret = super.getDefaultTotalTime();
		}

		return ret; // seconds
	}

	int getCooldownTime() {
		int ret = getWorkoutTimeByStateFromInterval(WorkoutStage.COOLDOWN);
		if (0 == ret) {
			ret = super.getDefaultCooldownTime();
		}
		return ret; // seconds
	}

	public int getWorkoutTimeByStateFromInterval(WorkoutStage stage) {
		int ret = 0;

		for (IntervalInfo interval : mIntervalList) {
			if (interval.getWorkoutStage() == stage) {
				ret += interval.getChangeTime(); // seconds
			}
		}

		return ret;
	}
}
