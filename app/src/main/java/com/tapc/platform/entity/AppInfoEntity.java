package com.tapc.platform.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * @author Jason.liu
 * @Email 1946711081@qq.com TODO
 */
public class AppInfoEntity {
	private String appLabel;// 应用程序标签
	private Drawable appIcon;// 应用程序图像
	private Intent intent;// 启动应用程序的Intent
							// ，一般是Action为Main和Category为Lancher的Activity
	private String pkgName;// 应用程序�?��应的包名
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public AppInfoEntity() {
	}

	public String getAppLabel() {
		return appLabel;
	}
	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
}
