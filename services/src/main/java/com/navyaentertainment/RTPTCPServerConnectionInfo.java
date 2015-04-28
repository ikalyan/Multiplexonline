package com.navyaentertainment;

public class RTPTCPServerConnectionInfo {
	
	public String clientInterface;
	public String clientAddress;
	public long receievedInKbps;
	public long totalBytesRecieved;
	
	public long recentRateControl;
	public long sendTimeWindow;
	public long receiveTimeWindow;
	
	public long packetCount;
	public long bytesPerSecond;
	public short averageRecieveTimeBetweenPackets;
	public short avarageSendTimeBetweenPackets;
	
	public short pingRequestCount;
	public long pingPacketsPerSecond;
	public short averageReceieveTimeBetweenPings;
	public short averageSendTimeBetweenPings;

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("======== Packet Count " + packetCount + "========");
		return buffer.toString();
	}
}
