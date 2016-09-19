package com.stringee.signaling.application.listener;

import com.stringee.application.protocol.codec.Decoder;
import com.stringee.application.protocol.codec.Encoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class SignalingServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;

	public SignalingServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}

		pipeline.addLast(new Decoder());
		pipeline.addLast(new Encoder());

		pipeline.addLast(new SignalingServerHandler(sslCtx != null));
	}
}
