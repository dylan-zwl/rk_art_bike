
package com.tapc.platform.dto.response;

import com.tapc.platform.dto.RemoteDTO;


public class RemotionResponse extends Response{
	private static final long serialVersionUID = -3037164732062503602L;
	
	private RemoteDTO response;

	/**
	 * @return the response
	 */
	public RemoteDTO getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(RemoteDTO response) {
		this.response = response;
	}
}