package com.tapc.platform;

import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

import com.tapc.android.controller.MachineController;

public class AppExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultUEH;

	public AppExceptionHandler() {

		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		/**
		 * 程序异常�?��处理，停止下�?
		 */
		Log.d("AppExceptionHandler", "app error exit");
		MachineController mController = MachineController.getInstance();
		mController.stopMachine(0);
		mController.stop();
		TapcApp.getInstance().mainActivity.reload();
		System.exit(1);
		// defaultUEH.uncaughtException(thread, ex);
	}

}
