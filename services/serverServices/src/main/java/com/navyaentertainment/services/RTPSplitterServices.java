package com.navyaentertainment.services;

import java.io.File;

import com.navyaentertainment.services.BufferDomain;
import com.navyaentertainment.services.RTPSplitterChannelDomain;

public interface RTPSplitterServices {

	boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain);
	
	RTPSplitterChannelDomain getRTPSChannel();
	
	boolean updateBufferSettings(BufferDomain bufferDomain);
	
	BufferDomain getBufferSettings();
	
	void setPropertyFile(File file);

}
