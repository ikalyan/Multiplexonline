package com.navyaentertainment.services;

import java.io.File;

import com.navyaentertainment.services.BufferDomain;
import com.navyaentertainment.services.RTPSplitterChannelDomain;

public interface DemuxClientServices {

	boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain);
	
	RTPSplitterChannelDomain getRTPSChannel();
	
	boolean updateBufferSettings(BufferDomain bufferDomain,String type);
	
	BufferDomain getBufferSettings(String type);
	
	void setPropertyFile(File file);
	
	boolean updateClientSettings(ClientSettings clientSettings, File file);
	
	ClientSettings getClientSettings(File file);
	
	void setClientSettings(ClientSettings clientSettings);
}
