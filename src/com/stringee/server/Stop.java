package com.stringee.server;

import com.stringee.threadpool.ThreadPoolWorker;

/**
 *
 * @author Andy Dau
 */
public class Stop {

	public static void main(String[] args) {
		Start.signalingServer.stop();
		Start.pingWorker.setRunning(false);

		ThreadPoolWorker.stopAllWorkers();
	}
}
