package com.tapc.platform.witget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.uart.Commands;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.utils.PreferenceHelper;

public class DeviceSelDialog extends RelativeLayout {
	@ViewInject(R.id.device_sel_tx)
	private TextView mDeviceSelTx;
	private Handler mHandler;

	public static final String ACTION_SHOW_DEVICE = "show_device";
	private Context mContext;
	private int mExitCout;

	public DeviceSelDialog(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.device_sel_dialog, this, true);
		ViewUtils.inject(this);
		mHandler = new Handler();
	}

	@OnClick(R.id.bike_b8900ut)
	protected void b8900ut(View v) {
		setDeviceType(4);
	}

	@OnClick(R.id.bike_b8900et)
	protected void b8900et(View v) {
		setDeviceType(2);
	}

	@OnClick(R.id.bike_b8900rt)
	protected void b8900rt(View v) {
		setDeviceType(3);
	}

	private boolean setDeviceType(int id) {
		byte[] ids = new byte[1];
		ids[0] = (byte) id;
		TapcApp.getInstance().controller.sendCommands(Commands.SET_DEVICE_TYPE, ids);
		SystemClock.sleep(500);
		int getMcuDeviceId = TapcApp.getInstance().controller.getDeviceId();
		if (getMcuDeviceId == id) {
			PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "device", id);
			setDeviceDialogHide(mContext);
			return true;
		} else {
			mDeviceSelTx.setText(R.string.device_sel_failed);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mDeviceSelTx.setText("");
				}
			}, 3000);
			return false;
		}
	}

	public void setDeviceDialogHide(Context context) {
		Intent intent = new Intent();
		intent.setAction(DeviceSelDialog.ACTION_SHOW_DEVICE);
		Bundle bundle = new Bundle();
		bundle.putBoolean("visibility", false);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	@OnClick(R.id.device_sel_exit)
	protected void exit(View v) {
		mExitCout++;
		if (mExitCout >= 3) {
			setDeviceDialogHide(mContext);
		}
	}
}
