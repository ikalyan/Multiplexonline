package com.livedevices.web.services;

public class BufferDomain {
	
	private int fetchSequence = -1;
	private int insertSequence = -1;
	private int resetCount = 0;
	
	private int missingWindowStart = 2;//1000;
	private int missingWindowPeriods = 1;
	private long missingSequence = 0;
	private int missingSequenceNo = 0;
	private long insertTime = 0;
	private long fetchTime = 0;
	private long lastMissingSequenceProcessed = 0;
	private long missingSeqThreshold = -1;
	
	/**
	 * @return the fetchSequence
	 */
	public int getFetchSequence() {
		return fetchSequence;
	}
	/**
	 * @param fetchSequence the fetchSequence to set
	 */
	public void setFetchSequence(int fetchSequence) {
		this.fetchSequence = fetchSequence;
	}
	/**
	 * @return the insertSequence
	 */
	public int getInsertSequence() {
		return insertSequence;
	}
	/**
	 * @param insertSequence the insertSequence to set
	 */
	public void setInsertSequence(int insertSequence) {
		this.insertSequence = insertSequence;
	}
	/**
	 * @return the resetCount
	 */
	public int getResetCount() {
		return resetCount;
	}
	/**
	 * @param resetCount the resetCount to set
	 */
	public void setResetCount(int resetCount) {
		this.resetCount = resetCount;
	}
	/**
	 * @return the missingWindowStart
	 */
	public int getMissingWindowStart() {
		return missingWindowStart;
	}
	/**
	 * @param missingWindowStart the missingWindowStart to set
	 */
	public void setMissingWindowStart(int missingWindowStart) {
		this.missingWindowStart = missingWindowStart;
	}
	/**
	 * @return the missingWindowPeriods
	 */
	public int getMissingWindowPeriods() {
		return missingWindowPeriods;
	}
	/**
	 * @param missingWindowPeriods the missingWindowPeriods to set
	 */
	public void setMissingWindowPeriods(int missingWindowPeriods) {
		this.missingWindowPeriods = missingWindowPeriods;
	}
	/**
	 * @return the missingSequence
	 */
	public long getMissingSequence() {
		return missingSequence;
	}
	/**
	 * @param missingSequence the missingSequence to set
	 */
	public void setMissingSequence(long missingSequence) {
		this.missingSequence = missingSequence;
	}
	/**
	 * @return the missingSequenceNo
	 */
	public int getMissingSequenceNo() {
		return missingSequenceNo;
	}
	/**
	 * @param missingSequenceNo the missingSequenceNo to set
	 */
	public void setMissingSequenceNo(int missingSequenceNo) {
		this.missingSequenceNo = missingSequenceNo;
	}
	/**
	 * @return the insertTime
	 */
	public long getInsertTime() {
		return insertTime;
	}
	/**
	 * @param insertTime the insertTime to set
	 */
	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}
	/**
	 * @return the fetchTime
	 */
	public long getFetchTime() {
		return fetchTime;
	}
	/**
	 * @param fetchTime the fetchTime to set
	 */
	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}
	/**
	 * @return the lastMissingSequenceProcessed
	 */
	public long getLastMissingSequenceProcessed() {
		return lastMissingSequenceProcessed;
	}
	/**
	 * @param lastMissingSequenceProcessed the lastMissingSequenceProcessed to set
	 */
	public void setLastMissingSequenceProcessed(long lastMissingSequenceProcessed) {
		this.lastMissingSequenceProcessed = lastMissingSequenceProcessed;
	}
	/**
	 * @return the missingSeqThreshold
	 */
	public long getMissingSeqThreshold() {
		return missingSeqThreshold;
	}
	/**
	 * @param missingSeqThreshold the missingSeqThreshold to set
	 */
	public void setMissingSeqThreshold(long missingSeqThreshold) {
		this.missingSeqThreshold = missingSeqThreshold;
	}
	
}
