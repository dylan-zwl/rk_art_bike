package com.tapc.platform.dto.response;

import com.tapc.platform.entity.DeviceData;

public class DeviceDataResponse extends Response {
	private DeviceData response;

	public DeviceData getResponse() {
		return response;
	}

	public void setResponse(DeviceData response) {
		this.response = response;
	}
}

