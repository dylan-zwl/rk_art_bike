package com.tapc.platform.receiver;

import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.utils.SysUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetWorkBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		// 如果无网络连接activeInfo为null
		if (activeInfo != null) {
			Config.isConnected = true;
			Toast.makeText(context, context.getString(R.string.net_connect), Toast.LENGTH_LONG).show();
			// TapcApp.getInstance().menuBar.setTimeDisplay(true);
			TapcApp.getInstance().menuBar.setWifiConnect(true);
			TapcApp.getInstance().autoUploadSportsData();
			if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				if (Config.DEVICE_ID == null || Config.DEVICE_ID.isEmpty()) {
					Config.DEVICE_ID = SysUtils.getLocalMacAddress(context);
					if (Config.DEVICE_ID == null) {
						Config.DEVICE_ID = "";
					}
				}
			}
		} else {
			Config.isConnected = false;
			Toast.makeText(context, context.getString(R.string.net_unconnect), Toast.LENGTH_LONG).show();
			// TapcApp.getInstance().menuBar.setTimeDisplay(false);
			TapcApp.getInstance().menuBar.setWifiConnect(false);
		}
	}
}
