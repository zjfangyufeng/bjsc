package com.ff.common.download;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class FileOpenHelper extends SQLiteOpenHelper {

	private static final String DBNAME = "downfile.db";

	private static FileOpenHelper mDatabaseHelper;

	private AtomicInteger mOpenCounter = new AtomicInteger();
	private SQLiteDatabase mDatabase;

	private FileOpenHelper(Context context) throws NameNotFoundException {
		super(context, DBNAME, null, context.getPackageManager()
				.getPackageInfo(context.getPackageName(),
						PackageManager.GET_CONFIGURATIONS).versionCode);
	}

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			mDatabase.close();
		}
	}

	public static FileOpenHelper getFileOpenHelper(Context c) {
		if (mDatabaseHelper == null) {
			try {
				mDatabaseHelper = new FileOpenHelper(c);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mDatabaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS filedown (id integer primary key autoincrement,"
				+ "softname text not null UNIQUE,"// 软件名称
				+ "softId text,"
				+ "sessionid text," // seesion id
				+ "downstatus text,"// 下载状态
				+ "packagename text,"// 包名
				+ "path text,"// 下载文件路径
				+ "url text,"// 下载路径
				+ "downfrom text,"// 下载来源
				+ "need_run_time INTEGER default 30,"
				+ "iconurl text,"// icon位置
				+ "progress INTEGER,"// 下载进度
				+ "display text,"// display
				+ "downloadpath text)");
		createDownloadReportTable(db);
		createFileDownlogTable(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 27) {
			db.execSQL("alter table filedown add column need_run_time INTEGER default 30");
		}
		if (oldVersion < 29) {
			createDownloadReportTable(db);
		}
		if (oldVersion < 32) {
			createFileDownlogTable(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	void createDownloadReportTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS download_report_log (id integer primary key autoincrement,"
				+ "task_id text not null ,"//UNIQUE
				+ "appName text,"
				+ "from_where text,"
				+ "status text)");
	}

	void createFileDownlogTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (softid text not null,threadid INTEGER, downlength INTEGER)");
	}


}
