package com.ff.common.download;

import android.preference.PreferenceManager;

import com.ff.common.ImmediatelyToast;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.SendDownloadReport;
import com.ff.common.model.DownloadTaskBean;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyDownloadManager implements SendDownloadReport.DownloadTaskStatusObserver {
	public static MyDownloadManager instance;
	public static ExecutorService pool;
	private LinkedHashMap<String, DownLoadFile> waitingDownLoadFilesList;// 等待
	public HashMap<String, DownLoadFile> downLoadingFilesList;// 正在下载
	public HashMap<String, DownLoadFile> retryDownLoadList;// 重试
	public HashMap<String, DownLoadFile> completeDownLoadList;// 完成
	public HashMap<String, DownLoadFile> pauseDownLoadList;// 暂停

	private MyDownloadManager() {
		pool = Executors.newCachedThreadPool();
		if (waitingDownLoadFilesList == null || downLoadingFilesList == null
				|| retryDownLoadList == null) {
			List<DownLoadFile> downtasklist = FileDB.getInstance()
					.getDownloadTaskList();
			waitingDownLoadFilesList = new LinkedHashMap();
			downLoadingFilesList = new HashMap();
			retryDownLoadList = new HashMap();
			completeDownLoadList = new HashMap();
			pauseDownLoadList = new HashMap();

			for (DownLoadFile d : downtasklist) {
				String state = d.getState();
				if (state.equals(MyConstants.WAITING)
						|| state.equals(MyConstants.INITWAITING)) {
					waitingDownLoadFilesList.put(d.package_name, d);
				} else if (state.equals(MyConstants.CONNECTING)
						|| state.equals(MyConstants.PAUSE)) {
					String string = ApplicationProxy.getInstance().getContext().getPackageName();
					if(!string.equals(d.getPackage_name())){
						d.setState(MyConstants.CONTINUE);
						pauseDownLoadList.put(d.package_name, d);
//						startDownLoadTask(d);
					}
				} else if (state.equals(MyConstants.RETRY)) {
					retryDownLoadList.put(d.package_name, d);
				} else if (state.equals(MyConstants.COMPLETE)) {
					completeDownLoadList.put(d.package_name, d);
				} else if (state.equals(MyConstants.CONTINUE)) {
					pauseDownLoadList.put(d.package_name, d);
				}
			}
		}
	}

	public synchronized static MyDownloadManager getInstance() {
		if (instance == null) {
			instance = new MyDownloadManager();
		}
		return instance;
	}

	// 请求下载
	public synchronized void requestDownload(DownloadTaskBean softItem, String from) {
		if(ToolUtils.isNull(softItem.package_name)){
			ImmediatelyToast.showLongMsg("包名不能为空");
			return;
		}
		if(isTaskDownloading(softItem.package_name)){
			ImmediatelyToast.showLongMsg("已在下载中");
			return;
		}
		if(isTaskInWaitingQueue(softItem.package_name)){
			ImmediatelyToast.showLongMsg("已在等待队列中,请稍候");
			checkDownLoadTask(null);
			return;
		}
		DownLoadFile downLoadFile = new DownLoadFile(softItem.task_id,softItem.package_name,softItem.app_name,
				softItem.app_logo, softItem.app_url,from,softItem.need_run_time);
		SendDownloadReport.sendDownloadReport(softItem.task_id,softItem.app_name, from, SendDownloadReport.TYPE_CLICK_DOWNLOAD, null);
		addDownTask(downLoadFile, true);
		checkDownLoadTask(downLoadFile);
	}

	public boolean isTaskDownloading(String appName){
		if (downLoadingFilesList.get(appName) != null) {
			return true;
		}
		return false;
	}

	public boolean isTaskInWaitingQueue(String appName){
		if (waitingDownLoadFilesList.get(appName) != null) {
			return true;
		}
		return false;
	}

	public synchronized void addDownTask(DownLoadFile item, boolean isNeedUpUI) {
		notifyViewUpdateState(item);
		FileDB fservice = FileDB.getInstance();
		fservice.insertFileDown(item);
	}

	public void downFail(DownLoadFile item, String hitStr, boolean isNeedUpUI) {
		if(item.isPause)return;
		item.setState(MyConstants.RETRY);
		String id = item.package_name;
		FileDB.getInstance().updateFileDown(item);
		retryDownLoadList.put(id, item);
		downLoadingFilesList.remove(id);
		checkDownLoadTask(null);
		notifyViewUpdateState(item);
		ToolUtils.showmsg(hitStr);
	}

	public synchronized void pauseDownload(String softId) {
		DownLoadFile item = downLoadingFilesList.get(softId);
		if (item != null) {
			item.Pause();
		}
	}

	public void deleteDownload(DownLoadFile item, boolean isNeedUpUI) {
		String softId = item.package_name;
		DownLoadFile downLoadFile = downLoadingFilesList.get(softId);
		if (downLoadFile != null) {
			downLoadFile.Pause();
			downLoadingFilesList.remove(softId);
			checkDownLoadTask(null);
		} else {
			if (waitingDownLoadFilesList != null) {
				waitingDownLoadFilesList.remove(softId);
			}
		}
		FileDB.getInstance().deleteFileDownbyId(softId);
		PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit()
				.remove(softId).commit();
		File file = new File(item.getDownLoadPath());
		if (file.exists()) {
			file.delete();
		}
		if (isNeedUpUI) {
			item.setState(MyConstants.NARMAL);
			notifyViewUpdateState(item);
		}
	}

	/**
	 * downLoadFile为null则检查是否可开始新任务，否则先增加任务再检查
	 *
	 * @param downLoadFile
	 */
	public void checkDownLoadTask(DownLoadFile downLoadFile) {

		// 判断下载任务是不是已经存在于等待列表了
		if (downLoadFile != null) {
			if (waitingDownLoadFilesList.get(downLoadFile.package_name) != null) {
				ImmediatelyToast.showLongMsg("已在等待列表");
				return;
			}
			waitingDownLoadFilesList
					.put(downLoadFile.package_name, downLoadFile);
		}

		// 不存在的话,并且正在下载的队列长度小于2
		if (downLoadingFilesList.size() < 10
				&& waitingDownLoadFilesList.size() > 0) {
			downLoadFile = waitingDownLoadFilesList.entrySet().iterator()
					.next().getValue();
			waitingDownLoadFilesList.remove(downLoadFile.package_name);
			startDownLoadTask(downLoadFile);
		}
	}

	public void startDownLoadTask(final DownLoadFile downLoadFile) {
		downLoadingFilesList.put(downLoadFile.package_name, downLoadFile);
		// 开始在在，并且下载完毕回调callback
		downLoadFile.start(new downOverCallback() {
			@Override
			public void callback() {
				downLoadingFilesList.remove(downLoadFile.package_name);
				checkDownLoadTask(null);
			}
		});
	}

	public DownLoadFile getDownLoadFileTask(String package_name) {
		if (downLoadingFilesList.get(package_name) != null) {
			return downLoadingFilesList.get(package_name);
		} else if (waitingDownLoadFilesList.get(package_name) != null) {
			return waitingDownLoadFilesList.get(package_name);
		} else if (completeDownLoadList.get(package_name) != null) {
			return completeDownLoadList.get(package_name);
		} else if (pauseDownLoadList.get(package_name) != null) {
			return pauseDownLoadList.get(package_name);
		} else if (retryDownLoadList.get(package_name) != null) {
			return retryDownLoadList.get(package_name);
		}
		return null;
	}

	public interface downOverCallback {
		void callback();
	}

	Map<String,DownloadObserver> downloadObservers = new HashMap<>();

	public void addObserver(String tag,DownloadObserver observer) {
		downloadObservers.put(tag,observer);
	}

	public void removeObserver(String tag) {
		downloadObservers.remove(tag);
	}

	public void notifyViewUpdateState(DownLoadFile item) {
		Map<String,DownloadObserver> localDownloadObservers = downloadObservers;
		for(Map.Entry<String, DownloadObserver> entry :localDownloadObservers.entrySet()){
			entry.getValue().notifyDownloadView(item);
		}
	}

	public interface DownloadObserver {
		void notifyDownloadView(DownLoadFile item);
	}

	public interface TaskStatusObserver {
		void onStatusChanged(String taskId, String status);
	}

	Map<String, TaskStatusObserver> mTaskStatusObservers = new HashMap<>(); /* Notice: It doesn't matter with network. */

	public void addTaskStatusObserver(String key, TaskStatusObserver observer) {
		mTaskStatusObservers.put(key, observer);
	}

	public void removeTaskStatusObserver(String key) {
		mTaskStatusObservers.remove(key);
	}

	@Override
	public void onStatusChanged(String taskId, String status) {
		Map<String, TaskStatusObserver> observers = mTaskStatusObservers;
		for(Map.Entry<String, TaskStatusObserver> entry : observers.entrySet()){
			entry.getValue().onStatusChanged(taskId, status);
		}
	}
}
