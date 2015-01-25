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
			
			prop.setProperty(ClientConfigConstant.DEFAULTBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getDefaultBuffer_bufferTime());
			prop.setProperty(ClientConfigConstant.DEFAULTBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getDefaultBuffer_fetchGracePeriod());
			prop.setProperty(ClientConfigConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getDefaultBuffer_processMissingPackets());
			prop.setProperty(ClientConfigConstant.MAXBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getMaxBuffer_bufferTime());
			prop.setProperty(ClientConfigConstant.MAXBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getMaxBuffer_fetchGracePeriod());
			prop.setProperty(ClientConfigConstant.MAXBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getMaxBuffer_processMissingPackets());
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
			
			channel.setDefaultBuffer_bufferTime(prop.getProperty(ClientConfigConstant.DEFAULTBUFFER_BUFFERTIME));
			channel.setDefaultBuffer_fetchGracePeriod(prop.getProperty(ClientConfigConstant.DEFAULTBUFFER_FETCHGRACEPERIOD));
			channel.setDefaultBuffer_processMissingPackets(prop.getProperty(ClientConfigConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS));
			channel.setMaxBuffer_bufferTime(prop.getProperty(ClientConfigConstant.MAXBUFFER_BUFFERTIME));
			channel.setMaxBuffer_fetchGracePeriod(prop.getProperty(ClientConfigConstant.MAXBUFFER_FETCHGRACEPERIOD));
			channel.setMaxBuffer_processMissingPackets(prop.getProperty(ClientConfigConstant.MAXBUFFER_PROCESSMISSINGPACKETS));
			
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
	public boolean updateBufferSettings(BufferDomain bufferDomain) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			prop.setProperty(ClientConfigConstant.FETCHSEQUENCE, bufferDomain.getFetchSequence()+"");
			prop.setProperty(ClientConfigConstant.INSERTSEQUENCE, bufferDomain.getInsertSequence()+"");
			prop.setProperty(ClientConfigConstant.RESETCOUNT, bufferDomain.getResetCount()+"");
			prop.setProperty(ClientConfigConstant.MISSINGWINDOWSTART, bufferDomain.getMissingWindowStart()+"");
			prop.setProperty(ClientConfigConstant.MISSINGWINDOWPERIODS, bufferDomain.getMissingWindowPeriods()+"");
			prop.setProperty(ClientConfigConstant.MISSINGSEQUENCE, bufferDomain.getMissingSequence()+"");
			prop.setProperty(ClientConfigConstant.MISSINGSEQUENCENO, bufferDomain.getMissingSequenceNo()+"");
			prop.setProperty(ClientConfigConstant.INSERTTIME, bufferDomain.getInsertTime()+"");
			
			prop.setProperty(ClientConfigConstant.FETCHTIME, bufferDomain.getFetchTime()+"");
			prop.setProperty(ClientConfigConstant.LASTMISSINGSEQUENCEPROCESSED, bufferDomain.getLastMissingSequenceProcessed()+"");
			prop.setProperty(ClientConfigConstant.MISSINGSEQTHRESHOLD, bufferDomain.getMissingSeqThreshold()+"");
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
	public BufferDomain getBufferSettings() {
		Properties prop = new Properties();
		InputStream input = null;
		BufferDomain bufferDomain = new BufferDomain();
		try {
			prop = getProperties();			
			bufferDomain.setFetchSequence(Integer.parseInt(prop.getProperty(ClientConfigConstant.FETCHSEQUENCE,"-1")));
			bufferDomain.setInsertSequence(Integer.parseInt(prop.getProperty(ClientConfigConstant.INSERTSEQUENCE,"-1")));
			bufferDomain.setResetCount(Integer.parseInt(prop.getProperty(ClientConfigConstant.RESETCOUNT,"0")));
			bufferDomain.setMissingWindowStart(Integer.parseInt(prop.getProperty(ClientConfigConstant.MISSINGWINDOWSTART,"2")));
			bufferDomain.setMissingWindowPeriods(Integer.parseInt(prop.getProperty(ClientConfigConstant.MISSINGWINDOWPERIODS,"1")));
			bufferDomain.setMissingSequence(Integer.parseInt(prop.getProperty(ClientConfigConstant.MISSINGSEQUENCE,"0")));
			bufferDomain.setMissingSequenceNo(Integer.parseInt(prop.getProperty(ClientConfigConstant.MISSINGSEQUENCENO,"0")));
			bufferDomain.setInsertTime(Integer.parseInt(prop.getProperty(ClientConfigConstant.INSERTTIME,"0")));
			bufferDomain.setFetchTime(Integer.parseInt(prop.getProperty(ClientConfigConstant.FETCHTIME,"0")));
			bufferDomain.setLastMissingSequenceProcessed(Integer.parseInt(prop.getProperty(ClientConfigConstant.LASTMISSINGSEQUENCEPROCESSED,"0")));
			bufferDomain.setMissingSeqThreshold(Integer.parseInt(prop.getProperty(ClientConfigConstant.MISSINGSEQTHRESHOLD,"-1")));
			
			
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
			prop.setProperty(ClientConfigConstant.UDPPORT, clientSettings.getUdpPort());
			prop.setProperty(ClientConfigConstant.SERVERIP, clientSettings.getServerIP());
			prop.setProperty(ClientConfigConstant.SERVERPORT, clientSettings.getServerPort());
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
			clientSettings.setServerIP(prop.getProperty(ClientConfigConstant.SERVERIP));
			clientSettings.setServerPort(prop.getProperty(ClientConfigConstant.SERVERPORT));
			clientSettings.setUdpPort(prop.getProperty(ClientConfigConstant.UDPPORT));
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
