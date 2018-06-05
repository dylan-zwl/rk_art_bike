package com.tapc.platform.dto;

import java.io.Serializable;

public class PublicGroupDto implements Serializable {
	private double groupId;
	private double membersCount;
	private String groupName;

	public double getGroupId() {
		return groupId;
	}

	public void setGroupId(double groupId) {
		this.groupId = groupId;
	}

	public double getMembersCount() {
		return membersCount;
	}

	public void setMembersCount(double membersCount) {
		this.membersCount = membersCount;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
