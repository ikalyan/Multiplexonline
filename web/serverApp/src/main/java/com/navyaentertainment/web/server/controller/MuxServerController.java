package com.navyaentertainment.web.server.controller;

import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.navyaentertainment.RTPTCPServerConnection;
import com.navyaentertainment.RTPTCPServerConnectionInfo;
import com.navyaentertainment.ServerApp;
import com.navyaentertainment.TCPServerConnectionManager;
import com.navyaentertainment.services.MuxServerServices;

@Controller
@RequestMapping("/muxServer")
public class MuxServerController {

	protected static Logger logger = Logger.getLogger(MuxServerController.class);
	
	ServerApp app = null;
	private boolean appStatus = false;
	
	@PostConstruct
	public void init() throws Exception {
		app = new ServerApp();
		app.start();
		appStatus = true;
	}
	
	@Autowired
	private MuxServerServices muxServerServices;
	
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
}