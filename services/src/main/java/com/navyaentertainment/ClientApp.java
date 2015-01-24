package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class ClientApp {

	protected static Logger logger = Logger.getLogger(ClientApp.class);
	private static RTPBuffer buffer = new RTPBuffer(250, 1000, false);

	public void start() throws Exception {
		Thread[] threads = {

				// Pass a lambda 
				new Thread(() -> {
					logger.info("Reading thread from Input");
					// readRTPStreamToBuffer();
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

				new Thread(() -> {
					logger.info("TCP Demux thread for input");
					// readRTPStreamToBuffer();

						try {
							RTPTCPDemuxStream stream = new RTPTCPDemuxStream();
							stream.send(buffer);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}) };

		// Start all threads
		Arrays.stream(threads).forEach(Thread::start);

		// Join all threads
		Arrays.stream(threads).forEach(t -> {

			try {
				logger.info("Wating for thread to Join " + t.getName());
				t.join();
			} catch (InterruptedException ignore) {
			}
		});

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
}
