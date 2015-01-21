package com.navyaentertainment;

//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;

//public class RemoteManagementServer extends Thread {
//
//	UDPServer server;
//	int port = 0;
//	boolean isBound = false;
//	private byte[] buf = new byte[1500];
//	int count = 0;
//	private DatagramPacket dp = new DatagramPacket(buf, 1500);
//	public RemoteManagementServer(InetAddress address, int port) {
//		this.port = port;
//		server = new UDPServer(address, port, true);
//	}
//	
//	public void run()  {
//		 try {
//			 if (!server.isBound()) {
//				server.bind();
//				isBound = true;
//			 }
//			 while (true) {
//				 try {
//						server.readPacket(dp);
//						System.out.println("Port: " + port + "Read packet #" + count + ", lenght " +dp.getLength());
//						processManagementPacket();
//						Thread.sleep(100);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			 }
//		 } catch (Exception e) {
//			 e.printStackTrace();
//		 }
//     }
//	
//	void processManagementPacket() {
//		ByteBuffer bb = ByteBuffer.wrap(dp.getData());
//		int[] data = bb.asIntBuffer().array();
//		if(data.length > 2 && data[1] == 1) {
//			// Missing packet information.
//			int seq = data[0]; // Make sure we process only one such packet it may be broadcast over many interfaces.
//			for (int i=2; i<data.length; i++) {
//				System.out.println("Packet# " + data[i]);
//			}
//		}
//	}
//	
//}

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

/**
 * Class initializes and starts the echo server, based on Grizzly 2.3
 */
public class RemoteManagementServer {
    private static final Logger logger = Logger.getLogger(RemoteManagementServer.class.getName());

    public static final String HOST = "0.0.0.0";
    public static final int PORT = 7777;
    
    final private TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
    boolean isBound = false;

    public RemoteManagementServer() {
    	 // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        // StringFilter is responsible for Buffer <-> String conversion
        // filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));

        // EchoFilter is responsible for echoing received messages
        filterChainBuilder.add(new ManagementFilter());

        transport.setProcessor(filterChainBuilder.build());
    }
    
    public void bind() throws Exception {
    	if (!isBound) {
    		 // binding transport to start listen on certain host and port
            transport.bind(HOST, PORT);

            // start the transport
            transport.start();
    	}
		
	}
    
    public void shutdown() throws Exception {
    	transport.shutdownNow();
    }
    
}

