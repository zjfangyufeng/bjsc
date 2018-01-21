package com.ff.common.download;

import com.ff.common.ImmediatelyToast;
import com.ff.common.LogUtil;
import com.ff.common.SdMemoryStatus;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.SendDownloadReport;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class DownLoadFile {
	public String softId, package_name,app_name, iconUrl,from;
	public int need_run_time;
	private String downLoadUrl;
	private String downLoadPath;
	Object key = new Object();

	public DownLoadFile(String softId,String package_name,String app_name, String iconUrl, String downLoadUrl,String from,int need_run_time) {
		this.softId = softId;
		this.package_name = package_name;
		this.app_name = app_name;
		this.iconUrl = iconUrl;
		this.downLoadUrl = downLoadUrl;
		this.from = from;
		this.need_run_time = need_run_time;
	}

    int alreadyDownSize;
    int progresspercentage;
	private volatile String state = MyConstants.INITWAITING;
	double fileSize;

	private final long MegaByte = 1048576;
	int chunks = 16;
	Map<Integer, Integer> data;

	MyDownloadManager.downOverCallback callback;

	public void start(MyDownloadManager.downOverCallback callback) {
		this.callback = callback;
		MyDownloadManager.pool.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (key){
					if(isPause)return;
					if(isDownloading()){
						ImmediatelyToast.showLongMsg("已在下载中..");
						return;
					}
				}
				setState(MyConstants.CONNECTING);
				MyDownloadManager.getInstance().notifyViewUpdateState(DownLoadFile.this);
				if (!makeTmpPath(package_name)) {
					MyDownloadManager.getInstance().downFail(DownLoadFile.this,
							"内存不足，请清理后重试", true);
					return;
				}

				File saveFile = new File(downLoadPath);
				if (!saveFile.exists()) {
					FileDB.getInstance().deleteFileDownChunksLog(package_name);
				}

				if (!saveFile.getParentFile().exists()) {
					saveFile.getParentFile().mkdirs();
				}

				if(isPause)return;
				if (!initDownLoadFileSize()) {
					MyDownloadManager.getInstance().downFail(DownLoadFile.this,
							"文件大小有误，下载失败，请检查网络通畅和下载链接正确", true);
					return;
				}

				convertChunks();

				if(alreadyDownSize == 0){
					saveFile.delete();
				}
				if(isPause)return;
				setState(MyConstants.PAUSE);
				FileDB.getInstance().updateFileDownIncludePathAndUrl(DownLoadFile.this);
				MyDownloadManager.getInstance().notifyViewUpdateState(DownLoadFile.this);

				startDownloadChunks();

			}
		});
	}

	public void checkDownloadFinish(DownloadThread downloadThread){
		workList[downloadThread.threadId-1] = null;

		for (DownloadThread dt : workList){
			if ( dt != null)
				return;
		}

		if (alreadyDownSize >= fileSize) {
            setState(MyConstants.COMPLETE);
			FileDB.getInstance().updateFileDown(
					DownLoadFile.this);
			MyDownloadManager.getInstance().completeDownLoadList.put(package_name,
					DownLoadFile.this);
			MyDownloadManager.getInstance().retryDownLoadList.remove(package_name);
			ToolUtils.installApk(getDownLoadPath());
			MyDownloadManager.getInstance()
					.notifyViewUpdateState(DownLoadFile.this);
			callback.callback();
			SendDownloadReport.sendDownloadReport(softId, app_name, from, SendDownloadReport.TYPE_DOWNLOAD_COMPLETE, null);
		} else {
			MyDownloadManager.getInstance().downFail(DownLoadFile.this, "文件不完整",
					true);
			LogUtil.i(alreadyDownSize+"文件不完整"+fileSize);
		}
		FileDB.getInstance().deleteFileDownChunksLog(package_name);

	}

	private void convertChunks(){
		int MaximumUserCHUNKS = chunks/2;
		chunks = 1;

		for (int f=1 ; f <=MaximumUserCHUNKS ; f++)
			if (fileSize > MegaByte*f)
				chunks = f*2;

		data = FileDB.getInstance().getFileDownChunkLog(this.package_name);
		if (data.size() == chunks) {
			for (int i = 0; i < chunks; i++) {
				alreadyDownSize += data.get(i + 1);
			}
		} else {
			data.clear();
			for (int i = 0; i < chunks; i++) {
				data.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
			}
			FileDB.getInstance().deleteFileDownChunksLog(this.package_name);
			FileDB.getInstance().saveFileDownChunkLog(this.package_name, data);
		}

		workList = new DownloadThread[chunks];

	}

	public DownloadThread[] workList;

	private void startDownloadChunks() {
		for (int i = 0; i < workList.length; i++) {
			int downLength = data.get(i + 1);
			int block = (int) (fileSize/chunks)+1;
			if(i==chunks-1){
				block = (int) (fileSize - (chunks-1)*block);
			}
			if (downLength < block) {
				workList[i] = new DownloadThread(DownLoadFile.this,
						downLoadUrl,
						new File(downLoadPath), block, downLength, i + 1);
				if(isPause)return;
				workList[i].start();

			} else {
				workList[i] = null;
			}
		}
	}

	public synchronized void append(int size) {
		alreadyDownSize += size;
		int oldpercent = progresspercentage;
		progresspercentage = (int) (alreadyDownSize
				/ fileSize * 100);
		if (oldpercent != progresspercentage) {
//			state = MyConstants.PAUSE;
			MyDownloadManager.getInstance()
					.notifyViewUpdateState(DownLoadFile.this);
		}
	}

	public synchronized void update(int threadId, int pos) {
		data.put(threadId, pos);
		FileDB.getInstance().update(this.package_name, data);
	}

	public int getThreadNum() {
		return chunks;
	}

	public int getFileSize() {
		return (int) fileSize;
	}

	public volatile boolean isPause = false;

	public void Pause() {
		isPause = true;
        setState(MyConstants.CONTINUE);
		MyDownloadManager.getInstance()
				.notifyViewUpdateState(DownLoadFile.this);
		MyDownloadManager.getInstance().downLoadingFilesList
				.remove(package_name);
		MyDownloadManager.getInstance().retryDownLoadList
				.remove(package_name);
		MyDownloadManager.getInstance().completeDownLoadList
				.remove(package_name);
		MyDownloadManager.getInstance().pauseDownLoadList.put(package_name,
				DownLoadFile.this);

		if(workList!=null){
			for (DownloadThread dt : workList){
				if ( dt != null)
					dt.pause();
			}
		}

		FileDB.getInstance().updateFileDown(
				DownLoadFile.this);
		MyDownloadManager.getInstance().checkDownLoadTask(null);

	}

	public void deleteFile() {
		Pause();
		if(!ToolUtils.isNull(downLoadPath)){
			File file = new File(downLoadPath);
			if(file.exists()){
				file.delete();
			}
		}
	}

	private static final int MAX_REDIRECT_TIMES = 5;

	public boolean initDownLoadFileSize() {
		try {
			String connectUrl = downLoadUrl;
			HttpURLConnection conn = (HttpURLConnection) new URL(connectUrl)
                    .openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.connect();
			int redirectCount = 0;
			while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_TIMES) {
				connectUrl = conn.getHeaderField("Location");
				conn = (HttpURLConnection) new URL(connectUrl)
						.openConnection();
				conn.setConnectTimeout(20000);
				conn.setReadTimeout(20000);
				conn.connect();
				redirectCount++;
			}

			if (conn.getResponseCode() == 200) {
				downLoadUrl = connectUrl;
                fileSize = conn.getContentLength();
                return true;
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean makeTmpPath(String name) {
		String tempFilePath = null;
		if (ToolUtils.isSDcardExist()
				&& SdMemoryStatus.getSDFreeSize() >= ToolUtils.SDCARD_MIN_SPACE) {
			tempFilePath = ToolUtils.initSDcardDownloadPath();
		} else if (SdMemoryStatus.getAvailableInternalMemorySize() >= ToolUtils.MEMORY_MIN_SPACE) {
			tempFilePath = ApplicationProxy.getInstance().getContext().getFilesDir()+"/";
		}

		if (null != tempFilePath) {
			StringBuilder sb = new StringBuilder(tempFilePath);
			File file = new File(tempFilePath);
			if (!file.exists()) {
				boolean b = file.mkdirs();
				ToolUtils.chmodPath(file.getAbsolutePath());
				if (!b) {
					return false;
				}
			}
			sb.append("ID_");
			sb.append(name);
			sb.append(".apk");
			downLoadPath = sb.toString();
		} else {
			return false;
		}
		return true;
	}

	public int getProgresspercentage() {
		return progresspercentage;
	}

	public void setProgresspercentage(int progresspercentage) {
		this.progresspercentage = progresspercentage;
	}

	public String getDownLoadUrl() {
		return downLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}

	public String getDownLoadPath() {
		return downLoadPath;
	}

	public void setDownLoadPath(String downLoadPath) {
		this.downLoadPath = downLoadPath;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPackage_name() {
		return package_name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	@Override
	public String toString() {
		return "softId:"+softId+"  packagename:"+package_name+"  softname:"+app_name+"  need_run_time:"+need_run_time+"  downstatus:"+getState()+"  progress:"+progresspercentage;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof DownLoadFile){
			DownLoadFile item = (DownLoadFile) o;
			return item.package_name.equals(this.package_name);
		}
		if(o instanceof String){
			return this.package_name.equals(o);
		}
		return false;
	}

	public boolean isDownloading() {
		return MyConstants.PAUSE.equals(state) || MyConstants.CONNECTING.equals(state);
	}

	public boolean isWaiting() {
		return MyConstants.WAITING.equals(state) || MyConstants.INITWAITING.equals(state);
	}
}
