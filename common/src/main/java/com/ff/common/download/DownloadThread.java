package com.ff.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {
	private File saveFile;
	private String downUrl;
	private int block;
	private boolean isPause = false;

	public int threadId;
	private int downLength;
	private DownLoadFile downloader;

	int tryTime = 0;
	int MAX_RETRY_TIMES = 2;

	public DownloadThread(DownLoadFile downloader, String downUrl, File saveFile,
			int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}

	@Override
	public void run() {
		startDownloadChunk();
	}

	void startDownloadChunk(){
		InputStream inStream = null;
		RandomAccessFile threadfile = null;
		try {
			HttpURLConnection http = (HttpURLConnection) new URL(downUrl)
					.openConnection();
			http.setConnectTimeout( 20 * 1000 );
			http.setReadTimeout( 20 * 1000 );
			int startPos = block * (threadId - 1) + downLength;
			int endPos;
			if (threadId == downloader.getThreadNum()) {
				endPos = downloader.getFileSize();
			} else {
				endPos = block * threadId - 1;
			}
			http.setRequestProperty("Range", "bytes=" + startPos + "-"
					+ endPos);
			inStream = http.getInputStream();
			byte[] buffer = new byte[1024 * 10];
			int offset;
			threadfile = new RandomAccessFile(this.saveFile, "rw");
			threadfile.seek(startPos);
			while ( (offset = inStream.read(buffer)) > 0 ) {
				threadfile.write(buffer, 0, offset);
				downLength += offset;
				downloader.append(offset);

				if (isPause) {
//					downloader.Pause();
					return;
				}
			}
			downloader.checkDownloadFinish(this);
		} catch (Exception e) {
			if (tryTime < MAX_RETRY_TIMES) {
				tryTime++;
				startDownloadChunk();
			}else {
				downloader.Pause();
				MyDownloadManager.getInstance().downFail(downloader, "下载失败，请重试",
						true);
			}
		} finally {
			downloader.update(threadId, downLength);
			try {
				if (threadfile != null)
					threadfile.close();
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
			}
		}
	}

	public void pause() {
		isPause = true;
	}

}
