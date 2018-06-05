package com.tapc.platform.dto;

import java.io.Serializable;
import java.util.Map;

public class GroupUserResponseDTO implements Serializable {

	private int status; // 状态码
	private String message; // 请求结果信息
	private Map<String, GroupUserDTO> response;

	public GroupUserResponseDTO() {
		super();
	}

	public GroupUserResponseDTO(int status, String message, Map<String, GroupUserDTO> response) {
		super();
		this.status = status;
		this.message = message;
		this.response = response;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, GroupUserDTO> getResponse() {
		return response;
	}

	public void setResponse(Map<String, GroupUserDTO> response) {
		this.response = response;
	}

}
