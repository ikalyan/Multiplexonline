package com.navyaentertainment;

import java.util.Date;

import org.glassfish.grizzly.Buffer;


public class TCPPingRequest extends TCPCommPacket {

	long requestTime;
	long responseTime;
	long respRecTime;
	long timeRequestTime;
	long timeResponseTime;
	long timeRecTime;
	String connectionInfo = "";
	
	long time;
	
	public TCPPingRequest() {
		super();
		this.type = TYPE_PING;
	}
	
	public TCPPingRequest(String connectionInfo) {
		super();
		this.type = TYPE_PING;
		this.connectionInfo = connectionInfo;
	}
	
	/*
	 * Returns the message length that is consumed.
	 */
	int readPacket(Buffer sourceBuffer) {
		long time = new Date().getTime();
		//System.out.println("Read Time:" + time);
		int size =  super.readPacket(sourceBuffer);
		if (responseTime != 0 && timeResponseTime == 0 && respRecTime == 0) respRecTime = time;
		if (timeResponseTime != 0) timeRecTime = time;

	    //System.out.printf("%d-%d-%d-%d-%d-%d\n", requestTime, responseTime, respRecTime, timeRequestTime, timeResponseTime, timeRecTime);
		return size;
	}
	
	public void readHeader(Buffer sourceBuffer) {
		super.readHeader(sourceBuffer);
	}
	
	public void readMessage(Buffer sourceBuffer) {
		requestTime = sourceBuffer.getLong();
		responseTime = sourceBuffer.getLong();
		respRecTime = sourceBuffer.getLong();
		timeRequestTime = sourceBuffer.getLong();
		timeResponseTime = sourceBuffer.getLong();
		message = new byte[messageSize-40];
	    sourceBuffer.get(message);
	    connectionInfo = new String(message).replace(',', ' ').trim();
	}
	
	
	@Override
	public Buffer writePacket() {
		time =  new Date().getTime();
		//System.out.println("Write: " + time);
		if (requestTime == 0) {
			requestTime = time;
		} else if (responseTime == 0) {
			responseTime = time;
		} else if (timeRequestTime == 0) {
			timeRequestTime = time;
		} else if (timeResponseTime == 0) {
			timeResponseTime = time;
		}
		return super.writePacket();
	}
	@Override
	public void writeMessage(Buffer bb) {
		bb.putLong(requestTime);
		bb.putLong(responseTime);
		bb.putLong(respRecTime);
		bb.putLong(timeRequestTime);
		bb.putLong(timeResponseTime);
		if (responseTime == 0 || (respRecTime != 0 && timeRequestTime == 0)) {
			byte[] connInfo = new byte[0];
			if (!"".equals(connectionInfo)) {
				 connInfo = connectionInfo.getBytes();
				bb.put(connInfo, 0, connInfo.length);
			}
			bb.put(this.message, connInfo.length, 1300 - connInfo.length);
		}
		//System.out.printf("%d-%d-%d-%d-%d-%d\n", requestTime, responseTime, respRecTime, timeRequestTime, timeResponseTime, timeRecTime);
	}
	
	@Override
	public void writeHeader(Buffer bb) {
		if (responseTime==0 || (respRecTime != 0 && timeRequestTime == 0)) messageSize = Long.BYTES*5 + 1300;
		else messageSize = Long.BYTES*5; 
		super.writeHeader(bb);
	}
	
	public int getRoundtripTimeClient() {
		return (int) (this.respRecTime - this.requestTime);
	}
	
	public int getRoundtripTimeClientTime() {
		return (int) (this.timeRecTime - this.timeRequestTime);
	}
	
	public int getRoundtripTimeServer() {
		return (int) (this.timeResponseTime - this.responseTime);
	}
	
	public int getRecieveTime() {
		return (int) (this.responseTime - this.requestTime);
	}
	
	public int getResponseTime() {
		return (int) (this.respRecTime - this.responseTime);
	}
	
	public int getTimeRecieveTime() {
		return (int) (this.timeResponseTime - this.timeRequestTime);
	}
	
	public int getTimeResponseTime() {
		return (int) (this.timeRecTime - this.timeResponseTime);
	}
	
}
