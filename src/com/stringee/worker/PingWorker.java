package com.stringee.worker;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import java.util.Map;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;

/**
 *
 * @author Andy Dau
 */
public class PingWorker extends Thread {

	private boolean running;
	private static final int SLEEP = 1000;//ms

	private static final int FIRST_PING_AFTER = 240000;//ms
	private static final int PING_AGAIN_IF_NOT_PONG_AFTER = 10000;//ms

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger("PingWorker");

	@Override
	public void run() {
		running = true;

		while (running) {
			long startTime = System.currentTimeMillis();

			//do ping
			Map<Long, Client> clients = ClientManager.getInstance().getClients();
			for (Client client : clients.values()) {
				long idleTime = System.currentTimeMillis() - client.getLastActiveTime();
				long timePing = System.currentTimeMillis() - client.getLastPingTime();

				if (idleTime > FIRST_PING_AFTER && timePing > PING_AGAIN_IF_NOT_PONG_AFTER) {
					LOGGER.info("Send ping to client: " + client.getId() + ", ping count: " + client.getPingCount());

					AppPacket pingPacket = new AppPacket(AppServiceType.PING);
					client.receive(pingPacket);

					client.setLastPingTime(System.currentTimeMillis());
					client.increasePingCount();

					if (client.getPingCount() > 2) {
						int index = (int) client.getId();
						if (index < 0) {
							index = Integer.MAX_VALUE + index;
						}
						//
						AppJob job = new AppJob(index);
						job.setClient(client);
						job.setPacket(new AppPacket(AppServiceType.LOGOUT));
						AppWorker.pubJob(job);
					}
				}
			}

			long time = SLEEP - (System.currentTimeMillis() - startTime);
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
