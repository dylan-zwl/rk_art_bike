package com.tapc.platform.entity;

import android.content.pm.IPackageDataObserver;

import com.lidroid.xutils.util.LogUtils;

public class PackageDataObserver extends IPackageDataObserver.Stub {
	public void onRemoveCompleted(String packageName, boolean succeeded) {
		if (succeeded) {
			LogUtils.d("----清除成功----" + packageName);
		} else {
			LogUtils.d("----清除失败----" + packageName);
		}
	}
}