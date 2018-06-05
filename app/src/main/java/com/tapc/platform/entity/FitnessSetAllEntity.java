package com.tapc.platform.entity;

import java.io.Serializable;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.ProgramSetting;

public class FitnessSetAllEntity implements Serializable {

	private static final long serialVersionUID = -8572875813687489294L;
	private int level;
	private double weight;
	private double incline;
	private double speed;
	private int minutes;
	private double distance;
	private double calorie;
	private int age;
	private long heartRate;
	private int programTime;
	private ProgramType programType;

	public void setProgramEntity(ProgramType programType, int level,
			double weight, double incline, double speed) {
		this.programType = programType;
		this.level = level;
		this.weight = weight;
		this.incline = incline;
		this.speed = speed;
	}

	public void setMinutesEntity(ProgramType programType, int minutes,
			double weight, double incline, double speed) {
		this.programType = programType;
		this.minutes = minutes;
		this.weight = weight;
		this.incline = incline;
		this.speed = speed;
	}

	public void setDistanceEntity(ProgramType programType, double distance,
			double weight, double incline, double speed) {
		this.programType = programType;
		this.distance = distance;
		this.weight = weight;
		this.incline = incline;
		this.speed = speed;
	}

	public void setCalorieEntity(ProgramType programType, double calorie,
			double weight, double incline, double speed) {
		this.programType = programType;
		this.calorie = calorie;
		this.weight = weight;
		this.incline = incline;
		this.speed = speed;
	}

	public void setMinutesEntity(ProgramType programType, int minutes, int age,
			long heartRate) {
		this.programType = programType;
		this.minutes = minutes;
		this.age = age;
		this.heartRate = heartRate;
	}

	public void setDistanceEntity(ProgramType programType, int distance,
			int age, long heartRate) {
		this.programType = programType;
		this.distance = distance;
		this.age = age;
		this.heartRate = heartRate;
	}

	public void setCalorieEntity(ProgramType programType, int calorie, int age,
			long heartRate) {
		this.programType = programType;
		this.calorie = calorie;
		this.age = age;
		this.heartRate = heartRate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getIncline() {
		return incline;
	}

	public void setIncline(double incline) {
		this.incline = incline;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getCalorie() {
		return calorie;
	}

	public void setCalorie(double calorie) {
		this.calorie = calorie;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public long getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(long heartRate) {
		this.heartRate = heartRate;
	}

	public void setProgramType(ProgramType programType) {
		this.programType = programType;
	}

	public ProgramType getProgramType() {
		return programType;
	}

	public void setProgramTime(int timeMin) {
		this.programTime = timeMin;
	}

	public int getProgramTime() {
		return programTime;
	}
}
