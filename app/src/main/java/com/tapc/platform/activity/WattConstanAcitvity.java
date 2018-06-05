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
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.workout.MessageType;

public class WattConstanAcitvity extends BaseActivity {
	@ViewInject(R.id.program_time_seekbar)
	private SeekBar mSeekBar;
	@ViewInject(R.id.watt_value_seekbar)
	private SeekBar mWattSeekBar;

	@ViewInject(R.id.program_time_title)
	private TextView mProgramTimeText;
	@ViewInject(R.id.watt_value_title)
	private TextView mWattValueTv;

	private int mProgramTimeMax = 99;
	private int mProgramTimeMin = 5;
	private int mStartTime = 30;
	private int mProgramTime;

	private int mWattValueMax = 0;
	private int mWattValueMin = 0;
	private int mStartWattValue = 120;
	private int mWattValue;

	private FitnessSetAllEntity mAllEntity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_watt_constan);
		ViewUtils.inject(this);

		mWattValueMax = (int) TapcApp.getInstance().MAX_SPEED;
		mWattValueMin = (int) TapcApp.getInstance().MIN_SPEED;

		mAllEntity = new FitnessSetAllEntity();
		TapcApp.getInstance().getSportsEngin().openFitness(mAllEntity);
		mAllEntity.setMinutesEntity(ProgramType.TIME, mStartTime, SystemSettingsHelper.WEIGHT,
				SystemSettingsHelper.DEFAULT_INCLINE, mStartWattValue);
		TapcApp.getInstance().mainActivity.setWattMode();

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
				mProgramTimeText.setText(String.format(getString(R.string.watt_time_title), mProgramTime));
				if (mAllEntity != null) {
					mAllEntity.setProgramTime(mProgramTime);
				}
			}
		});
		mSeekBar.setMax(mProgramTimeMax - mProgramTimeMin);
		mSeekBar.setProgress(mStartTime - mProgramTimeMin);

		mWattSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				mWattValue = progress + mWattValueMin;
				mWattValueTv.setText(String.format(getString(R.string.watt_value_title), mWattValue));
				if (mAllEntity != null) {
					mAllEntity.setSpeed(mWattValue);
				}
			}
		});
		mWattSeekBar.setMax(mWattValueMax - mWattValueMin);
		mWattSeekBar.setProgress(mStartWattValue - mWattValueMin);
	}

	@OnClick(R.id.watt_constan_start)
	protected void start(View v) {
		if (mAllEntity != null) {
			TapcApp.getInstance().mainActivity.setWattMode();
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

	@OnClick(R.id.watt_value_add)
	protected void addWattValue(View v) {
		mWattSeekBar.setProgress(mWattSeekBar.getProgress() + 1);
	}

	@OnClick(R.id.watt_value_sub)
	protected void subWattValue(View v) {
		mWattSeekBar.setProgress(mWattSeekBar.getProgress() - 1);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
