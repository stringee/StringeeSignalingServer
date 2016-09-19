package com.stringee.signaling.application.listener;

import com.stringee.util.FileByteUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;

/**
 *
 * @author Andy Dau
 */
public class SSLContextCreator {

	public static KeyManagerFactory createKey(String keystoreFile, String keystorePassword) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");

		File original = new File(keystoreFile);
		byte[] keyCert = FileByteUtil.toByteArray(new FileInputStream(original));

		ks.load(new ByteArrayInputStream(keyCert), keystorePassword.toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keystorePassword.toCharArray());

		return kmf;
	}

	public static SslContext createClientSslContext() throws SSLException {
		return SslContextBuilder.forClient().sslProvider(SslProvider.JDK).build();
	}

}
