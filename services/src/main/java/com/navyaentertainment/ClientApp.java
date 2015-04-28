package com.navyaentertainment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import com.navyaentertainment.services.ClientConfigSettings;

/**
 * Hello world!
 *
 */
public class ClientApp {

	protected static Logger logger = Logger.getLogger(ClientApp.class);
	private static RTPBuffer buffer;
	private Thread[] clientThreads;
	RTPInputStream stream = null;
	RTPTCPDemuxStream demuxStream = null;
	public void run() {
		buffer =  new RTPBuffer(ClientConfigSettings.bufferTime, ClientConfigSettings.gracePeriod, false);
		Thread[] threads = {

				// Pass a lambda 
				new Thread(() -> {
					logger.info("Reading thread from Input");
					// readRTPStreamToBuffer();
						int port = ClientConfigSettings.udpPort == null ? 9000 : Integer.parseInt(ClientConfigSettings.udpPort);
						InetAddress address = null;
						try {
							address = InetAddress.getByName("0.0.0.0");
							if (stream == null) stream = new RTPInputStream(address, port);
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
							demuxStream = new RTPTCPDemuxStream();
							demuxStream.send(buffer);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}) };
		clientThreads = threads;
		// Start all threads
		Arrays.stream(clientThreads).forEach(Thread::start);
	}
	
	public void stop(){
		buffer = null;
//		if (stream != null) stream.unbound();
//		stream = null;
		if (demuxStream != null) demuxStream.reset();
		demuxStream = null;
		if (clientThreads != null) Arrays.stream(clientThreads).forEach(Thread::stop);
		clientThreads = null;
	}
	
}
