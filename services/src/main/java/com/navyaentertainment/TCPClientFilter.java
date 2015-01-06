package com.navyaentertainment;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;

public class TCPClientFilter extends BaseFilter {
	private int count = 0;
	
	TCPClientManager manager = TCPClientManager.getInstance();
	
	public TCPClientFilter() {
	}
	
	 @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
		 try {
	        TCPCommPacket packet = new TCPCommPacket();
	        final Buffer sourceBuffer = ctx.getMessage();
	        int sourceBufferLength = sourceBuffer.remaining();
	        int size;
	        
	        int type = packet.getPacketType(sourceBuffer);
	        if ( type == TCPCommPacket.TYPE_RTP) {
	        	packet = new RTPTCPPacket();
	        } else if (type == packet.TYPE_PING) {
	        	packet = new TCPPingRequest();
	        } else if (type == TCPCommPacket.TYPE_MISSING_PACKETS) {
	        	packet = new RTPTCPMissingPackets();
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
	    		
    	        if (type == packet.TYPE_PING) {
    	        	TCPClientManager.getInstance().registerPingResponse(ctx.getConnection(), (TCPPingRequest)packet);
    	        } else if (type == RTPTCPPacket.TYPE_MISSING_PACKETS) {
    	        	System.out.println("Client Recieved Missing packets " + ((RTPTCPMissingPackets)packet).seqNumbers);
    	        	TCPClientManager.getInstance().setClientMissingPackets(((RTPTCPMissingPackets)packet).seqNumbers);
    	        }
				
    	        return ctx.getInvokeAction(remainder);
	        };
	        return ctx.getStopAction(sourceBuffer);
		 } catch (Exception e) {
			 e.printStackTrace();
			 throw e;
		 }
	 }
	 
	 
	 // Execute a unit of processing work to be performed, when connection has been closed.
	 @Override
	 public NextAction	handleClose(FilterChainContext ctx)  {
		 System.out.println("Connection Closed in the Filter");
		 System.out.println("Connection " + ctx.getConnection().getPeerAddress() + " " + ctx.getConnection().getLocalAddress());
//		 manager.connectionMap.get(ctx.getConnection()).markConnectionAsClosed();
//		 manager.connectionMap.remove(ctx.getConnection());
		 return ctx.getStopAction();
	 }
	 
	 //Execute a unit of processing work to be performed, when channel gets connected.
	 @Override
	 public NextAction	handleConnect(FilterChainContext ctx) {
		 System.out.println("Connection Suceeded in the handler");
		 System.out.println("Connection " + ctx.getConnection().getPeerAddress() + " " + ctx.getConnection().getLocalAddress());
		 return ctx.getStopAction();
	 }
	 

}
