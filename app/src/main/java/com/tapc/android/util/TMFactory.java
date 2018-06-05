package com.tapc.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.machine.Machine;
import com.tapc.android.machine.MachineData;
import com.tapc.android.machine.Treadmill;
import com.tapc.android.machine.TreadmillData;
import com.tapc.android.workouting.IntervalProgram;
import com.tapc.android.workouting.ManualProgram;
import com.tapc.android.workouting.Program;

import android.content.Context;
import android.os.Handler;


public class TMFactory implements Factory {
	
	@Override
	public Machine makeMachineInterface() {
		return Treadmill.getIntance();
	}

	@Override
	public Program makeProgram(ProgramSetting programSetting, Handler handler) {
		Program program = null;
		switch (programSetting.getProgramType()) {
/*			case TIME:
			case DISTANCE:
			case CALORIE:*/
			case _5K:
			case _10K:
			case INTERVALS:
			case FAT_BURN:
			case TAPC_PROG: {
				program = new IntervalProgram(programSetting, handler);
			}
			break;
			case MARATHON:
			{	
				double distance = 42.1f;
				if (SystemSettingsHelper.UNITS==1) {
					distance *= 0.6213712f; 
				}
				programSetting.setProgramType(ProgramType.DISTANCE);
				programSetting.SetDistance(distance);
				program = new ManualProgram(programSetting, handler);
			}
			break;
			default: {
				program = new ManualProgram(programSetting, handler);
			}
			break;
		}
		return program;
	}
	
	@Override
	public ArrayList<ProgramType> makeProgramList(boolean isGuest) {
		ArrayList<ProgramType> programTypeList = new ArrayList<ProgramType>();
		
		programTypeList.add(ProgramType.MANUAL);
		programTypeList.add(ProgramType._5K);
		programTypeList.add(ProgramType._10K);
		programTypeList.add(ProgramType.INTERVALS);
		programTypeList.add(ProgramType.FAT_BURN);
		programTypeList.add(ProgramType.TAPC_PROG);
		return programTypeList;
	}
	
	@Override
	public ArrayList<String> makeAppList() {
		ArrayList<String> appList = new ArrayList<String>();
		
		appList.add("app_web_browser");
		appList.add("app_simple");
		appList.add("app_profile");
		appList.add("app_media");
		appList.add("app_ipod");
		appList.add("app_twitter");
		appList.add("app_facebook");
		appList.add("app_tv");
		appList.add("app_weather");
		appList.add("app_youtube");
		appList.add("app_facility");
		appList.add("app_change_workout");
		appList.add("app_mfp");
		appList.add("app_calendar");
		appList.add("app_va");
		
		return appList;
	}

	@Override
	public MachineData getMachineData() {
		// TODO Auto-generated method stub
		return new TreadmillData();
	}
}
