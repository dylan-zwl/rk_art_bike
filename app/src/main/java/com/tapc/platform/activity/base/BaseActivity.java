package com.tapc.platform.activity.base;

import java.util.Locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.lidroid.xutils.ViewUtils;
import com.tapc.platform.TapcApp;

public class BaseActivity extends FragmentActivity {

	protected FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		TapcApp.getInstance().noNoActionCount = 0;
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN :
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		mFragmentManager = getSupportFragmentManager();
	}
	public void switchLanguage(String language) {
		Resources resource = TapcApp.getInstance().getResources();
		Configuration config = resource.getConfiguration();
		DisplayMetrics dm = resource.getDisplayMetrics();
		if (language.equals("2")) {
			config.locale = Locale.ENGLISH;
		} else if (language.equals("1")) {
			config.locale = Locale.TAIWAN;
		} else if (language.equals("0")) {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}
		resource.updateConfiguration(config, dm);
	}

	public void replaceFragment(int id, Fragment fragment) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.replace(id, fragment);
		ft.commit();
	}

	public void replaceFragment(Fragment fragment) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.replace(android.R.id.tabcontent, fragment);

		ft.commit();
	}

	public void replaceFragmentBack(Fragment fragment) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.setCustomAnimations(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		ft.replace(android.R.id.tabcontent, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}

	public void hideFragment(Fragment fragment) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.hide(fragment);
		ft.commit();
	}

	public void showFragment(Fragment fragment) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.show(fragment);
		ft.commit();
	}

	public FragmentManager getFM() {
		return mFragmentManager;
	}
}
