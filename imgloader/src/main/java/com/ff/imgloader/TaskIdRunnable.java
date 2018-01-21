package com.ff.imgloader;

public abstract class TaskIdRunnable implements Runnable {

	String taskId;

	public TaskIdRunnable(String taskId) {
		super();
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
