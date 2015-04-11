package com.navyaentertainment;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class RTPTCPDemuxStream {
	
	private long referenceTime = 0;
	
	private static TCPClientManager manager = TCPClientManager.getInstance();

	private ArrayList<RTPTCPClient> tcpClients = new ArrayList<RTPTCPClient>();
	public RTPTCPDemuxStream() {
		manager.initiateConnections();	
	}
	
	public void send(RTPBuffer buffer) throws Exception {
		InetAddress ipAddress = InetAddress.getByName("localhost");
		int count = 0;

		RTPDatagramPacket packet  = null;
    	while(true) {
    		try {
    			manager.sortReadyClientsByRateControl();
    			for (int i=0; i < manager.getInstance().readyClients.size(); i++) {
    				if (TCPClientManager.getInstance().getMissingPackets() != null && TCPClientManager.getInstance().getMissingPackets().size() > 0) {
    					buffer.setClientMissingPackets(TCPClientManager.getInstance().getMissingPackets());
    					TCPClientManager.getInstance().clearMissingPacket();
    				}
    				if (packet == null) {
    					boolean sleep = false;
	    				while ((packet = buffer.getPacket()) == null) {
	    					Thread.sleep(100);
	    					//if (!sleep) System.out.println("Sleeping..");
	    					sleep = true;
	    				}
    				}
    				boolean success = false;
					RTPTCPClient client = manager.getInstance().readyClients.get(i);
    				//while (!success)
    				try {
    					if (client != null && client.canWrite()) {
    						long missingSequence = (new Date().getTime() - RTPTCPClient.referenceTime)/1000;
	    					if ((count % 100000) != 0 || (packet.getMissingSequence() != -1 && (missingSequence - packet.getMissingSequence()) > 10))  {
	    						if((packet.getMissingSequence() != -1) &&(missingSequence - packet.getMissingSequence()) > 10) {
	    							packet.setMissingSequence(-1);
	    							//System.out.println("Missing SEQ GREATER THAN 10 " + missingSequence + " : " + packet.getMissingSequence());
	    						}
	    						packet.resetSendTime();
	    						client.writePacket(packet);
	    					} else {
	    						packet.resetSendTime();
	    						packet.setSendTime();
	    						packet.setMissingSequence(missingSequence);
	    						System.out.println("Ignoring packet " + packet.getSequenceNumber() + " : " + packet.getMissingSequence());
	    					}
	    					count++;
	    					if (count %1000 == 0) System.out.println("TCP: Sending packet #" + count + ", " + client.getLocalAddress() + " SEQ : " + packet.getSequenceNumber() + "   " + packet.getLength());
	    					packet = null;
    					} else {
    						if (client == null) {
    							System.out.println("client is null");
    						} else {
    							//System.out.println("Client is not ready for write " + client.isBlocking());
    						}
    					}
    					//success = true;
    				} catch (Exception e) {
    					System.out.println("Exception in Source Address, Dest Address" + " " + client.HOST  + ", " + ipAddress + ", " +  " 7777");
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
