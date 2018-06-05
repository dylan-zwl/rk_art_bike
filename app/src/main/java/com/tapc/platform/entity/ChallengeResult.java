package com.tapc.platform.entity;

import java.io.Serializable;

public class ChallengeResult implements Serializable {
	private static final long serialVersionUID = -1L;
	private String ranking;
	private String beatperson;

	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public String getBeatperson() {
		return beatperson;
	}

	public void setBeatperson(String beatperson) {
		this.beatperson = beatperson;
	}
}
