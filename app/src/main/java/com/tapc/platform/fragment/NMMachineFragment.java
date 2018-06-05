package com.tapc.platform.fragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.controller.MachieStatusController;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.helper.SpeedRatioParam;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.utils.UnderCtl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NMMachineFragment extends Fragment {
	private Context mContext;
	// nm machine
	@ViewInject(R.id.ctl_nm_motor_belt_diameter)
	private EditText ctl_nm_motor_belt_diameter;

	@ViewInject(R.id.ctl_nm_roller_diameter)
	private EditText ctl_nm_roller_diameter;

	@ViewInject(R.id.ctl_nm_drum_pulley_diameter)
	private EditText ctl_nm_drum_pulley_diameter;

	private SpeedRatioParam mParam;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ctl_nm_set, container, false);
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
		// ctl_nm_motor_belt_diameter.addTextChangedListener(mTextWatcher);
		ctl_nm_roller_diameter.addTextChangedListener(mTextWatcher);
		// ctl_nm_drum_pulley_diameter.addTextChangedListener(mTextWatcher);
		mParam = new SpeedRatioParam();
		TapcApp.getInstance().controller.getMachinePram(mGetParamHandler);
	}

	private Handler mGetParamHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SpeedRatioParam.RECV_DATA_SUCCESS:
				mParam = TapcApp.getInstance().controller.getMachinePramData();
				// showInitValue(ctl_nm_motor_belt_diameter,
				// mParam.getMotorBeltDiameter());
				showInitValue(ctl_nm_roller_diameter, mParam.getRollerDiameter());
				// showInitValue(ctl_nm_roller_diameter,
				// mParam.getDrumPulleyDiameter());
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void showInitValue(EditText text, int value) {
		text.setText("" + value);
	}

	private int getEditTextValue(EditText text) {
		String valueStr = text.getText().toString();
		int value = Integer.parseInt(valueStr);
		return value;
	}

	@OnClick(R.id.underctl_nm_save)
	protected void saveValue(View v) {
		// if (UnderCtl.checkTextIsEmpty(ctl_nm_motor_belt_diameter) ||
		// UnderCtl.checkTextIsEmpty(ctl_nm_roller_diameter)
		// || UnderCtl.checkTextIsEmpty(ctl_nm_drum_pulley_diameter)) {
		// UnderCtl.uiMessage(mContext,
		// getString(R.string.admin_machine_set_fault));
		// return;
		// }
		if (UnderCtl.checkTextIsEmpty(ctl_nm_roller_diameter)) {
			UnderCtl.uiMessage(mContext, getString(R.string.admin_machine_set_fault));
			return;
		}

		// mParam.setMotorBeltDiameter(getEditTextValue(ctl_nm_motor_belt_diameter));
		// mParam.setRollerDiameter(getEditTextValue(ctl_nm_roller_diameter));
		mParam.setMotorBeltDiameter(0);
		mParam.setRollerDiameter(getEditTextValue(ctl_nm_roller_diameter));
		mParam.setDrumPulleyDiameter(0);
		TapcApp.getInstance().controller.setMachinePram(mParam.getAllParam());

		TapcApp.getInstance().controller.getMachinePram(mGetParamHandler);
		UnderCtl.uiMessage(mContext, getString(R.string.admin_machine_set_success));
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (arg0.toString().isEmpty()) {
				UnderCtl.uiMessage(mContext, getString(R.string.admin_machine_input_error));
			}
		}
	};
}
