package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;

public class RTPInputStream {

	private UDPServer server;
	private boolean isBound = false;
	private RTPDatagramPacket rtppacket = new RTPDatagramPacket();
	private int count = 0;
	private InetAddress address;
	private int port;
	private boolean logEveryPacket = false;


	public RTPInputStream(InetAddress address, int port) {
		this.address = address;
		this.port = port;
		server = new UDPServer(address, port, true);
	}

	
	public void recieve(RTPBuffer buffer) throws Exception {
		if (!server.isBound()) {
			server.bind();
			isBound = true;
		}
    	while(true) {
    		try {
				server.readPacket(rtppacket);
	    		count++;
	    		if (count % 1000 == 0 || (logEveryPacket && count % 100 == 0)) System.out.println(new Date() + " Port: " + port + "Read packet #" + count + ", lenght " + rtppacket.getLength() + "Seq #" + rtppacket.getSequenceNumber() );
				rtppacket = buffer.insert(rtppacket);
				//Thread.sleep(2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setLogEveryPacket(boolean logEveryPacket) {
		this.logEveryPacket = logEveryPacket;
	}
	
	public void unbound(){
		server.unbound();
	}
	
}
