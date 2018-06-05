package com.tapc.android.util;

import com.tapc.android.data.Enum.MachineType;

public class AbstractFactory {
	static AbstractFactory instance;
	private AbstractFactory() {
		instance = null;
	}
	
	public static AbstractFactory getInstance() {
		if (null == instance) {
			instance = new AbstractFactory();
		}
		
		return instance;
	}
	
	public Factory getAFGFactory(MachineType machineType) {
		Factory factory = null;
		
		switch (machineType) {
			case TREADMILL: {
				factory = new TMFactory();
			}
			break; 
			default: break;
		}
		
		return factory;
	}
}
