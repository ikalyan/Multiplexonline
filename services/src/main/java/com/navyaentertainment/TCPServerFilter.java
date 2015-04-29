package com.navyaentertainment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
			RTPTCPServerConnection serverConnection = TCPServerConnectionManager.getInstance().getServerConnection(ctx.getConnection());
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
				long ratecontrol = serverConnection.registerPacket(packet);
				if (ratecontrol > 0) {
					TCPRateControl rpacket = new TCPRateControl((short)ratecontrol);
					if (ctx.getConnection().canWrite()) {
						ctx.getConnection().write(rpacket.writePacket());
						System.out.println(" Server sending rate control packet " + ratecontrol + " : " + ctx.getConnection());
					} else {
						System.out.println(" UNABLE TO SEND RATE CONTOL PACKET **** PANIC *****");
					}
				}
				//System.out.println(serverConnection.getRTPTCPServerConnectionInfo().toString());
    	        count++;
    	        try {
					if (packet.type == packet.TYPE_RTP) {
						RTPDatagramPacket dp = new RTPDatagramPacket();
						dp.initWithTCPPacket((RTPTCPPacket)packet);
						if (count % 1000 == 0) System.out.println(new Date() + "TCP: " + "Read packet #" + count + ", lenght " + dp.getLength() + "Seq #" + dp.getSequenceNumber() + " client: " + ctx.getConnection().getPeerAddress().toString());
			    		rtpBuffer.insert(dp);
			    		if (rtpBuffer.serverMissingPackets.size() > 0) {
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
	
	@Override
    public NextAction handleAccept(FilterChainContext ctx) throws IOException {
		System.out.println("Conenction accepted " + ctx.getConnection().toString());
		TCPServerConnectionManager.getInstance().connectionAccept(ctx.getConnection());
		return ctx.getInvokeAction();
	}
	
	@Override
    public NextAction handleClose(FilterChainContext ctx) throws IOException {
		System.out.println("Conenction closed" + ctx.getConnection().toString());
		TCPServerConnectionManager.getInstance().connectionClose(ctx.getConnection());
		return ctx.getInvokeAction();
	}
}
