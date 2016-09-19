package com.stringee.signaling.application.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import javax.net.ssl.KeyManagerFactory;
import org.apache.log4j.Logger;

public final class SignalingServer {

	private final static Logger LOGGER = Logger.getLogger("SignalingServer");

	private ServerBootstrap b;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public void start(int port, boolean useSsl, boolean sslDevelopmentMode, String cerFilePath, String cerPassword) throws Exception {
		SslContext sslCtx = null;
		if (useSsl) {
			if (sslDevelopmentMode) {
				//self signed certificate
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			} else {
				//CA certificate
				KeyManagerFactory kmf = SSLContextCreator.createKey(cerFilePath, cerPassword);
				sslCtx = SslContextBuilder.forServer(kmf).build();
			}
		}

		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new SignalingServerInitializer(sslCtx));

		b.bind(port).sync();

		if (useSsl) {
			LOGGER.info("Server is running on: " + port + " (SSL/TLS); sslDevelopmentMode: " + sslDevelopmentMode);
		} else {
			LOGGER.info("Server is running on: " + port);
		}
	}

	public void stop() {
		LOGGER.info("Server is shutting down...");
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
