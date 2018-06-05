package com.tapc.platform.dto.response;

import java.io.Serializable;


public class QuerryAdvResponse implements Serializable {

	/**
	 * 2015-3-31 Jason.liu Email 1946711081@qq.com 
	 * TODO
	 */
	private static final long serialVersionUID = -7013638991356274053L;
	private int id;
	private String name;
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
