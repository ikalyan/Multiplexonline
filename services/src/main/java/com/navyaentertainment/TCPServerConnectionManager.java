package com.navyaentertainment;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;


public class TCPServerConnectionManager {

	private static final Logger logger = Logger.getLogger(TCPServerConnectionManager.class.getName());
	
	private static TCPServerConnectionManager instance;
	Vector<RTPTCPServerConnection> connections = new Vector<RTPTCPServerConnection>(); 
	ConcurrentHashMap<Connection<TCPNIOConnection>, RTPTCPServerConnection> connectionMap = new ConcurrentHashMap<Connection<TCPNIOConnection>, RTPTCPServerConnection>();

	private TCPServerConnectionManager() {
	}
	
	static public TCPServerConnectionManager getInstance() {
		if (instance == null) {
			synchronized (TCPServerConnectionManager.class) {
				if (instance == null) {
					instance = new TCPServerConnectionManager();
				}
			}
		}
		return instance;
	}
	
	public RTPTCPServerConnection getServerConnection(Connection<TCPNIOConnection> connection) {
		return connectionMap.get(connection);
	}
	
	public void connectionAccept(Connection<TCPNIOConnection> connection) {
		logger.info("Accept : No of connections " + connections.size());
		RTPTCPServerConnection serverConnection = new RTPTCPServerConnection(connection);
		synchronized (TCPServerConnectionManager.class) {
			connections.add(serverConnection);
			connectionMap.put(connection, serverConnection);
		}
		logger.info("Accept : No of connections " + connections.size());
	}
	
	public void connectionClose(Connection<TCPNIOConnection> connection) {
		logger.info("Close : No of connections " + connections.size());
		synchronized (TCPServerConnectionManager.class) {
			RTPTCPServerConnection serverConnection = connectionMap.get(connection);
			if (serverConnection != null) connections.remove(serverConnection);
			connectionMap.remove(connection);
		}
		logger.info("Close : No of connections " + connections.size());
	}

}
