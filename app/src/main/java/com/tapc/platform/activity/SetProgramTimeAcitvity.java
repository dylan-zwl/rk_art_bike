package com.tapc.platform.activity;

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
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.workout.MessageType;

public class SetProgramTimeAcitvity extends BaseActivity {
	@ViewInject(R.id.program_time_seekbar)
	private SeekBar mSeekBar;

	@ViewInject(R.id.program_time_title)
	private TextView mProgramTimeText;

	private int mProgramTimeMax = 99;
	private int mProgramTimeMin = 5;
	private int mStartTime = 30;
	private int mProgramTime;
	private FitnessSetAllEntity mAllEntity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_program_time);
		ViewUtils.inject(this);
		mAllEntity = TapcApp.getInstance().getSportsEngin().getFitness();
		int program = mAllEntity.getLevel();
		if (program >= 1 && program <= 6) {
			TapcApp.getInstance().mainActivity.setWattMode();
		} else if (program >= 6 && program <= 9) {
			TapcApp.getInstance().mainActivity.setLoadMode();
		}
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				mProgramTime = progress + mProgramTimeMin;
				mProgramTimeText.setText(String.format(getString(R.string.program_time), mProgramTime));
				if (mAllEntity != null) {
					mAllEntity.setProgramTime(mProgramTime);
				}
			}
		});
		mSeekBar.setMax(mProgramTimeMax - mProgramTimeMin);
		mSeekBar.setProgress(mStartTime - mProgramTimeMin);
	}

	@OnClick(R.id.program_start)
	protected void start(View v) {
		if (mAllEntity != null) {
			TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@OnClick(R.id.program_time_add)
	protected void addTime(View v) {
		mSeekBar.setProgress(mSeekBar.getProgress() + 1);
	}

	@OnClick(R.id.program_time_sub)
	protected void subTime(View v) {
		mSeekBar.setProgress(mSeekBar.getProgress() - 1);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
