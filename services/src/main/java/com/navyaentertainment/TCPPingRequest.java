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
	
	long time;
	
	public TCPPingRequest() {
		super();
		this.type = TYPE_PING;
	}
	
	public TCPPingRequest(int packetid) {
		this();
		this.packetid = packetid;
		
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
		if (responseTime == 0 || (respRecTime != 0 && timeRequestTime == 0)) bb.put(this.message, 0, 1300);
		//System.out.printf("%d-%d-%d-%d-%d-%d\n", requestTime, responseTime, respRecTime, timeRequestTime, timeResponseTime, timeRecTime);
	}
	
	@Override
	public void writeHeader(Buffer bb) {
		if (responseTime==0 || (respRecTime != 0 && timeRequestTime == 0)) messageSize = Long.BYTES*5 + 1300;
		else messageSize = Long.BYTES*5; 
		bb.putInt(this.messageSize);
    	bb.putInt(this.packetid);
    	timeSeq = (int)(time - RTPTCPClient.referenceTime)/1000;
    	bb.putInt(timeSeq);
    	bb.putInt(this.type);
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
