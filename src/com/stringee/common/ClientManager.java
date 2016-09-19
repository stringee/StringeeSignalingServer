package com.stringee.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Andy Dau
 */
public class ClientManager {

	private final static Logger LOGGER = Logger.getLogger("ClientManager");

	private ClientManager() {
	}

	public static ClientManager getInstance() {
		return ClientManagerHolder.INSTANCE;
	}

	private static class ClientManagerHolder {

		private static final ClientManager INSTANCE = new ClientManager();
	}

	private final Map<Long, Client> clients = new ConcurrentHashMap<>();

	public void add(Client client) {
		clients.put(client.getId(), client);
		LOGGER.info("Client: " + client.getId() + " logged in, ip: " + client.getChannel().remoteAddress());
	}

	public void remove(Client client) {
		clients.remove(client.getId());
	}

	public Client get(long id) {
		return clients.get(id);
	}

	public Map<Long, Client> getClients() {
		return clients;
	}

}
