package com.stringee.worker;

import com.stringee.common.Client;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;
import com.stringee.threadpool.ThreadPoolJob;
import com.stringee.worker.processor.CallByeProcessor;
import com.stringee.worker.processor.CallInviteProcessor;
import com.stringee.worker.processor.CallStatusChangeProcessor;
import com.stringee.worker.processor.LogoutProcessor;
import com.stringee.worker.processor.MessageProcessor;
import com.stringee.worker.processor.PingProcessor;

/**
 *
 * @author Andy Dau
 */
public class AppJob extends ThreadPoolJob {

	private static final Logger LOGGER = Logger.getLogger("AppJob");
	private AppPacket packet;
	private Client client;

	private static final Map<Short, AppProcessorBase> MAP_PROCESSORS = new HashMap<>();

	static {
		MAP_PROCESSORS.put(AppServiceType.PING.getValue(), new PingProcessor());
		MAP_PROCESSORS.put(AppServiceType.LOGOUT.getValue(), new LogoutProcessor());
		MAP_PROCESSORS.put(AppServiceType.CALL_INVITE.getValue(), new CallInviteProcessor());
		MAP_PROCESSORS.put(AppServiceType.CALL_STATUS_CHANGE.getValue(), new CallStatusChangeProcessor());
		MAP_PROCESSORS.put(AppServiceType.CALL_BYE.getValue(), new CallByeProcessor());
		MAP_PROCESSORS.put(AppServiceType.MESSAGE.getValue(), new MessageProcessor());

	}

	public AppJob(int workerChooseIndex) {
		super(workerChooseIndex);
	}

	@Override
	public void run() {
		AppProcessorBase processor = MAP_PROCESSORS.get(packet.getService());
		if (processor != null) {
			processor.process(this);
		} else {
			LOGGER.error("Could not found processor to process packet: " + packet);
		}
	}

	@Override
	public void timeout() {
	}

	public AppPacket getPacket() {
		return packet;
	}

	public void setPacket(AppPacket packet) {
		this.packet = packet;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
