package com.tapc.platform.activity;

import java.util.Map;

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
import com.tapc.platform.utils.SysUtils;

public class LoadCtlSetAcitvity extends BaseActivity {
	public static final String SPEED = "speed";
	public static final String INCLINE = "incline";
	@ViewInject(R.id.program_time_seekbar)
	private SeekBar mSeekBar;
	@ViewInject(R.id.watt_value_seekbar)
	private SeekBar mWattSeekBar;

	@ViewInject(R.id.program_time_title)
	private TextView mProgramTimeText;
	@ViewInject(R.id.watt_value_title)
	private TextView mWattValueTv;

	private int mProgramTimeMax = 1024;
	private int mProgramTimeMin = 0;
	private int mStartTime = 0;
	private int mProgramTime;

	private int mWattValueMax = 0;
	private int mWattValueMin = 0;
	private int mStartWattValue = 0;
	private int mWattValue;

	private Map<String, Integer> mLoadMap;

	public static void launch(Context c, String type, double starValue) {
		Intent i = new Intent(c, LoadCtlSetAcitvity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("type", type);
		i.putExtra("starValue", starValue);
		c.startActivity(i);
		((Activity) c).overridePendingTransition(R.anim.in, R.anim.out);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_watt_constan);
		ViewUtils.inject(this);

		mWattValueMax = (int) TapcApp.getInstance().MAX_INCLINE;
		mWattValueMin = (int) TapcApp.getInstance().MIN_INCLINE;
		mStartWattValue = (int) getIntent().getDoubleExtra("starValue", 0);

		mLoadMap = SysUtils.getLoadMap(this);

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
				mProgramTimeText.setText(String.format(getString(R.string.load_pwn_title), mProgramTime));
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
				mWattValueTv.setText(String.format(getString(R.string.load_level_title), mWattValue));
				int value = mLoadMap.get("" + mWattValue).intValue();
				if (value >= mProgramTimeMin && value <= mProgramTimeMax) {
					mSeekBar.setProgress(value);
				}
			}
		});
		mWattSeekBar.setMax(mWattValueMax - mWattValueMin);
		mWattSeekBar.setProgress(mStartWattValue - mWattValueMin);
	}

	@OnClick(R.id.watt_constan_start)
	protected void start(View v) {
		mLoadMap.put(String.valueOf(mWattValue), mProgramTime);
		SysUtils.saveLoadMap(this, mLoadMap);
		TapcApp.getInstance().mainActivity.getInclineCtrl().setCtlValue(mWattValue);
		finish();
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
	protected void onPause() {
		super.onPause();
		finish();
		overridePendingTransition(R.anim.in, R.anim.out);
	}
}
