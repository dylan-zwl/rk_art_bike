package com.tapc.platform.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.tapc.platform.Config;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.entity.AppSwitchEntity;
import com.tapc.platform.entity.PackageDataObserver;

public class AppUtils {
	public static void setAppType(Context context, String pkgName, int appType) {
		ArrayList<AppSwitchEntity> appSwitch = getListApp(PreferenceHelper
				.readString(context, Config.SETTING_CONFIG, "set_app"));
		for (int i = 0; i < appSwitch.size(); i++) {
			if (appSwitch.get(i).getApkName().equals(pkgName)) {
				appSwitch.get(i).setType(appType);
			}
		}
		PreferenceHelper.write(context, Config.SETTING_CONFIG, "set_app",
				AppUtils.toSwitchGsonApp(appSwitch));
	}

	public static ArrayList<AppSwitchEntity> queryFirstAppInfo(Context context) {
		ArrayList<AppSwitchEntity> mlistAppInfo = getListApp(PreferenceHelper
				.readString(context, Config.SETTING_CONFIG, "set_app"));
		ArrayList<AppSwitchEntity> appConfig = null;
		if (mlistAppInfo == null) {
			mlistAppInfo = new ArrayList<AppSwitchEntity>();
			try {
				InputStream inputStream = context.getResources().getAssets()
						.open("app_config.xml");
				appConfig = getAppSortInfor(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PackageManager pm = context.getPackageManager();// 获取PackageManager对象
		// 通过查询，获得所有ResolveInfo对象.
		List<ApplicationInfo> appInfos = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		if (appConfig != null) {
			for (int i = 0; i < appConfig.size(); i++) {
				for (ApplicationInfo applicationInfo : appInfos) {
					if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
						String labelName = (String) applicationInfo
								.loadLabel(pm);
						if (labelName.contains(appConfig.get(i).getApkName())) {
							AppSwitchEntity appInfo = new AppSwitchEntity();
							appInfo.setApkName(applicationInfo.packageName);
							appInfo.setType(appConfig.get(i).type);
							Log.d("labelName: " + labelName + " packageName:  "
									+ appInfo.apkName, "" + appInfo.type);
							mlistAppInfo.add(appInfo);// 添加至列表中
							break;
						}
					}
				}
			}
		}

		for (ApplicationInfo applicationInfo : appInfos) {
			String pakageName = applicationInfo.packageName;// 获得应用程序的包�?
			AppSwitchEntity appInfo = new AppSwitchEntity();
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				if (!pakageName.equals("com.tapc.platform")) {
					boolean isHaveApk = false;
					for (int i = 0; i < mlistAppInfo.size(); i++) {
						if (mlistAppInfo.get(i).getApkName().equals(pakageName)) {
							isHaveApk = true;
							break;
						}
					}
					if (isHaveApk == false) {
						appInfo.setApkName(pakageName);
						appInfo.setType(AppType.NO_TYPE);
						mlistAppInfo.add(appInfo);// 添加至列表中
					}
				}
			}
		}
		return mlistAppInfo;
	}

	public static ArrayList<AppInfoEntity> getAllAppInfo(Context context) {
		ArrayList<AppInfoEntity> mlistAppInfo = new ArrayList<AppInfoEntity>();
		PackageManager pm = context.getPackageManager();// 获取PackageManager对象
		List<ApplicationInfo> appInfos = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (ApplicationInfo applicationInfo : appInfos) {
			if (applicationInfo != null) {
				String pakageName = applicationInfo.packageName;// 获得应用程序的包�?
				String appLabel = (String) applicationInfo.loadLabel(pm);// 获得应用程序的Label
				Drawable icon = applicationInfo.loadIcon(pm);// 获得应用程序图标
				// 为应用程序的启动Activity 准备Intent
				Intent launchIntent = new Intent();
				if (pakageName != null) {
					launchIntent = context.getPackageManager()
							.getLaunchIntentForPackage(pakageName);
				}
				// 创建�?��AppInfo对象，并赋�?
				AppInfoEntity appInfo = new AppInfoEntity();
				if (!pakageName.equals("com.tapc.platform")
						&& !pakageName.equals("com.tapc.test")
						&& (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					appInfo.setAppLabel(appLabel);
					appInfo.setPkgName(pakageName);
					appInfo.setAppIcon(icon);
					appInfo.setIntent(launchIntent);
					mlistAppInfo.add(appInfo);// 添加至列表中
				}
			}
		}
		return mlistAppInfo;
	}

	// 分类显示
	public static ArrayList<AppInfoEntity> queryAppInfo(Context context,
			int appType) {
		ArrayList<AppSwitchEntity> appSwitch = getListApp(PreferenceHelper
				.readString(context, Config.SETTING_CONFIG, "set_app"));
		ArrayList<AppInfoEntity> mlistAppInfo = new ArrayList<AppInfoEntity>();
		PackageManager pm = context.getPackageManager();// 获取PackageManager对象
		if (mlistAppInfo != null) {
			mlistAppInfo.clear();
			for (int appIndex = 0; appIndex < appSwitch.size(); appIndex++) {
				try {
					if (appSwitch.get(appIndex).getType() == appType) {
						ApplicationInfo applicationInfo = pm
								.getApplicationInfo(appSwitch.get(appIndex)
										.getApkName(), 0);
						if (applicationInfo != null) {
							String pakageName = applicationInfo.packageName;// 获得应用程序的包�?
							String appLabel = (String) applicationInfo
									.loadLabel(pm);// 获得应用程序的Label
							Drawable icon = applicationInfo.loadIcon(pm);// 获得应用程序图标
							// 为应用程序的启动Activity 准备Intent
							Intent launchIntent = new Intent();
							if (pakageName != null) {
								launchIntent = context.getPackageManager()
										.getLaunchIntentForPackage(pakageName);
							}
							// 创建�?��AppInfo对象，并赋�?
							AppInfoEntity appInfo = new AppInfoEntity();
							if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
								appInfo.setAppLabel(appLabel);
								appInfo.setPkgName(pakageName);
								appInfo.setAppIcon(icon);
								appInfo.setIntent(launchIntent);
								mlistAppInfo.add(appInfo);// 添加至列表中
							}
						}
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return mlistAppInfo;
	}

	public static String toGsonApp(List<AppInfoEntity> infoEntities) {
		Gson gson = new Gson();
		return gson.toJson(infoEntities);
	}

	public static String toSwitchGsonApp(ArrayList<AppSwitchEntity> infoEntities) {
		Gson gson = new Gson();
		return gson.toJson(infoEntities);
	}

	public static ArrayList<AppSwitchEntity> getListApp(String str) {
		ArrayList<AppSwitchEntity> mNewAppInfo = null;
		if (str != null) {
			mNewAppInfo = new ArrayList<AppSwitchEntity>();
			Gson gson = new Gson();
			mNewAppInfo = gson.fromJson(str,
					new TypeToken<List<AppSwitchEntity>>() {
					}.getType());
		}
		return mNewAppInfo;
	}

	public static List<AppInfoEntity> getNewApp(List<AppInfoEntity> oldAppList,
			List<AppInfoEntity> newAppList) {
		List<AppInfoEntity> mNewAppInfo = new ArrayList<AppInfoEntity>();
		if (oldAppList.size() > 0 && oldAppList.size() > 0) {
			for (AppInfoEntity newAppListEntity : newAppList) {
				for (AppInfoEntity oldAppListEntity : oldAppList) {
					if (newAppListEntity.getAppLabel().equals(
							oldAppListEntity.getAppLabel())) {
						if (!mNewAppInfo.contains(oldAppListEntity)) {
							mNewAppInfo.add(oldAppListEntity);
						}
					} else {
						mNewAppInfo.add(newAppListEntity);
					}
				}
			}
		}
		return mNewAppInfo;
	}

	public static ArrayList<AppSwitchEntity> getAppSortInfor(InputStream xml)
			throws Exception {
		ArrayList<AppSwitchEntity> mAppCongfig = new ArrayList<AppSwitchEntity>();
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		AppSwitchEntity appInfo = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			String nodeName = pullParser.getName();
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("app".equals(nodeName)) {
					appInfo = new AppSwitchEntity();
				} else if ("name".equals(nodeName)) {
					appInfo.apkName = pullParser.nextText();
				} else if ("type".equals(nodeName)) {
					appInfo.type = Integer.valueOf(pullParser.nextText())
							.intValue();
				}
				break;
			case XmlPullParser.END_TAG:
				mAppCongfig.add(appInfo);
				break;
			}
			event = pullParser.next();
		}
		return mAppCongfig;
	}

	public static void clearAppUserData(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			List<ApplicationInfo> appInfos = pm
					.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			for (ApplicationInfo applicationInfo : appInfos) {
				PackageDataObserver dataObserver = new PackageDataObserver();
				String pakageName = applicationInfo.packageName;
				if (!pakageName.equals("com.tapc.platform")
						&& (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					pm.clearApplicationUserData(pakageName, dataObserver);
				}
			}
		} catch (Exception e) {
			LogUtils.e("clearAppUserData failed");
		}
	}

	public static void clearAppUserData(Context context, String pakageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageDataObserver dataObserver = new PackageDataObserver();
			pm.clearApplicationUserData(pakageName, dataObserver);
		} catch (Exception e) {
			LogUtils.e("clearAppUserData failed");
		}
	}
}
