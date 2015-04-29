package com.navyaentertainment.services;

public class ClientConfigSettings {
	
	private String udpPort ="9000";
	private String serverIP = "127.0.0.1";
	private int serverPort = 7777;
	
	private int bufferTime =5000;
	private int gracePeriod = 250;
	
	public String getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(String udpPort) {
		this.udpPort = udpPort;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getBufferTime() {
		return bufferTime;
	}

	public void setBufferTime(int bufferTime) {
		this.bufferTime = bufferTime;
	}

	public int getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	private static ClientConfigSettings instance = null;
	protected ClientConfigSettings() {
		
	}
	
	public static ClientConfigSettings getInstance() {
	      if(instance == null) {
	         instance = new ClientConfigSettings();
	      }
	      return instance;
	   }
}
