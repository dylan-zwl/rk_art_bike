package com.tapc.platform.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;

public class WorkoutCtlSetActivity extends BaseActivity {
	public static final String SPEED = "speed";
	public static final String INCLINE = "incline";
	@ViewInject(R.id.workout_ctl_seekbar)
	private SeekBar mSeekBar;

	@ViewInject(R.id.workout_ctl_title)
	private TextView mValueText;

	private String mType;
	private int mValueMax = 0;
	private int mValueMin = 0;
	private int mStartValue = 0;
	private int mValue;
	private int mValueStep;
	private int mValuePoint = 1;

	public static void launch(Context c, String type, double starValue) {
		Intent i = new Intent(c, WorkoutCtlSetActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("type", type);
		i.putExtra("starValue", starValue);
		c.startActivity(i);
		((Activity) c).overridePendingTransition(R.anim.in, R.anim.out);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_workout_ctl);
		ViewUtils.inject(this);

		mType = getIntent().getStringExtra("type");
		if (mType != null) {
			mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {

				}

				@Override
				public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
					if (progress >= mValueMin) {
						mValue = progress;
						if (mType.equals(SPEED)) {
							mValueText.setText(getString(R.string.speed) + " "
									+ String.format(getString(R.string.workout_ctl_title), mValue / mValuePoint) + "."
									+ (mValue % mValuePoint) + " w");
						} else if (mType.equals(INCLINE)) {
							mValueText.setText(getString(R.string.incline) + " "
									+ String.format(getString(R.string.workout_ctl_title), mValue / mValuePoint) + " %");
						}
					} else {
						mSeekBar.setProgress(mValueMin);
					}
				}
			});

			if (mType.equals(SPEED)) {
				mValuePoint = 1;
				mValueMax = (int) (TapcApp.MAX_SPEED * mValuePoint);
				mValueMin = (int) (TapcApp.MIN_SPEED * mValuePoint);
				mValueStep = (int) (TapcApp.STEP_SPEED * mValuePoint);
				mStartValue = (int) (getIntent().getDoubleExtra("starValue", 0) * mValuePoint);
				mSeekBar.setMax(mValueMax);
				mSeekBar.setProgress(mStartValue);
			} else if (mType.equals(INCLINE)) {
				mValuePoint = 1;
				mValueMax = (int) (TapcApp.MAX_INCLINE);
				mValueMin = (int) (TapcApp.MIN_INCLINE);
				mValueStep = (int) (TapcApp.STEP_INCLINE);
				mStartValue = (int) (getIntent().getDoubleExtra("starValue", 0));
				mSeekBar.setMax(mValueMax);
				mSeekBar.setProgress(mStartValue);
			}
		} else {
			finish();
		}
	}

	@OnClick(R.id.confirm_btn)
	protected void confirm(View v) {
		if (TapcApp.getInstance().getSportsEngin().isRunning()) {
			if (mType.equals(SPEED)) {
				TapcApp.getInstance().mainActivity.getSpeedCtrl().setCtlValue(((double) mValue) / mValuePoint);
			} else if (mType.equals(INCLINE)) {
				TapcApp.getInstance().mainActivity.getInclineCtrl().setCtlValue(mValue / mValuePoint);
			}
		}
		finish();
	}

	@OnClick(R.id.cancle_btn)
	protected void cancle(View v) {
		finish();
	}

	@OnClick(R.id.workout_ctl_add)
	protected void addTime(View v) {
		mSeekBar.setProgress(mSeekBar.getProgress() + mValueStep);
	}

	@OnClick(R.id.workout_ctl_sub)
	protected void subTime(View v) {
		if ((mSeekBar.getProgress() - mValueStep) >= mValueMin) {
			mSeekBar.setProgress(mSeekBar.getProgress() - mValueStep);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
		overridePendingTransition(R.anim.in, R.anim.out);
	}
}
