/**
 * AddRecordResponse.java[v 1.0.0]
 * classes:com.jht.tapc.platform.dto.response.AddRecordResponse
 * fch Create of at 2015�?�?2�?下午3:53:44
 */
package com.tapc.platform.dto.response;


public class AddRecordResponse extends Response{
	private static final long serialVersionUID = -4816240208820399827L;
	
	private String response;

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}
}
