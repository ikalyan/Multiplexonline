package com.navyaentertainment;

import java.util.logging.Logger;

import org.glassfish.grizzly.IOStrategy;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

public class RTPTCPInputStream {
	
	private RTPBuffer buffer;


	private static final Logger logger = Logger.getLogger(RemoteManagementServer.class.getName());

    public static final String HOST = "0.0.0.0";
    public static final int PORT = 7777;
    
    final private TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
    boolean isBound = true;

    
    

	public RTPTCPInputStream(RTPBuffer buffer) {
		 // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        // StringFilter is responsible for Buffer <-> String conversion
        // filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));

        filterChainBuilder.add(new TCPServerFilter(buffer));

        transport.setProcessor(filterChainBuilder.build());
        this.buffer = buffer;
	}
	
    public void bind() throws Exception {
    	if (!isBound) {
    		System.out.println("IOStrategy: " +transport.getIOStrategy().toString());
    		transport.setIOStrategy(org.glassfish.grizzly.strategies.SameThreadIOStrategy.getInstance());
    		System.out.println("IOStrategy: " +transport.getIOStrategy().toString());
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
