package com.stringee.worker.processor;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import com.stringee.common.Config;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppProcessorBase;

/**
 *
 * @author Andy Dau
 */
public class CallInviteProcessor extends AppProcessorBase {

	private static final Logger LOGGER = Logger.getLogger("CallInviteProcessor");
	private static final AtomicInteger NAT_SERVER_CHOOSER = new AtomicInteger(0);

	@Override
	public void process(AppJob job) {
		Client client = job.getClient();
		AppPacket packet = job.getPacket();
		LOGGER.info("client: " + client.getId() + "; packet: " + packet);

		packet.decodeJson();

		long to = packet.getFieldLong("to");
		long callId = packet.getFieldLong("callId");
		//
		int status;
		String statusText;

		//choose NAT server
		int index = Math.abs(NAT_SERVER_CHOOSER.incrementAndGet() % Config.nat_servers.size());
		LOGGER.info("nat server index: " + index);
		InetSocketAddress natServer = Config.nat_servers.get(index);

		Client toClient = ClientManager.getInstance().get(to);
		if (toClient == null) {
			status = 404;
			statusText = "Not Found";
		} else if (to == client.getId()) {
			status = 400;
			statusText = "Bad Request";
		} else {
			status = 100;
			statusText = "Trying";

			//send packet to Callee
			AppPacket invite = new AppPacket(AppServiceType.CALL_INVITE);
			invite.setField("from", client.getId());
			invite.setField("callId", callId);
			invite.setField("natServerIp", natServer.getHostString());
			invite.setField("natServerPort", natServer.getPort());
			toClient.receive(invite);
		}

		//send response to Caller
		AppPacket res = new AppPacket(AppServiceType.CALL_STATUS_CHANGE);
		res.setField("status", status);
		if (status == 100) {
			res.setField("natServerIp", natServer.getHostString());
			res.setField("natServerPort", natServer.getPort());
		}
		res.setField("from", to);
		res.setField("callId", callId);
		res.setField("statusText", statusText);
		client.receive(res);

		LOGGER.info(job.getPacket());
	}

}
