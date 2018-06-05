package com.tapc.platform.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

import com.tapc.platform.Config;

public class UnderCtl {
	public static void showInitValue(Context context, EditText text,
			String key, int defValue) {
		int value = PreferenceHelper.readInt(context,
				Config.MACHINE_PARAM_CONFIG, key, defValue);
		text.setText("" + value);
	}

	public static void setValue(Context context, EditText text, String key,
			int defValue) {
		String valueStr = text.getText().toString();
		int value = 0;
		if (!valueStr.isEmpty()) {
			value = Integer.parseInt(valueStr);
		} else {
			value = defValue;
		}
		PreferenceHelper
				.write(context, Config.MACHINE_PARAM_CONFIG, key, value);
	}

	public static void setValue(Context context, EditText text, String key) {
		String valueStr = text.getText().toString();
		int value = Integer.parseInt(valueStr);
		PreferenceHelper
				.write(context, Config.MACHINE_PARAM_CONFIG, key, value);
	}

	public static boolean checkTextIsEmpty(EditText text) {
		String valueStr = text.getText().toString();
		if (valueStr.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static void uiMessage(Context context, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text);
		AlertDialog alert = builder.create();
		alert.show();
	}
}
