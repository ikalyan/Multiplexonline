package com.navyaentertainment;

import java.util.ArrayList;
import java.util.Date;

public class RTPBuffer {
	
	private RTPDatagramPacket[] packets = new RTPDatagramPacket[65536];
	private int fetchSequence = -1;
	private int insertSequence = -1;
	private int resetCount = 0;
	private int bufferTime = 1500; // milli seconds
	private int fetchGracePeriod = 500; // milli seconds
	
	private int missingWindowStart = 4;//1000;
	private int missingWindowPeriods = 1;
	private long lastInsertMissingSequence = 0;
	private int missingSequenceNo = 0;
	boolean serverProcessMissingPackets = false;
	private long insertTime = 0;
	private long fetchTime = 0;
	private long lastMissingSequenceProcessed = 0;
	private long missingSeqThreshold = -1;
	
	private MaxSizeHashMap<Long, Integer> msMap = new MaxSizeHashMap<Long, Integer>(4);
	public ArrayList<Integer> serverMissingPackets = new ArrayList<Integer>();
	public ArrayList<Integer> clientMissingPackets = new ArrayList<Integer>();
	
	public RTPBuffer(int bufferTime, int fetchGracePeriod, boolean processMissingPackets) {
		System.out.println("BUFFER SETTINGS " + bufferTime + ":" + fetchGracePeriod); 
		for (int i=0; i<65536; i++) {
			packets[i] = new RTPDatagramPacket();
		}
		this.bufferTime = bufferTime;
		this.fetchGracePeriod = fetchGracePeriod;
		
//		long currTime = new Date().getTime();
//		missingSequence = (currTime / missingWindowStart);
		this.serverProcessMissingPackets = processMissingPackets;
	}
	
	public RTPBuffer() {
		for (int i=0; i<65536; i++) {
			packets[i] = new RTPDatagramPacket();
		}

//		long currTime = new Date().getTime();
//		missingSequence = (currTime / missingWindowStart);
	}
	
	synchronized public RTPDatagramPacket insert(RTPDatagramPacket packet) {
		
		/* Check for duplicate packet, could occur due to retransmission of Broadcast mode */
		RTPDatagramPacket returnPacket = packets[packet.getSequenceNumber()];
		if (returnPacket.getMissingSequence() != -1 && returnPacket.getMissingSequence() == packet.getMissingSequence())  {
			System.out.println("******** Missing Sequence is : *********" + returnPacket.getMissingSequence());
			return null;
		}
		
		long recieveTime = packet.getRecieveTime(); //new Date().getTime();
		if (insertSequence == -1) {
			insertSequence = packet.getSequenceNumber();
			fetchSequence = insertSequence;
			lastMissingSequenceProcessed = packet.getMissingSequence();
		}
		if (insertTime != 0 && insertTime < (recieveTime - bufferTime) && (packet.getMissingSequence() > lastMissingSequenceProcessed)) {
			// There was no insert for a long time
			System.out.println("There is not insert for long time resetting buffer" + (recieveTime - insertTime));
			resetBuffer();
		}
		insertTime = recieveTime;
		packets[packet.getSequenceNumber()] = packet;
		updateInsertSequence(packet.getSequenceNumber());
			
		long currSequence = packet.getMissingSequence();//(recieveTime / missingWindowStart);
		//packet.setMissingSequence(currSequence);
		
		if (serverProcessMissingPackets) {
			
			// Update missing sequence map;
			Integer mapSeqNumber = msMap.get(currSequence);
			if (mapSeqNumber == null)  {
				msMap.put(currSequence, packet.getSequenceNumber());

				//System.out.println("Missing Sequence Map : " +  msMap);
			} else {
				if (packet.getSequenceNumber() < mapSeqNumber && ((packet.getSequenceNumber() + 1000)%65536) > packet.getSequenceNumber()) {
					msMap.put(currSequence, packet.getSequenceNumber());

					//System.out.println("Missing Sequence Map : " +  msMap);
				}
			}
			// Completed Map update
			// Process a sequence for missing packets;
			
			long starSeqToBeProcessed = currSequence - missingWindowStart;
			long endSeqTobeProcessed = starSeqToBeProcessed - missingWindowPeriods;
			if (starSeqToBeProcessed > 0 && endSeqTobeProcessed > 0) {
				collectMissingPackets(starSeqToBeProcessed, endSeqTobeProcessed);
			}
			
		} 
		return returnPacket; // Packet that can be reused with out being GCed.
	}
	
	private void collectMissingPackets(long start, long end) {
		if (serverMissingPackets.size() > 0) {
			long priorSequence = -1;
			for (int i=0; i<serverMissingPackets.size(); i++) {
				long seq = packets[serverMissingPackets.get(i).intValue()].getMissingSequence();
				if (priorSequence != seq) {
					System.out.println(" Server Missing Packet SEQ: " + seq);
					priorSequence = seq;
				}
			}
		}
		serverMissingPackets.clear();
		if (start == lastMissingSequenceProcessed) return;
		lastMissingSequenceProcessed = start;
		Integer endSeq = msMap.get(end);
		if (endSeq == null) return;
		Integer startSeq = msMap.get(start);
		if (startSeq == null) return;
		//System.out.println(Thread.currentThread().getName() +" Collection Missing packets " + startSeq + ":" +endSeq + "::" + packets[startSeq].getMissingSequence());
		int seqNum = endSeq;
		while(seqNum != startSeq) {
			RTPDatagramPacket packet = packets[seqNum];
			if (packet.getMissingSequence() < end) {
				//System.out.println("Packet MSEQ :" + packet.getMissingSequence() + ": end : start :" + end + ":" + start + " :SEQ: " + packet.getSequenceNumber());
				serverMissingPackets.add(seqNum);
			}
			seqNum = (seqNum + 1) % 65536;
		}
		if(serverMissingPackets.size() > 0) System.out.println("Server Missing packets Size: " + serverMissingPackets.size());
	}
	
	private void updateInsertSequence(int currSequence) {
		/* do nothing for missing or out of sequence packet
		 * Also take care of rollover condition
		 */
		if (currSequence < insertSequence) return;
		if (((currSequence + 10000) % 65536) < ((insertSequence + 10000) % 65536)) {
			// nothing to do, to take care of rollover condition like 
			// SEQ 65000 packet being inserted while SEQ 2 packet has been already inserted.
			return;
		}
		insertSequence = currSequence % 65536;
	}
	
	synchronized public RTPDatagramPacket getPacket() {
		if (insertSequence == -1) return null;
		if (fetchSequence == -1) fetchSequence = insertSequence;
		long fetchtime = new Date().getTime();
		/* there is no recent insert */
		if ((insertTime + (bufferTime + fetchGracePeriod - fetchGracePeriod - 100)) < fetchtime) {
			resetBuffer();
			return null;
		}
		RTPDatagramPacket packet = null;
		while (true) {
			/**
			 * First process any missing packets that can be sent.
			 */
			if (clientMissingPackets.size() > 0) {
				System.out.println("Missing packets " + clientMissingPackets);
				packet = packets[clientMissingPackets.get(0)];
				System.out.println("Missing packet send time " + packet.getSendTime() + " : " + packet.getSequenceNumber());
				clientMissingPackets.remove(0);
				if ((fetchtime - packet.getSendTime()) > 10000) continue; 
				System.out.println("Client Buffer returning Missing packet :" + packet.getSequenceNumber());
				return packet;
			}
			
			packet = packets[fetchSequence];
			long elapsedTime = fetchtime - packet.getRecieveTime();

			//System.out.println("BT : FGP : ET " + bufferTime + " : " + fetchGracePeriod + " : " + elapsedTime);
			if (packet.getSequenceNumber() != fetchSequence) {
				fetchSequence = (fetchSequence + 1) % 65536;
				continue;
			}
			if (elapsedTime > (bufferTime + fetchGracePeriod))  {
				fetchSequence = (fetchSequence + 1) % 65536;
				continue;
			}
			if ((elapsedTime <= (bufferTime + fetchGracePeriod)) && (elapsedTime >= bufferTime) || 
					//Taking care of missing packets where the receiev time does not fit into the buffer logic
				((missingSeqThreshold != -1) && (missingSeqThreshold >= packet.getMissingSequence()) && (elapsedTime < bufferTime -1000))) {
				fetchSequence = (fetchSequence + 1) % 65536;
				missingSeqThreshold = packet.getMissingSequence() + 1;
				return packet;
			}
			return null;
		}
	}
	
	
	// To be used for retransmitting missing packets
	synchronized public RTPDatagramPacket peekPacket(int sequenceNumber) {
		if (fetchSequence == -1) return null;
		return packets[sequenceNumber];
	}
	
	synchronized public void resetBuffer() {
		System.out.println("Buffer resetting " + Thread.currentThread().getName());
		fetchSequence = -1;
		insertSequence = -1;
		insertTime = 0;
		fetchTime = 0;
		lastInsertMissingSequence = 0;
		missingSequenceNo = 0;
		msMap.clear();
		serverMissingPackets.clear();
		clientMissingPackets.clear();
		resetCount++;
		for (int i=0; i<packets.length; i++) {
			packets[i].setMissingSequence(-1);
		}
	}
	
	synchronized void setClientMissingPackets(ArrayList<Integer> missingPackets) {
		long time = new Date().getTime();
		for (Integer seq : missingPackets) {
			RTPDatagramPacket packet = packets[seq.intValue()];
			if ((time - packet.getSendTime()) < 10000) {
				this.clientMissingPackets.add(seq);
			} else {
				System.out.println("Missing packets delay:" + (time - packet.getSendTime()));
			}
		}
		System.out.println("Client Missing packets :"  + this.clientMissingPackets);
	}
}
