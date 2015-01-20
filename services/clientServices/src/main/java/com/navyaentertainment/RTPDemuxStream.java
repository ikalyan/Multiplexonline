package com.navyaentertainment;

import java.net.InetAddress;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class RTPDemuxStream {

	private ArrayList<UDPServer> servers = new ArrayList<UDPServer>();
	private ArrayList<Selector>	selectors = new ArrayList<Selector>();
	public RTPDemuxStream() {
		try {
			Interfaces interfaces = new Interfaces();
			for (InetAddress address: interfaces.getInetAddresses()) {
				UDPServer server = new UDPServer(address, 0, true);
				server.bind();
				servers.add(server);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(RTPBuffer buffer) throws Exception {
		InetAddress ipAddress = InetAddress.getByName("localhost");
		for (int i=0; i<servers.size(); i++) {
			if (!servers.get(i).isBound()) servers.get(i).bind();
//			Selector selector = Selector.open();
//			servers.get(i).registerWriteSelector(selector);
//			selectors.add(selector);
		}
		int count = 0;
    	while(true) {
    		try {
				RTPDatagramPacket packet  = null;
    			for (int i=0; i<servers.size(); i++) {
    				// Process Missing packets
    				
    				if (packet == null) {
    					boolean sleep = false;
	    				while ((packet = buffer.getPacket()) == null) {
	    					Thread.sleep(1);
	    					//if (!sleep) System.out.println("Sleeping..");
	    					sleep = true;
	    				}
    				}
//    				if (selectors.get(i).selectNow() > 0) {
//    					servers.get(i).writePacket(packet, ipAddress, 6000);
//    					System.out.println("Sending packet : " + ipAddress.toString() + " SEQ : " + packet.getSequenceNumber());
//    					packet = null;
//    				}
    				boolean success = false;
					UDPServer server = servers.get(i);
    				while (!success)
    				try {
    					server.writePacket(packet, ipAddress, 6000);
    					count++;
    					if (count % 1 == 0) System.out.println("Sending packet #" + count + ", " + ipAddress.toString() + " SEQ : " + packet.getSequenceNumber());
    					packet = null;
    					success = true;
    				} catch (Exception e) {
    					System.out.println("Exception in Source Address, Dest Address" + " " + server.getAddress() + ", " + ipAddress + ", " +  " 6000");
    					e.printStackTrace();
    				}
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
