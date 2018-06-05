package com.tapc.platform.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.workout.MessageType;

public class GoalActivity extends BaseActivity {
	@ViewInject(R.id.goal_screen1)
	LinearLayout mLinearLayoutScreen1;
	@ViewInject(R.id.goal_screen2)
	LinearLayout mLinearLayoutScreen2;
	@ViewInject(R.id.page_1)
	RadioButton mRadioButtonPage1;
	@ViewInject(R.id.page_2)
	RadioButton mRadioButtonPage2;
	@ViewInject(R.id.goal_value)
	TextView mTextView;
	@ViewInject(R.id.goal_seekbar)
	SeekBar mSeekBar;
	@ViewInject(R.id.goal_edit_title)
	TextView mGoalEditTitle;
	@ViewInject(R.id.goal_title)
	TextView mGoalTitle;
	@ViewInject(R.id.goal_edit)
	EditText mGoalEdit;
	@ViewInject(R.id.goal_edit_age)
	EditText mGoalEditAge;
	@ViewInject(R.id.goal_edit_weight)
	EditText mGoalEditWeight;

	private int value = 0;
	private int max = 0;
	private int mPosition;
	private FitnessSetAllEntity allEntity;
	private buttonStatusReceiver button;

	public static void launch(Context c, int position) {
		Intent i = new Intent(c, GoalActivity.class);
		i.putExtra("position", position);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_goal);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick(R.id.goal_sub)
	private void Sub(View v) {
		mSeekBar.setProgress(mSeekBar.getProgress() - 1);
	}

	@OnClick(R.id.goal_add)
	private void Add(View v) {
		mSeekBar.setProgress(mSeekBar.getProgress() + 1);
	}

	@OnClick(R.id.goal_next)
	private void Next(View v) {
		mLinearLayoutScreen1.setVisibility(View.GONE);
		mLinearLayoutScreen2.setVisibility(View.VISIBLE);
		mRadioButtonPage1.setChecked(false);
		mRadioButtonPage2.setChecked(true);
	}

	@OnClick(R.id.goal_pre)
	private void Pre(View v) {
		setSeekBarProgress();
		mLinearLayoutScreen1.setVisibility(View.VISIBLE);
		mLinearLayoutScreen2.setVisibility(View.GONE);
		mRadioButtonPage1.setChecked(true);
		mRadioButtonPage2.setChecked(false);
	}

	@OnClick(R.id.goal_start)
	private void OnStart(View v) {
		switch (mPosition) {
		case 1:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 100) {
				Toast.makeText(this, "The value you entered is out of range (1~100km)", Toast.LENGTH_SHORT).show();
				mSeekBar.setProgress(max - 1);
				return;
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(progress - 1);
			}
			break;
		case 2:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 120) {
				Toast.makeText(this, "The value you entered is out of range (1~120min)", Toast.LENGTH_SHORT).show();
				mSeekBar.setProgress(max - 1);
				return;
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(progress - 1);
			}
			break;
		case 3:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 1000) {
				Toast.makeText(this, "The value you entered is out of range (1~1000kcal)", Toast.LENGTH_SHORT).show();
				mSeekBar.setProgress(max - 1);
				return;
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(progress - 1);
			}
			break;
		}
		if (mGoalEditAge.getText().toString().isEmpty() || Integer.valueOf(mGoalEditAge.getText().toString()) < 10
				|| Integer.valueOf(mGoalEditAge.getText().toString()) > 99) {
			Toast.makeText(this, "The value you entered is out of range (10~99year)", Toast.LENGTH_SHORT).show();
			mGoalEditAge.setText("20");
			return;
		}
		int age = Integer.valueOf(mGoalEditAge.getText().toString());

		if (mGoalEditWeight.getText().toString().isEmpty()
				|| Integer.valueOf(mGoalEditWeight.getText().toString()) < 34
				|| Integer.valueOf(mGoalEditWeight.getText().toString()) > 181) {
			Toast.makeText(this, "The value you entered is out of range (34~181kg)", Toast.LENGTH_SHORT).show();
			mGoalEditWeight.setText("68");
			return;
		}
		int weight = Integer.valueOf(mGoalEditWeight.getText().toString());

		if (allEntity != null) {
			allEntity.setAge(age);
			allEntity.setWeight(weight);
		}

		TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	@SuppressLint("NewApi")
	void Init() {
		allEntity = new FitnessSetAllEntity();
		TapcApp.getInstance().getSportsEngin().openFitness(allEntity);
		mPosition = getIntent().getExtras().getInt("position");
		mRadioButtonPage1.setEnabled(false);
		mRadioButtonPage2.setEnabled(false);

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@SuppressLint("NewApi")
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				// TODO Auto-generated method stub
				switch (mPosition) {
				case 1:
					mTextView.setText(String.format("%d km", progress + 1));
					allEntity.setDistanceEntity(ProgramType.DISTANCE, (double) (progress + 1),
							SystemSettingsHelper.WEIGHT, SystemSettingsHelper.DEFAULT_INCLINE,
							SystemSettingsHelper.DEFAULT_SPEED);
					break;
				case 2:
					mTextView.setText(String.format("%d min", progress + 1));
					allEntity.setMinutesEntity(ProgramType.TIME, progress + 1, SystemSettingsHelper.WEIGHT,
							SystemSettingsHelper.DEFAULT_INCLINE, SystemSettingsHelper.DEFAULT_SPEED);
					break;
				case 3:
					mTextView.setText(String.format("%d kcal", progress + 1));
					allEntity.setCalorieEntity(ProgramType.CALORIE, (double) (progress + 1),
							SystemSettingsHelper.WEIGHT, SystemSettingsHelper.DEFAULT_INCLINE,
							SystemSettingsHelper.DEFAULT_SPEED);
					break;
				}
				int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				mTextView.measure(width, height);
				width = mTextView.getMeasuredWidth();
				height = mTextView.getMeasuredHeight();
				Rect bounds = mSeekBar.getProgressDrawable().getBounds();
				int left = mSeekBar.getPaddingLeft() + (bounds.width() - mSeekBar.getThumb().getBounds().width() / 2)
						* mSeekBar.getProgress() / mSeekBar.getMax();
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
				params.setMargins(left, 0, 0, 0);
				mTextView.setLayoutParams(params);
				mGoalEdit.setText(Integer.valueOf(progress + 1).toString());
			}
		});

		switch (mPosition) {
		case 1:
			value = 3;
			max = 100;
			mGoalTitle.setText(R.string.goal_distance_title);
			mGoalEditTitle.setHint(R.string.goal_distance_hint);
			mSeekBar.setMax(max - 1);
			mSeekBar.setProgress(value - 1);
			mTextView.setText(String.format("%d km", value));
			break;
		case 2:
			value = 20;
			max = 120;
			mGoalTitle.setText(R.string.goal_time_title);
			mGoalEditTitle.setHint(R.string.goal_time_hint);
			mSeekBar.setMax(max - 1);
			mSeekBar.setProgress(value - 1);
			mTextView.setText(String.format("%d min", value));
			break;
		case 3:
			value = 100;
			max = 1000;
			mGoalTitle.setText(R.string.goal_calorie_title);
			mGoalEditTitle.setHint(R.string.goal_calorie_hint);
			mSeekBar.setMax(max - 1);
			mSeekBar.setProgress(value - 1);
			mTextView.setText(String.format("%d kcal", value));
			break;
		}

		mGoalEdit.setText(String.format("%d", value));
		int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		mTextView.measure(width, height);
		width = mTextView.getMeasuredWidth();
		height = mTextView.getMeasuredHeight();
		Rect bounds = mSeekBar.getProgressDrawable().getBounds();
		int left = mSeekBar.getPaddingLeft() + (bounds.width() - mSeekBar.getThumb().getBounds().width() / 2)
				* mSeekBar.getProgress() / mSeekBar.getMax();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		params.setMargins(left, 0, 0, 0);
		LogUtils.d("params " + mSeekBar.getPaddingLeft());
		mTextView.setLayoutParams(params);

		button = new buttonStatusReceiver();
		IntentFilter buttonFilter = new IntentFilter("Physical buttons");
		registerReceiver(button, buttonFilter);
	}

	public class buttonStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			OnStart(null);
		}
	}

	private void setSeekBarProgress() {
		switch (mPosition) {
		case 1:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 100) {
				Toast.makeText(GoalActivity.this, "The value you entered is out of range (1~100km)", Toast.LENGTH_SHORT)
						.show();
				mSeekBar.setProgress(max - 1);
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(0);
				mSeekBar.setProgress(progress - 1);
			}
			break;
		case 2:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 120) {
				Toast.makeText(GoalActivity.this, "The value you entered is out of range (1~120min)",
						Toast.LENGTH_SHORT).show();
				mSeekBar.setProgress(max - 1);
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(0);
				mSeekBar.setProgress(progress - 1);
			}
			break;
		case 3:
			if (mGoalEdit.getText().toString().isEmpty() || Integer.valueOf(mGoalEdit.getText().toString()) < 1
					|| Integer.valueOf(mGoalEdit.getText().toString()) > 1000) {
				Toast.makeText(GoalActivity.this, "The value you entered is out of range (1~1000kcal)",
						Toast.LENGTH_SHORT).show();
				mSeekBar.setProgress(max - 1);
			} else {
				int progress = Integer.valueOf(mGoalEdit.getText().toString());
				mSeekBar.setProgress(0);
				mSeekBar.setProgress(progress - 1);
			}
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(button);
		finish();
	}
}
