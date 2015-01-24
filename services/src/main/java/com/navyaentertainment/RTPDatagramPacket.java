package com.navyaentertainment;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Date;

public class RTPDatagramPacket {
	
	private byte[] buf = new byte[1500];
	private DatagramPacket dp = new DatagramPacket(buf, 1500);
	private long recieveTime = 0;
	private long sendTime = 0;
	private long missingSequence = -1;

	public void initWithTCPPacket(RTPTCPPacket tcpPacket) {
		System.arraycopy(tcpPacket.message, 0, buf, 0, tcpPacket.messageSize);
		dp.setLength(tcpPacket.messageSize);
		recieveTime = new Date().getTime();
		this.missingSequence = tcpPacket.getTimeSequence();
		System.out.println("Init TCPPacket : " + getSequenceNumber() + " : " + missingSequence);
	}
	
	public byte[] getBuffer() {
		return buf;
	}

	public int getLength() {
		return dp.getLength();
	}
	
	public DatagramPacket getDatagramPacket() {
		return dp;
	}
	
	public int getSequenceNumber() {
		byte[] bytes = new byte[4];
		bytes[2] = buf[2];
		bytes[3] = buf[3];
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		//bb.order(ByteOrder.LITTLE_ENDIAN);
		int val = bb.getInt(0);
		return val;
	}
	
	public void setRecieveTime() {
		recieveTime = new Date().getTime();
	}
	
	public long getRecieveTime() {
		return recieveTime;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime() {
		
		if (this.sendTime == 0) this.sendTime = new Date().getTime();
	}
	
	public void resetSendTime() {
		this.sendTime = 0;
	}

	public long getMissingSequence() {
		return missingSequence;
	}

	public void setMissingSequence(long missingStartSequence) {
		if (this.missingSequence == -1 || missingStartSequence == -1) {
			this.missingSequence = missingStartSequence;
		}
	}
}
