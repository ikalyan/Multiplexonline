package com.livedevices.web.services;

public interface RTPSplitterServices {

	boolean updateChannels(RTPSplitterChannelDomain rtpSplitterChannelDomain);
	
	RTPSplitterChannelDomain getRTPSChannel();
	

	boolean updateBufferSettings(BufferDomain bufferDomain);
	
	BufferDomain getBufferSettings();

}
