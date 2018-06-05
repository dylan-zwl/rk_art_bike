package com.tapc.platform.dto.response;

import com.tapc.platform.entity.ChallengeRank;

public class GetChallengeRankResponse extends Response {
	private static final long serialVersionUID = -1L;
	private ChallengeRank response;

	public ChallengeRank getResponse() {
		return response;
	}

	public void setResponse(ChallengeRank response) {
		this.response = response;
	}
}
