package com.tapc.platform.entity;

import java.io.Serializable;

/**
 * @author sean.guo
 * @date 2015�?�?2�?上午11:00:00
 */
public class RankingInfo implements Serializable {

	private static final long serialVersionUID = -1L;
	private String ranking;
	private String user;
	private String data;

	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
