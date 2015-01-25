package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class ServerApp {
	
	protected static Logger logger = Logger.getLogger(ServerApp.class);
	private static RTPBuffer buffer = new RTPBuffer(250, 1000, false);
	private static RTPBuffer muxBuffer = new RTPBuffer(5000, 250, true);
	private Thread[] appThreads;
	RTPOutputStream outputStream = null;
	RTPTCPInputStream inputStream = null;
	public void start() throws Exception {
		Thread[] threads = {

				// Pass a lambda to a thread
				new Thread(() -> {
					logger.info("UDP Writing thread from Muxer");
					int port = 10000;
					InetAddress address = null;
					try {
						address = InetAddress.getByName("127.0.0.1");
						outputStream = new RTPOutputStream(address, address, port);
						outputStream.send(muxBuffer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}),

				// Pass a lambda to a thread
				new Thread(() -> {
					logger.info("TCP Reading thread for muxing");
					int port = 6000;
					InetAddress address = null;
					try {
						address = InetAddress.getByName("0.0.0.0");
						inputStream = new RTPTCPInputStream(muxBuffer);
						inputStream.bind();
						while (true) {
							Thread.sleep(100);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}), };
		appThreads = threads;
		// Start all threads
		Arrays.stream(appThreads).forEach(Thread::start);
	}

	public static void readRTPStreamToBuffer() {
		try {
			int port = 9000;
			InetAddress address = InetAddress.getByName("127.0.0.1");
			UDPServer server = new UDPServer(address, port, true);
			server.bind();
			long count = 0;
			RTPDatagramPacket rtppacket = new RTPDatagramPacket();
			Date start = new Date();
			while (count < 5000) {
				try {
					server.readPacket(rtppacket);
					rtppacket = buffer.insert(rtppacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
				// logger.info("Read packet #" + count + ", lenght " +
				// rtppacket.getLength() + "Seq #" +
				// rtppacket.getSequenceNumber() );
			}
			Date end = new Date();
			logger.info("Total no of packets read: " + count + "time :" + (count / ((end.getTime() - start.getTime()) / 1000)) + "packes/ms.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendRTPStream() {

	}
	
	public void stop() throws Exception{
		outputStream.unbind();
		inputStream.shutdown();
		Arrays.stream(appThreads).forEach(Thread::stop);
	}
}
