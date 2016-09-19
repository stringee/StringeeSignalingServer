package com.stringee.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Andy Dau
 * @since Nov 7, 2011
 * @version 1.0
 */
public class Config {

	public static final String LOG_CONF_FILE_PATH = System.getProperty("user.dir") + File.separator + "config" + File.separator + "log.conf";
	public static final String APP_CONF_FILE_PATH = System.getProperty("user.dir") + File.separator + "config" + File.separator + "app.conf";
	private final static Logger LOGGER = Logger.getLogger("Config");

	//port config
	public static int listening_port;
	//SSL config
	public static boolean use_ssl;
	public static boolean ssl_development_mode;
	public static String cer_file_path;
	public static String cer_file_password;

	public static int number_of_worker;
	public static List<InetSocketAddress> nat_servers = new ArrayList<>();

	public static void loadConfig() {
		try {
			org.apache.log4j.PropertyConfigurator.configure(Config.LOG_CONF_FILE_PATH);

			Properties properties = new Properties();
			properties.load(new FileInputStream(APP_CONF_FILE_PATH));

			listening_port = Integer.valueOf(properties.getProperty("listening_port", "6695").trim());
			use_ssl = Boolean.valueOf(properties.getProperty("use_ssl").trim());
			ssl_development_mode = Boolean.valueOf(properties.getProperty("ssl_development_mode").trim());

			cer_file_path = properties.getProperty("cer_file_path").trim();
			cer_file_password = properties.getProperty("cer_file_password").trim();

			number_of_worker = Integer.valueOf(properties.getProperty("number_of_worker", "4").trim());

			//parse nat_servers config
			String[] _nat_servers = properties.getProperty("nat_servers").trim().split(";");
			for (String _nat_server : _nat_servers) {
				_nat_server = _nat_server.trim();
				String ip = _nat_server.split(":")[0].trim();
				int port = Integer.valueOf(_nat_server.split(":")[1].trim());

				nat_servers.add(new InetSocketAddress(ip, port));
			}
			LOGGER.info("NAT servers: " + nat_servers + "\n");

		} catch (IOException | NumberFormatException ex) {
			ex.printStackTrace();
			LOGGER.error(ex, ex);
		}
	}

}
