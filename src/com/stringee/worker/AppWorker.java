package com.stringee.worker;

import org.apache.log4j.Logger;
import com.stringee.threadpool.ThreadPoolJob;
import com.stringee.threadpool.ThreadPoolWorker;

/**
 *
 * @author Andy Dau
 */
public class AppWorker extends ThreadPoolWorker {

	private static final Logger LOGGER = Logger.getLogger("AppWorker");

	public AppWorker(String name, int timeout) {
		super(name, timeout);
	}

	public static void pubJob(ThreadPoolJob threadPoolJob) {
		ThreadPoolWorker.pubJob(threadPoolJob, AppWorker.class);
	}

}
