package com.tapc.platform.dto.response;

import com.tapc.platform.entity.UploadUserConfig;

public class SetUserLanguageResponse extends Response {
	private static final long serialVersionUID = -6878409407619773362L;
	private UploadUserConfig response;

	public UploadUserConfig getResponse() {
		return response;
	}

	public void setResponse(UploadUserConfig response) {
		this.response = response;
	}

}
