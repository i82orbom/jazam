package FingerPrinting.computation.matching;

public class DataPoint {

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
