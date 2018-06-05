package com.tapc.android.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.tapc.android.data.Enum.WorkoutStage;

import android.content.Context;
import android.util.Log;

public class WorkoutInterval {
	private static final String TAG = "WorkoutIntervalList";
	
	public List<WorkoutIntervalInfo> mIntervalList = new LinkedList<WorkoutIntervalInfo>();
	WorkoutStage mWorkoutStage;
	long startTimeStamp;
	public void addEntry(Workout workout) {
		if (workout.getCurDistance()==0 || workout.getCurTime()==0)
			return;
		WorkoutIntervalInfo interval = new WorkoutIntervalInfo();
		// workout id
//		Log.e("addEntry", "WorkoutStage"+mWorkoutStage);
		// start time	
	/*	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strTime = format.format(startTimeStamp);*/
		interval.setStartTime(new Date().getTime());
		// distance
		interval.setDistance(workout.getCurDistance());
		
		// calorie
		interval.setCalorie(workout.getCurCalorie());
						
		// second
		interval.setTime(workout.getCurTime());
		
		// pace
		interval.setPace(workout.getCurPace());
		
		// altitude
		interval.setAltitude(workout.getCurAltitude());
		
		// heart
		interval.setHeart(workout.getCurHeart());
		
		// interval index
		interval.setIntervalIndex(getCount());
		
		Log.e("interval info", "time  "+interval.getTime()+"  "+
							   "Calorie  "+interval.getCalorie()+"  "+
							   "Distance  "+interval.getDistance());
				
		// watts
				
		// mets
				
		// RPM
				
		// target heart
		
		// add interval to list
	
/*     */
		mIntervalList.add(interval);
		
		// set last timestamp as now
	//	setStartTimestamp(new Date().getTime());
	}
	
	public int getCount() {
		return mIntervalList.size();
	}

	public void setStartTimestamp(long time) {
		startTimeStamp = time;
	}
	
	public void setCurrentWorkoutState(WorkoutStage stage) {
		this.mWorkoutStage = stage;
	}
}
