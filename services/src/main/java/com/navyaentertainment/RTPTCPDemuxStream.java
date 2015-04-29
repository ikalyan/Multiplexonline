package com.navyaentertainment;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class RTPTCPDemuxStream {
	
	private static TCPClientManager manager = TCPClientManager.getInstance();
	
	private boolean process = true;
	private int demuxAlgorithm = TCPClientManager.CP_ROUND_ROBIN;
	
	/**
	 * @return the demuxAlgorithm
	 */
	public int getDemuxAlgorithm() {
		return demuxAlgorithm;
	}

	/**
	 * @param demuxAlgorithm the demuxAlgorithm to set
	 */
	public void setDemuxAlgorithm(int demuxAlgorithm) {
		this.demuxAlgorithm = demuxAlgorithm;
	}

	public RTPTCPDemuxStream() {
		manager.setDemuxAlgorithm(demuxAlgorithm);
		manager.initiateConnections();	
	}
	
	public void reset() {
		process = false;
		manager.clearAllConnections();
	}
	
	public void send(RTPBuffer buffer) throws Exception {
		InetAddress ipAddress = InetAddress.getByName("localhost");
		int count = 0;

		RTPDatagramPacket packet  = null;
    	while(process) {
    		if (getDemuxAlgorithm() == TCPClientManager.CP_ROUND_ROBIN) {
	    		try {
	    			manager.sortReadyClientsByRateControl();
	    			for (int i=0; i < manager.readyClients.size(); i++) {
	    				if (TCPClientManager.getInstance().getMissingPackets() != null && TCPClientManager.getInstance().getMissingPackets().size() > 0) {
	    					buffer.setClientMissingPackets(TCPClientManager.getInstance().getMissingPackets());
	    					TCPClientManager.getInstance().clearMissingPacket();
	    				}
	    				if (packet == null) {
		    				while ((packet = buffer.getPacket()) == null) {
		    					Thread.sleep(100);
		    				}
	    				}
						RTPTCPClient client = manager.readyClients.get(i);
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
		    					if (count %1000 == 0) System.out.println(new Date() + " TCPRR: Sending packet #" + count + ", " + client.getLocalAddress() + " SEQ : " + packet.getSequenceNumber() + "   " + packet.getLength());
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
    		} else if (getDemuxAlgorithm() == manager.CP_RATE_CONTROL) { 
    			try {
    				if (TCPClientManager.getInstance().getMissingPackets() != null && TCPClientManager.getInstance().getMissingPackets().size() > 0) {
    					buffer.setClientMissingPackets(TCPClientManager.getInstance().getMissingPackets());
    					TCPClientManager.getInstance().clearMissingPacket();
    				}
    				if (packet == null) {
	    				while ((packet = buffer.getPacket()) == null) {
	    					Thread.sleep(100);
	    				}
    				}
					count++;
					if (count %1000 == 0) System.out.println(new Date() + " TCPBC: Sending packet #" + count + ", SEQ : " + packet.getSequenceNumber() + "   " + packet.getLength());
	    			manager.sortReadyClientsByRateControl();
	    			boolean success = false;
	    			for (int i=0; i < manager.getInstance().readyClients.size() && !success; i++) {
						RTPTCPClient client = manager.getInstance().readyClients.get(i);
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
		    						success = true;
		    					} else {
		    						packet.resetSendTime();
		    						packet.setSendTime();
		    						packet.setMissingSequence(missingSequence);
		    						System.out.println("Ignoring packet " + packet.getSequenceNumber() + " : " + packet.getMissingSequence());
		    					}
		    					count++;
		    					if (count % 1000 == 0) System.out.println(new Date() + " TCPCR: Sending packet #" + count + ", " + client.getLocalAddress() + " SEQ : " + packet.getSequenceNumber() + "   " + packet.getLength());
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
    		} else if (getDemuxAlgorithm() == manager.CP_BROADCAST) { 
    			try {
    				if (TCPClientManager.getInstance().getMissingPackets() != null && TCPClientManager.getInstance().getMissingPackets().size() > 0) {
    					buffer.setClientMissingPackets(TCPClientManager.getInstance().getMissingPackets());
    					TCPClientManager.getInstance().clearMissingPacket();
    				}
    				if (packet == null) {
	    				while ((packet = buffer.getPacket()) == null) {
	    					Thread.sleep(100);
	    				}
    				}
    				long missingSequence = (new Date().getTime() - RTPTCPClient.referenceTime)/1000;
					if ((count % 100000) != 0 || (packet.getMissingSequence() != -1 && (missingSequence - packet.getMissingSequence()) > 10))  {
						if((packet.getMissingSequence() != -1) &&(missingSequence - packet.getMissingSequence()) > 10) {
							packet.setMissingSequence(-1);
							//System.out.println("Missing SEQ GREATER THAN 10 " + missingSequence + " : " + packet.getMissingSequence());
						}
						packet.resetSendTime();
					}
					count++;
					if (count %1000 == 0) System.out.println(new Date() + " TCPBC: Sending packet #" + count + ", SEQ : " + packet.getSequenceNumber() + "   " + packet.getLength());
	    			manager.sortReadyClientsByRateControl();
	    			boolean packetWritten = false;
	    			for (int i=0; i < manager.getInstance().readyClients.size(); i++) {
						RTPTCPClient client = manager.getInstance().readyClients.get(i);
	    				try {
	    					if (client != null && client.canWrite()) {
	    						client.writePacket(packet);
	    						packetWritten = true;
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
	    			if (packetWritten == true) {
	    				packet = null;
	    			}
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
		}
	}
}
