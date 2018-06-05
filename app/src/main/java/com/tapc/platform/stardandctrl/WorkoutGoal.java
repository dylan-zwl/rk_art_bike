package com.tapc.platform.stardandctrl;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class WorkoutGoal extends LinearLayout {

	// @ViewInject(R.id.goalSub)
	// private Button mSub;
	//
	// @ViewInject(R.id.goalAdd)
	// private Button mAdd;

	@ViewInject(R.id.goalProgress)
	private ProgressBar mGoalProgress;

	@ViewInject(R.id.totalTitle)
	private WorkoutTitle mWorkoutTitle;

	public WorkoutGoal(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WorkoutGoal(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		View view = LayoutInflater.from(context).inflate(R.layout.workout_goal,
				this);
		ViewUtils.inject(view);
	}

	public void SetRange(float min, float max) {
		mGoalProgress.setMax((int) max);
	}

	public float getPos() {
		return mGoalProgress.getProgress();
	}

	public void setPos(float pos) {
		mGoalProgress.setProgress((int) pos);
		invalidate();
	}

	public void setTitleName(String titleName, String stringUnit) {
		mWorkoutTitle.setTitleName(titleName, stringUnit);
	}
	// @OnClick(R.id.goalSub)
	// private void subClick(View v)
	// {
	//
	// }
	//
	// @OnClick(R.id.goalAdd)
	// private void addClick(View v)
	// {
	//
	// }
}