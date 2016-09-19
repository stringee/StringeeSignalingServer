package com.stringee.threadpool;

/**
 *
 * @author Andy Dau
 */
public abstract class ThreadPoolJob {

	private int workerChooseIndex;
	protected boolean timedout = false;

	/**
	 *
	 * @param workerChooseIndex use this index to choose worker, if index < 0 so worker will choose random
	 */
	public ThreadPoolJob(int workerChooseIndex) {
		this.workerChooseIndex = workerChooseIndex;
	}

	public int getWorkerChooseIndex() {
		return workerChooseIndex;
	}

	public void setWorkerChooseIndex(int workerChooseIndex) {
		this.workerChooseIndex = workerChooseIndex;
	}

	public abstract void run();

	public abstract void timeout();
}
