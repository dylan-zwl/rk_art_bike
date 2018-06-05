package com.tapc.android.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.machine.Machine;
import com.tapc.android.machine.MachineData;
import com.tapc.android.workouting.Program;

import android.content.Context;
import android.os.Handler;


public interface Factory {
	Program makeProgram(ProgramSetting programSetting, Handler handler);
	Machine makeMachineInterface();
	ArrayList<ProgramType> makeProgramList(boolean isGuest);
	ArrayList<String> makeAppList();
//	HashMap<String, String> makeFeedbackInfoList(Context context, FeedbackGroupType groupType);
	MachineData getMachineData();
}
