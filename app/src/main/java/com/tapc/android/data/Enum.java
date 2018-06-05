package com.tapc.android.data;

public class Enum {
	public enum MachineType {
		BIKE,
		STEPPER,
		RAMP,
		SWING,
		TREADMILL,
		ELLIPTICAL
	}

	public enum ProgramType {
		MANUAL,
		TIME,
	    DISTANCE,
	    CALORIE,
	    _5K,//5KM
	    _10K,//10KM
	    INTERVALS,
	    FAT_BURN,
	    TAPC_PROG,
	    MODERATE_BURN,
	    VIGOROUS_BURN,
	    MAP,
	    VIRTUAL_ACTIVE,
	    MARATHON,
	    NORMAL
	}
	
	public enum WorkoutStage {
		WARMUP,
		NORMAL,
		COOLDOWN
	}
	
	public enum WorkoutState {
		RUNNING,
		PAUSE,
		STOP
	}
	
	public enum TranslateDataType {
		NORMAL,
		HEART_RATE,
		RPM,
		INCLINE
	}
	
	public enum WorkoutGoal {
		TIME,
		DISTANCE,
		CALORIE
	}
	
	public enum Unit {
		STANDARD,
		METRIC
	}
}
