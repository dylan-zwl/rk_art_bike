package com.tapc.platform.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.fragment.AdminMachineFragment;
import com.tapc.platform.fragment.AdminSystemFragment;
import com.tapc.platform.fragment.AdminUnderCtlFragment;
import com.tapc.platform.fragment.InstallAppFragment;
import com.tapc.platform.fragment.UninstallAppFragment;

public class SettingActivity extends BaseActivity {
	@ViewInject(R.id.setting_linearlayout)
	private LinearLayout mSettingLayout;
	private EditText mPasswordEdt;

	private boolean isNeedPassword = false;
	private String mPassword = "654321";
	private Context mContext;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);
		mContext = this;
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, AdminSystemFragment.class.getName()));
		if (isNeedPassword) {
			mSettingLayout.setVisibility(View.INVISIBLE);
			mPasswordEdt = new EditText(mContext);
			mPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			inputPassward();
		} else {
			mSettingLayout.setVisibility(View.VISIBLE);
		}
	}

	private void inputPassward() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Password").setView(mPasswordEdt).setCancelable(false)
				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@SuppressLint("ShowToast")
					public void onClick(DialogInterface dialog, int id) {
						if (mPasswordEdt.getText().toString().equals(mPassword)) {
							mSettingLayout.setVisibility(View.VISIBLE);
							dialog.cancel();
						} else {
							dialog.cancel();
							finish();
						}
					}
				}).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@OnClick(R.id.app_settings)
	protected void SystemSetting(View v) {
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, AdminSystemFragment.class.getName()));
	}

	@OnClick(R.id.under_ctl_settings)
	protected void Uder_ctl_Setting(View v) {
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, AdminUnderCtlFragment.class.getName()));
	}

	@OnClick(R.id.machine_settings)
	protected void MachineSetting(View v) {
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, AdminMachineFragment.class.getName()));
	}

	@OnClick(R.id.systems_settings)
	protected void SystemsSetting(View v) {
		this.startActivity(new Intent(Settings.ACTION_SETTINGS));
	}

	@OnClick(R.id.install_app)
	protected void installApp(View v) {
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, InstallAppFragment.class.getName()));
	}

	@OnClick(R.id.uninstall_app)
	protected void uninstallApp(View v) {
		replaceFragment(R.id.setting_frame, Fragment.instantiate(this, UninstallAppFragment.class.getName()));
	}

	@Override
	protected void onStop() {
		super.onStop();
		// finish();
	}
}
