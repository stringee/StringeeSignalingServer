package com.stringee.common;

import io.netty.channel.Channel;
import java.util.concurrent.atomic.AtomicLong;
import com.stringee.signaling.application.protocol.packet.AppPacket;

/**
 *
 * @author Andy Dau
 */
public class Client {

	private static final AtomicLong ID_GENERATOR = new AtomicLong(15500);

	private final long id;
	private Channel channel;

	private long lastActiveTime = System.currentTimeMillis();
	private long lastPingTime = System.currentTimeMillis();
	private int pingCount = 0;

	public Client() {
		id = ID_GENERATOR.incrementAndGet();
	}

	public void receive(AppPacket packet) {
		channel.writeAndFlush(packet);
	}

	public long getId() {
		return id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTimeNow() {
		this.lastActiveTime = System.currentTimeMillis();
		pingCount = 0;
	}

	public long getLastPingTime() {
		return lastPingTime;
	}

	public void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}

	public int getPingCount() {
		return pingCount;
	}

	public void increasePingCount() {
		this.pingCount++;
	}

}
