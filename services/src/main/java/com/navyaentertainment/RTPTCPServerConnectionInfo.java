package com.navyaentertainment;

public class RTPTCPServerConnectionInfo {
	
	public String clientInterface;
	public String clientAddress;
	public long packetCount;
	public long totalBytesRecieved;
	public short byesPerSecond;
	public short averageRecieveTimeBetweenPackets;
	public short avarageSendTimeBetweenPackets;
	
	public short pingRequestCount;
	public short pingPacketsPerSecond;
	public short averageReceieveTimeBetweenPings;
	public short averageSendTimeBetweenPings;

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("======== Packet Count " + packetCount + "========");
		return buffer.toString();
	}
}
