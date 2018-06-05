package com.tapc.platform.sql;

import com.tapc.platform.entity.SportsData;
import com.tapc.platform.entity.UserInfor;

public class SportRecordItem {
	public int id = -1;
	public int uploadStatus = 0;
	public String datetime;
	public UserInfor userInfor;
	public SportsData sportsData;

	public SportRecordItem() {
		userInfor = new UserInfor();
		sportsData = new SportsData();
	}

	public SportRecordItem(UserInfor userInfor, SportsData sportsData,
			String datetime, int uploadStatus) {
		this.sportsData = sportsData;
		this.userInfor = userInfor;
		this.datetime = datetime;
		this.uploadStatus = uploadStatus;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void setSportsData(SportsData sportsData) {
		this.sportsData = sportsData;
	}

	public SportsData getSportsData() {
		return sportsData;
	}

	public void setUserInfor(UserInfor userInfor) {
		this.userInfor = userInfor;
	}

	public UserInfor getUserInfor() {
		return userInfor;
	}

	public void setDateTime(String datetime) {
		this.datetime = datetime;
	}

	public String getDateTime() {
		return datetime;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}
}
