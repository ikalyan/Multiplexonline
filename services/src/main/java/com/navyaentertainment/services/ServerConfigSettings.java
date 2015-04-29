package com.navyaentertainment.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


public class ServerConfigSettings {
	
	private static ServerConfigSettings instance = null;
	protected ServerConfigSettings() {
		
	}
	
	
	public static ServerConfigSettings getInstance(String servletContext) {
	      if(instance == null) {
	    	  String configFileName = ConfigConstant.FILE_LOCATION+servletContext+".properties";
	    	  FileInputStream fis = null;
	    	  try {
		    	  instance = new ServerConfigSettings();
		    	  instance.servletContext = servletContext;
	    		  fis = new FileInputStream(configFileName);
	    		  Properties props = new Properties();
	    		  props.load(fis);
	    		  instance.bufferTime = Integer.parseInt(props.getProperty(ConfigConstant.SERVER_BUFFERTIME));
	    		  instance.gracePeriod = Integer.parseInt(props.getProperty(ConfigConstant.SERVER_GRACEPERIOD));
	    	  } catch (Exception ex) {
	    		  
	    	  } finally {
	    		  if (fis != null) {
	    			  try {
	    				  fis.close();
	    			  } catch(Exception e){}
	    		  }
	    	  }
	      }
	      return instance;
	    }
	
	public static ServerConfigSettings getInstance() {
		
      if(instance == null) {
    	  instance = new ServerConfigSettings();
      }
      return instance;
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

	public  void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	private  int bufferTime =5000;
	private  int gracePeriod = 250;
	private String servletContext;
	
	public BufferDomain getBufferDomain() {
		BufferDomain domain = new BufferDomain();
		domain.setBufferTime(""+bufferTime);
		domain.setGracePeriod("" + gracePeriod);
		return domain;
	}
	
	public void setBufferDomain(BufferDomain domain) {
		String configFileName = ConfigConstant.FILE_LOCATION+servletContext+".properties";
  	  	FileOutputStream fos = null;
  	  try {
		  instance = new ServerConfigSettings();
		  fos = new FileOutputStream(configFileName);
		  Properties props = new Properties();
		  props.setProperty(ConfigConstant.SERVER_BUFFERTIME, domain.getBufferTime());
		  props.setProperty(ConfigConstant.SERVER_GRACEPERIOD, domain.getGracePeriod());
		  instance.bufferTime = Integer.parseInt(domain.getBufferTime());
		  instance.gracePeriod = Integer.parseInt(domain.getGracePeriod());
		  props.store(fos, "");
	  } catch (Exception ex) {
		  
	  } finally {
		  if (fos != null) {
			  try {
				  fos.close();
			  } catch(Exception e){}
		  }
	  }
	}
}
