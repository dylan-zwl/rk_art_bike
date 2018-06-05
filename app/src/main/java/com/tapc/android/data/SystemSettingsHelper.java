package com.tapc.android.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class SystemSettingsHelper {
	public static final String SETTINGS_FILE = "treadmill_config";

	// incline
	public static float MIN_INCLINE = 1;
	public static float MAX_INCLINE = 40;
	public static float STEP_INCLINE = 1f;
	public static float ORIGIN_INCLINE = 0;
	public static float DEFAULT_INCLINE = 1;
	public static int MIN_INCLINE_ADC = 35328;
	public static int MAX_INCLINE_ADC = 59008;
	// speed
	public static float MIN_SPEED;
	public static float MAX_SPEED;
	public static float STEP_SPEED;
	public static float DEFAULT_SPEED;
	private static float MIN_SPEED_KM = 10f;
	private static float MAX_SPEED_KM = 500f;
	private static float STEP_SPEED_KM = 1.0f;
	private static float MIN_SPEED_MI = 0.5f;
	private static float MAX_SPEED_MI = 12.5f;
	private static float STEP_SPEED_MI = 0.1f;
	private static float DEFAULT_SPEED_KM = 1.0f;
	private static float DEFAULT_SPEED_MI = 1.0f;
	public static int MIN_RPM = 170;
	public static int MAX_RPM = 170;

	// workout time
	public static int WORKOUT_TIME = 20;
	public static int MAX_WORKOUT_TIME = 99;
	public static int PAUSE_TIME = 5;
	public static int WARMUP_TIME = 0;
	public static int COOLDOWN_TIME = 2;
	// info
	public static int AGE = 24;
	public static int WEIGHT = 68;
	public static boolean GENDER = true;
	public static long TARGET_HEART_RATE = 85;
	public static boolean ENERGY_SAVER = true;
	// parameter
	public static boolean FIRST_BOOT = true;
	public static int MACHINE_TYPE = 4;
	public static boolean DEMO_MODE = false;
	public static boolean PASSPORT_SYNC = false;
	public static int MODEL = 1;
	public static int UNITS = 0;
	public static int MOTOR_DIA = 40;
	public static int ROLLER_DIA = 100;
	public static int ROLLER_BELT_DIA = 150;
	public static float KPH_PER_RPM = 199.044586f;

	// db
	public final static String DB_PROGRAMS_NAME = "PremierPrograms.db";

	// Default
	public final static String KEY_WORKOUT_TIME = "workout_time";
	public final static String KEY_MAX_WORKOUT_TIME = "max_workout_time";
	public final static String KEY_PAUSE_TIME = "pause_time";
	public final static String KEY_WARMUP_TIME = "warmup_time";
	public final static String KEY_COOLDOWN_TIME = "cooldown_time";
	public final static String KEY_AGE = "age";
	public final static String KEY_WEIGHT = "weight";
	public final static String KEY_GENDER = "gender";
	public final static String KEY_TARGET_HEART_RATE = "target_heart_rate";

	// Settings
	public final static String KEY_ENERGY_SAVER = "energy_saver";
	public final static String KEY_MIN_INCLINE_ADC = "min_incline_adc";
	public final static String KEY_MAX_INCLINE_ADC = "max_incline_adc";
	public final static String KEY_MIN_RPM = "min_rpm";
	public final static String KEY_MAX_RPM = "max_rpm";
	public final static String KEY_PROGRAM_SPEED = "program_speed";
	public final static String KEY_FIRST_BOOT = "first_boot";
	public final static String KEY_MACHINE_TYPE = "machine_type";
	public final static String KEY_DEMO_MODE = "demo_mode";
	public final static String KEY_PASSPORT_SYNC = "passport_sync";
	public final static String KEY_UNITS = "units";

	// Used for UI
	public final static String KEY_ORIGIN_INCLINE = "origin_incline";
	public final static String KEY_MIN_INCLINE = "min_incline";
	public final static String KEY_MAX_INCLINE = "max_incline";
	public final static String KEY_MIN_SPEED = "min_speed";
	public final static String KEY_MAX_SPEED = "max_speed";
	private final static String KEY_MIN_SPEED_KM = "min_speed_km";
	private final static String KEY_MAX_SPEED_KM = "max_speed_km";
	private final static String KEY_MIN_SPEED_MI = "min_speed_mi";
	private final static String KEY_MAX_SPEED_MI = "max_speed_mi";
	public final static String KEY_MIN_RESISTANCE = "min_resistance";
	public final static String KEY_MAX_RESISTANCE = "max_resistance";
	public final static String KEY_INCLINE_STEP = "incilne_step";
	public final static String KEY_SPEED_STEP = "speed_step";
	private final static String KEY_SPEED_STEP_KM = "speed_step_km";
	private final static String KEY_SPEED_STEP_MI = "speed_step_mi";
	public final static String KEY_DEFAULT_INCLINE = "default_incilne";
	public final static String KEY_DEFAULT_SPEED = "default_speed";
	private final static String KEY_DEFAULT_SPEED_KM = "default_speed_km";
	private final static String KEY_DEFAULT_SPEED_MI = "default_speed_mi";

	// models
	public final static String KEY_MODEL = "model";

	// speed Converter rpm
	// public final static String KEY_KPH_PER_RPM = "kph_per_rpm";
	public final static String KEY_MOTOR_DIA = "motor_dia";
	public final static String KEY_ROLLER_DIA = "roller_dia";
	public final static String KEY_ROLLER_BELT_DIA = "roller_belt_dia";

	//
	public static Context mContext = null;

	public static boolean mIntervalSound = false;

	// data shared
	private static final String TAG = "SystemSettingsHelper";

	/*
	 * private static final String DEFAULT_SETTINGS_XML_FILE =
	 * "default_config.xml"; private static final String
	 * MACHINE_SETTINGS_XML_FILE[] =
	 * {null,null,null,null,"treadmill_config.xml","elliptical_config.xml"};
	 */

	private SystemSettingsHelper() {
	}

	/*
	 * public static void loadSettings(Context context) throws IOException,
	 * XmlPullParserException { mContext = context; //
	 * loadSettingsFromXML(DEFAULT_SETTINGS_XML_FILE, mContext); }
	 * 
	 * public static void loadSettings(int machineType, Context context) throws
	 * IOException, XmlPullParserException { // String xmlPath =
	 * MACHINE_SETTINGS_XML_FILE[machineType]; // loadSettingsFromXML(xmlPath,
	 * context); }
	 * 
	 * static void loadSettingsFromXML(String xmlPath, Context context) throws
	 * IOException, XmlPullParserException { SharedPreferences sp =
	 * context.getSharedPreferences(SYSTEM_SETTINGS_FILE, Context.MODE_PRIVATE);
	 * SharedPreferences.Editor edit = sp.edit(); InputStream inputStream =
	 * context.getResources().getAssets().open(xmlPath); XmlPullParser parser =
	 * Xml.newPullParser(); parser.setInput(inputStream, "UTF-8"); try { int
	 * eventType = parser.getEventType();
	 * 
	 * String tag = ""; while (XmlPullParser.END_DOCUMENT != eventType) { if
	 * (XmlPullParser.START_TAG == eventType) { tag = parser.getName(); //
	 * Log.d(TAG, "start tag: " + parser.getName()); } else if
	 * (XmlPullParser.END_TAG == eventType) { // Log.d(TAG, "end tag: " + tag);
	 * tag = ""; } else if (XmlPullParser.TEXT == eventType) { // Log.d(TAG,
	 * "get text: " + parser.getText()); String value = parser.getText(); if
	 * (tag.equals(KEY_MAX_WORKOUT_TIME)) { edit.putInt(KEY_MAX_WORKOUT_TIME,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_PAUSE_TIME)) {
	 * edit.putInt(KEY_PAUSE_TIME, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_WORKOUT_TIME)) { edit.putInt(KEY_WORKOUT_TIME,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_WARMUP_TIME)) {
	 * edit.putInt(KEY_WARMUP_TIME, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_COOLDOWN_TIME)) { edit.putInt(KEY_COOLDOWN_TIME,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_AGE)) {
	 * edit.putInt(KEY_AGE, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_WEIGHT)) { edit.putInt(KEY_WEIGHT,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_GENDER)) {
	 * edit.putBoolean(KEY_GENDER, Boolean.valueOf(value)); } else if
	 * (tag.equals(KEY_TARGET_HEART_RATE)) { edit.putLong(KEY_TARGET_HEART_RATE,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_ENERGY_SAVER)) {
	 * edit.putBoolean(KEY_ENERGY_SAVER, Boolean.valueOf(value)); } else if
	 * (tag.equals(KEY_MIN_INCLINE_ADC)) { edit.putInt(KEY_MIN_INCLINE_ADC,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_MAX_INCLINE_ADC)) {
	 * edit.putInt(KEY_MAX_INCLINE_ADC, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_MIN_RPM)) { edit.putInt(KEY_MIN_RPM,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_MAX_RPM)) {
	 * edit.putInt(KEY_MAX_RPM, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_PROGRAM_SPEED)) { edit.putInt(KEY_PROGRAM_SPEED,
	 * Integer.valueOf(value)); } else if (tag.equals(KEY_MACHINE_TYPE)) {
	 * edit.putInt(KEY_MACHINE_TYPE, Integer.valueOf(value)); } else if
	 * (tag.equals(KEY_DEMO_MODE)) { edit.putBoolean(KEY_DEMO_MODE,
	 * Boolean.valueOf(value)); } else if (tag.equals(KEY_PASSPORT_SYNC)) {
	 * edit.putBoolean(KEY_PASSPORT_SYNC, Boolean.valueOf(value)); } else if
	 * (tag.equals(KEY_ORIGIN_INCLINE)) { edit.putFloat(KEY_ORIGIN_INCLINE,
	 * Float.valueOf(value)); } else if (tag.equals(KEY_MIN_INCLINE)) {
	 * edit.putFloat(KEY_MIN_INCLINE, Float.valueOf(value)); } else if
	 * (tag.equals(KEY_MAX_INCLINE)) { edit.putFloat(KEY_MAX_INCLINE,
	 * Float.valueOf(value)); } else if (tag.equals(KEY_MIN_SPEED)) {
	 * edit.putFloat(KEY_MIN_SPEED, Float.valueOf(value)); } else if
	 * (tag.equals(KEY_MAX_SPEED)) { edit.putFloat(KEY_MAX_SPEED,
	 * Float.valueOf(value)); } else if (tag.equals(KEY_DEFAULT_INCLINE)) {
	 * edit.putFloat(KEY_DEFAULT_INCLINE, Float.valueOf(value)); } else if
	 * (tag.equals(KEY_DEFAULT_SPEED)) { edit.putFloat(KEY_DEFAULT_SPEED,
	 * Float.valueOf(value)); } else if (tag.equals(KEY_INCLINE_STEP)) {
	 * edit.putFloat(KEY_INCLINE_STEP, Float.valueOf(value)); } else if
	 * (tag.equals(KEY_SPEED_STEP)) { edit.putFloat(KEY_SPEED_STEP,
	 * Float.valueOf(value)); }else if (tag.equals(KEY_MODEL)) {
	 * edit.putInt(KEY_MODEL, Integer.valueOf(value)); }else if
	 * (tag.equals(KEY_KPH_PER_RPM)) { edit.putFloat(KEY_KPH_PER_RPM,
	 * Float.valueOf(value)); }
	 * 
	 * }
	 * 
	 * eventType = parser.next(); } } catch (XmlPullParserException e) {
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
	 * edit.commit(); }
	 */

	public static void Init(Context context) {
		mContext = context;
		Load();
		if (FIRST_BOOT) {
			SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = sp.edit();
			edit.putBoolean(KEY_FIRST_BOOT, false);
			// incline
			edit.putFloat(KEY_MIN_INCLINE, MIN_INCLINE);
			edit.putFloat(KEY_MAX_INCLINE, MAX_INCLINE);
			edit.putFloat(KEY_INCLINE_STEP, STEP_INCLINE);
			edit.putFloat(KEY_ORIGIN_INCLINE, ORIGIN_INCLINE);
			edit.putFloat(KEY_DEFAULT_INCLINE, DEFAULT_INCLINE);
			edit.putInt(KEY_MIN_INCLINE_ADC, MIN_INCLINE_ADC);
			edit.putInt(KEY_MAX_INCLINE_ADC, MAX_INCLINE_ADC);
			// speed
			edit.putFloat(KEY_MIN_SPEED_KM, MIN_SPEED_KM);
			edit.putFloat(KEY_MAX_SPEED_KM, MAX_SPEED_KM);
			edit.putFloat(KEY_SPEED_STEP_KM, STEP_SPEED_KM);
			edit.putFloat(KEY_DEFAULT_SPEED_KM, DEFAULT_SPEED_KM);
			edit.putFloat(KEY_MIN_SPEED_MI, MIN_SPEED_MI);
			edit.putFloat(KEY_MAX_SPEED_MI, MAX_SPEED_MI);
			edit.putFloat(KEY_SPEED_STEP_MI, STEP_SPEED_MI);
			edit.putFloat(KEY_DEFAULT_SPEED_MI, DEFAULT_SPEED_MI);
			edit.putInt(KEY_MIN_RPM, MIN_RPM);
			edit.putInt(KEY_MAX_RPM, MAX_RPM);

			// workout time
			edit.putInt(KEY_WORKOUT_TIME, WORKOUT_TIME);
			edit.putInt(KEY_MAX_WORKOUT_TIME, MAX_WORKOUT_TIME);
			edit.putInt(KEY_PAUSE_TIME, PAUSE_TIME);
			edit.putInt(KEY_WARMUP_TIME, WARMUP_TIME);
			edit.putInt(KEY_COOLDOWN_TIME, COOLDOWN_TIME);
			// info
			edit.putInt(KEY_AGE, AGE);
			edit.putInt(KEY_WEIGHT, WEIGHT);
			edit.putBoolean(KEY_GENDER, GENDER);
			edit.putLong(KEY_TARGET_HEART_RATE, TARGET_HEART_RATE);
			edit.putBoolean(KEY_ENERGY_SAVER, ENERGY_SAVER);
			// parameter
			edit.putInt(KEY_MACHINE_TYPE, MACHINE_TYPE);
			edit.putBoolean(KEY_DEMO_MODE, DEMO_MODE);
			edit.putBoolean(KEY_PASSPORT_SYNC, PASSPORT_SYNC);
			edit.putInt(KEY_MODEL, MODEL);
			edit.putInt(KEY_MOTOR_DIA, MOTOR_DIA);
			edit.putInt(KEY_ROLLER_DIA, ROLLER_DIA);
			edit.putInt(KEY_ROLLER_BELT_DIA, ROLLER_BELT_DIA);
			edit.putInt(KEY_UNITS, UNITS);
			edit.commit();
			KPH_PER_RPM = 10000 * ROLLER_BELT_DIA;
			KPH_PER_RPM /= (1.884f * MOTOR_DIA * ROLLER_DIA);
		}
	}

	private static void Load() {
		FIRST_BOOT = getBoolean(KEY_FIRST_BOOT, FIRST_BOOT);
		MIN_INCLINE = getFloat(KEY_MIN_INCLINE, MIN_INCLINE);
		MAX_INCLINE = getFloat(KEY_MAX_INCLINE, MAX_INCLINE);
		STEP_INCLINE = getFloat(KEY_INCLINE_STEP, STEP_INCLINE);
		ORIGIN_INCLINE = getFloat(KEY_ORIGIN_INCLINE, ORIGIN_INCLINE);
		DEFAULT_INCLINE = getFloat(KEY_DEFAULT_INCLINE, DEFAULT_INCLINE);
		MIN_INCLINE_ADC = getInt(KEY_MIN_INCLINE_ADC, MIN_INCLINE_ADC);
		MAX_INCLINE_ADC = getInt(KEY_MAX_INCLINE_ADC, MAX_INCLINE_ADC);
		// speed
		/*
		 * MIN_SPEED = getFloat(KEY_MIN_SPEED); MAX_SPEED =
		 * getFloat(KEY_MAX_SPEED); STEP_SPEED = getFloat(KEY_SPEED_STEP);
		 * DEFAULT_SPEED = getFloat(KEY_DEFAULT_SPEED);
		 */
		MIN_SPEED_KM = getFloat(KEY_MIN_SPEED_KM, MIN_SPEED_KM);
		MAX_SPEED_KM = getFloat(KEY_MAX_SPEED_KM, MAX_SPEED_KM);
		STEP_SPEED_KM = getFloat(KEY_SPEED_STEP_KM, STEP_SPEED_KM);
		DEFAULT_SPEED_KM = getFloat(KEY_DEFAULT_SPEED_KM, DEFAULT_SPEED_KM);
		MIN_SPEED_MI = getFloat(KEY_MIN_SPEED_MI, MIN_SPEED_MI);
		MAX_SPEED_MI = getFloat(KEY_MAX_SPEED_MI, MAX_SPEED_MI);
		STEP_SPEED_MI = getFloat(KEY_SPEED_STEP_MI, STEP_SPEED_MI);
		DEFAULT_SPEED_MI = getFloat(KEY_DEFAULT_SPEED_MI, DEFAULT_SPEED_MI);
		MIN_RPM = getInt(KEY_MIN_RPM, MIN_RPM);
		MAX_RPM = getInt(KEY_MAX_RPM, MAX_RPM);

		// workout time
		WORKOUT_TIME = getInt(KEY_WORKOUT_TIME, WORKOUT_TIME);
		MAX_WORKOUT_TIME = getInt(KEY_MAX_WORKOUT_TIME, MAX_WORKOUT_TIME);
		PAUSE_TIME = getInt(KEY_PAUSE_TIME, PAUSE_TIME);
		WARMUP_TIME = getInt(KEY_WARMUP_TIME, WARMUP_TIME);
		COOLDOWN_TIME = getInt(KEY_COOLDOWN_TIME, COOLDOWN_TIME);
		// info
		AGE = getInt(KEY_AGE, AGE);
		WEIGHT = getInt(KEY_WEIGHT, WEIGHT);
		GENDER = getBoolean(KEY_GENDER, GENDER);
		TARGET_HEART_RATE = getLong(KEY_TARGET_HEART_RATE, TARGET_HEART_RATE);
		ENERGY_SAVER = getBoolean(KEY_ENERGY_SAVER, ENERGY_SAVER);
		// parameter
		MACHINE_TYPE = getInt(KEY_MACHINE_TYPE, MACHINE_TYPE);
		DEMO_MODE = getBoolean(KEY_DEMO_MODE, DEMO_MODE);
		PASSPORT_SYNC = getBoolean(KEY_PASSPORT_SYNC, PASSPORT_SYNC);
		MODEL = getInt(KEY_MODEL, MODEL);
		MOTOR_DIA = getInt(KEY_MOTOR_DIA, MOTOR_DIA);
		ROLLER_DIA = getInt(KEY_ROLLER_DIA, ROLLER_DIA);
		ROLLER_BELT_DIA = getInt(KEY_ROLLER_BELT_DIA, ROLLER_BELT_DIA);
		KPH_PER_RPM = 10000 * ROLLER_BELT_DIA;
		KPH_PER_RPM /= (1.884f * MOTOR_DIA * ROLLER_DIA);
		UNITS = getInt(KEY_UNITS, UNITS);
		// KPH_PER_RPM = getFloat(KEY_KPH_PER_RPM);
		if (UNITS == 0) {
			MIN_SPEED = MIN_SPEED_KM;
			MAX_SPEED = MAX_SPEED_KM;
			STEP_SPEED = STEP_SPEED_KM;
			DEFAULT_SPEED = DEFAULT_SPEED_KM;
		} else {
			MIN_SPEED = MIN_SPEED_MI;
			MAX_SPEED = MAX_SPEED_MI;
			STEP_SPEED = STEP_SPEED_MI;
			DEFAULT_SPEED = DEFAULT_SPEED_MI;
		}
	}

	public static float getFloat(String key) {
		return getFloat(key, 0);
	}

	public static float getFloat(String key, float def) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		if (key.equals(KEY_MIN_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_MIN_SPEED_KM;
			} else {
				key = KEY_MAX_SPEED_MI;
			}
		}
		if (key.equals(KEY_MAX_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_MAX_SPEED_KM;
			} else {
				key = KEY_MAX_SPEED_MI;
			}
		}
		if (key.equals(KEY_SPEED_STEP)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_SPEED_STEP_KM;
			} else {
				key = KEY_SPEED_STEP_MI;
			}
		}
		if (key.equals(KEY_DEFAULT_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_DEFAULT_SPEED_KM;
			} else {
				key = KEY_DEFAULT_SPEED_MI;
			}
		}
		return sp.getFloat(key, def);
	}

	public static void setFloat(String key, Float value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		if (key.equals(KEY_MIN_INCLINE) && value < 0) {
			ORIGIN_INCLINE = Math.abs(value);
			edit.putFloat(KEY_ORIGIN_INCLINE, ORIGIN_INCLINE);
			value = 0f;
		}
		if (key.equals(KEY_MIN_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_MIN_SPEED_KM;
			} else {
				key = KEY_MAX_SPEED_MI;
			}
		}
		if (key.equals(KEY_MAX_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_MAX_SPEED_KM;
			} else {
				key = KEY_MAX_SPEED_MI;
			}
		}
		if (key.equals(KEY_SPEED_STEP)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_SPEED_STEP_KM;
			} else {
				key = KEY_SPEED_STEP_MI;
			}
		}
		if (key.equals(KEY_DEFAULT_SPEED)) {
			if (getInt(KEY_UNITS) == 0) {
				key = KEY_DEFAULT_SPEED_KM;
			} else {
				key = KEY_DEFAULT_SPEED_MI;
			}
		}
		edit.putFloat(key, value);
		edit.commit();
		Load();
	}

	public static int getInt(String key) {
		return getInt(key, 0);
	}

	public static int getInt(String key, int def) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		return sp.getInt(key, def);
	}

	public static void setInt(String key, int value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
		Load();
	}

	public static long getLong(String key) {
		return getLong(key, 0);
	}

	public static long getLong(String key, long def) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		return sp.getLong(key, def);
	}

	public static void setLong(String key, long value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong(key, value);
		edit.commit();
		Load();
	}

	public static boolean getBoolean(String key) {
		return getBoolean(key, true);
	}

	public static boolean getBoolean(String key, boolean def) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(key, def);
	}

	public static void setBoolean(String key, boolean value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
		Load();
	}

	public static String getString(String key, String def) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		return sp.getString(key, def);
	}

	public static void setString(String key, String value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
		Load();
	}

	public static void setUnit(int unit) {
		UNITS = unit;
		if (UNITS == 0) {
			MIN_SPEED = MIN_SPEED_KM;
			MAX_SPEED = MAX_SPEED_KM;
			STEP_SPEED = STEP_SPEED_KM;
			DEFAULT_SPEED = DEFAULT_SPEED_KM;
		} else {
			MIN_SPEED = MIN_SPEED_MI;
			MAX_SPEED = MAX_SPEED_MI;
			STEP_SPEED = STEP_SPEED_MI;
			DEFAULT_SPEED = DEFAULT_SPEED_MI;
		}
	}

	public static void setIntervalSound(boolean bOn) {
		mIntervalSound = bOn;
	}

	public static boolean getIntervalSound() {
		return mIntervalSound;
	}
}
