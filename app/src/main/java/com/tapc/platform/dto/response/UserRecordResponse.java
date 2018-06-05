package com.tapc.platform.dto.response;

import java.io.Serializable;


public class UserRecordResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String runDate;//运动的日�?
	private String calorie;//运动的卡路里
	private String minute;// 运动的时间分�?
	private String duration;//运动的距�?
	private String credit;//当前运动 的积�?
	private String mk;//  步数：分�?公里
	private String times;// 心率：次/分钟 心率

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public String getCalorie() {
		return calorie;
	}

	public void setCalorie(String calorie) {
		this.calorie = calorie;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getMk() {
		return mk;
	}

	public void setMk(String mk) {
		this.mk = mk;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

}
