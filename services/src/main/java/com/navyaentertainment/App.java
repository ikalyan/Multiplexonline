package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import com.navyaentertainment.services.RTPSplitterChannelDomain;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static Interfaces interfaces = new Interfaces();
	private static RTPBuffer buffer;
	private static RTPBuffer muxBuffer;
	private RTPSplitterChannelDomain rtpSplitterChannelDomain = null;
	Thread[] runningThreads;
	
	public App(){
		
	}
	public App(RTPSplitterChannelDomain rtpSplitterChannelDomain){
		this.rtpSplitterChannelDomain = rtpSplitterChannelDomain;
		buffer = new RTPBuffer(Integer.parseInt(rtpSplitterChannelDomain.getDefaultBuffer_bufferTime()), Integer.parseInt(rtpSplitterChannelDomain.getDefaultBuffer_fetchGracePeriod()), Boolean.parseBoolean(rtpSplitterChannelDomain.getDefaultBuffer_processMissingPackets()));
		muxBuffer = new RTPBuffer(Integer.parseInt(rtpSplitterChannelDomain.getMaxBuffer_bufferTime()), Integer.parseInt(rtpSplitterChannelDomain.getMaxBuffer_fetchGracePeriod()), Boolean.parseBoolean(rtpSplitterChannelDomain.getMaxBuffer_processMissingPackets()));
	}
    public void start() throws Exception
    {
    	Thread[] threads = {
    			
    			// Pass a lambda to a thread
    		    new Thread(() -> {
    		    	System.out.println("UDP Writing thread from Muxer");
//    		    	readRTPStreamToBuffer();
    		    	int port = Integer.parseInt(rtpSplitterChannelDomain.getInputTCPChannelPort());
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName(rtpSplitterChannelDomain.getInputTCPChannel());
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
    		    	int port = Integer.parseInt(rtpSplitterChannelDomain.getOutputTCPChannelPort());
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName(rtpSplitterChannelDomain.getOutputTCPChannel());
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
    		    	int port = Integer.parseInt(rtpSplitterChannelDomain.getOutputUDPChannelPort());
    		    	InetAddress address = null;
					try {
						address = InetAddress.getByName(rtpSplitterChannelDomain.getOutputUDPChannel());
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
    		 
    		runningThreads = threads;
    		// Start all threads
    		Arrays.stream(runningThreads).forEach(Thread::start);
    		 
    		// Join all threads
    		Arrays.stream(runningThreads).forEach(t -> {
    			
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
    public void stop(){
    	Arrays.stream(runningThreads).forEach(t -> {
    		t.interrupt();
    	});
    }
    
    public static void sendRTPStream() {
    	
    }
}
