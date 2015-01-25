package com.navyaentertainment.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class RTPSplitterServicesImpl implements RTPSplitterServices {
	
	private File propertyFile;
	
	public boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getDefaultBuffer_bufferTime());
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getDefaultBuffer_fetchGracePeriod());
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getDefaultBuffer_processMissingPackets());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getMaxBuffer_bufferTime());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getMaxBuffer_fetchGracePeriod());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getMaxBuffer_processMissingPackets());
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
			
			channel.setDefaultBuffer_bufferTime(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_BUFFERTIME));
			channel.setDefaultBuffer_fetchGracePeriod(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_FETCHGRACEPERIOD));
			channel.setDefaultBuffer_processMissingPackets(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS));
			channel.setMaxBuffer_bufferTime(prop.getProperty(RTPSplitterConstant.MAXBUFFER_BUFFERTIME));
			channel.setMaxBuffer_fetchGracePeriod(prop.getProperty(RTPSplitterConstant.MAXBUFFER_FETCHGRACEPERIOD));
			channel.setMaxBuffer_processMissingPackets(prop.getProperty(RTPSplitterConstant.MAXBUFFER_PROCESSMISSINGPACKETS));
			
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
			prop.setProperty(RTPSplitterConstant.FETCHSEQUENCE, bufferDomain.getFetchSequence()+"");
			prop.setProperty(RTPSplitterConstant.INSERTSEQUENCE, bufferDomain.getInsertSequence()+"");
			prop.setProperty(RTPSplitterConstant.RESETCOUNT, bufferDomain.getResetCount()+"");
			prop.setProperty(RTPSplitterConstant.MISSINGWINDOWSTART, bufferDomain.getMissingWindowStart()+"");
			prop.setProperty(RTPSplitterConstant.MISSINGWINDOWPERIODS, bufferDomain.getMissingWindowPeriods()+"");
			prop.setProperty(RTPSplitterConstant.MISSINGSEQUENCE, bufferDomain.getMissingSequence()+"");
			prop.setProperty(RTPSplitterConstant.MISSINGSEQUENCENO, bufferDomain.getMissingSequenceNo()+"");
			prop.setProperty(RTPSplitterConstant.INSERTTIME, bufferDomain.getInsertTime()+"");
			
			prop.setProperty(RTPSplitterConstant.FETCHTIME, bufferDomain.getFetchTime()+"");
			prop.setProperty(RTPSplitterConstant.LASTMISSINGSEQUENCEPROCESSED, bufferDomain.getLastMissingSequenceProcessed()+"");
			prop.setProperty(RTPSplitterConstant.MISSINGSEQTHRESHOLD, bufferDomain.getMissingSeqThreshold()+"");
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
			bufferDomain.setFetchSequence(Integer.parseInt(prop.getProperty(RTPSplitterConstant.FETCHSEQUENCE,"-1")));
			bufferDomain.setInsertSequence(Integer.parseInt(prop.getProperty(RTPSplitterConstant.INSERTSEQUENCE,"-1")));
			bufferDomain.setResetCount(Integer.parseInt(prop.getProperty(RTPSplitterConstant.RESETCOUNT,"0")));
			bufferDomain.setMissingWindowStart(Integer.parseInt(prop.getProperty(RTPSplitterConstant.MISSINGWINDOWSTART,"2")));
			bufferDomain.setMissingWindowPeriods(Integer.parseInt(prop.getProperty(RTPSplitterConstant.MISSINGWINDOWPERIODS,"1")));
			bufferDomain.setMissingSequence(Integer.parseInt(prop.getProperty(RTPSplitterConstant.MISSINGSEQUENCE,"0")));
			bufferDomain.setMissingSequenceNo(Integer.parseInt(prop.getProperty(RTPSplitterConstant.MISSINGSEQUENCENO,"0")));
			bufferDomain.setInsertTime(Integer.parseInt(prop.getProperty(RTPSplitterConstant.INSERTTIME,"0")));
			bufferDomain.setFetchTime(Integer.parseInt(prop.getProperty(RTPSplitterConstant.FETCHTIME,"0")));
			bufferDomain.setLastMissingSequenceProcessed(Integer.parseInt(prop.getProperty(RTPSplitterConstant.LASTMISSINGSEQUENCEPROCESSED,"0")));
			bufferDomain.setMissingSeqThreshold(Integer.parseInt(prop.getProperty(RTPSplitterConstant.MISSINGSEQTHRESHOLD,"-1")));
			
			
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
			prop.setProperty(RTPSplitterConstant.UDPPORT, clientSettings.getUdpPort());
			prop.setProperty(RTPSplitterConstant.SERVERIP, clientSettings.getServerIP());
			prop.setProperty(RTPSplitterConstant.SERVERPORT, clientSettings.getServerPort());
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
			clientSettings.setServerIP(prop.getProperty(RTPSplitterConstant.SERVERIP));
			clientSettings.setServerPort(prop.getProperty(RTPSplitterConstant.SERVERPORT));
			clientSettings.setUdpPort(prop.getProperty(RTPSplitterConstant.UDPPORT));
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
