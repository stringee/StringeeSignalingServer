package com.stringee.worker.processor;

import com.stringee.common.Client;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppProcessorBase;

/**
 *
 * @author Andy Dau
 */
public class PingProcessor extends AppProcessorBase {

	private static final Logger LOGGER = Logger.getLogger("PingProcessor");

	@Override
	public void process(AppJob job) {
		Client client = job.getClient();
		AppPacket packet = job.getPacket();
		LOGGER.info("client: " + client.getId() + "; packet: " + packet);
	}

}
