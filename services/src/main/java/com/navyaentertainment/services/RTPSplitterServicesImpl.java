package com.navyaentertainment.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class RTPSplitterServicesImpl implements RTPSplitterServices {
	
	protected static Logger logger = Logger.getLogger(RTPSplitterServicesImpl.class);
	
	private File propertyFile;
	
	public boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			prop = getProperties();
			output = new FileOutputStream(propertyFile);
			prop.setProperty(RTPSplitterConstant.INPUT_TCP_CHANNEL_KEY, rtpSplitterChannelDomain.getInputTCPChannel());
			prop.setProperty(RTPSplitterConstant.OUTPUT_TCP_CHANNEL_KEY, rtpSplitterChannelDomain.getOutputTCPChannel());
			prop.setProperty(RTPSplitterConstant.INPUT_UDP_CHANNEL_KEY, rtpSplitterChannelDomain.getInputUDPChannel());
			prop.setProperty(RTPSplitterConstant.OUTPUT_UDP_CHANNEL_KEY, rtpSplitterChannelDomain.getOutputUDPChannel());
			prop.setProperty(RTPSplitterConstant.INPUT_TCP_PORT_KEY, rtpSplitterChannelDomain.getInputTCPChannelPort());
			prop.setProperty(RTPSplitterConstant.OUTPUT_TCP_PORT_KEY, rtpSplitterChannelDomain.getOutputTCPChannelPort());
			prop.setProperty(RTPSplitterConstant.INPUT_UDP_PORT_KEY, rtpSplitterChannelDomain.getInputUDPChannelPort());
			prop.setProperty(RTPSplitterConstant.OUTPUT_UDP_PORT_KEY, rtpSplitterChannelDomain.getOutputUDPChannelPort());
			
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getDefaultBuffer_bufferTime());
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getDefaultBuffer_fetchGracePeriod());
			prop.setProperty(RTPSplitterConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getDefaultBuffer_processMissingPackets());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_BUFFERTIME, rtpSplitterChannelDomain.getMaxBuffer_bufferTime());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_FETCHGRACEPERIOD, rtpSplitterChannelDomain.getMaxBuffer_fetchGracePeriod());
			prop.setProperty(RTPSplitterConstant.MAXBUFFER_PROCESSMISSINGPACKETS, rtpSplitterChannelDomain.getMaxBuffer_processMissingPackets());
			prop.store(output, null);

		} catch (IOException io) {
			logger.error(io.getMessage());
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
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
			channel.setInputTCPChannel(prop.getProperty(RTPSplitterConstant.INPUT_TCP_CHANNEL_KEY));
			channel.setOutputTCPChannel(prop.getProperty(RTPSplitterConstant.OUTPUT_TCP_CHANNEL_KEY));
			channel.setInputUDPChannel(prop.getProperty(RTPSplitterConstant.INPUT_UDP_CHANNEL_KEY));
			channel.setOutputUDPChannel(prop.getProperty(RTPSplitterConstant.OUTPUT_UDP_CHANNEL_KEY));
			channel.setInputTCPChannelPort(prop.getProperty(RTPSplitterConstant.INPUT_TCP_PORT_KEY));
			channel.setOutputTCPChannelPort(prop.getProperty(RTPSplitterConstant.OUTPUT_TCP_PORT_KEY));
			channel.setInputUDPChannelPort(prop.getProperty(RTPSplitterConstant.INPUT_UDP_PORT_KEY));
			channel.setOutputUDPChannelPort(prop.getProperty(RTPSplitterConstant.OUTPUT_UDP_PORT_KEY));
			
			channel.setDefaultBuffer_bufferTime(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_BUFFERTIME));
			channel.setDefaultBuffer_fetchGracePeriod(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_FETCHGRACEPERIOD));
			channel.setDefaultBuffer_processMissingPackets(prop.getProperty(RTPSplitterConstant.DEFAULTBUFFER_PROCESSMISSINGPACKETS));
			channel.setMaxBuffer_bufferTime(prop.getProperty(RTPSplitterConstant.MAXBUFFER_BUFFERTIME));
			channel.setMaxBuffer_fetchGracePeriod(prop.getProperty(RTPSplitterConstant.MAXBUFFER_FETCHGRACEPERIOD));
			channel.setMaxBuffer_processMissingPackets(prop.getProperty(RTPSplitterConstant.MAXBUFFER_PROCESSMISSINGPACKETS));
			
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
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
			logger.error(io.getMessage());
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
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
			logger.error(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return bufferDomain;
	}

	@Override
	public void setPropertyFile(File file) {
		this.propertyFile = file;
	}

	@Override
	public void updateUploadFileLocation(String fileLocation) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			File file = new File(getFileName());
			output = new FileOutputStream(file);
			prop.setProperty(RTPSplitterConstant.UPLOADFILE, fileLocation);
			prop.store(output, null);
		}catch (IOException io) {
			logger.error(io.getMessage());
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	private String getFileName() throws IOException{
		File file = new File(RTPSplitterConstant.DIRECTORY);
		if(!file.exists()){
			file.mkdir();
		}
		file = new File(RTPSplitterConstant.COMPLETE_PATH_PROPERTY_FILE);
		if(!file.exists()){
			file.createNewFile();
		}
		return RTPSplitterConstant.COMPLETE_PATH_PROPERTY_FILE;
	}
}
