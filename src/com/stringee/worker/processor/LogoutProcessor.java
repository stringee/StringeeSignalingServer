package com.stringee.worker.processor;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import org.apache.log4j.Logger;
import static com.stringee.signaling.application.listener.SignalingServerHandler.CHANNEL_ATTACHMENT_ATTR;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppProcessorBase;

/**
 *
 * @author Andy Dau
 */
public class LogoutProcessor extends AppProcessorBase {

	private static final Logger LOGGER = Logger.getLogger("LogoutProcessor");

	@Override
	public void process(AppJob job) {
		Client client = job.getClient();
		AppPacket packet = job.getPacket();
		LOGGER.info("client: " + client.getId() + "; packet: " + packet);

		ClientManager.getInstance().remove(client);

		client.getChannel().attr(CHANNEL_ATTACHMENT_ATTR).set(null);
		client.getChannel().close();
	}

}
