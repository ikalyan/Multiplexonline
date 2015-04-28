package com.navyaentertainment.web.client.controller;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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
import com.navyaentertainment.services.ClientSettings;
import com.navyaentertainment.services.RTPSplitterChannelDomain;
import com.navyaentertainment.services.ConfigConstant;
import com.navyaentertainment.services.DemuxClientServices;

@Controller
@RequestMapping("/deMuxClient")
public class DemuxClientController {

	protected static Logger logger = Logger.getLogger(DemuxClientController.class);
	 
	private List<NetworkInterface> availableNetworkInterface = new ArrayList<NetworkInterface>();
	public static ClientApp app = null;
	private boolean appStatus = false;
	
	@PostConstruct
	public void init() throws Exception {
		String fileName = ConfigConstant.FILE_LOCATION+servletContext.getContextPath()+".properties";
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		ClientSettings clientSettings = demuxClientServices.getClientSettings(file);
		demuxClientServices.setClientSettings(clientSettings);

		app = new ClientApp();
		app.run();
		appStatus = true;
	}
	
	@Autowired
    private ServletContext servletContext;
	
	@Autowired
	private DemuxClientServices demuxClientServices;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<AvailableChannels> explore() throws IOException {
		Interfaces interfaces = new Interfaces();
		availableNetworkInterface = interfaces.getNetworkInterfaces();
		List<AvailableChannels> availableChannels = new ArrayList<AvailableChannels>();
		for (NetworkInterface networkInterface : availableNetworkInterface) {
			AvailableChannels channel = new AvailableChannels();
			channel.setDisplayName(networkInterface.getDisplayName());
			channel.setName(networkInterface.getName());
			channel.setAddrs(networkInterface.getInterfaceAddresses());
			channel.setStatus(interfaces.getConnectionStatus().get(channel.getName()));
			availableChannels.add(channel);
		}
		return availableChannels;
	}
	@RequestMapping(value = "/updateChannels", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateChannels(@RequestBody List<Map<String,String>> network) throws IOException {
		for (Map<String, String> channel : network) {
			Interfaces.getConnectionStatus().put(channel.get("name"),Boolean.parseBoolean(channel.get("status")));
		}
		return true;
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
	
	@RequestMapping(value = "/channels", method = RequestMethod.GET)
	@ResponseBody
	public RTPSplitterChannelDomain getRTPSplitterChannels() throws IOException {
		RTPSplitterChannelDomain channelDomain = demuxClientServices.getRTPSChannel();
		return channelDomain;
	}
	
	@RequestMapping(value = "/channels", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateRTPSplitterChannels(@RequestBody RTPSplitterChannelDomain rtpSplitterChannelDomain) throws IOException {
		return demuxClientServices.updateChannels(rtpSplitterChannelDomain);
	}
	
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.GET)
	@ResponseBody
	public BufferDomain getBufferSettings() throws IOException {
		BufferDomain bufferDomain = demuxClientServices.getBufferSettings(ConfigConstant.CLIENT);
		return bufferDomain;
	}
	
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateBufferSettings(@RequestBody BufferDomain bufferDomain) throws IOException {
		demuxClientServices.updateBufferSettings(bufferDomain,ConfigConstant.CLIENT);
		app.stop();
//		app = new ClientApp();
		app.run();
		return true;
	}
	
	@RequestMapping(value = "/startApp", method = RequestMethod.GET)
	@ResponseBody
	public boolean startApp(@RequestParam("state") String state) throws Exception {
		
		if(state.equals("Stop")){
			app.stop();
			appStatus = false;
		}else{
			app.run();
			appStatus = true;
		}
		return appStatus;
	}
	@RequestMapping(value = "/getAppStatus", method = RequestMethod.GET)
	@ResponseBody
	public boolean getAppStatus() throws Exception {
		return appStatus;
	}
	@RequestMapping(value = "/clientSettings", method = RequestMethod.POST)
	@ResponseBody
	public boolean setIpSettings(@RequestBody ClientSettings clientSettings) throws Exception {
		String fileName = ConfigConstant.FILE_LOCATION+servletContext.getContextPath()+".properties";
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		demuxClientServices.updateClientSettings(clientSettings, file);
		app.stop();
		app.run();
		return true;
	}
	@RequestMapping(value = "/clientSettings", method = RequestMethod.GET)
	@ResponseBody
	public ClientSettings getClientSettings() throws Exception {
		String fileName = ConfigConstant.FILE_LOCATION+servletContext.getContextPath()+".properties";
		File file = new File(fileName);
		return demuxClientServices.getClientSettings(file);
	}
}