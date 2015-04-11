package com.navyaentertainment;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Date;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import com.navyaentertainment.services.ClientConfigSettings;


public class RTPTCPClient  implements CompletionHandler<Connection> {
	
	private static final Logger logger = Logger.getLogger(RTPTCPClient.class.getName());
	TCPClientManager manager = TCPClientManager.getInstance();

	static final public long referenceTime = new Date().getTime();

	final public static int CONN_ESTABLISHED = 1;
	final public static int CONN_READY = 2;
	final public static int CONN_CLOSED = 3;
	final public static int CONN_INITIATED = 4;
	final public static int CONN_PING_INITIATED = 5;

	private AtomicInteger connectionState = new AtomicInteger(0);
	
	private void setConnectionState(int state) {
		connectionState.set(state);
	}
	
	public int getConnectionState() {
		return connectionState.get();
	}
	
	public void markConnectionAsClosed() {
		setConnectionState(CONN_CLOSED);
		if (connection != null) { 
			connection = null;
		}
		clearStatistics();
	}
	
	private Thread connectionManager = null;
	
	short averageRoundTrip;
	short averageTBPCleint;
	short averageTBPServer;
	short averageClientThruput;
	short averageServerThruput;
	long lastWriteTime = 0;
	
	private int timeDiff = -65000;
	
	static final public int maxPings = 	1000;
	
	private ArrayList<TCPPingRequest> pingRequests = new ArrayList<TCPPingRequest>();
	private int pendingRequests = 0;
	private int pingRequestCount = 0;
	
	private short rateControlInKbps = 2000;

	private void clearStatistics() {
		averageRoundTrip = 0;
		averageTBPCleint = 0;
		averageTBPServer = 0;
		averageClientThruput = 0;
		averageServerThruput = 0;
		lastWriteTime = 0;
		
		timeDiff = -65000;
		pingRequests.clear();
		pingRequestCount = 0;
		pendingRequests = 0;
	}
	
	private Connection<TCPNIOConnection> connection = null;
	private InetAddress localAddress = null;
	private SocketAddress remote = null, local = null;
	private NetworkInterface netint = null;
	
	
	public RTPTCPClient(InetAddress localAddress) {
		 // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new TCPClientFilter());
        transport.setProcessor(filterChainBuilder.build());
        this.localAddress = localAddress;
        
        
        //remote = new InetSocketAddress("183.83.32.18", 7777);
        remote = new InetSocketAddress("127.0.0.1", 7777);
        local = new InetSocketAddress(localAddress, 0);
        
        setConnectionState(CONN_CLOSED);
        
        connectionManager = new Thread(() -> {
	    	while(true) {
	    		try {
					Thread.sleep(5000);
					if (getConnectionState() == CONN_CLOSED) {
						connect();
					} else if (getConnectionState() == CONN_ESTABLISHED) {
						sendPingRequests();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    });
        connectionManager.setName("Connection Manager - " + localAddress);
	}
	
	public RTPTCPClient(NetworkInterface netint) {
		 // Create a FilterChain using FilterChainBuilder
       FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
       // for reading and writing data to the connection
       filterChainBuilder.add(new TransportFilter());
       filterChainBuilder.add(new TCPClientFilter());
       transport.setProcessor(filterChainBuilder.build());
       this.netint = netint;
       this.localAddress = Interfaces.getInetAddress(netint);

       setConnectionState(CONN_CLOSED);
       
       connectionManager = new Thread(() -> {
    	   System.out.println("Connection Manager Started : " + localAddress);
	    	while(true) {
	    		try {
					Thread.sleep(5000);
					if (getConnectionState() == CONN_CLOSED) {
						connect();
					} else if (getConnectionState() == CONN_ESTABLISHED) {
						sendPingRequests();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    });
       connectionManager.setName("Connection Manager - " + netint.getDisplayName());
       connectionManager.start();
	}


    public static final String HOST = "0.0.0.0";
    public static final int PORT = 7777;
    
    final private TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
    boolean isBound = false;

    public void connect() throws Exception {
    	try {
	       //remote = new InetSocketAddress("192.168.56.1", 7777);
	       remote = new InetSocketAddress(ClientConfigSettings.serverIP, ClientConfigSettings.serverPort);
	       local = new InetSocketAddress(Interfaces.getInetAddress(netint), 0);
	    	if (getConnectionState() == CONN_CLOSED) {
	    		// start the transport
	            if (transport.isStopped()) transport.start();
	            transport.setReuseAddress(false);

	            // perform async. connect to the server
	            System.out.println("Conencting from : " + local + " to " + remote);
	            setConnectionState(CONN_INITIATED);
	            transport.connect(remote, local, this);
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(" Exception address: " + localAddress);
    		throw e;
    	}
	}
    
    private void initBufferSizes() {
    	if (getConnectionState() == CONN_ESTABLISHED) {
	    	System.out.println("Write buffer size : " + connection.getWriteBufferSize());
	        connection.setWriteBufferSize(connection.getWriteBufferSize()/100);
	        System.out.println("Write buffer size : " + connection.getWriteBufferSize());
	        connection.configureBlocking(false);
	        connection.setMaxAsyncWriteQueueSize(connection.getMaxAsyncWriteQueueSize()/100);
	        System.out.println("Connection Maximum Async Queue:" + connection.getMaxAsyncWriteQueueSize());
	        System.out.println("Connection :" + connection.toString() + ":" + connection.hashCode());
    	}
    }
    
    public void writePacket(RTPDatagramPacket packet) throws Exception {
    	
    	int timeSeq = (int)(new Date().getTime() - referenceTime)/1000;
    	if (packet.getMissingSequence() == -1) packet.setMissingSequence(timeSeq);
    	else timeSeq = (int)packet.getMissingSequence();
    	
		packet.setSendTime();
    	TCPCommPacket tcpPacket = new TCPCommPacket(packet.getBuffer(), packet.getLength(), timeSeq, TCPCommPacket.TYPE_RTP, packet.getSendTime());

    	connection.write(tcpPacket.writePacket());
	}
    

    
    private long currentIntervalBytes = 0;
    private long currentInterval = 0;
    private long lastUnsuccesfulWriteTime = 0;
    private long lastSuccesssfulWriteTime = 0;
    private long packetsPerMilliSecond = 0;
    public void writePacket(TCPCommPacket packet) throws Exception {
    	lastWriteTime = new Date().getTime();
    	connection.write(packet.writePacket());
		packet.setSendTime(lastWriteTime);
		currentIntervalBytes += packet.messageSize;
		//System.out.println("Current Interval Bytes : " + currentIntervalBytes + " " + lastWriteTime);
	}
    
    public void sendPingRequests() throws Exception {
    	connectionState.set(CONN_PING_INITIATED);
    	for (int i=0; i< maxPings;) {
    		TCPPingRequest packet = new TCPPingRequest();
    		
    		if (sendPingRequest(packet))  {
    			i++;
    		}
    	}
    }
    
    public boolean sendPingRequest(TCPPingRequest request) throws Exception {    	
    	if (canSendPing(request)) {
	    	writePacket(request);
	    	pendingRequests++;
	    	pingRequestCount++;
	    	return true;
    	} else return false;
    }
    
    public boolean canSendPing(TCPPingRequest request) {
    	long timeSinceLastPacket = new Date().getTime() - lastWriteTime;
    	if ((getConnectionState() == CONN_ESTABLISHED || getConnectionState() == CONN_PING_INITIATED)
    			&& canWrite(request) 
    			//&& (timeSinceLastPacket >= averageTBPServer/2 && timeSinceLastPacket >= 2)
    			&& pingRequestCount < maxPings)
    		return true;
 
    	return false;
    }
    
    public void shutdown() throws Exception {
    	transport.shutdownNow();
    }
    
    InetAddress getLocalAddress() {
    	return localAddress;
    }
    
    
    public boolean canWrite() {
    	if (connection != null) 
    		return connection.canWrite();
    	return false;
    }
    
    public boolean canWrite(TCPCommPacket packet) {
    	if (canWrite()) {
    		long currentTime = new Date().getTime();
    		long timeInterval = currentTime/1000;
    		if (lastUnsuccesfulWriteTime == currentTime) return false;
    		if (lastSuccesssfulWriteTime == currentTime && packetsPerMilliSecond >= 2) {
    			lastUnsuccesfulWriteTime = currentTime;
    			return false;
    		}
    		if (lastSuccesssfulWriteTime != currentTime) packetsPerMilliSecond = 0;
    		//System.out.println(" TI : CTI " + timeInterval + " : " + currentInterval);
    		if (timeInterval != currentInterval) {
    			currentIntervalBytes = 0;
    			currentInterval = timeInterval;
    			lastSuccesssfulWriteTime = currentTime;
    			packetsPerMilliSecond++;
    			return true;
    		} else {
    			if (rateControlInKbps == 0) {
        			lastSuccesssfulWriteTime = currentTime;
        			packetsPerMilliSecond++;
    				return true;
    			}
//    			System.out.println("RTC:TI:CIB:CWB : " + rateControlInKbps + " : " + (currentTime%1000 + 1) + " : " + currentIntervalBytes + " :" + rateControlInKbps*(currentTime%1000 + 1)/8);
    			if (rateControlInKbps*((currentTime%1000 + 1)/8) > (currentIntervalBytes+packet.messageSize)) {
        			lastSuccesssfulWriteTime = currentTime;
        			packetsPerMilliSecond++;
    				return true;
    			} else {
//    				System.out.println("RTC:TI:CIB:CWB : " + rateControlInKbps + " : " + (currentTime%1000 + 1) + " : " + currentIntervalBytes + " :" + rateControlInKbps*(currentTime%1000 + 1)/8);
//    				System.out.println("*************************************************************Cannot Write ");
    	        	lastUnsuccesfulWriteTime = currentTime;
    				return false;
    			}
    		}	
    	}
    	return false;
    }
    
    public boolean isBlocking() {
    	return connection.isBlocking();
    }
    
    public void closeConnetion() {
    	if (connection != null) {
    		connection.close();
    		setConnectionState(CONN_CLOSED);
    	}
    }
    
    public Connection<TCPNIOConnection> getConnection() {
    	return connection;
    }
    
    public int getAveragePingRTT() {
    	int sum = 0;

    	for (TCPPingRequest req : pingRequests) {
    		sum+= req.getRoundtripTimeClient();
    	}
    	
    	return sum/pingRequests.size();
    }
    
    public int getAveragePingRecieveTime() {
    	int sum = 0;

    	for (TCPPingRequest req : pingRequests) {
    		sum+= req.getRecieveTime();
    	}
    	
    	return sum/pingRequests.size();
    }
    
    public int getAveragePingResponseTime() {
    	int sum = 0;

    	for (TCPPingRequest req : pingRequests) {
    		sum+= req.getResponseTime();
    	}
    	
    	return sum/pingRequests.size();
    }
    
    public int getTotalReceivWindow() {
    	return (int) (pingRequests.get(pingRequests.size()-1).respRecTime - pingRequests.get(0).requestTime);
    }
    
    public int getPingRequestSize() {
    	return pingRequests.size();
    }
    
    public void insertPingRequest(TCPPingRequest request) {
//    	if (timeDiff == -65000) {
//    		if (request.timeRecTime != 0) {
//    			// Determine time here
//    			timeDiff = 0;
//    			
//    			System.out.printf("TIME DIFFS: [ %d ]-[ %d ]-[ %d ]-[ %d ]-[ %d ]-[ %d ]-[ %d ]", 
//    													 request.getRoundtripTimeClient(),
//    													 request.getRoundtripTimeServer(),
//    													 request.getRoundtripTimeClientTime(),
//    													 request.getRecieveTime(),
//    													 request.getResponseTime(),
//    													 request.getTimeRecieveTime(),
//    													 request.getTimeResponseTime());
//    		} else {
//    			try {
//    				sendPingRequest(request);
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    			}
//    		}
//    	} else {
        	pingRequests.add(request);
        	pendingRequests--;
        	if (pingRequests.size() >= (maxPings/2) && pingRequests.size() % (maxPings/2) == 0) {
        		calculateClientThruput(pingRequests.size()-(maxPings/2));
        	}
        	if (pingRequests.size() == pingRequestCount && pingRequestCount == maxPings) {
        		connectionState.set(CONN_READY);
        		manager.readyClients.add(this);
        	}
//    	}
    	
    }
    
    public int getPendingRequestCount() {
    	return pendingRequests;
    }
    
    public void getPingSummary() {
    	
    }
    
    public void registerRateControlInKbps(TCPRateControl packet) {
    	this.rateControlInKbps = (short)(packet.getTargetRateInKbps()*1.1);
    }
    
    private void calculateClientThruput(int window) {
    	System.out.println("CLI :" + getLocalAddress());
    	short totalClientTP = 0, totalServerTP = 0;
    	int count =0;
    	for (int i=window+1; i<pingRequests.size(); i++ ) {
    		TCPPingRequest c = pingRequests.get(i);
    		TCPPingRequest p = pingRequests.get(i-1);
    		short clientTP = (short) (c.requestTime - p.requestTime);
    		long serverTP = (short) (c.responseTime-p.responseTime);
    		totalClientTP += clientTP;
    		totalServerTP += serverTP;
    		count++;
    		//System.out.printf("CLI %4d SER %4d\n", clientTP, serverTP);
    	}
    	averageClientThruput = (short) (totalClientTP/count);
    	averageServerThruput = (short) (totalServerTP/count);
    	
    	System.out.printf("COUNT %3d  AVE CLI: %3d AVE SER: %3d\n", count, averageClientThruput, averageServerThruput);
    }

	@Override
	public void cancelled() {
		// TODO Auto-generated method stub
		// Let the manager know about the connection closed state.
		System.out.println("Connection Cancelled");
	}

	@Override
	public void failed(Throwable t) {
		this.connection = null;
		setConnectionState(CONN_CLOSED);
		// Let the manager know about the connection Close
		System.out.println("Connection Failed.");
		t.printStackTrace();
	}

	@Override
	public void completed(Connection connection) {
		this.connection = connection;
		setConnectionState(CONN_ESTABLISHED);
		// let the manager know about the connection establishment.
		System.out.println("Connection Establisehd");
		initBufferSizes();
		manager.connectionSuccess(connection, this);
	}

	@Override
	public void updated(Connection connection) {
		System.out.println("Connection updated? ");
		this.connection = connection;
		// Let the manager know about the update.
		
		System.out.print("Connection Updated");
	}
	
	public Short getRateControlInKbps() {
		return rateControlInKbps;
	}

}
