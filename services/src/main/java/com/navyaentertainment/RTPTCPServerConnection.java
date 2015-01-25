package com.navyaentertainment;

import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;

public class RTPTCPServerConnection {
	private static final Logger logger = Logger.getLogger(RTPTCPServerConnection.class.getName());
	private Connection<TCPNIOConnection> connection = null;
	
	private static Object lock = new Object();
	
	private short pingRequests = 0;
	public long packetCount;
	public long totalBytesRecieved;
	public short byesPerSecond;
	public short averageRecieveTimeBetweenPackets;
	public short avarageSendTimeBetweenPackets;
	
	private long lastPacketInterval = 0;
	private short lastIntervalPackets = 0;
	private short lastIntervalBytes = 0;
	
	private long currentPacketInterval = 0;
	private short currentIntervalPackets = 0;
	private short currentIntervalBytes = 0;
	
	public RTPTCPServerConnection(Connection<TCPNIOConnection> connection) {
		this.connection = connection;
	}

	public void registerPacket(TCPCommPacket packet) {
		synchronized (lock) {
			long timeInterval = packet.recieveTime/1000;
			if (timeInterval != currentPacketInterval) {
				lastPacketInterval = currentPacketInterval;
				lastIntervalBytes = currentIntervalBytes;
				lastIntervalPackets = currentIntervalPackets;
				currentPacketInterval = timeInterval;
				currentIntervalPackets = 0;
				currentIntervalBytes = 0;
			}
			if (TCPCommPacket.TYPE_PING == packet.type) {
				pingRequests++;
			} else if (TCPCommPacket.TYPE_RTP == packet.type) {
				packetCount++;
				totalBytesRecieved+= packet.messageSize;
				currentIntervalBytes+= packet.messageSize;
				currentIntervalPackets++;
			}
		}
		
	}
	
	public RTPTCPServerConnectionInfo getRTPTCPServerConnectionInfo() {
		RTPTCPServerConnectionInfo info = new RTPTCPServerConnectionInfo();
		synchronized (lock) {
			info.byesPerSecond = lastIntervalBytes;
			info.packetCount = packetCount;
			info.totalBytesRecieved = totalBytesRecieved;
		}
		return info;
	}
}
