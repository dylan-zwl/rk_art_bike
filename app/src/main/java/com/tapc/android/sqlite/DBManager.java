package com.tapc.android.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.tapc.android.data.SystemSettingsHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBManager {

	private final int BUFFER_SIZE = 400000;
	public static final String DB_NAME = "premierprograms.db";
	private SQLiteDatabase mDatabase;

	public DBManager() {
	}

	public SQLiteDatabase openDatabase() {
		String dbPath = "/data"+
				Environment.getDataDirectory().getAbsolutePath()+"/"+ 
				SystemSettingsHelper.mContext.getPackageName()+"/"+DB_NAME;
		return (this.mDatabase = this.openDatabase(dbPath));
	}

	private SQLiteDatabase openDatabase(String dbPath) {

		try {
			if (!(new File(dbPath).exists())) {

				InputStream is = SystemSettingsHelper.mContext.getResources().getAssets().open(DB_NAME);
				FileOutputStream fos = new FileOutputStream(dbPath);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				Log.e("Database", "file:" + dbPath + "##dbfile not found");
			} else {
				Log.e("Database", "file:" + dbPath + "##dbfile found");
			}

			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbPath,
					null);
			return database;

		} catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
		return null;
	}

	public void closeDatabase() {
		this.mDatabase.close();

	}
}