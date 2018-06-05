package com.tapc.platform.fragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.utils.PreferenceHelper;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AdminUnderCtlFragment extends Fragment {
	private Context mContext;
	private int setType = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_under_control,
				container, false);
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

	private void initUI() {
		replaceFragment(
				R.id.machine_frame,
				Fragment.instantiate(mContext,
						NMMachineFragment.class.getName()));
	}

	@OnRadioGroupCheckedChange(R.id.set_machine_group)
	protected void onSetMachineChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.ctl_nm_machine :
				setType = MachineTypes.NM_MACHINE;
				replaceFragment(
						R.id.machine_frame,
						Fragment.instantiate(mContext,
								NMMachineFragment.class.getName()));
				break;
			case R.id.ctl_hz_machine :
				setType = MachineTypes.HZ_MACHINE;
				replaceFragment(
						R.id.machine_frame,
						Fragment.instantiate(mContext,
								HZMachineFragment.class.getName()));
				break;
		}
	}

	public void replaceFragment(int id, Fragment fragment) {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.replace(id, fragment);
		ft.commit();
	}

	private class MachineTypes {
		static private final int NM_MACHINE = 0;
		static private final int HZ_MACHINE = 1;
	}
}
