package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import org.omg.SendingContext.RunTimeOperations;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static Interfaces interfaces = new Interfaces();
	private static RTPBuffer buffer = new RTPBuffer(250, 1000, false);
	private static RTPBuffer muxBuffer = new RTPBuffer(5000, 250, true);
    public static void main( String[] args ) throws Exception
    {
    	Thread[] threads = {
    			
    			// Pass a lambda to a thread
    		    new Thread(() -> {
    		    	System.out.println("UDP Writing thread from Muxer");
//    		    	readRTPStreamToBuffer();
    		    	int port = 10000;
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName("127.0.0.1");
						RTPOutputStream stream = new RTPOutputStream(address, address, port);
						stream.send(muxBuffer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    	
    		    }),
    			// Pass a lambda to a thread
    		  /*  new Thread(() -> {
    		    	System.out.println("UDP Reading thread for muxing");
//    		    	readRTPStreamToBuffer();
    		    	int port = 6000;
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName("0.0.0.0");
						RTPInputStream stream = new RTPInputStream(address, port);
						stream.recieve(muxBuffer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    	
    		    }),*/
    		 // Pass a lambda to a thread
    		    new Thread(() -> {
    		    	System.out.println("TCP Reading thread for muxing");
//    		    	readRTPStreamToBuffer();
    		    	int port = 6000;
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName("0.0.0.0");
						RTPTCPInputStream stream = new RTPTCPInputStream(muxBuffer);
						stream.bind();
						while (true) { Thread.sleep(100);}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    	
    		    }),
    		    // Pass a lambda to a thread
    		    new Thread(() -> {
    		    	System.out.println("Reading thread from Input");
//    		    	readRTPStreamToBuffer();
    		    	int port = 9000;
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName("0.0.0.0");
						RTPInputStream stream = new RTPInputStream(address, port);
						stream.recieve(buffer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    	
    		    }),
//    		    new Thread(() -> {
//    		    	System.out.println("Demux thread for input");
////    		    	readRTPStreamToBuffer();
//    		    	
//					try {
//						RTPDemuxStream stream = new RTPDemuxStream();
//						stream.send(buffer);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//    		    	
//    		    }),
    		    
    		    new Thread(() -> {
    		    	System.out.println("TCP Demux thread for input");
//    		    	readRTPStreamToBuffer();
    		    	
					try {
						RTPTCPDemuxStream stream = new RTPTCPDemuxStream();
						stream.send(buffer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		    	
    		    })
    		};
    		 
    		// Start all threads
    		Arrays.stream(threads).forEach(Thread::start);
    		 
    		// Join all threads
    		Arrays.stream(threads).forEach(t -> {
    			
    		    try { 
    		    	System.out.println("Wating for thread to Join " + t.getName());
    		    	t.join(); 
    		    }
    		    catch (InterruptedException ignore) {}
    		});
    	
    }
    
    public static void readRTPStreamToBuffer() {
    	try {
	    	int port = 9000;
	    	InetAddress address = InetAddress.getByName("127.0.0.1");
	    	UDPServer server = new UDPServer(address, port, true);
	    	server.bind();
	    	long count =0;
			RTPDatagramPacket rtppacket = new RTPDatagramPacket();
			Date start = new Date();
	    	while(count < 5000) {
	    		try {
					server.readPacket(rtppacket);
					rtppacket = buffer.insert(rtppacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		count++;
	    		//System.out.println("Read packet #" + count + ", lenght " + rtppacket.getLength() + "Seq #" + rtppacket.getSequenceNumber() );
	    	}
	    	Date end = new Date();
	    	System.out.println("Total no of packets read: " + count + "time :" + (count/((end.getTime() - start.getTime())/1000)) + "packes/ms."); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void sendRTPStream() {
    	
    }
}
