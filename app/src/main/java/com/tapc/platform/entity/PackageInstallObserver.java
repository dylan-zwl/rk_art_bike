package com.tapc.platform.entity;

import android.content.pm.IPackageInstallObserver;
import com.lidroid.xutils.util.LogUtils;

public class PackageInstallObserver extends IPackageInstallObserver.Stub {
	private boolean isFinish = false;

	@Override
	public void packageInstalled(String packageName, int returnCode) {
		if (returnCode == 1) {
			LogUtils.d("...安装成功..." + packageName);
		} else {
			LogUtils.d("...安装失败..." + packageName);
		}
		isFinish = true;
	}

	public void setStatus(boolean flag) {
		isFinish = flag;
	}

	public boolean getStatus() {
		return isFinish;
	}
};
