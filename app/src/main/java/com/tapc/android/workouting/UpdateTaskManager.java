package com.tapc.android.workouting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.tapc.android.interfaceset.UpdateTaskEx;


public class UpdateTaskManager {
	HashMap<String, UpdateTaskEx> mTaskMap = new HashMap<String, UpdateTaskEx>();
	
	public void clear() {
		mTaskMap.clear();
	}
	
	public void add(String key, UpdateTaskEx task) {
		mTaskMap.put(key, task);
	}
	
	public void remove(UpdateTaskEx task) {
		mTaskMap.remove(task);
	}
	
	public UpdateTaskEx get(String key) {
		return (mTaskMap.get(key));
	}
	
	public void trigger() {
		Iterator<Entry<String, UpdateTaskEx>> it = mTaskMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, UpdateTaskEx> entery = (Map.Entry<String, UpdateTaskEx>) it.next();
			UpdateTaskEx task = (UpdateTaskEx) entery.getValue();
			task.run();
		}
	}
}
