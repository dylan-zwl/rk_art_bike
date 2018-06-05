package com.tapc.android.machine;

import com.tapc.android.controller.MachineController;
import com.tapc.android.data.Enum.MachineType;
import com.tapc.android.data.Enum.Unit;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.data.Workout;
import com.tapc.android.interfaceset.RPMListener;
import com.tapc.android.util.AbstractFactory;
import com.tapc.android.util.UnitConverter;
import com.tapc.android.workouting.IntervalInfo;
import com.tapc.platform.Config;
import com.tapc.platform.TapcApp;
import com.tapc.platform.entity.BikeCtlType;

public abstract class Machine {

	private static final String TAG = "Machine";

	public abstract void loadInterval(IntervalInfo interval);

	public abstract void start();

	public abstract void resume();

	public abstract void stop();

	public abstract void pause();

	public abstract void setLeftPanel(double value);

	public abstract void setRightPanel(double value);

	protected MachineData mMachineData;
	private RPMListener mRPMListener;
	private Workout mWorkout;
	protected MachineController mController = MachineController.getInstance();

	protected static float MIN_INCLINE = 0.0f;
	protected static float MAX_INCLINE = 15.0f;

	private static int MIN_RESISTANCE = 0;
	private static int MAX_RESISTANCE = 100;

	private static float MIN_SPEED = 0.5f;
	private static float MAX_SPEED = 12.0f;

	private static int MIN_WORKOUT_TIME = 5;
	private static int MAX_WORKOUT_TIME = 50;

	private static int MIN_WEIGHT = 80;
	private static int MAX_WEIGHT = 400;

	private static int SOFTWARE_VERSION = 7;
	private static int USER_COUNT = 4;
	private static int MACHINE_SERIAL = 424;

	private static int MIN_INCLINE_ADC = 26;
	private static int MAX_INCLINE_ADC = 1000;

	private static int MIN_RPM = 170;
	private static int MAX_RPM = 4145;

	private static double MAX_MPH;
	private static double MIN_MPH;

	private static double MAX_KPH;
	private static double MIN_KPH;

	private static double KPH_PER_RPM;

	private int RPM = 0;
	// Interpolations
	private Interpolation mIncline = new Interpolation();
	private Interpolation mMPH = new Interpolation();
	private Interpolation mKPH = new Interpolation();

	private static MachineType MACHINE_TYPE = MachineType.TREADMILL;

	public Machine() {

		// machine type
		int machineType = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MACHINE_TYPE, MACHINE_TYPE.ordinal());
		MACHINE_TYPE = MachineType.values()[machineType];

		// KPH
		MIN_KPH = SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MIN_SPEED);
		MAX_KPH = SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MAX_SPEED);

		// MPH
		MIN_MPH = MIN_KPH * UnitConverter.MPH_PER_KPH;
		MAX_MPH = MAX_KPH * UnitConverter.MPH_PER_KPH;

		// RPM
		MIN_RPM = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MIN_RPM);
		MAX_RPM = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MAX_RPM);

		// incline ADC
		MIN_INCLINE_ADC = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MIN_INCLINE_ADC);
		MAX_INCLINE_ADC = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MAX_INCLINE_ADC);

		// incline
		MIN_INCLINE = (SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MIN_INCLINE));
		MAX_INCLINE = (SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MAX_INCLINE));

		// speed
		MIN_SPEED = (SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MIN_SPEED));
		MAX_SPEED = (SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MAX_SPEED));

		KPH_PER_RPM = SystemSettingsHelper.KPH_PER_RPM;

		// max workout time
		MAX_WORKOUT_TIME = SystemSettingsHelper.getInt(SystemSettingsHelper.KEY_MAX_WORKOUT_TIME);

		// unit
		/*
		 * int unit = SystemSettingsHelper.getInt(mContext,
		 * SystemSettingsHelper.KEY_UNITS, Unit.STANDARD.ordinal()); UNIT =
		 * Unit.values()[unit];
		 */

		// interpolation
		mMPH.setMinMax(MIN_MPH, MAX_MPH, MIN_RPM, MAX_RPM);
		mKPH.setMinMax(MIN_KPH, MAX_KPH, MIN_RPM, MAX_RPM);
		mIncline.setMinMax(MIN_INCLINE, MAX_INCLINE, MIN_INCLINE_ADC, MAX_INCLINE_ADC);

		// machine data
		mMachineData = AbstractFactory.getInstance().getAFGFactory(MACHINE_TYPE).getMachineData();
	}

	public void setRPMListener(RPMListener rpmListener) {
		mRPMListener = rpmListener;
	}

	public double getIncline() {
		if (Config.sBikeCtlType == BikeCtlType.WATT) {
			int incline = mController.getIncline();
			mMachineData.setIncline(incline);
			return incline;
		} else {
			return mMachineData.getIncline();
		}
	}

	public void setIncline(double incline) {
		mMachineData.setIncline(incline);
		if (Config.sBikeCtlType == BikeCtlType.LOAD && !TapcApp.getInstance().getSportsEngin().isPause()) {
			setInclineADC((int) (incline));
		}
		/*
		 * int permillageIncline = (int)(incline/MAX_INCLINE*1000);
		 * setInclineADC(permillageIncline);
		 */
	}

	public void setSpeed(double speed) {
		mMachineData.setSpeed(speed);
		int rpm = 0;
		if (Unit.values()[SystemSettingsHelper.UNITS] == Unit.STANDARD) {
			rpm = (int) (speed * KPH_PER_RPM);
		}
		if (Unit.values()[SystemSettingsHelper.UNITS] == Unit.METRIC) {
			rpm = (int) (speed * 1.609344f * KPH_PER_RPM);
		}

		setRPM(rpm);
		if (Config.sBikeCtlType == BikeCtlType.WATT && !TapcApp.getInstance().getSportsEngin().isPause()) {
			mController.setSpeed((int) (speed * 10));
		}
	}

	public double getSpeed() {
		if (Config.sBikeCtlType == BikeCtlType.LOAD) {
			int speed = mController.getSpeed() / 10;
			mMachineData.setSpeed(speed);
			return speed;
		} else {
			return mMachineData.getSpeed();
		}
	}

	public int getWatt() {
		return mController.getSpeed() / 10;
	}

	public double getRpmSpeed() {
		return mController.getRpmSpeed();
	}

	/*
	 * public double getCalorie(double weight) { return
	 * mMachineData.getCalorie(weight); }
	 */

	public double getPaceRate() {
		int rate = mController.getPaceRate();
		double paceRate = (rate & 0x7F);
		paceRate = paceRate / 10;
		return paceRate;
	}

	public int getPaceFlag() {
		int flag = mController.getPaceRate();
		return flag & 0x80;
	}

	public int getHeartRate() {
		// Log.e("getHeartRate", " "+mController.getHeartRate());
		return mController.getHeartRate();
	}

	// climb speed. altutide
	public double getClimbSpeed() {
		return Math.sin(Math.toRadians(getIncline() - SystemSettingsHelper.ORIGIN_INCLINE)) * getSpeed();
	}

	public double getInclineADC() {
		return mController.getIncline();
		// return 0;
	}

	// permillage
	public void setInclineADC(int inclineADC) {
		mController.setIncline(inclineADC);
	}

	public double getRealRPM() {
		return mController.getSpeed();
		// return 0;
	}

	public int getRPM() {
		return RPM;
	}

	// rpm
	public void setRPM(int rpm) {
		// Log.e("----setRPM-----", ""+rpm);
		RPM = rpm;
		// mRPMListener.onRPMChanged(rpm);
		// mController.setSpeed(rpm);
	}

	public void setRealRPM(int rpm) {
		mController.setSpeed(rpm);
	}
}
