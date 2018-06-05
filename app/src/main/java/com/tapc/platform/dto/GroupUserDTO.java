package com.tapc.platform.dto;

import java.io.Serializable;

import com.tapc.platform.utils.SysUtils;

public class GroupUserDTO implements Serializable {
	public static final int VALUE = 100;
	private String runner;
	private String machine;
	private String location;
	private int distance;
	private int speed;
	private int calories;
	private long duration;

	public GroupUserDTO() {
		super();
	}

	public GroupUserDTO(String runner, String machine, String location, int distance, int speed, int calories,
			long duration) {
		super();
		this.runner = runner;
		this.machine = machine;
		this.location = location;
		this.distance = distance;
		this.speed = speed;
		this.calories = calories;
		this.duration = duration;
	}

	public String getRunner() {
		return runner;
	}

	public void setRunner(String runner) {
		this.runner = runner;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDistanceStr() {
		double distanceDouble = distance;
		distanceDouble = distanceDouble / VALUE;
		distanceDouble = SysUtils.formatDouble(2, distanceDouble);
		String distanceStr = Double.toString(distanceDouble);
		if (distanceStr == null) {
			return "";
		}
		return distanceStr;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getSpeedStr() {
		double speedDouble = speed;
		speedDouble = speedDouble / VALUE;
		speedDouble = SysUtils.formatDouble(1, speedDouble);
		String speedStr = Double.toString(speedDouble);
		if (speedStr == null) {
			return "";
		}
		return speedStr;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getCaloriesStr() {
		double caloriesDouble = calories;
		caloriesDouble = caloriesDouble / VALUE;
		caloriesDouble = SysUtils.formatDouble(2, caloriesDouble);
		String caloriesStr = Double.toString(caloriesDouble);
		if (caloriesStr == null) {
			return "";
		}
		return caloriesStr;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public String getTimeTampStr() {
		String timeStr = String.format("%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60);
		if (timeStr == null) {
			return "";
		}
		return timeStr;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "GroupUserDTO [runner=" + runner + ", machine=" + machine + "]";
	}
}
