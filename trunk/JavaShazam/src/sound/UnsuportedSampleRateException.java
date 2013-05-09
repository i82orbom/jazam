package sound;

public class UnsuportedSampleRateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3135051173558941161L;
	private int sampleRate;
	
	public UnsuportedSampleRateException(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public String toString(){
		return new String("Unsuported sample rate: " + this.sampleRate);
	}
}
