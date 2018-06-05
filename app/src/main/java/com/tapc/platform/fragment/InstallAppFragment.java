package com.tapc.platform.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.adpater.AppGridViewAdapter;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.entity.PackageInstallObserver;
import com.tapc.platform.utils.SysUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class InstallAppFragment extends Fragment {
	private Context mContext;
	// 内容显示gridview
	@ViewInject(R.id.install_app_grid)
	private GridView mInstallGrid;
	private AppGridViewAdapter mAdapter;
	private List<String> mListFilePath = new ArrayList<String>();
	private List<AppInfoEntity> mListApkInfor = new ArrayList<AppInfoEntity>();
	private ProgressDialog mProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_install_app, container,
				false);
		ViewUtils.inject(this, view);
		initUI();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
			mProgressDialog = null;
		}
	}

	private void initUI() {
		getFiles(Config.USB_FILE_PATH, ".apk", true);
		getFiles(Config.EX_SD_FILE_PATH, "tapc_test", true);
		if (mListFilePath != null && mListFilePath.size() > 0) {
			getAppList();
			mAdapter = new AppGridViewAdapter(mContext, mListApkInfor);
			mInstallGrid.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			mInstallGrid.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(
							Uri.fromFile(new File(mListFilePath.get(position))),
							"application/vnd.android.package-archive");
					mContext.startActivity(intent);
				}
			});
		}
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMax(mListFilePath.size());
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.hide();
		}
	}

	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 255) {
				mProgressDialog.hide();
			} else {
				mProgressDialog.show();
				mProgressDialog.setProgress(msg.what);
			}
		};
	};

	private Runnable installAppRunnable = new Runnable() {
		public void run() {
			PackageInstallObserver packageInstallObserver = new PackageInstallObserver();
			progressHandler.sendEmptyMessage(0);
			for (String filePath : mListFilePath) {
				packageInstallObserver.setStatus(false);
				SysUtils.installApk(mContext, new File(filePath),
						packageInstallObserver);
				while (!packageInstallObserver.getStatus()) {
					SystemClock.sleep(200);
				}
				progressHandler.sendEmptyMessage(mListFilePath
						.indexOf(filePath) + 1);
			}
			SystemClock.sleep(2000);
			progressHandler.sendEmptyMessage(255);
		}
	};

	private void getFiles(String Path, String Extension, boolean IsIterative) // 搜索目录，扩展名，是否进入子文件夹
	{
		File[] files = new File(Path).listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (f.isFile()) {
					if (f.getName().contains(Extension)
							&& !f.getName().equals(Config.UPDATE_FILE_NAME)) {
						mListFilePath.add(f.getPath());
					}
					if (!IsIterative)
						break;
				} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
					getFiles(f.getPath(), Extension, IsIterative);
			}
		}
	}

	private void getAppList() {
		for (String apkPath : mListFilePath) {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(apkPath,
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				ApplicationInfo appInfo = info.applicationInfo;
				AppInfoEntity appEntity = new AppInfoEntity();
				// 得到安装包名称
				appInfo.sourceDir = apkPath;
				appInfo.publicSourceDir = apkPath;
				appEntity.setAppLabel(apkPath.substring(apkPath
						.lastIndexOf("/") + 1));
				try {
					appEntity.setAppIcon(appInfo.loadIcon(pm));
				} catch (OutOfMemoryError e) {
					Log.e("ApkIconLoader", e.toString());
				}
				Log.d("app file", "" + appEntity.getAppLabel());
				mListApkInfor.add(appEntity);
			}
		}
	}

	@OnClick(R.id.a_key_install)
	protected void unistallApp(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(getString(R.string.a_key_install_app))
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new Thread(installAppRunnable).start();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
