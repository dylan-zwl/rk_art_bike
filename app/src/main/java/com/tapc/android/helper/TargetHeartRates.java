package com.tapc.android.helper;

import com.tapc.android.data.Enum.ProgramType;

public class TargetHeartRates {

	public TargetHeartRates() {
		// TODO Auto-generated constructor stub
	}

	public static long getTargetHeartRates(int age, ProgramType programType) {
		long hrTarget = 0;
		double hrMax = 206.9-(0.67*age);
		double hr = 0;
		switch (programType) {
		case  MODERATE_BURN:
		{
			hr = Math.round(0.65*hrMax);
		}
			break;
		case VIGOROUS_BURN:
		{
			hr = Math.round(0.8*hrMax);
		}
			break;
		default:
			break;
		}
		hrTarget = (int)hr;
		return hrTarget;
	}
}
