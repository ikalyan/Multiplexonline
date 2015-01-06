package com.navyaentertainment;

import java.net.InetAddress;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class RTPOutputStream {

	
	private UDPServer server;
	private boolean isBound = false;
	private RTPDatagramPacket rtppacket = new RTPDatagramPacket();
	private int count = 0;
	private InetAddress address;
	private int port;
	private InetAddress bindAddress;
	
	public RTPOutputStream(InetAddress bindAddress, InetAddress address, int port) {
		try {
			server = new UDPServer(bindAddress, 0, true);
			server.bind();
			this.address = address;
			this.port = port;
			this.bindAddress = bindAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(RTPBuffer buffer) throws Exception {
		InetAddress ipAddress = InetAddress.getByName("localhost");
    	while(true) {
    		try {
					RTPDatagramPacket packet  = null;
    				if (packet == null) {
    					boolean sleep = false;
    					
	    				while (packet == null) {
	    					packet = buffer.getPacket();
	    					if (packet == null) Thread.sleep(1);
	    					//if (!sleep) System.out.println("Sleeping..");
	    					sleep = true;
	    				}
    				}
    				boolean success = false;
    				while (!success)
    				try {
    					server.writePacket(packet, address, port);
    					count++;
    					if (count % 1000 == 0) System.out.println("Sending packet #" + count + ", " + ipAddress.toString() + " SEQ : " + packet.getSequenceNumber() +",port " + port);
    					packet = null;
    					success = true;
    				} catch (Exception e) {
    					System.out.println("Exception in Source Address, Dest Address" + " " + server.getAddress() + ", " +  port);
    					e.printStackTrace();
    				}
    			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
