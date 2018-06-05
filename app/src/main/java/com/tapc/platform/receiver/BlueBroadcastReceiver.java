package com.tapc.platform.receiver;

import com.tapc.platform.TapcApp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BlueBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
			TapcApp.getInstance().menuBar.setBlueConnect(true);
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
			TapcApp.getInstance().menuBar.setBlueConnect(false);
		}
	}
}
