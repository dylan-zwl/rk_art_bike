package com.tapc.platform.dto.response;

import com.tapc.platform.entity.ChallengeResult;

public class GetChallengeResultResponse extends Response {
	private static final long serialVersionUID = -1L;
	private ChallengeResult response;

	public ChallengeResult getResponse() {
		return response;
	}

	public void setResponse(ChallengeResult response) {
		this.response = response;
	}
}
