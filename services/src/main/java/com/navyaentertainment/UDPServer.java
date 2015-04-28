package com.navyaentertainment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UDPServer {
	
	private NetworkInterface intf;
	private InetAddress address;
	private int port;
	private DatagramSocket socket = null;
	private boolean isblocking = true;
	
	public UDPServer(InetAddress address, int port, boolean blocking) {
		super();
		this.address = address;
		this.port = port;
		this.isblocking = blocking;
	}
	
	public UDPServer(NetworkInterface intf) {
		this.intf = intf;
	}
	
	public UDPServer() {
		
	}
	
	public void bind() throws Exception {
		if (socket == null) {
			if (!isblocking) {
				DatagramChannel serverChannel = DatagramChannel.open();
				serverChannel.connect(new InetSocketAddress(address, port));
				socket = serverChannel.socket();
			} else { 
				if (address != null) {
					socket = new DatagramSocket(new InetSocketAddress(address, port));
				} else {
					socket = new DatagramSocket();
				}
			}	
		}
	}
	
	public boolean isBound() {
		return (socket != null);
	}
	
	public void registerReadSelector(Selector selector) throws Exception {

		DatagramChannel dc = socket.getChannel();
		//dc.configureBlocking( false );
		
		// Register it with the Selector, for reading.
		dc.register( selector, SelectionKey.OP_READ );
	}
	
	public void registerWriteSelector(Selector selector) throws Exception {

		DatagramChannel dc = socket.getChannel();
		//dc.configureBlocking( false );
		
		// Register it with the Selector, for writing.
		dc.register( selector, SelectionKey.OP_WRITE );
	}
	
	void readPacket(DatagramPacket packet) throws Exception {
		socket.receive(packet);
	}
	
	void writePacket(DatagramPacket packet, InetAddress ipAddress, int port) throws Exception {
		DatagramPacket sendPacket = new DatagramPacket(packet.getData(), packet.getLength(), ipAddress, port);
		socket.send(sendPacket);
	}
	
	void readPacket(RTPDatagramPacket packet) throws Exception {
		readPacket(packet.getDatagramPacket());
		packet.setRecieveTime();
	}
	
	void writePacket(RTPDatagramPacket packet, InetAddress ipAddress, int port) throws Exception {
		writePacket(packet.getDatagramPacket(), ipAddress, port);
		packet.setSendTime();
	}

	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void unbound(){
		if (socket != null) socket.disconnect();
	}
}
