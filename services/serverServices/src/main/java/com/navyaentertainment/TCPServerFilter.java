package com.navyaentertainment;

import java.io.IOException;
import java.util.ArrayList;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

public class TCPServerFilter extends BaseFilter {

	private RTPBuffer rtpBuffer;
	private int count = 0;
	
	public TCPServerFilter(RTPBuffer buffer) {
		this.rtpBuffer = buffer;
	}
	 @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
		 try {
			 
			TCPCommPacket packet = new TCPCommPacket();
			final Buffer sourceBuffer = ctx.getMessage();
			int sourceBufferLength = sourceBuffer.remaining();
			int size;
			
			int type = packet.getPacketType(sourceBuffer);
			if ( type == packet.TYPE_RTP) {
				packet = new RTPTCPPacket();
			} else if (type == packet.TYPE_PING) {
				packet = new TCPPingRequest();
			}

	        if ((size = packet.readPacket(sourceBuffer)) != 0) {
	        	if (size < 0 || size > 1500) { // Client is sending Junk;
	        		ctx.getConnection().close();
	        		return ctx.getStopAction();
	        	}
	        	final Buffer remainder = sourceBufferLength > size ?
	        	        sourceBuffer.split(size) : null;
    	        ctx.setMessage(packet);
				sourceBuffer.tryDispose();
    	        count++;
				sourceBuffer.tryDispose();
    	        try {
					if (packet.type == packet.TYPE_RTP) {
						RTPDatagramPacket dp = new RTPDatagramPacket();
						dp.initWithTCPPacket((RTPTCPPacket)packet);
						if (count % 999 == 0) System.out.println("TCP: " + "Read packet #" + count + ", lenght " + dp.getLength() + "Seq #" + dp.getSequenceNumber() + " client: " + ctx.getConnection().getPeerAddress().toString());
			    		rtpBuffer.insert(dp);
			    		if (rtpBuffer.serverMissingPackets.size() >0) {
			    			ArrayList<Integer> missing = new ArrayList<Integer>(rtpBuffer.serverMissingPackets);
			    			RTPTCPMissingPackets mpacket = new RTPTCPMissingPackets();
			    			mpacket.setMissingPackets(missing);
			    			ctx.getConnection().write(mpacket.writePacket());
			    		}
					} else if (packet.type == packet.TYPE_PING) {
						ctx.getConnection().write(packet.writePacket());
					}
    	        } finally {
    	        	try {
    	        		return ctx.getInvokeAction(remainder);
    	        	} catch (Exception e) {
    	        		e.printStackTrace();
    	        	}
    	        }
	        };
	        return ctx.getStopAction(sourceBuffer);
		 } catch (Exception e) {
			 e.printStackTrace();
			 throw e;
		 }
	 }

}
