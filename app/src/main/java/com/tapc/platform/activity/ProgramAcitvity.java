package com.tapc.platform.activity;

import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.workout.MessageType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class ProgramAcitvity extends BaseActivity {
	@ViewInject(R.id.program_viewpage)
	private ViewPager mProgramViewpage;

	private ArrayList<View> mListViews;
	int program = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_program);

		mListViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		mListViews.add(mInflater.inflate(R.layout.activity_program1, null));
		mListViews.add(mInflater.inflate(R.layout.activity_program2, null));

		ViewUtils.inject(this);
		ViewUtils.inject(this, mListViews.get(0));
		ViewUtils.inject(this, mListViews.get(1));
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mListViews.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(mListViews.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(mListViews.get(position));
				return mListViews.get(position);
			}
		};

		mProgramViewpage.setAdapter(mPagerAdapter);
		mProgramViewpage.setCurrentItem(0);
		mProgramViewpage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
	}

	@OnClick(R.id.program_nextbutton)
	protected void nextbuttonOnClick(View v) {
		mProgramViewpage.setCurrentItem(1);
	}

	@OnClick(R.id.program_prebutton)
	protected void prebuttonOnClick(View v) {
		mProgramViewpage.setCurrentItem(0);
	}

	@OnClick(R.id.program1)
	private void Program1(View v) {
		program = 1;
		start();
	}

	@OnClick(R.id.program2)
	private void Program2(View v) {
		program = 2;
		start();
	}

	@OnClick(R.id.program3)
	private void Program3(View v) {
		program = 3;
		start();
	}

	@OnClick(R.id.program4)
	private void Program4(View v) {
		program = 4;
		start();
	}

	@OnClick(R.id.program5)
	private void Program5(View v) {
		program = 5;
		start();
	}

	@OnClick(R.id.program6)
	private void Program6(View v) {
		program = 6;
		start();
	}

	@OnClick(R.id.program7)
	private void Program7(View v) {
		program = 7;
		start();
	}

	@OnClick(R.id.program8)
	private void Program8(View v) {
		program = 8;
		start();
	}

	@OnClick(R.id.program9)
	private void Program9(View v) {
		program = 9;
		start();
	}

	@OnClick(R.id.program10)
	private void Program10(View v) {
		program = 10;
		start();
	}

	@OnClick(R.id.program11)
	private void Program11(View v) {
		program = 11;
		start();
	}

	@OnClick(R.id.program12)
	private void Program12(View v) {
		program = 12;
		start();
	}

	private void start() {
		FitnessSetAllEntity allEntity = new FitnessSetAllEntity();
		TapcApp.getInstance().getSportsEngin().openFitness(allEntity);
		allEntity.setProgramEntity(ProgramType.TAPC_PROG, program,
				SystemSettingsHelper.WEIGHT,
				SystemSettingsHelper.DEFAULT_INCLINE,
				SystemSettingsHelper.DEFAULT_SPEED);
		Intent intent = new Intent();
		intent.setClass(ProgramAcitvity.this, SetProgramTimeAcitvity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
