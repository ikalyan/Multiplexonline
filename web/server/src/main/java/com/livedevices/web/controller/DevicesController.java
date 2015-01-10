package com.livedevices.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.navyaentertainment.App;
import com.navyaentertainment.Interfaces;
import com.navyaentertainment.services.BufferDomain;
import com.navyaentertainment.services.RTPSplitterChannelDomain;
import com.navyaentertainment.services.RTPSplitterConstant;
import com.navyaentertainment.services.RTPSplitterServices;

@Controller
@RequestMapping("/rtpSplitter")
public class DevicesController {

	protected static Logger logger = Logger.getLogger(DevicesController.class);
	 
	private File propertyFile;
	
	@PostConstruct
	public void init() {
		URL url = getClass().getResource(RTPSplitterConstant.FILE_NAME);
		this.propertyFile = new File(url.getPath());
		rtpSplitterServices.setPropertyFile(propertyFile);
	}
	
	@Autowired
	private RTPSplitterServices rtpSplitterServices;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<AvailableChannels> explore() throws IOException {
		Interfaces interfaces = new Interfaces();
		ArrayList<NetworkInterface> address = interfaces.getNetworkInterfaces();
		List<AvailableChannels> availableChannels = new ArrayList<AvailableChannels>();
		for (NetworkInterface networkInterface : address) {
			AvailableChannels channel = new AvailableChannels();
			channel.setDisplayName(networkInterface.getDisplayName());
			channel.setName(networkInterface.getName());
			channel.setAddrs(networkInterface.getInterfaceAddresses());
			availableChannels.add(channel);
		}
		//List<InetAddress> inetAddress = interfaces.getInetAddresses();
		//List<String> hostNames = new ArrayList<String>();
		//String address = "[{\"data\":\"Devices\",\"attr\":\"0\",\"children\":[{\"attr\":{\"id\":\"1\"},\"data\":\"DVB-S/DVB-S2/MPEG-2\"},{\"attr\":{\"id\":\"2\"},\"data\":\"CAS modules\"},{\"attr\":{\"id\":\"3\"},\"data\":\"CVBS/RGB/YPbPr, \"},{\"attr\":{\"id\":\"4\"},\"data\":\"USB2.0 port, \"},{\"attr\":{\"id\":\"5\"},\"data\":\"PVR, \"},{\"attr\":{\"id\":\"6\"},\"data\":\"Multimedia player\"},{\"attr\":{\"id\":\"7\"},\"data\":\"SCPC and MCPC\"},{\"attr\":{\"id\":\"8\"},\"data\":\"DiSEqC 1.0/1.1/1.2/1.3 (USALS)\"},{\"attr\":{\"id\":\"9\"},\"data\":\"LNB, NIT search\"},{\"attr\":{\"id\":\"10\"},\"data\":\"Softwareupgrade over OTA\"},{\"attr\":{\"id\":\"11\"},\"data\":\"DiSEqC1.0/DiSEqC1.2\"},{\"attr\":{\"id\":\"12\"},\"data\":\"EPG,\"},{\"attr\":{\"id\":\"13\"},\"data\":\"VBI teletext\"},{\"attr\":{\"id\":\"14\"},\"data\":\"F950 to 2,150MHz\"},{\"attr\":{\"id\":\"15\"},\"data\":\"auto/manual program search\"},{\"attr\":{\"id\":\"16\"},\"data\":\"Multiple Languages\"},{\"attr\":{\"id\":\"17\"},\"data\":\"USB2.0 for PVR\"},{\"attr\":{\"id\":\"18\"},\"data\":\"LDPC/BCH \"},{\"attr\":{\"id\":\"19\"},\"data\":\"VHF & HUF bands\"}]}]";
		return availableChannels;
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
			App app =new App();
			app.stop();
		}else{
			RTPSplitterChannelDomain channelDomain = rtpSplitterServices.getRTPSChannel();
			App app =new App(channelDomain);
			app.start();
		}
		return true;
	}
	
}