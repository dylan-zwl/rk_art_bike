package com.tapc.platform.entity;

import java.io.Serializable;

public class UploadUserConfig implements Serializable {

	private static final long serialVersionUID = 7847503753938973894L;

	private String token;
	private String language;
	private String measurement = "0";

	public UploadUserConfig(String token, String language, String measurement) {
		this.token = token;
		this.language = language;
		this.measurement = measurement;
	}

	public UploadUserConfig() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

}
