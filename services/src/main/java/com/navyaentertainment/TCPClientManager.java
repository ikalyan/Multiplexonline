package com.navyaentertainment;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.grizzly.CloseListener;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.ICloseType;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;

import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;

public class TCPClientManager implements CloseListener<TCPNIOConnection, ICloseType>{
	
	private static TCPClientManager instance;
	Vector<RTPTCPClient> clients = new Vector<RTPTCPClient>(); 
	ArrayList<NetworkInterface> nets = new ArrayList<NetworkInterface>();
	ArrayList<InetAddress> addresses = null;
	Interfaces interfaces;
	Vector<RTPTCPClient> readyClients = new Vector<RTPTCPClient>();
	Vector<RTPTCPClient> connectedClients = new Vector<RTPTCPClient>();
	Vector<RTPTCPClient> retryClients = new Vector<RTPTCPClient>();
	ConcurrentHashMap<Connection<TCPNIOConnection>, RTPTCPClient> connectionMap = new ConcurrentHashMap<Connection<TCPNIOConnection>, RTPTCPClient>();
	private ArrayList<Integer> missingPackets = null;
	
	Thread connectionManager = null;
	
	private TCPClientManager() {
		interfaces = new Interfaces();
		nets = interfaces.getNetworkInterfaces();
		connectionManager = new Thread(() -> {
	    	while(true) {
	    		try {
					Thread.sleep(5000);
//					retryConnections();
//					initiatePingRequests();
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    });
	}
	
	private void retryConnections() {
		synchronized (retryClients) {
			for (RTPTCPClient client : retryClients)
			try {
				if (client.getConnectionState() == RTPTCPClient.CONN_CLOSED) {
					client.connect();
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	private boolean initiatePingRequests() {
		if (connectedClients.size()>0) {
			RTPTCPClient client = connectedClients.get(0);
			long startTime = new Date().getTime();
			for (int i=0; i<1000 && ((new Date().getTime() - startTime) < 5000);) {
				TCPPingRequest request = new TCPPingRequest();
				try {
					if (client.sendPingRequest(request)) i++; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					client.closeConnetion();
					connectedClients.remove(client);
					retryClients.add(client);
					return true;
				}
			}
			connectedClients.remove(client);
			readyClients.add(client);
			return true;
		}
		return false;
	}
	
	public void manageConnections() {
		initiateConnections();
	}
	static public TCPClientManager getInstance() {
		if (instance == null) instance = new TCPClientManager();
		return instance;
	}
	
	public void connectionSuccess(Connection<TCPNIOConnection> connection, RTPTCPClient client) {
		clients.remove(client);
		retryClients.remove(client);
		connectedClients.remove(client);
		clients.add(client);
		connectedClients.add(client);
		connectionMap.put(connection, client);
		connection.removeCloseListener(this);
		connection.addCloseListener(this);
	}
	
	public void failConnection(RTPTCPClient client) {
		retryClients.remove(client);
		retryClients.add(client);
	}
	
	public void initiateConnections() {
		for (NetworkInterface netint: interfaces.getNetworkInterfaces()) {
			RTPTCPClient client = new RTPTCPClient(netint);
			try {
				client.connect();
//				clients.add(client);
//				connectedClients.add(client);
//				connectionMap.put(client.getConnection(), client);
//				client.getConnection().addCloseListener(this);
			} catch (Exception e) {
				retryClients.add(client);
			}
		}
		
		try {
			Thread.sleep(5000); // Sleep for 10 seconds for all connections to be established
		} catch (Exception e) {
			// do nothing
		}
		System.out.println("Total connected clients are : " + connectedClients.size());
//			// Send request to determine time difference
//			for (RTPTCPClient client : connectedClients)  {
//				try {
//					if (client.getConnectionState() == RTPTCPClient.CONN_ESTABLISHED) {
//						TCPPingRequest packet = new TCPPingRequest();
//						client.sendPingRequest(packet);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			
//			try {
//				Thread.sleep(10000); 
//			} catch( Exception e) {
//				e.printStackTrace();
//			}
			
//			System.out.println("ALL CONNECTIONS ESTABLISHED -- SEDING PINQ REQUESTS");
//			int i=0;
//			long start = new Date().getTime();
//			while(i<10000 && (new Date().getTime() - start) < 20000 ) {
//				//i += sendPingRequestsCheckCanWrite();
//				i += sendPingRequestLeastPendingRequests(i);
//				
//			}	
		connectionManager.start();
	}
	
	private int sendPingRequestLeastPendingRequests(int count) {
		int result =0;
		RTPTCPClient cli = null;
		int pendingReqs = 10000;
		synchronized (connectedClients) {
			for (RTPTCPClient client : connectedClients) {
				
				if (pendingReqs >= client.getPendingRequestCount()) {
					cli = client;
					pendingReqs = cli.getPendingRequestCount();
				}
			}
		}
		try {
			if (cli != null) {
				TCPPingRequest packet = new TCPPingRequest();
				if (cli.sendPingRequest(packet)) result++;
				//result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			//Thread.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void registerPingResponse(Connection<TCPNIOConnection> connection, TCPPingRequest request) {
		RTPTCPClient client = connectionMap.get(connection);
		client.insertPingRequest(request);
		if (client.getPingRequestSize() > 0 && client.getPingRequestSize()%(RTPTCPClient.maxPings/2) == 0)System.out.printf("PING COUNT: ARTT : CRTT : AREC : CREC : ARES : CRES : ADDRESS  :: %5d : %4d : %4d : %4d : %4d : %4d : %4d : %4d : %s\n", 
												client.getPingRequestSize(), 
												client.getAveragePingRTT(),
												request.getRoundtripTimeClient(),
												client.getAveragePingRecieveTime(), 
												request.getRecieveTime(),
												client.getAveragePingResponseTime(), 
												request.getResponseTime(), 
												client.getTotalReceivWindow(),
												connection.getLocalAddress());
	}
	
	public void registerRateControl(Connection<TCPNIOConnection> connection, TCPRateControl packet) {
		RTPTCPClient client  = connectionMap.get(connection);
		client.registerRateControlInKbps(packet);
	}

	@Override
	public void onClosed(TCPNIOConnection connection, ICloseType arg1) throws IOException {
		System.out.println("Connection Closed " + arg1);
		System.out.println("Connection " + connection.getPeerAddress() + " " + connection.getLocalAddress());
		RTPTCPClient client = connectionMap.get(connection);
		client.markConnectionAsClosed();
		retryClients.add(client);
		connectionMap.remove(connection);
	}
	
	synchronized void setClientMissingPackets(ArrayList<Integer> missingPackets) {
		if (this.missingPackets == null) this.missingPackets = new ArrayList<Integer>();
		for (Integer seq : missingPackets) {
			this.missingPackets.add(seq);
		}
		System.out.println("Client Manager Missing packets :"  + this.missingPackets);
	}
	
	synchronized ArrayList<Integer> getMissingPackets() {
		if (this.missingPackets == null) this.missingPackets = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (Integer seq : missingPackets) {
			result.add(seq);
		}
		return result;
	}
	
	synchronized void clearMissingPacket() {
		if (missingPackets != null) missingPackets.clear();
		System.out.println("Client Manager clear missing packets");
	}

}
