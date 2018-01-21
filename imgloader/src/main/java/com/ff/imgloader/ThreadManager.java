package com.ff.imgloader;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
	private static ThreadPoolExecutor sExecutor = null;
	private static BlockingQueue<Runnable> sWorkQueue = null;
	private static ThreadManager instance;
	private HashMap<String, TaskIdRunnable> taskMap = new HashMap<String, TaskIdRunnable>();
	private BlockingQueue<TaskIdRunnable> waitTaskQueue = new LinkedBlockingQueue<TaskIdRunnable>();

	private boolean addTaskCache(String taskId, TaskIdRunnable r) {
		if (!taskMap.containsKey(taskId)) {
			taskMap.put(taskId, r);
			return true;
		}
		return false;
	}

	public static synchronized ThreadManager getInstance() {
		if (instance == null)
			instance = new ThreadManager();
		return instance;
	}

	public ThreadManager() {
		sWorkQueue = new LinkedBlockingQueue<>();
		sExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60,
				TimeUnit.SECONDS, sWorkQueue,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public synchronized boolean remove(String taskId) {
		boolean res = false;
		if (taskMap.get(taskId) != null) {
			res = sExecutor.remove(taskMap.get(taskId));
			taskMap.remove(taskId);
		}
		for (TaskIdRunnable r : waitTaskQueue) {
			if (!taskMap.containsKey(r.getTaskId())) {
				if (addTaskCache(r.getTaskId(), r)) {
					waitTaskQueue.remove(r);
//					 Log.i("bb", taskId + "---for");
					execute(r);
					break;
				}
			}
		}
		return res;
	}

	public void execute(Runnable command) {
		try {
			sExecutor.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean execute(String taskId, TaskIdRunnable command) {
		try {
			if (addTaskCache(taskId, command)) {
				sExecutor.execute(command);
				return true;
			} else {
				waitTaskQueue.put(command);
//				 Log.i("bb", taskId + "---workTaskMap.put");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
