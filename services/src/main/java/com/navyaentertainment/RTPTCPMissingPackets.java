package com.navyaentertainment;

import java.util.ArrayList;
import java.util.Date;

import org.glassfish.grizzly.Buffer;

public class RTPTCPMissingPackets extends TCPCommPacket {

	int noOfMissingPackets;
	ArrayList<Integer> seqNumbers = new ArrayList<Integer>();
	
	public RTPTCPMissingPackets() {
		super();
		this.type = TYPE_MISSING_PACKETS;
	}
	
	public RTPTCPMissingPackets(int packetid) {
		this();
		this.packetid = packetid;
		
	}
	
	public void setMissingPackets(ArrayList<Integer> packets) {
		this.seqNumbers = packets;
		noOfMissingPackets = packets.size();
	}
	/*
	 * Returns the message length that is consumed.
	 */
	int readPacket(Buffer sourceBuffer) {
		long time = new Date().getTime();
		//System.out.println("Read Time:" + time);
		int size =  super.readPacket(sourceBuffer);
		return size;
	}
	
	public void readHeader(Buffer sourceBuffer) {
		super.readHeader(sourceBuffer);
	}
	
	public void readMessage(Buffer sourceBuffer) {
		noOfMissingPackets = sourceBuffer.getInt();
		for (int i=0; i<noOfMissingPackets; i++) {
			seqNumbers.add(sourceBuffer.getInt());
		}
	}
	
	
	@Override
	public Buffer writePacket() {
		return super.writePacket();
	}
	@Override
	public void writeMessage(Buffer bb) {
		bb.putInt(noOfMissingPackets);
		for (int i=0; i<noOfMissingPackets; i++) {
			bb.putInt(seqNumbers.get(i).intValue());
		}
	}
	
	@Override
	public void writeHeader(Buffer bb) {
		messageSize = Integer.BYTES*(noOfMissingPackets+1);
		//bb.putInt(this.messageSize);
    	super.writeHeader(bb);
	}
}
