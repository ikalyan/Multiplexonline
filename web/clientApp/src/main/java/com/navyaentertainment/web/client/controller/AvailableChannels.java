package com.navyaentertainment.web.client.controller;

import java.io.Serializable;
import java.net.InterfaceAddress;
import java.util.List;

public class AvailableChannels implements Serializable {
	private String name;
    private String displayName;
    private List<InterfaceAddress> addrs;
    private Boolean status;
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
	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}
}
