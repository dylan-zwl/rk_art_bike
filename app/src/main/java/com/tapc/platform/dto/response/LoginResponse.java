package com.tapc.platform.dto.response;

import com.tapc.platform.entity.UploadUserConfig;

/**
 * @author Jason.liu
 * @Email 1946711081@qq.com TODO
 */
public class LoginResponse extends Response {

	private static final long serialVersionUID = 4041513257371337989L;
	private UploadUserConfig response;
	public UploadUserConfig getResponse() {
		return response;
	}
	public void setResponse(UploadUserConfig response) {
		this.response = response;
	}

}
