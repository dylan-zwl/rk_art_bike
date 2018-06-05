package com.tapc.platform.fragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.tapc.platform.AppExceptionHandler;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.utils.UnderCtl;

import android.R.integer;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class HZMachineFragment extends Fragment {
	private Context mContext;
	// hz machine
	private int mHZpower;

	@ViewInject(R.id.power_220)
	private RadioButton ctl_power_220;

	@ViewInject(R.id.power_110)
	private RadioButton ctl_power_110;

	@ViewInject(R.id.ctl_rated_voltage)
	private EditText ctl_rated_voltage;

	@ViewInject(R.id.ctl_rated_current)
	private EditText ctl_rated_current;

	@ViewInject(R.id.ctl_rated_speed)
	private EditText ctl_rated_speed;

	@ViewInject(R.id.ctl_overload_current)
	private EditText ctl_overload_current;

	@ViewInject(R.id.ctl_rotor_winding)
	private EditText ctl_rotor_winding;

	@ViewInject(R.id.ctl_cutoff_current)
	private EditText ctl_cutoff_current;

	@ViewInject(R.id.ctl_min_speed)
	private EditText ctl_min_speed;

	@ViewInject(R.id.ctl_max_speed)
	private EditText ctl_max_speed;

	@ViewInject(R.id.ctl_acceleration)
	private EditText ctl_acceleration;

	@ViewInject(R.id.ctl_deceleration)
	private EditText ctl_deceleration;

	@ViewInject(R.id.ctl_max_slope)
	private EditText ctl_max_slope;

	@ViewInject(R.id.ctl_min_speed_than)
	private EditText ctl_min_speed_than;

	@ViewInject(R.id.ctl_mid_speed_than)
	private EditText ctl_mid_speed_than;

	@ViewInject(R.id.ctl_max_speed_than)
	private EditText ctl_max_speed_than;

	@ViewInject(R.id.ctl_roller_speed_than)
	private EditText ctl_roller_speed_than;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ctl_hz_set, container, false);
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
		ctl_rated_voltage.addTextChangedListener(mTextWatcher);
		ctl_rated_current.addTextChangedListener(mTextWatcher);
		ctl_rated_speed.addTextChangedListener(mTextWatcher);
		ctl_overload_current.addTextChangedListener(mTextWatcher);
		ctl_rotor_winding.addTextChangedListener(mTextWatcher);
		ctl_cutoff_current.addTextChangedListener(mTextWatcher);
		ctl_min_speed.addTextChangedListener(mTextWatcher);
		ctl_max_speed.addTextChangedListener(mTextWatcher);
		ctl_acceleration.addTextChangedListener(mTextWatcher);
		ctl_deceleration.addTextChangedListener(mTextWatcher);
		ctl_max_slope.addTextChangedListener(mTextWatcher);
		ctl_min_speed_than.addTextChangedListener(mTextWatcher);
		ctl_mid_speed_than.addTextChangedListener(mTextWatcher);
		ctl_max_speed_than.addTextChangedListener(mTextWatcher);
		ctl_roller_speed_than.addTextChangedListener(mTextWatcher);

		mHZpower = PreferenceHelper.readInt(mContext,
				Config.MACHINE_PARAM_CONFIG, "ctl_power", 220);
		if (mHZpower == 220) {
			ctl_power_220.setChecked(true);
		} else {
			ctl_power_110.setChecked(true);
		}
		UnderCtl.showInitValue(mContext, ctl_rated_voltage,
				"ctl_rated_voltage", HZDefValue.RATED_VOLTAGE);
		UnderCtl.showInitValue(mContext, ctl_rated_current,
				"ctl_rated_current", HZDefValue.RATED_CURRENT);
		UnderCtl.showInitValue(mContext, ctl_rated_speed, "ctl_rated_speed",
				HZDefValue.RATED_SPEED);
		UnderCtl.showInitValue(mContext, ctl_overload_current,
				"ctl_overload_current", HZDefValue.OVERLOAD_CURRENT);
		UnderCtl.showInitValue(mContext, ctl_rotor_winding,
				"ctl_rotor_winding", HZDefValue.ROTOR_WINDING);
		UnderCtl.showInitValue(mContext, ctl_cutoff_current,
				"ctl_cutoff_current", HZDefValue.CUTOFF_CURRENT);
		UnderCtl.showInitValue(mContext, ctl_min_speed, "ctl_min_speed",
				HZDefValue.MIN_SPEED);
		UnderCtl.showInitValue(mContext, ctl_max_speed, "ctl_max_speed",
				HZDefValue.MAX_SPEED);
		UnderCtl.showInitValue(mContext, ctl_max_slope, "ctl_max_slope",
				HZDefValue.MAX_SLOPE);
		UnderCtl.showInitValue(mContext, ctl_acceleration, "ctl_acceleration",
				HZDefValue.ACCELERATION);
		UnderCtl.showInitValue(mContext, ctl_deceleration, "ctl_deceleration",
				HZDefValue.DECELERATION);
		UnderCtl.showInitValue(mContext, ctl_min_speed_than,
				"ctl_min_speed_than", HZDefValue.MIN_SPEED_THAN);
		UnderCtl.showInitValue(mContext, ctl_mid_speed_than,
				"ctl_mid_speed_than", HZDefValue.MID_SPEED_THAN);
		UnderCtl.showInitValue(mContext, ctl_max_speed_than,
				"ctl_max_speed_than", HZDefValue.MAX_SPEED_THAN);
		UnderCtl.showInitValue(mContext, ctl_roller_speed_than,
				"ctl_roller_speed_than", HZDefValue.ROLLER_SPEED_THAN);
	}

	@OnRadioGroupCheckedChange(R.id.ctl_hz_power_group)
	protected void onSetHzPowerChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.power_220 :
				mHZpower = 220;
				break;
			case R.id.power_110 :
				mHZpower = 110;
				break;
		}
	}

	@OnClick(R.id.underctl_hz_save)
	protected void saveValue(View v) {
		if (UnderCtl.checkTextIsEmpty(ctl_rated_voltage)
				|| UnderCtl.checkTextIsEmpty(ctl_rated_current)
				|| UnderCtl.checkTextIsEmpty(ctl_rated_speed)
				|| UnderCtl.checkTextIsEmpty(ctl_overload_current)
				|| UnderCtl.checkTextIsEmpty(ctl_rotor_winding)
				|| UnderCtl.checkTextIsEmpty(ctl_cutoff_current)
				|| UnderCtl.checkTextIsEmpty(ctl_min_speed)
				|| UnderCtl.checkTextIsEmpty(ctl_max_speed)
				|| UnderCtl.checkTextIsEmpty(ctl_max_slope)
				|| UnderCtl.checkTextIsEmpty(ctl_acceleration)
				|| UnderCtl.checkTextIsEmpty(ctl_deceleration)
				|| UnderCtl.checkTextIsEmpty(ctl_min_speed_than)
				|| UnderCtl.checkTextIsEmpty(ctl_mid_speed_than)
				|| UnderCtl.checkTextIsEmpty(ctl_max_speed_than)
				|| UnderCtl.checkTextIsEmpty(ctl_roller_speed_than)) {
			UnderCtl.uiMessage(mContext, "有数据没有填写，请重新输入");
			return;
		}
		PreferenceHelper.write(mContext, Config.MACHINE_PARAM_CONFIG,
				"ctl_power", mHZpower);
		UnderCtl.setValue(mContext, ctl_rated_voltage, "ctl_rated_voltage");
		UnderCtl.setValue(mContext, ctl_rated_current, "ctl_rated_current");
		UnderCtl.setValue(mContext, ctl_rated_speed, "ctl_rated_speed");
		UnderCtl.setValue(mContext, ctl_overload_current,
				"ctl_overload_current");
		UnderCtl.setValue(mContext, ctl_rotor_winding, "ctl_rotor_winding");
		UnderCtl.setValue(mContext, ctl_cutoff_current, "ctl_cutoff_current");
		UnderCtl.setValue(mContext, ctl_min_speed, "ctl_min_speed");
		UnderCtl.setValue(mContext, ctl_max_speed, "ctl_max_speed");
		UnderCtl.setValue(mContext, ctl_max_slope, "ctl_max_slope");
		UnderCtl.setValue(mContext, ctl_acceleration, "ctl_acceleration");
		UnderCtl.setValue(mContext, ctl_deceleration, "ctl_deceleration");
		UnderCtl.setValue(mContext, ctl_min_speed_than, "ctl_min_speed_than");
		UnderCtl.setValue(mContext, ctl_mid_speed_than, "ctl_mid_speed_than");
		UnderCtl.setValue(mContext, ctl_max_speed_than, "ctl_max_speed_than");
		UnderCtl.setValue(mContext, ctl_roller_speed_than,
				"ctl_roller_speed_than");
		UnderCtl.uiMessage(mContext, "写入数据成功");
	}
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (arg0.toString().isEmpty()) {
				UnderCtl.uiMessage(mContext, "输入值不能为空，请重新输入");
			}
		}
	};

	class HZDefValue {
		public final static int POWER = 220;
		public final static int RATED_VOLTAGE = 0;
		public final static int RATED_CURRENT = 0;
		public final static int RATED_SPEED = 0;
		public final static int OVERLOAD_CURRENT = 0;
		public final static int ROTOR_WINDING = 0;
		public final static int CUTOFF_CURRENT = 0;
		public final static int MIN_SPEED = 0;
		public final static int MAX_SPEED = 0;
		public final static int MAX_SLOPE = 0;
		public final static int ACCELERATION = 0;
		public final static int DECELERATION = 0;
		public final static int MIN_SPEED_THAN = 0;
		public final static int MID_SPEED_THAN = 0;
		public final static int MAX_SPEED_THAN = 0;
		public final static int ROLLER_SPEED_THAN = 0;
	}
}
