
package com.tapc.platform.dto;

import com.tapc.platform.entity.UploadUserConfig;

import java.io.Serializable;


public class UserDto extends UploadUserConfig implements Serializable{
	
	private static final long serialVersionUID = -1492260957571903875L;
	
	private String userName;
	
	public UserDto() {
	}
	
	public UserDto(String userName, String token, String languange, String measurement) {
		super(token, languange, measurement);
		this.userName = userName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
