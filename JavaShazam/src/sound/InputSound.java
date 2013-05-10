package sound;

public abstract class InputSound {

	public abstract byte[] getSamples(int sampleQtty);
	public abstract float getOutputSampleRate();
}
