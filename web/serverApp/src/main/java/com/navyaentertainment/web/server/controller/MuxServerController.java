package com.navyaentertainment.web.server.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.navyaentertainment.ClientApp;
import com.navyaentertainment.RTPTCPServerConnectionInfo;
import com.navyaentertainment.ServerApp;
import com.navyaentertainment.TCPServerConnectionManager;
import com.navyaentertainment.services.BufferDomain;
import com.navyaentertainment.services.ClientSettings;
import com.navyaentertainment.services.ConfigConstant;
import com.navyaentertainment.services.DemuxClientServices;
import com.navyaentertainment.services.MuxServerServices;
import com.navyaentertainment.services.ServerConfigSettings;

@Controller
@RequestMapping("/muxServer")
public class MuxServerController {

	protected static Logger logger = Logger.getLogger(MuxServerController.class);
	
	ServerApp app = null;
	private boolean appStatus = false;
	
	@Autowired
    private ServletContext servletContext;
	
	@PostConstruct
	public void init() throws Exception {
		String fileName = ConfigConstant.FILE_LOCATION+servletContext.getContextPath()+".properties";
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		ServerConfigSettings.getInstance(servletContext.getContextPath());
//		ClientSettings clientSettings = demuxClientServices.getClientSettings(file);
//		demuxClientServices.setClientSettings(clientSettings);
		app = new ServerApp();
		app.start();
		appStatus = true;
	}
	
	@Autowired
	private MuxServerServices muxServerServices;
	
	@Autowired
	private DemuxClientServices demuxClientServices;
	
	@RequestMapping(value = "/startApp", method = RequestMethod.GET)
	@ResponseBody
	public boolean startServer(@RequestParam("state") String state) throws Exception {
		
		if(state.equals("Stop")){
			app.stop();
			appStatus = false;
		}else{
			app.start();
			appStatus = true;
		}
		return appStatus;
	}
	
	@RequestMapping(value = "/getAppStatus", method = RequestMethod.GET)
	@ResponseBody
	public boolean getAppStatus() throws Exception {
		return appStatus;
	}
	
	@RequestMapping(value = "/getServerInfo", method = RequestMethod.GET)
	@ResponseBody
	public List<RTPTCPServerConnectionInfo> getServerInfo() throws Exception {
		List<RTPTCPServerConnectionInfo> serverConnections = TCPServerConnectionManager.getInstance().getServerConnectionInfo();
		return serverConnections;
	}
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.GET)
	@ResponseBody
	public BufferDomain getBufferSettings() throws IOException {
		BufferDomain bufferDomain = ServerConfigSettings.getInstance().getBufferDomain();//demuxClientServices.getBufferSettings(ConfigConstant.SERVER);
//		ServerConfigSettings.getInstance().setBufferTime(Integer.parseInt(bufferDomain.getBufferTime()));
//		ServerConfigSettings.getInstance().setGracePeriod(Integer.parseInt(bufferDomain.getGracePeriod()));
		return bufferDomain;
	}
	
	@RequestMapping(value = "/bufferSettings", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateBufferSettings(@RequestBody BufferDomain bufferDomain) throws Exception {
		demuxClientServices.updateBufferSettings(bufferDomain,ConfigConstant.SERVER);
		ServerConfigSettings.getInstance().setBufferTime(Integer.parseInt(bufferDomain.getBufferTime()));
		ServerConfigSettings.getInstance().setGracePeriod(Integer.parseInt(bufferDomain.getGracePeriod()));
		app.stop();
		app = new ServerApp();
		app.start();
		return true;
	}
}