package com.tapc.platform.rfid;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;

import com.tapc.platform.usb.UsbCtl;

import java.util.Timer;
import java.util.TimerTask;

public class RfidModel {
	private UsbCtl mUsbCtl;
	private RfidCtl mRfidCtl;

	private boolean isRfidReady = false;
	private boolean hasSetPcdTx = false;

	public RfidModel() {
	}

	// 连接usb转串口
	public boolean connectUsb(Activity activity) {
		isRfidReady = false;
		if (mUsbCtl == null) {
			getUsbPermission(activity);
			mUsbCtl = new UsbCtl(activity);
		}
		mUsbCtl.init();
		if (mUsbCtl.isUsbConnect()) {
			return true;
		}
		return false;
	}

	// 连接RFID模块
	public interface DetectListener {
		void Uid(byte[] uid);

		void connectStatus(boolean isConnected);
	}

	public void connectRfid(final DetectListener detectListener) {
		if (mRfidCtl != null) {
			mRfidCtl = null;
		}
		mRfidCtl = new RfidCtl(mUsbCtl);
		// mRfidCtl.init();
		mRfidCtl.setRfidListener(new RfidListener() {

			@Override
			public void detectCard(byte[] uid) {
				if (detectListener != null && isRfidReady) {
					detectListener.Uid(uid);
				}
			}
		});
		if (hasSetPcdTx) {
			checkRfidConnect(false, detectListener);
		} else {
			checkRfidConnect(true, detectListener);
		}
	}

	private void checkRfidConnect(boolean isCheckConnect, final DetectListener detectListener) {
		if (isCheckConnect) {
			mRfidCtl.setConnectDevice(false);
			mRfidCtl.getDvcInfo();
		} else {
			mRfidCtl.setConnectDevice(true);
		}
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				boolean isConnected = isRfidConnect();
				if (detectListener != null) {
					detectListener.connectStatus(isConnected);
					if (isConnected) {
						startAutoDetect();
						isRfidReady = true;
					}
				}
			}
		}, 2000);
	}

	private boolean isRfidConnect() {
		if (mRfidCtl != null && mRfidCtl.isConnectDevice()) {
			return true;
		} else {
			return false;
		}
	}

	private void startAutoDetect() {
		// 启动天线载波
		if (!hasSetPcdTx) {
			mRfidCtl.PCDSetTX((byte) 0x02);
			SystemClock.sleep(50);
			hasSetPcdTx = true;
		}
		if (mRfidCtl != null) {
			mRfidCtl.piceAutoDetect((byte) 0x0f, (byte) 0x02, (byte) 0x26, (byte) 'F', (byte) 0x60, null, 10);
		}
	}

	// 获取usb权限
	private static final String ACTION_USB_PERMISSION = "com.Android.example.USB_PERMISSION";
	private UsbManager mManager;

	private void getUsbPermission(Context context) {
		try {
			mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
			PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION),
					0);
			Intent intent = new Intent();
			intent.setAction(ACTION_USB_PERMISSION);
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACTION_USB_PERMISSION);
			filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			context.registerReceiver(mReceiver, filter);
			// Request permission
			for (UsbDevice device : mManager.getDeviceList().values()) {
				intent.putExtra(UsbManager.EXTRA_DEVICE, device);
				intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);

				final PackageManager pm = context.getPackageManager();
				try {
					ApplicationInfo aInfo = pm.getApplicationInfo(context.getPackageName(), 0);
					try {
						IBinder b = ServiceManager.getService(Context.USB_SERVICE);
						IUsbManager service = IUsbManager.Stub.asInterface(b);
						service.grantDevicePermission(device, aInfo.uid);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				context.sendBroadcast(intent);
				mManager.requestPermission(device, mPermissionIntent);
				Log.d("usb ctl", "UsbManager.EXTRA_DEVICE" + mManager.openDevice(device));
			}
		} catch (Exception e) {
			Log.d("usb ctl", "can not get default permission");
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					Log.d("usb ctl", "UsbManager.EXTRA_DEVICE" + intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
					Log.d("usb ctl", "是否有权限了？   " + mManager.hasPermission(device));
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							Log.d("usb ctl", "Opening reader: " + device.getDeviceName() + "...");
						}
					} else {
						if (device != null) {
							Log.d("usb ctl",
									"Permission no EXTRA_PERMISSION_GRANTED for device " + device.getDeviceName());
						}

					}
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				synchronized (this) {

				}
			}
		}
	};
}
