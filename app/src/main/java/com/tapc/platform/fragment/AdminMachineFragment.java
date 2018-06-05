package com.tapc.platform.fragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminMachineFragment extends Fragment {
	private Context mContext;
	@ViewInject(R.id.admin_default_speed)
	private TextView defaultSpeedTextView;
	// 最小速度
	@ViewInject(R.id.admin_min_speed_ed)
	private EditText minSpeed;
	// 最大速度
	@ViewInject(R.id.admin_max_speed_ed)
	private EditText maxSpeed;
	// 速度增减幅
	@ViewInject(R.id.admin_damping_speed_ed)
	private EditText dampingSpeed;
	// 默认初始速度
	@ViewInject(R.id.admin_default_speed_ed)
	private EditText defaultSpeed;
	// 最小坡度
	@ViewInject(R.id.admin_min_slope_ed)
	private EditText minSlope;
	// 最大坡度
	@ViewInject(R.id.admin_max_slope_ed)
	private EditText maxSlope;
	// 坡度增减幅
	@ViewInject(R.id.admin_damping_slope_ed)
	private EditText dampingSlope;
	// 默认初始坡度
	@ViewInject(R.id.admin_default_slope_ed)
	private EditText defaultSlope;

	private AlertDialog mAlert;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_admin_machine_maintenance, container, false);
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
	}
	private void initUI() {
		minSpeed.setText(String.format("%.1f", SystemSettingsHelper.MIN_SPEED));
		maxSpeed.setText(String.format("%.1f", SystemSettingsHelper.MAX_SPEED));
		dampingSpeed.setText(String.format("%.1f",
				SystemSettingsHelper.STEP_SPEED));
		defaultSpeed.setText(String.format("%.1f",
				SystemSettingsHelper.DEFAULT_SPEED));

		minSlope.setText(String.format("%.1f", SystemSettingsHelper.MIN_INCLINE
				- SystemSettingsHelper.ORIGIN_INCLINE));
		maxSlope.setText(String
				.format("%.1f", SystemSettingsHelper.MAX_INCLINE));
		dampingSlope.setText(String.format("%.1f",
				SystemSettingsHelper.STEP_INCLINE));
		defaultSlope.setText(String.format("%.1f",
				SystemSettingsHelper.DEFAULT_INCLINE));

		// minSpeed.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) < 0) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// maxSpeed.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) < SystemSettingsHelper.MIN_SPEED *
		// 2) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// dampingSpeed.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) == 0
		// || Float.valueOf(arg0.toString()) > SystemSettingsHelper.MAX_SPEED
		// - SystemSettingsHelper.MIN_SPEED) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// defaultSpeed.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) == 0
		// || Float.valueOf(arg0.toString()) < SystemSettingsHelper.MIN_SPEED
		// || Float.valueOf(arg0.toString()) > SystemSettingsHelper.MAX_SPEED) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// minSlope.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// maxSlope.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) < SystemSettingsHelper.MIN_INCLINE)
		// {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// dampingSlope.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) == 0
		// || Float.valueOf(arg0.toString()) > SystemSettingsHelper.MAX_INCLINE
		// - SystemSettingsHelper.MIN_INCLINE) {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
		//
		// defaultSlope.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// // TODO Auto-generated method stub
		// if (arg0.toString().isEmpty()
		// || Float.valueOf(arg0.toString()) < 0
		// || Float.valueOf(arg0.toString()) > SystemSettingsHelper.MAX_INCLINE)
		// {
		// uiMessage(getString(R.string.admin_machine_input_error));
		// }
		// }
		// });
	}

	private boolean setValue() {
		if (!minSpeed.getText().toString().isEmpty()
				&& Float.valueOf(minSpeed.getText().toString()) > 0) {
			SystemSettingsHelper.setFloat(SystemSettingsHelper.KEY_MIN_SPEED,
					Float.valueOf(minSpeed.getText().toString()));
		} else {
			return false;
		}
		if (!maxSpeed.getText().toString().isEmpty()
				&& Float.valueOf(maxSpeed.getText().toString()) >= SystemSettingsHelper.MIN_SPEED * 2) {
			SystemSettingsHelper.setFloat(SystemSettingsHelper.KEY_MAX_SPEED,
					Float.valueOf(maxSpeed.getText().toString()));
		} else {
			return false;
		}
		if (!dampingSpeed.getText().toString().isEmpty()
				&& Float.valueOf(dampingSpeed.getText().toString()) > 0
				&& Float.valueOf(dampingSpeed.getText().toString()) <= SystemSettingsHelper.MAX_SPEED
						- SystemSettingsHelper.MIN_SPEED) {
			SystemSettingsHelper.setFloat(SystemSettingsHelper.KEY_SPEED_STEP,
					Float.valueOf(dampingSpeed.getText().toString()));
		} else {
			return false;
		}
		if (!defaultSpeed.getText().toString().isEmpty()
				&& Float.valueOf(defaultSpeed.getText().toString()) != 0
				&& Float.valueOf(defaultSpeed.getText().toString()) >= SystemSettingsHelper.MIN_SPEED
				&& Float.valueOf(defaultSpeed.getText().toString()) <= SystemSettingsHelper.MAX_SPEED) {
			SystemSettingsHelper.setFloat(
					SystemSettingsHelper.KEY_DEFAULT_SPEED,
					Float.valueOf(defaultSpeed.getText().toString()));
		} else {
			return false;
		}
		if (!minSlope.getText().toString().isEmpty()) {
			if (minSlope.getText().toString().equals("-")) {
				SystemSettingsHelper.setFloat(
						SystemSettingsHelper.KEY_MIN_INCLINE, 0.0f);
				SystemSettingsHelper.setFloat(
						SystemSettingsHelper.KEY_ORIGIN_INCLINE,
						Math.abs(Float.valueOf(minSlope.getText().toString())));
			} else {
				SystemSettingsHelper.setFloat(
						SystemSettingsHelper.KEY_MIN_INCLINE,
						Float.valueOf(minSlope.getText().toString()));
			}
		} else {
			return false;
		}
		if (!maxSlope.getText().toString().isEmpty()
				&& Float.valueOf(maxSlope.getText().toString()) >= SystemSettingsHelper.MIN_INCLINE) {
			SystemSettingsHelper.setFloat(SystemSettingsHelper.KEY_MAX_INCLINE,
					Float.valueOf(maxSlope.getText().toString()));
		} else {
			return false;
		}
		if (!dampingSlope.getText().toString().isEmpty()
				&& Float.valueOf(dampingSlope.getText().toString()) > 0
				&& Float.valueOf(dampingSlope.getText().toString()) <= SystemSettingsHelper.MAX_INCLINE
						- SystemSettingsHelper.MIN_INCLINE) {
			SystemSettingsHelper.setFloat(
					SystemSettingsHelper.KEY_INCLINE_STEP,
					Float.valueOf(dampingSlope.getText().toString()));
		} else {
			return false;
		}
		if (!defaultSlope.getText().toString().isEmpty()
				&& Float.valueOf(defaultSlope.getText().toString()) >= 0
				&& Float.valueOf(defaultSlope.getText().toString()) <= SystemSettingsHelper.MAX_INCLINE) {
			SystemSettingsHelper.setFloat(
					SystemSettingsHelper.KEY_DEFAULT_INCLINE,
					Float.valueOf(defaultSlope.getText().toString()));
		} else {
			return false;
		}
		TapcApp.getInstance().setMachineParameter();
		return true;
	}

	@OnClick(R.id.machine_setting_Save)
	protected void saveValue(View v) {
		if (setValue()) {
			uiMessage(getString(R.string.admin_machine_set_success));
		} else {
			uiMessage(getString(R.string.admin_machine_set_fault));
		}
	}

	private void uiMessage(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(text);
		if (mAlert != null && mAlert.isShowing()) {
			mAlert.dismiss();
		}
		mAlert = builder.create();
		mAlert.show();
	}
}
