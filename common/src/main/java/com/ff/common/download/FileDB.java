package com.ff.common.download;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileDB {
	private static FileDB instance;
	private FileOpenHelper fileHelper = null;

	public synchronized static FileDB getInstance() {
		if (instance == null)
			instance = new FileDB();
		return instance;
	}

	public FileDB() {
		fileHelper = FileOpenHelper.getFileOpenHelper(ApplicationProxy.getInstance().getContext());
	}

	public synchronized List<DownLoadFile> getDownloadTaskList() {
		SQLiteDatabase queryDb_f = fileHelper.openDatabase();
		Cursor cursor = queryDb_f.rawQuery("select * from filedown", null);
		List<DownLoadFile> list = new ArrayList<DownLoadFile>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				try {
					DownLoadFile downLoadFile = new DownLoadFile(
							cursor.getString(cursor.getColumnIndex("softId")),
							cursor.getString(cursor.getColumnIndex("packagename")),
							cursor.getString(cursor.getColumnIndex("softname")),
							cursor.getString(cursor.getColumnIndex("iconurl")),
							cursor.getString(cursor.getColumnIndex("downloadpath")),
							cursor.getString(cursor.getColumnIndex("downfrom")),
							cursor.getInt(cursor.getColumnIndex("need_run_time")));
					downLoadFile.setState(cursor.getString(cursor
							.getColumnIndex("downstatus")));
					downLoadFile.setProgresspercentage(cursor.getInt(cursor
							.getColumnIndex("progress")));
					downLoadFile.setDownLoadPath(cursor.getString(cursor
							.getColumnIndex("path")));
					downLoadFile.setDownLoadUrl(cursor.getString(cursor
							.getColumnIndex("url")));
					if(!ToolUtils.isNull(downLoadFile.package_name))
						list.add(downLoadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (cursor != null)
			cursor.close();
		fileHelper.closeDatabase();
		return list;
	}

	public synchronized void updateFileDown(DownLoadFile item) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL(
				"update filedown set downstatus=?,progress=? where packagename=?",
				new Object[] { item.getState(), item.getProgresspercentage(),
						item.package_name});
		fileHelper.closeDatabase();
	}

	public synchronized void updateFileDownIncludePathAndUrl(DownLoadFile item) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL(
				"update filedown set downstatus=?,path=?,url=?,progress=? where packagename=?",
				new Object[] { item.getState(), item.getDownLoadPath(),
						item.getDownLoadUrl(), item.getProgresspercentage(),
						item.package_name});
		fileHelper.closeDatabase();
	}

	public synchronized void insertFileDown(DownLoadFile item) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL(
				"insert or replace into filedown(softId,packagename,softname,downstatus,"
						+ "iconurl,downloadpath,downfrom,need_run_time)" + "values(?,?,?,?,?,?,?,?)",
				new Object[] { item.softId,item.package_name,item.app_name,item.getState(),item.getIconUrl(),
						item.getDownLoadPath(),item.from,item.need_run_time });
		fileHelper.closeDatabase();
	}

	public synchronized void deleteFileDownbyId(String packagename) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL("delete from filedown where packagename=?",
				new Object[] { packagename });
		fileHelper.closeDatabase();
	}

	public synchronized void insertDownloadReportLog(final String task_id,final String appName, final String from,
													 final String status) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL(
				"insert or replace into download_report_log(task_id,appName,from_where,status) values(?,?,?,?)",
				new Object[] { task_id,appName,from,status });
		fileHelper.closeDatabase();
	}

	public synchronized void deleteDownloadReportLog(FileDB.DownloadReportLogBean bean) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL("delete from download_report_log where task_id=? and status=?",
				new Object[] { bean.task_id,bean.status });
		fileHelper.closeDatabase();
	}

	public synchronized void deleteDownloadReportLog(String taskId) {
		SQLiteDatabase db_f = fileHelper.openDatabase();
		db_f.execSQL("delete from download_report_log where task_id=?",
				new Object[] { taskId });
		fileHelper.closeDatabase();
	}

	public synchronized List<DownloadReportLogBean> getDownloadReportLogList() {
		SQLiteDatabase queryDb_f = fileHelper.openDatabase();
		Cursor cursor = queryDb_f.rawQuery("select * from download_report_log", null);
		List<DownloadReportLogBean> list = new ArrayList();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				try {
					DownloadReportLogBean bean = new DownloadReportLogBean(
							cursor.getString(cursor.getColumnIndex("task_id")),
							cursor.getString(cursor.getColumnIndex("appName")),
							cursor.getString(cursor.getColumnIndex("from_where")),
							cursor.getString(cursor.getColumnIndex("status")));
					list.add(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (cursor != null)
			cursor.close();
		fileHelper.closeDatabase();
		return list;
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 *
	 * @return
	 */
	public synchronized Map<Integer, Integer> getFileDownChunkLog(String packagename) {
		SQLiteDatabase querydb_fl = fileHelper.openDatabase();
		Cursor cursor = querydb_fl.rawQuery(
				"select threadid, downlength from filedownlog where softid=?",
				new String[] { packagename });
		Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				data.put(cursor.getInt(0), cursor.getInt(1));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return data;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 *
	 * @param map
	 */
	public synchronized void saveFileDownChunkLog(String softid, Map<Integer, Integer> map) {
		SQLiteDatabase db_fl = fileHelper.openDatabase();
		db_fl.beginTransaction();

		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db_fl.execSQL(
						"insert into filedownlog(softid, threadid, downlength) values(?,?,?)",
						new Object[] { softid, entry.getKey(), entry.getValue() });
			}

			db_fl.setTransactionSuccessful();
		} finally {
			db_fl.endTransaction();
		}
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 *
	 * @param map
	 */
	public synchronized void update(String softid, Map<Integer, Integer> map) {
		SQLiteDatabase db_fl = fileHelper.openDatabase();
		db_fl.beginTransaction();

		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db_fl.execSQL(
						"update filedownlog set downlength=? where softid=? and threadid=?",
						new Object[] { entry.getValue(), softid, entry.getKey() });
			}

			db_fl.setTransactionSuccessful();
		} finally {
			db_fl.endTransaction();
		}
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 *
	 */
	public synchronized void deleteFileDownChunksLog(String softid) {
		SQLiteDatabase db_fl = fileHelper.openDatabase();
		db_fl.execSQL("delete from filedownlog where softid=?",
				new Object[] { softid });
	}

	public static class DownloadReportLogBean {
		public String task_id,appName,from, status;

		public DownloadReportLogBean(String task_id, String appName, String from, String status) {
			this.task_id = task_id;
			this.appName = appName;
			this.from = from;
			this.status = status;
		}
	}

}
