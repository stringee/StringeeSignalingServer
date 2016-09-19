package com.stringee.worker.processor;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppProcessorBase;

/**
 *
 * @author Andy Dau
 */
public class CallByeProcessor extends AppProcessorBase {

	private static final Logger LOGGER = Logger.getLogger("CallByeProcessor");

	@Override
	public void process(AppJob job) {
		Client client = job.getClient();
		AppPacket packet = job.getPacket();
		LOGGER.info("client: " + client.getId() + "; packet: " + packet);

		packet.decodeJson();

		long to = packet.getFieldLong("to");
		long callId = packet.getFieldLong("callId");

		Client toClient = ClientManager.getInstance().get(to);

		if (toClient != null) {
			AppPacket statusPacket = new AppPacket(AppServiceType.CALL_BYE);
			statusPacket.setField("from", client.getId());
			statusPacket.setField("callId", callId);
			toClient.receive(statusPacket);
		}
	}

}
