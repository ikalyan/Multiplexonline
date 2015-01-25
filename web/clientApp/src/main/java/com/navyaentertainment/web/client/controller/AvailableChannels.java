package com.navyaentertainment.web.client.controller;

import java.net.InterfaceAddress;
import java.util.List;

public class AvailableChannels {
	private String name;
    private String displayName;
    private List<InterfaceAddress> addrs;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the addrs
	 */
	public List<InterfaceAddress> getAddrs() {
		return addrs;
	}
	/**
	 * @param addrs the addrs to set
	 */
	public void setAddrs(List<InterfaceAddress> addrs) {
		this.addrs = addrs;
	}
}
