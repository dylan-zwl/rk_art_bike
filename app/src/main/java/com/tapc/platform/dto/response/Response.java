/**
 * 
 */
package com.tapc.platform.dto.response;

import java.io.Serializable;

public class Response implements Serializable {
	private static final long serialVersionUID = 8573160645266619982L;
	private int status;
	private String message;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ResPonse [status=" + status + ", message=" + message + "]";
	}

}
