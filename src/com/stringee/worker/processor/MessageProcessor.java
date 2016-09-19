package com.stringee.worker.processor;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppProcessorBase;

/**
 *
 * @author Andy Dau
 */
public class MessageProcessor extends AppProcessorBase {

	private static final Logger LOGGER = Logger.getLogger("MessageProcessor");

	@Override
	public void process(AppJob job) {
		Client client = job.getClient();
		AppPacket packet = job.getPacket();
		packet.decodeJson();
		LOGGER.info("client: " + client.getId() + "; packet: " + packet);

		long to = packet.getFieldLong("to");
		JSONObject message = packet.getFieldJsonObject("message");
		long requestId = packet.optFieldLong("requestId", -1L);

		//res
		int r;
		AppPacket resPacket = new AppPacket(AppServiceType.MESSAGE_RESULT);

		Client toClient = ClientManager.getInstance().get(to);
		if (toClient == null) {
			r = 1;
		} else {
			r = 0;

			AppPacket toPacket = new AppPacket(AppServiceType.MESSAGE);
			toPacket.setField("from", client.getId());
			toPacket.setField("message", message);
			toClient.receive(toPacket);
		}

		if (requestId >= 0) {
			resPacket.setField("r", r);
			resPacket.setField("requestId", requestId);
			client.receive(resPacket);
		}
	}

}
