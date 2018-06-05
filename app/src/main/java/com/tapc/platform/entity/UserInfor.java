package com.tapc.platform.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserInfor {
	private String uid = "";
	private String username = "";
	private String nickname = "";
	private String password = "";
	private String token = "";

	public UserInfor() {
	}

	public UserInfor(String uid, String username, String nickname,
			String password) {
		this.uid = uid;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String id) {
		this.uid = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean loginResult(String strResult) {
		try {
			JSONObject jsonObj = new JSONObject(strResult);
			if (jsonObj != null) {
				if (!jsonObj.has("err")) {
					uid = jsonObj.getString("uid");
					username = jsonObj.getString("username");
					password = jsonObj.getString("password");
					nickname = jsonObj.getString("nickname");
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getErrResultCode(String strResult) {
		int err = 0;
		try {
			JSONObject jsonObj = new JSONObject(strResult);
			if (jsonObj != null) {
				if (jsonObj.has("err")) {
					err = jsonObj.getInt("err");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return err;
	}

	public static String toGsonUserInfor(UserInfor userInfor) {
		Gson gson = new Gson();
		return gson.toJson(userInfor);
	}

	public static UserInfor getUserInfor(String str) {
		Gson gson = new Gson();
		return gson.fromJson(str, new TypeToken<UserInfor>() {
		}.getType());
	}
}
