package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import com.navyaentertainment.services.ServerConfigSettings;

/**
 * Hello world!
 *
 */
public class ServerApp {
	
	protected static Logger logger = Logger.getLogger(ServerApp.class);
	private static RTPBuffer muxBuffer = null;
	private Thread[] appThreads;
	RTPOutputStream outputStream = null;
	RTPTCPInputStream inputStream = null;
	public void start() throws Exception {
		ServerApp.muxBuffer = new RTPBuffer(ServerConfigSettings.getInstance().getBufferTime(), ServerConfigSettings.getInstance().getGracePeriod(), true);
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
					int port = 7777;
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

	public static void sendRTPStream() {

	}
	
	public void stop() throws Exception{
		
		outputStream.unbind();
		inputStream.shutdown();
		muxBuffer = null;
		Arrays.stream(appThreads).forEach(Thread::stop);
	}
}
