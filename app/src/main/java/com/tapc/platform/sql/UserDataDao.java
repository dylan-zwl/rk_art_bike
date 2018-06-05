package com.tapc.platform.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.utils.DesUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDataDao {
	private SQLiteDatabase userDatadb;

	public UserDataDao(Context context) {
		UserSqliteHelper userUserSqliteHelper = UserSqliteHelper.getInstance(context);
		this.userDatadb = userUserSqliteHelper.getWritableDatabase();
	}

	// 插入
	public void insertItem(UserInfor userInfor) {
		String sql = "insert into " + UserSqliteHelper.TABLE_NAME + " (uid,username,nickname,password) values ('"
				+ userInfor.getUid() + "','" + userInfor.getUsername() + "','" + userInfor.getNickname() + "','"
				+ userInfor.getPassword() + "')";
		if (userDatadb.isOpen()) {
			try {
				userDatadb.execSQL(sql);
			} catch (SQLException e) {
			}
		}
	}

	public void close() {
		this.userDatadb.close();
	}

	// 删除表所有数据
	public void deleteAllItem() {
		try {
			String sql = "delete from " + UserSqliteHelper.TABLE_NAME;
			userDatadb.execSQL(sql);
		} catch (SQLException e) {
		}
	}

	public UserInfor getUserRecord(String userName) {
		UserInfor userInfor = null;
		if (userDatadb.isOpen()) {
			Cursor cursor = userDatadb.rawQuery(" select * from " + UserSqliteHelper.TABLE_NAME + " where username='"
					+ userName + "' ", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int j = 0;
					userInfor = new UserInfor();
					userInfor.setUid(cursor.getString(j));
					userInfor.setUsername(cursor.getString(++j));
					userInfor.setNickname(cursor.getString(++j));
					userInfor.setPassword(cursor.getString(++j));
				}
			}
		}
		return userInfor;
	}

	public void updateUploadStatus(int username, UserInfor user) {
		String sql = "update " + UserSqliteHelper.TABLE_NAME + " set isupload=" + " where username=" + username + ";";
		if (userDatadb.isOpen()) {
			try {
				userDatadb.execSQL(sql);
			} catch (SQLException e) {
			}
		}
	}

	public void query() {
		if (userDatadb.isOpen()) {
			Cursor cursor = userDatadb.rawQuery(" select * from " + UserSqliteHelper.TABLE_NAME, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int j = 0;
					String uid = cursor.getString(j);
					String username = cursor.getString(++j);
					String nickname = cursor.getString(++j);
					String password = cursor.getString(++j);
					Log.d("user data", uid + " " + username + " " + nickname + " " + password);
				}
			}
		}
	}
}
