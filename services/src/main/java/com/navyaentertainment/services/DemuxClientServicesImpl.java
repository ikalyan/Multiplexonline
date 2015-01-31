package com.navyaentertainment.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class DemuxClientServicesImpl implements DemuxClientServices {
	
	private File propertyFile;
	
	public boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			
			
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public RTPSplitterChannelDomain getRTPSChannel() {
		Properties prop = new Properties();
		InputStream input = null;
		RTPSplitterChannelDomain channel = new RTPSplitterChannelDomain();
		try {
			prop = getProperties();
			
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return channel;
	}
	
	private Properties getProperties() throws IOException{
		Properties properties = new Properties();
		InputStream input = new FileInputStream(propertyFile);
		properties.load(input);
		return properties;
	}
	@Override
	public boolean updateBufferSettings(BufferDomain bufferDomain,String type) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			if(ConfigConstant.CLIENT.equals(type)){
				prop.setProperty(ConfigConstant.CLIENT_BUFFERTIME, bufferDomain.getBufferTime());
				prop.setProperty(ConfigConstant.CLIENT_GRACEPERIOD, bufferDomain.getGracePeriod());
				ClientConfigSettings.bufferTime = Integer.parseInt(bufferDomain.getBufferTime());
				ClientConfigSettings.gracePeriod = Integer.parseInt(bufferDomain.getGracePeriod());
			}else{
				prop.setProperty(ConfigConstant.SERVER_BUFFERTIME, bufferDomain.getBufferTime());
				prop.setProperty(ConfigConstant.SERVER_GRACEPERIOD, bufferDomain.getGracePeriod());
				ServerConfigSettings.bufferTime = Integer.parseInt(bufferDomain.getBufferTime());
				ServerConfigSettings.gracePeriod = Integer.parseInt(bufferDomain.getGracePeriod());
			}
			
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	@Override
	public BufferDomain getBufferSettings(String type) {
		Properties prop = new Properties();
		InputStream input = null;
		BufferDomain bufferDomain = new BufferDomain();;
		try {
			prop = getProperties();			
			if(ConfigConstant.CLIENT.equals(type)){
				bufferDomain = new BufferDomain();
				bufferDomain.setBufferTime(prop.getProperty(ConfigConstant.CLIENT_BUFFERTIME,"250"));
				bufferDomain.setGracePeriod(prop.getProperty(ConfigConstant.CLIENT_GRACEPERIOD,"1000"));
				ClientConfigSettings.bufferTime = Integer.parseInt(bufferDomain.getBufferTime());
				ClientConfigSettings.gracePeriod = Integer.parseInt(bufferDomain.getGracePeriod());
			}else{
				bufferDomain.setBufferTime(prop.getProperty(ConfigConstant.SERVER_BUFFERTIME,"5000"));
				bufferDomain.setGracePeriod(prop.getProperty(ConfigConstant.SERVER_GRACEPERIOD,"250"));
				ServerConfigSettings.bufferTime = Integer.parseInt(bufferDomain.getBufferTime());
				ServerConfigSettings.gracePeriod = Integer.parseInt(bufferDomain.getGracePeriod());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bufferDomain;
	}

	@Override
	public void setPropertyFile(File file) {
		this.propertyFile = file;
	}
	
	public boolean updateClientSettings(ClientSettings clientSettings, File file) {
		propertyFile = file;
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			prop.setProperty(ConfigConstant.UDPPORT, clientSettings.getUdpPort());
			prop.setProperty(ConfigConstant.SERVERIP, clientSettings.getServerIP());
			prop.setProperty(ConfigConstant.SERVERPORT, clientSettings.getServerPort());
			prop.store(output, null);
			setClientSettings(clientSettings);;
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	public ClientSettings getClientSettings(File file) {
		propertyFile = file;
		Properties prop = new Properties();
		ClientSettings clientSettings = new ClientSettings();
		try {
			prop = getProperties();
			clientSettings.setServerIP(prop.getProperty(ConfigConstant.SERVERIP));
			clientSettings.setServerPort(prop.getProperty(ConfigConstant.SERVERPORT));
			clientSettings.setUdpPort(prop.getProperty(ConfigConstant.UDPPORT));
		} catch (IOException io) {
			io.printStackTrace();
		} 
		return clientSettings;
	}
	
	public void setClientSettings(ClientSettings clientSettings){
		ClientConfigSettings.udpPort = clientSettings.getUdpPort();
		ClientConfigSettings.serverIP = clientSettings.getServerIP();
		ClientConfigSettings.serverPort = clientSettings.getServerPort() == null ? 7777 : Integer.parseInt(clientSettings.getServerPort());
	}
}
