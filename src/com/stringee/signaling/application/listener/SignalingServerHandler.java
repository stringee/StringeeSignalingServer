package com.stringee.signaling.application.listener;

import com.stringee.common.Client;
import com.stringee.common.ClientManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;
import com.stringee.signaling.application.protocol.packet.AppServiceType;
import com.stringee.worker.AppJob;
import com.stringee.worker.AppWorker;

public class SignalingServerHandler extends SimpleChannelInboundHandler<AppPacket> {

	private final static Logger LOGGER = Logger.getLogger("SignalingServerHandler");
	public static final AttributeKey<Client> CHANNEL_ATTACHMENT_ATTR = AttributeKey.valueOf("CHANNEL_ATTACHMENT_ATTR");

	private final boolean useSsl;

	public SignalingServerHandler(boolean useSsl) {
		this.useSsl = useSsl;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		if (useSsl) {
			ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
				@Override
				public void operationComplete(Future<Channel> future) throws Exception {
					channelConnectComplete(ctx);
				}
			});
		} else {
			channelConnectComplete(ctx);
		}
	}

	private void channelConnectComplete(ChannelHandlerContext ctx) {
		if (!ctx.channel().isActive()) {
			return;
		}

		Client client = new Client();
		client.setChannel(ctx.channel());
		ctx.channel().attr(CHANNEL_ATTACHMENT_ATTR).set(client);

		ClientManager.getInstance().add(client);

		AppPacket packet = new AppPacket(AppServiceType.LOGIN);
		packet.setField("clientId", client.getId());
		client.receive(packet);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Client client = ctx.channel().attr(CHANNEL_ATTACHMENT_ATTR).get();
		LOGGER.info("channelInactive");
		if (client == null) {
			return;
		}

		int index = (int) client.getId();
		if (index < 0) {
			index = Integer.MAX_VALUE + index;
		}

		AppJob job = new AppJob(index);
		job.setClient(client);
		job.setPacket(new AppPacket(AppServiceType.LOGOUT));
		AppWorker.pubJob(job);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, AppPacket packet) throws Exception {
		Client client = ctx.channel().attr(CHANNEL_ATTACHMENT_ATTR).get();
		client.setLastActiveTimeNow();

		LOGGER.info(client.getId() + ", packet: " + packet);

		int index = (int) client.getId();
		if (index < 0) {
			index = Integer.MAX_VALUE + index;
		}

		AppJob job = new AppJob(index);
		job.setClient(client);
		job.setPacket(packet);
		AppWorker.pubJob(job);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
//		ctx.close();
	}
}
