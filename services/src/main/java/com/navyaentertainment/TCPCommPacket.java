package com.navyaentertainment;

import java.util.Date;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.ByteBufferWrapper;

public class TCPCommPacket {
	
	final static int TYPE_CONN_INFO = 1;
	final static int TYPE_PING = 2;
	final static int TYPE_RTP = 3;
	final static int TYPE_MISSING_PACKETS = 4;
	final static int TYPE_CONN_STATS = 5;
	final static int TYPE_STATS = 6;

	int packetid;
	int messageSize;
	int timeSeq;
	int type;
	byte[] message = new byte[1500];
	long sendTime = 0;
	long recieveTime = 0;

	public TCPCommPacket(byte[] message, int messageSize, int timeSeq, int type) {
		this.message = message;
		this.messageSize = messageSize;
		this.timeSeq = timeSeq;
		this.type = type;
	}
	
	public TCPCommPacket() {
		this.type = TYPE_RTP;
	}
	
	int getPacketType(Buffer sourceBuffer) {
		final int sourceBufferLength = sourceBuffer.remaining();
//		message = new byte[sourceBufferLength];

	    // If source buffer doesn't contain length
	    if (sourceBufferLength < 16) {
	        return 0;
	    }

	    // Get the body length
	    messageSize = sourceBuffer.getInt();
	    // The complete message length
	    final int completeMessageLength = 16 + messageSize;

	    readHeader(sourceBuffer);
	    // If the source message doesn't contain entire body
	    if (sourceBufferLength < completeMessageLength) {
	    	sourceBuffer.rewind();
	    	return 0;
	    }
	    sourceBuffer.rewind();
	    return this.type;
	}

	/*
	 * Returns the message length that is consumed.
	 */
	int readPacket(Buffer sourceBuffer) {
		final int sourceBufferLength = sourceBuffer.remaining();
//		message = new byte[sourceBufferLength];

	    // If source buffer doesn't contain length
	    if (sourceBufferLength < 16) {
	        return 0;
	    }

	    // Get the body length
	    messageSize = sourceBuffer.getInt();
	    // The complete message length
	    final int completeMessageLength = 16 + messageSize;

	    // If the source message doesn't contain entire body
	    if (sourceBufferLength < completeMessageLength) {
	    	sourceBuffer.rewind();
	    	return 0;
	    }

	    readHeader(sourceBuffer);
	    readMessage(sourceBuffer);
	    recieveTime = new Date().getTime();
		return completeMessageLength;
	}
	
	public void readHeader(Buffer sourceBuffer) {
		// MessageSize is already read
	    this.packetid = sourceBuffer.getInt();
	    this.timeSeq = sourceBuffer.getInt();
	    this.type = sourceBuffer.getInt();
	}
	
	public void readMessage(Buffer sourceBuffer) {
		message = new byte[messageSize];
	    sourceBuffer.get(message);
	}
	
	public Buffer writePacket() {
    	ByteBufferManager manager = new  ByteBufferManager();
    	ByteBufferWrapper bb = manager.allocate(1500);
//    	ByteBuffer bb = ByteBuffer.wrap(bytes);
    	writeHeader(bb);
    	writeMessage(bb);
    	bb.trim();
    	return bb;
	}
	
	public void writeMessage(Buffer bb) {
		bb.put(this.message, 0, this.messageSize);
	}
	
	public void writeHeader(Buffer bb) {
		bb.putInt(this.messageSize);
    	bb.putInt(this.packetid);
    	timeSeq = (int)(new Date().getTime() - RTPTCPClient.referenceTime)/1000;
    	bb.putInt(timeSeq);
    	bb.putInt(type);
	}
	
	public int getTimeSequence() {
		return timeSeq;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
}
