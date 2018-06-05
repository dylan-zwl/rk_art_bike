package com.tapc.platform.dto;

import java.io.Serializable;

public class RemoteDTO implements Serializable {
	private static final long serialVersionUID = 1778516506980177889L;

	private String download;
	private String versionName;
	private String versionCode;

	private String description;

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
