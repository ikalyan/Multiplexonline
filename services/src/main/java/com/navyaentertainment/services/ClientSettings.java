package com.navyaentertainment.services;

public class ClientSettings {
	private String udpPort;
	private String serverIP;
	private String serverPort;
	
	private String bufferTime;
	private String gracePeriod;
	
	public String getBufferTime() {
		return bufferTime;
	}
	public void setBufferTime(String bufferTime) {
		this.bufferTime = bufferTime;
	}
	public String getGracePeriod() {
		return gracePeriod;
	}
	public void setGracePeriod(String gracePeriod) {
		this.gracePeriod = gracePeriod;
	}
	/**
	 * @return the udpPort
	 */
	public String getUdpPort() {
		return udpPort;
	}
	/**
	 * @param udpPort the udpPort to set
	 */
	public void setUdpPort(String udpPort) {
		this.udpPort = udpPort;
	}
	/**
	 * @return the serverIP
	 */
	public String getServerIP() {
		return serverIP;
	}
	/**
	 * @param serverIP the serverIP to set
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	/**
	 * @return the serverPort
	 */
	public String getServerPort() {
		return serverPort;
	}
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
}
