package com.navyaentertainment;

import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;

public class RTPTCPServerConnection {
	private static final Logger logger = Logger.getLogger(RTPTCPServerConnection.class.getName());
	private Connection<TCPNIOConnection> connection = null;
	
	
	private String clientInterface;
	private String clientAddress;
	
	private static Object lock = new Object();
	
	private short pingRequests = 0;
	public long packetCount;
	public long totalBytesRecieved;
	public long bytesPerSecond;
	public short averageRecieveTimeBetweenPackets;
	public short avarageSendTimeBetweenPackets;
	
	private long lastPacketInterval = 0;
	private short lastIntervalPackets = 0;
	private long lastIntervalBytes = 0;
	
	private long currentPacketInterval = 0;
	private short currentIntervalPackets = 0;
	private long currentIntervalBytes = 0;
	
	private long lastSendTime = 0;
	private long lastReceieveTime = 0;
	
	private short sendKbps = 0;
	private short recKbps = 0;
	private long sendTimeWindow = 0;
	private long receiveTimeWindow = 0;
	
	private long recentRateControl = 0;
	private MaxSizeHashMap<Short, Short> recentRecKbps = new MaxSizeHashMap<Short, Short>(4);
	private MaxSizeHashMap<Short, Short> recentSendKbps = new MaxSizeHashMap<Short, Short>(4);
	
	public RTPTCPServerConnection(Connection<TCPNIOConnection> connection) {
		this.connection = connection;
	}

	/**
	 * @param packet
	 * @return rateControl information to be sent back to the client.
	 */
	public long registerPacket(TCPCommPacket packet) {
		long result = 0;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeInterval = packet.sendTime/1000; // vs 1 second
		synchronized (lock) {
			if (currentPacketInterval == 0) {
				currentPacketInterval = timeInterval;
				lastSendTime = packet.sendTime;
				lastReceieveTime = packet.recieveTime;
			}
			if (timeInterval != currentPacketInterval) {
				lastPacketInterval = currentPacketInterval;
				lastIntervalBytes = currentIntervalBytes;
				lastIntervalPackets = currentIntervalPackets;
				
				sendTimeWindow = (packet.sendTime - lastSendTime);
				receiveTimeWindow = (packet.recieveTime - lastReceieveTime);
				lastSendTime = packet.sendTime;
				lastReceieveTime = packet.recieveTime;
				
				if (sendTimeWindow > 0 && receiveTimeWindow > 0) {
					
					recKbps = (short)(lastIntervalBytes*8/receiveTimeWindow);
					sendKbps = (short)((lastIntervalBytes*8)/(sendTimeWindow));
					
					recentRecKbps.put(recKbps, recKbps);
					recentSendKbps.put(sendKbps, sendKbps);
					recKbps = getAverageRecKbps();
					sendKbps = getAverageSendKbps();
					
					if (recentRateControl == 0) {
						recentRateControl = recKbps;
						result = recentRateControl;
					}
				}
				
				if (recKbps > 1.05*recentRateControl) {
					result = recKbps;
				} 

				long ratediff = (sendKbps - recKbps);
				long ratediffperc = 0;
				if (ratediff > 0) {
					if (recKbps > 0) ratediffperc = (ratediff*100)/recKbps;
					if (ratediffperc >= 10 && sendKbps > recentRateControl*1.1) result = recKbps;
				}
				
//				System.out.println("====== BITRATES REC:SEND:RATE DIFF :" + recKbps + " : " + sendKbps + " : " + ratediffperc);

				currentPacketInterval = timeInterval;
				currentIntervalPackets = 0;
				currentIntervalBytes = 0;
			}
			if (TCPCommPacket.TYPE_PING == packet.type) {
				pingRequests++;
				totalBytesRecieved+= packet.messageSize;
				currentIntervalBytes+= packet.messageSize;
				currentIntervalPackets++;
				if (clientAddress == null) {
					TCPPingRequest request = (TCPPingRequest) packet;
					clientAddress = request.connectionInfo.split(":")[1];
					clientInterface = request.connectionInfo.split(":")[0];
				}
			} else if (TCPCommPacket.TYPE_RTP == packet.type) {
				packetCount++;
				totalBytesRecieved+= packet.messageSize;
				currentIntervalBytes+= packet.messageSize;
				currentIntervalPackets++;
			}
		}
		if (result != 0) recentRateControl = result;
		return result;
		
	}
	
	private short getAverageRecKbps() {
		short result = 0;
		for (Short rate : recentRecKbps.values()) {
			result += rate;
		}
		return (short) (result/recentRecKbps.size());
	}
	
	private short getAverageSendKbps() {
		short result = 0;
		for (Short rate : recentSendKbps.values()) {
			result += rate;
		}
		return (short) (result/recentRecKbps.size());
	}
	
	public RTPTCPServerConnectionInfo getRTPTCPServerConnectionInfo() {
		RTPTCPServerConnectionInfo info = new RTPTCPServerConnectionInfo();
		synchronized (lock) {
			info.bytesPerSecond = lastIntervalBytes;
			info.packetCount = lastIntervalPackets;
			info.totalBytesRecieved = totalBytesRecieved;
			info.pingPacketsPerSecond =  lastIntervalPackets;
			info.pingRequestCount = pingRequests;
			info.recentRateControl = recentRateControl;
			info.sendTimeWindow = sendTimeWindow;
			info.receiveTimeWindow = receiveTimeWindow;
			info.receievedInKbps = lastIntervalBytes*8/1000;
			info.clientAddress = clientAddress;
			info.clientInterface = clientInterface;
		}
		return info;
	}
}
