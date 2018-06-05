package com.tapc.android.interfaceset;

import com.tapc.android.workouting.IntervalInfo;


public interface IntervalUpdateListener {
	void onIntervalChanged(int index, IntervalInfo interval);
	void onIntervalElapsedChanged(int elapsed);
}
