package com.tapc.platform.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SportsDataDao {
	private SQLiteDatabase sportDatadb;

	public SportsDataDao(Context context) {
		SqliteHelper sqliteHelper = SqliteHelper.getInstance(context);
		this.sportDatadb = sqliteHelper.getWritableDatabase();
	}

	// 插入
	public void insertItem(SportRecordItem sportRecordItem) {
		String useName = "";
		String useUid = "";
		if (sportRecordItem.userInfor.getUsername().isEmpty()) {
			useName = "-";
			useUid = "-";
		} else {
			useName = sportRecordItem.userInfor.getUsername();
			useUid = sportRecordItem.userInfor.getUid();
		}
		String sql = "insert into "
				+ SqliteHelper.TABLE_NAME
				+ " (uid,username,datetime,runtime,distance,calories,steps,isupload) values ('"
				+ useUid + "','" + useName + "','" + sportRecordItem.datetime
				+ "','" + sportRecordItem.sportsData.getRuntime() + "','"
				+ sportRecordItem.sportsData.getDistance() + "','"
				+ sportRecordItem.sportsData.getCalories() + "','"
				+ sportRecordItem.sportsData.getSteps() + "','"
				+ sportRecordItem.uploadStatus + "')";

		if (sportDatadb.isOpen()) {
			try {
				sportDatadb.execSQL(sql);
			} catch (SQLException e) {
			}
		}
	}

	public void close() {
		this.sportDatadb.close();
	}

	// 删除表所有数据
	public void deleteAllItem() {
		try {
			String sql = "delete from " + SqliteHelper.TABLE_NAME;
			sportDatadb.execSQL(sql);
		} catch (SQLException e) {
		}
	}

	public List<SportRecordItem> getRecord(String userName) {
		List<SportRecordItem> list = new ArrayList<SportRecordItem>();
		if (userName.isEmpty()) {
			userName = "-";
		}
		if (sportDatadb.isOpen()) {
			Cursor cursor = sportDatadb.rawQuery(" select * from "
					+ SqliteHelper.TABLE_NAME + " where username='" + userName
					+ "'" + " order by datetime desc", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int j = 0;
					SportRecordItem sportRecordItem = new SportRecordItem();
					sportRecordItem.setID(cursor.getInt(j));
					sportRecordItem.userInfor.setUid(cursor.getString(++j));
					sportRecordItem.userInfor
							.setUsername(cursor.getString(++j));
					sportRecordItem.datetime = cursor.getString(++j);
					sportRecordItem.sportsData
							.setRuntime(cursor.getString(++j));
					sportRecordItem.sportsData.setDistance(cursor
							.getString(++j));
					sportRecordItem.sportsData.setCalories(cursor
							.getString(++j));
					sportRecordItem.sportsData.setSteps(cursor.getString(++j));
					sportRecordItem.uploadStatus = cursor.getInt(++j);
					list.add(sportRecordItem);
				}
			}
		}
		return list;
	}

	public List<SportRecordItem> getNotUploadRecord(String userName) {
		List<SportRecordItem> list = new ArrayList<SportRecordItem>();
		if (userName.isEmpty()) {
			userName = "-";
		}
		if (sportDatadb.isOpen()) {
			Cursor cursor = sportDatadb.rawQuery(" select * from "
					+ SqliteHelper.TABLE_NAME + " where username='" + userName
					+ "' and isupload=0 " + " order by datetime asc", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int j = 0;
					SportRecordItem sportRecordItem = new SportRecordItem();
					sportRecordItem.setID(cursor.getInt(j));
					sportRecordItem.userInfor.setUid(cursor.getString(++j));
					sportRecordItem.userInfor
							.setUsername(cursor.getString(++j));
					sportRecordItem.datetime = cursor.getString(++j);
					sportRecordItem.sportsData
							.setRuntime(cursor.getString(++j));
					sportRecordItem.sportsData.setDistance(cursor
							.getString(++j));
					sportRecordItem.sportsData.setCalories(cursor
							.getString(++j));
					sportRecordItem.sportsData.setSteps(cursor.getString(++j));
					sportRecordItem.uploadStatus = cursor.getInt(++j);
					list.add(sportRecordItem);
				}
			}
		}
		return list;
	}

	public void updateUploadStatus(int id, int status) {
		String sql = "update " + SqliteHelper.TABLE_NAME + " set isupload="
				+ status + " where id=" + id + ";";
		if (sportDatadb.isOpen()) {
			try {
				sportDatadb.execSQL(sql);
			} catch (SQLException e) {
			}
		}
	}

	public void query() {
		if (sportDatadb.isOpen()) {
			Cursor cursor = sportDatadb.rawQuery(" select * from "
					+ SqliteHelper.TABLE_NAME, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int j = 0;
					int id = cursor.getInt(j);
					String uid = cursor.getString(++j);
					String username = cursor.getString(++j);
					String datetime = cursor.getString(++j);
					String runtime = cursor.getString(++j);
					String distance = cursor.getString(++j);
					String calories = cursor.getString(++j);
					String steps = cursor.getString(++j);
					int uploadStatus = cursor.getInt(++j);
					Log.d("sports data", id + " " + uid + " " + username + " "
							+ datetime + " " + runtime + " " + distance + " "
							+ calories + " " + steps + " " + uploadStatus);
				}
			}
		}
	}
}
