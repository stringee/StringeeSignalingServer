package com.stringee.server;

import com.stringee.common.Config;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.listener.SignalingServer;
import com.stringee.worker.AppWorker;
import com.stringee.worker.PingWorker;

/**
 *
 * @author Andy Dau
 */
public class Start {

	private final static Logger LOGGER = Logger.getLogger("Start");

	public static SignalingServer signalingServer;
	public static PingWorker pingWorker;

	public static void main(String[] args) {
		try {
			Config.loadConfig();

			//create workers for process imcoming packet from client
			for (int i = 0; i < Config.number_of_worker; i++) {
				AppWorker worker = new AppWorker("app-worker-" + i, 120);
				worker.start();
			}

			pingWorker = new PingWorker();
			pingWorker.start();

			signalingServer = new SignalingServer();
			signalingServer.start(Config.listening_port, Config.use_ssl, Config.ssl_development_mode, Config.cer_file_path, Config.cer_file_password);
		} catch (Exception ex) {
			LOGGER.error(ex, ex);
			System.exit(1);
		}
//
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException ex) {
//			java.util.logging.Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		Stop.main(args);
	}

}
