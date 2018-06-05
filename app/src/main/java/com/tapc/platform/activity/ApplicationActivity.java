package com.tapc.platform.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.adpater.AppGridViewAdapter;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.utils.AppType;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.SysUtils;

/**
 * 应用
 * 
 * @author sean.guo
 * @date 2015.3.24
 */
public class ApplicationActivity extends BaseActivity {
	// 内容显示gridview
	@ViewInject(R.id.show_app_grid)
	private GridView show_app_grid;
	private AppGridViewAdapter mAdapter;
	private List<AppInfoEntity> mlistAppInfo;
	private int mAppType;

	public static void launch(Context c, int appType) {
		Intent i = new Intent(c, ApplicationActivity.class);
		i.putExtra("appType", appType);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		c.startActivity(i);
	}

	public static void launch(Context c, int appType, String packageName, String className) {
		Intent i = new Intent(c, ApplicationActivity.class);
		i.putExtra("appType", appType);
		i.putExtra("packageName", packageName);
		i.putExtra("className", className);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		c.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mlistAppInfo = TapcApp.getInstance().listAppInfo;
		if (mlistAppInfo == null) {
			mlistAppInfo = new ArrayList<AppInfoEntity>();
			mlistAppInfo = AppUtils.getAllAppInfo(this);
		}
		mAppType = getIntent().getIntExtra("appType", 0);
		switch (mAppType) {
		case AppType.NO_TYPE:
			setContentView(R.layout.activity_app);
			ViewUtils.inject(this);
			init();
			break;
		case AppType.START_APP:
			String packageName = getIntent().getStringExtra("packageName");
			String className = getIntent().getStringExtra("className");
			if (packageName != null && className != null) {
				for (AppInfoEntity app : mlistAppInfo) {
					if (app.getPkgName().equals(packageName)) {
						Intent intent = app.getIntent();
						intent.setAction("android.intent.action.VIEW");
						startActivity(intent);
						Log.d("start packagename", packageName);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void init() {
		getThirdApplication();
	}

	private void getThirdApplication() {
		mAdapter = new AppGridViewAdapter(this, mlistAppInfo);
		show_app_grid.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		show_app_grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = mlistAppInfo.get(position).getIntent();
				if (intent.getPackage().equals(Config.TEST_APP_PACKAGENAME)) {
					TapcApp.getInstance().mainActivity.startTestMachine(intent);
				} else {
					intent.setAction("android.intent.action.VIEW");
					startActivity(intent);
				}
			}
		});
	}

	@OnClick(R.id.app_exit)
	protected void exitAppOnClick(View v) {
		TapcApp.getInstance().clearAppExit(mlistAppInfo);
		Toast.makeText(ApplicationActivity.this, R.string.app_clear_completed, Toast.LENGTH_LONG).show();
	}

	@OnClick(R.id.clear_app_data)
	protected void clearAppOnClick(View v) {
		TapcApp.getInstance().clearAppExit(mlistAppInfo);
		for (AppInfoEntity app : mlistAppInfo) {
			AppUtils.clearAppUserData(this, app.getPkgName());
		}
		SysUtils.RecursionDeleteFile(new File(Config.IN_SD_FILE_PATH));
		Toast.makeText(ApplicationActivity.this, R.string.app_clear_completed, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
