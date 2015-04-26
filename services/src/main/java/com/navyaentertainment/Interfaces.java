package com.navyaentertainment;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Interfaces {
	
	private ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
	private HashMap<String, InetAddress> addressMap = new HashMap<String, InetAddress>();
	private ArrayList<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
	private InetAddress loopback = null;
	private static Map<String,Boolean> connectionStatus = new HashMap<String, Boolean>();

	public Interfaces() {
		super();
		refreshInterfaces();
	}
	
	public ArrayList<InetAddress> getInetAddresses() {
		return addresses;
	}
	
	public HashMap<String, InetAddress> getInetAddressMap() {
		return addressMap;
	}
	
	public ArrayList<NetworkInterface> getNetworkInterfaces() {
		return interfaces;
	}

	InetAddress getLoopBack() {
		return loopback;
	}
	
	public static InetAddress getInetAddress(NetworkInterface netint) {
		try {
			netint = NetworkInterface.getByName(netint.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	if (!(inetAddress instanceof Inet4Address)) continue;
            return inetAddress;
        }
        return null;
	}
	
	public void refreshInterfaces() {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				if (netint.isLoopback()) continue;
//				//if ("en5".equals(netint.getDisplayName())) continue;
//				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
//		        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
//		        	if (netint.isLoopback()) continue;
//		        	if (!(inetAddress instanceof Inet4Address)) continue;
//		            System.out.printf("InetAddress: %s\n", inetAddress + "interface Naeme : " + netint.getDisplayName());
//	            	addresses.add(inetAddress);
//	            	interfaces.add(netint);
////		            if (netint.isLoopback()) {
////		            	loopback = inetAddress;
////		            	//addresses.add(inetAddress);
////		            	//addressMap.put(netint.getName(), inetAddress);
////		            }
//		        }
				InetAddress address = getInetAddress(netint);
				if ( address != null) {
					addresses.add(address);
					interfaces.add(netint);
					if(connectionStatus.get(netint.getName()) == null){
						connectionStatus.put(netint.getName(),true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the connectionStatus
	 */
	public static Map<String, Boolean> getConnectionStatus() {
		return connectionStatus;
	}

	/**
	 * @param connectionStatus the connectionStatus to set
	 */
	public static void setConnectionStatus(Map<String, Boolean> connectionStatus) {
		Interfaces.connectionStatus = connectionStatus;
	}
}
