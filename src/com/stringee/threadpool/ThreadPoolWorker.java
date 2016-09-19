package com.stringee.threadpool;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Andy Dau
 */
public class ThreadPoolWorker extends Thread {

	private static final Logger LOGGER = Logger.getLogger("ThreadPoolWorker");
	private static final Timer TIMER = new HashedWheelTimer();
	/**
	 * Worker class name <--> list workers
	 */
	public static final Map<String, CopyOnWriteArrayList<ThreadPoolWorker>> MAP_LIST_WORKERS = new HashMap<>();
	private static final Random RANDOM = new Random();

	private LinkedBlockingQueue<ThreadPoolJob> jobQueue;
	private boolean running = true;
	private int processJobTimeOut = 0;//second
	private int replaceTime = 0;
	private String orgName;

	/**
	 * Tao 1 worker
	 *
	 * @param name Ten cua worker
	 * @param processJobTimeOut Thoi gian toi' da thuc hien cong viec (tinh bang giay)
	 */
	public ThreadPoolWorker(String name, int processJobTimeOut) {
		super(name);
		this.orgName = name;
		this.jobQueue = new LinkedBlockingQueue<>();
		this.processJobTimeOut = processJobTimeOut;

		CopyOnWriteArrayList<ThreadPoolWorker> listWorkers = MAP_LIST_WORKERS.get(this.getClass().getName());
		if (listWorkers == null) {
			listWorkers = new CopyOnWriteArrayList<>();
			MAP_LIST_WORKERS.put(this.getClass().getName(), listWorkers);
		}

		LOGGER.debug("created worker: " + this.getClass().getName());

		listWorkers.add(this);
	}

	@Override
	public void run() {
		LOGGER.info(this.getName() + " is started");
		while (running) {
			try {
				final ThreadPoolJob job = jobQueue.take();

				Timeout timeout = null;
				if (processJobTimeOut > 0) {
					//neu can` giam' sat thoi gian chay
					timeout = TIMER.newTimeout(new TimerTask() {

						@Override
						public void run(Timeout tmt) throws Exception {
							job.timeout();

							//destroy job
							destroyWorkerAndAddNew();
						}
					}, processJobTimeOut, TimeUnit.SECONDS);
				}

				try {
					job.run();
				} catch (Exception ex) {
					LOGGER.error("Error when process job", ex);
				}

				if (processJobTimeOut > 0) {
					timeout.cancel();
				}
			} catch (InterruptedException ex) {
				if (running) {
					LOGGER.error(ex, ex);
				}
			}
		}
	}

	public static void pubJob(ThreadPoolJob job, Class workerClass) {
		try {
			CopyOnWriteArrayList<ThreadPoolWorker> listWorkers = MAP_LIST_WORKERS.get(workerClass.getName());

			LOGGER.debug("name: " + workerClass.getName() + "; size: " + listWorkers.size());

			int i;
			if (job.getWorkerChooseIndex() > -1) {
				i = Math.abs(job.getWorkerChooseIndex()) % listWorkers.size();
			} else {
				i = RANDOM.nextInt(listWorkers.size());
			}

			ThreadPoolWorker worker = listWorkers.get(i);

			worker.getJobQueue().put(job);
		} catch (Exception ex) {
			LOGGER.error("error in exe job", ex);
		}
	}

	public void stopWorker() {
		this.running = false;
		this.interrupt();
	}

	public static void stopAllWorkers() {
		for (Map.Entry<String, CopyOnWriteArrayList<ThreadPoolWorker>> entry : MAP_LIST_WORKERS.entrySet()) {
			CopyOnWriteArrayList<ThreadPoolWorker> listWorkers = entry.getValue();
			for (ThreadPoolWorker worker : listWorkers) {
				worker.stopWorker();
			}
		}
	}

	private void destroyWorkerAndAddNew() {
		LOGGER.info("destroyWorker: " + this.getName() + "; class name: " + this.getClass().getName());

		CopyOnWriteArrayList<ThreadPoolWorker> listWorkers = MAP_LIST_WORKERS.get(this.getClass().getName());
		listWorkers.remove(this);

		this.setRunning(false);
		this.interrupt();

		ThreadPoolWorker newThreadPoolWorker = null;

		try {
			Class<?> clazz = Class.forName(this.getClass().getName());
			Constructor<?> ctor = clazz.getConstructors()[0];
			newThreadPoolWorker = (ThreadPoolWorker) ctor.newInstance(new Object[]{this.getName(), processJobTimeOut});

			newThreadPoolWorker.setReplaceTime(replaceTime + 1);
			newThreadPoolWorker.setOrgName(orgName);
			newThreadPoolWorker.setName(orgName + "_new_" + newThreadPoolWorker.getReplaceTime());
			newThreadPoolWorker.setJobQueue(jobQueue);

			newThreadPoolWorker.start();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | ClassNotFoundException ex) {
			LOGGER.error(ex, ex);
		}
	}

	public LinkedBlockingQueue<ThreadPoolJob> getJobQueue() {
		return jobQueue;
	}

	public void setJobQueue(LinkedBlockingQueue<ThreadPoolJob> jobQueue1) {
		this.jobQueue = jobQueue1;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getReplaceTime() {
		return replaceTime;
	}

	public void setReplaceTime(int replaceTime) {
		this.replaceTime = replaceTime;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public int getProcessJobTimeOut() {
		return processJobTimeOut;
	}

	public void setProcessJobTimeOut(int processJobTimeOut) {
		this.processJobTimeOut = processJobTimeOut;
	}

}
