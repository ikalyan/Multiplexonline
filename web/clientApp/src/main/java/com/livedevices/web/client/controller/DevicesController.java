package com.livedevices.web.client.controller;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.navyaentertainment.ClientApp;
import com.navyaentertainment.Interfaces;
import com.navyaentertainment.RTPTCPClient;
import com.navyaentertainment.services.BufferDomain;
import com.navyaentertainment.services.RTPSplitterChannelDomain;
import com.navyaentertainment.services.RTPSplitterConstant;
import com.navyaentertainment.services.RTPSplitterServices;

@Controller
@RequestMapping("/rtpSplitter")
public class DevicesController {

	protected static Logger logger = Logger.getLogger(DevicesController.class);
	 
	private File propertyFile;
	
	private List<NetworkInterface> availableNetworkInterface = new ArrayList<NetworkInterface>();
	
	@PostConstruct
	public void init() {
		//URL url = getClass().getResource(RTPSplitterConstant.FILE_NAME);
		//this.propertyFile = new File(url.getPath());
		//rtpSplitterServices.setPropertyFile(propertyFile);
		//Interfaces interfaces = new Interfaces();
		//availableNetworkInterface = interfaces.getNetworkInterfaces();
	}
	
	@Autowired
	private RTPSplitterServices rtpSplitterServices;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<AvailableChannels> explore() throws IOException {
		
		List<AvailableChannels> availableChannels = new ArrayList<AvailableChannels>();
		for (NetworkInterface networkInterface : availableNetworkInterface) {
			AvailableChannels channel = new AvailableChannels();
			channel.setDisplayName(networkInterface.getDisplayName());
			channel.setName(networkInterface.getName());
			channel.setAddrs(networkInterface.getInterfaceAddresses());
			availableChannels.add(channel);
		}
		return availableChannels;
	}
	
	@RequestMapping(value = "/pingStatus", method = RequestMethod.GET)
	@ResponseBody
	public boolean getPingStatus(@RequestParam("network_name") String name) throws Exception {
		//System.out.println(addrs.size());
		for (NetworkInterface networkInterface : availableNetworkInterface) {
			if(networkInterface.getName().equals(name)){
				RTPTCPClient rtptcpClient = new RTPTCPClient(networkInterface);
				//rtptcpClient.connect();
				System.out.println(rtptcpClient.getAveragePingRecieveTime());
				System.out.println(rtptcpClient.getAveragePingResponseTime());
				System.out.println(rtptcpClient.getAveragePingRTT());
				System.out.println(rtptcpClient.getPendingRequestCount());
				System.out.println(rtptcpClient.getPingRequestSize());
			}
		}
		
		return true;
	}
	
	@RequestMapping(value = "/rtpSplitterChannels", method = RequestMethod.GET)
	@ResponseBody
	public RTPSplitterChannelDomain getRTPSplitterChannels() throws IOException {
		RTPSplitterChannelDomain channelDomain = rtpSplitterServices.getRTPSChannel();
		return channelDomain;
	}
	
	@RequestMapping(value = "/rtpSplitterChannels", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateRTPSplitterChannels(@RequestBody RTPSplitterChannelDomain rtpSplitterChannelDomain) throws IOException {
		return rtpSplitterServices.updateChannels(rtpSplitterChannelDomain);
	}
	
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.GET)
	@ResponseBody
	public BufferDomain getBufferSettings() throws IOException {
		BufferDomain bufferDomain = rtpSplitterServices.getBufferSettings();
		return bufferDomain;
	}
	
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateBufferSettings(@RequestBody BufferDomain bufferDomain) throws IOException {
		return rtpSplitterServices.updateBufferSettings(bufferDomain);
	}
	
	@RequestMapping(value = "/startServer", method = RequestMethod.GET)
	@ResponseBody
	public boolean startServer(@RequestParam("state") String state) throws Exception {
		
		if(state =="Stop"){
			//ClientApp app =new ClientApp();
			//app.stop();
		}else{
			//RTPSplitterChannelDomain channelDomain = rtpSplitterServices.getRTPSChannel();
			ClientApp app =new ClientApp();
			app.start();
		}
		return true;
	}
	
}