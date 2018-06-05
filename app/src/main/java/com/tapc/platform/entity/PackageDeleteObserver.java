package com.tapc.platform.entity;

import com.lidroid.xutils.util.LogUtils;

import android.content.pm.IPackageDeleteObserver;
import android.os.IBinder;
import android.os.RemoteException;

public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
	private boolean isFinish = false;

	@Override
	public void packageDeleted(String packageName, int returnCode) {
		if (returnCode == 1) {
			LogUtils.d("...卸载成功..." + packageName);
		} else {
			LogUtils.d("...卸载失败..." + packageName);
		}
		isFinish = true;
	}

	public void setStatus(boolean flag) {
		isFinish = flag;
	}

	public boolean getStatus() {
		return isFinish;
	}

	@Override
	public IBinder asBinder() {
		return super.asBinder();
	}
}
