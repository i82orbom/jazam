package FingerPrinting.computation.matching;

import java.io.Serializable;

public class DataPoint implements Serializable{

	private long fingerprint;
	public long getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(long fingerprint) {
		this.fingerprint = fingerprint;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	private int timestamp;
	
	
}
