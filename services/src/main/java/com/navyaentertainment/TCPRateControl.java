package com.navyaentertainment;

import org.glassfish.grizzly.Buffer;


public class TCPRateControl extends TCPCommPacket {

	private short targetRateInKbps = 0;

	public TCPRateControl(short targetRateInKbps) {
		super();
		this.type = TYPE_RATE_CONTROL;
		this.targetRateInKbps = targetRateInKbps;
	}

	public void readMessage(Buffer sourceBuffer) {
		targetRateInKbps = sourceBuffer.getShort();
	}
	
	@Override
	int readPacket(Buffer sourceBuffer) { 
		System.out.println("Inside Rate Control Read packet");
		return super.readPacket(sourceBuffer);
	};
	
	@Override
	public void writeHeader(Buffer bb) {
		messageSize = Short.BYTES; 
		super.writeHeader(bb);
	}
	
	@Override
	public void writeMessage(Buffer bb) {
		bb.putShort(targetRateInKbps);
	}	
	
	
	
	public short getTargetRateInKbps() {
		return targetRateInKbps;
	}

}
