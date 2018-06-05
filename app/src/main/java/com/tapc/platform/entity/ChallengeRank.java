package com.tapc.platform.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ChallengeRank implements Serializable {

	private static final long serialVersionUID = -1L;
	private String people;
	private String successpercent;
	private ArrayList<RankingInfo> rankresponse;

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getSuccesspercent() {
		return successpercent;
	}

	public void setSuccesspercent(String successpercent) {
		this.successpercent = successpercent;
	}

	public ArrayList<RankingInfo> getRankresponse() {
		return rankresponse;
	}

	public void setRankresponse(ArrayList<RankingInfo> rankresponse) {
		this.rankresponse = rankresponse;
	}
}
