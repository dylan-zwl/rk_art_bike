package com.tapc.platform.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSqliteHelper extends SQLiteOpenHelper {
	private static UserSqliteHelper mInstance = null;
	/** 数据库名称 **/
	public static final String DATABASE_NAME = "user.db";
	public static final String TABLE_NAME = "data";
	/** 数据库版本号 **/
	private static final int DATABASE_VERSION = 1;

	/** 数据库SQL语句 添加一个表 **/
	private static final String NAME_TABLE_CREATE = "create table if not exists "
			+ TABLE_NAME
			+ "(uid text,"
			+ "username text,"
			+ "nickname text,"
			+ "password text)";

	UserSqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** 单例模式 **/
	public static synchronized UserSqliteHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new UserSqliteHelper(context);
		}
		return mInstance;
	}

	public void onCreate(SQLiteDatabase db) {
		/** 向数据中添加表 **/
		db.execSQL(NAME_TABLE_CREATE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/** 可以拿到当前数据库的版本信息 与之前数据库的版本信息 用来更新数据库 **/
	}

	/**
	 * 删除数据库
	 * 
	 * @param context
	 * @return
	 */
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}
}
