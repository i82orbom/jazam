package sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicSoundDecoder extends InputSound {


	private byte[] _fullBuffer;
	private int fullBufferIdx;

	public MicSoundDecoder(){
		System.out.print("Recording...");
		this._fullBuffer = runMicSampling();
		System.out.println("Done!");
		this.fullBufferIdx = 0;
	}
	
	
	public byte[] getSamples(int sampleQtty){
		int finalQtty = 0;
		if ((this._fullBuffer.length - this.fullBufferIdx) < sampleQtty){
			finalQtty = this._fullBuffer.length - this.fullBufferIdx;
		}
		else if ((this._fullBuffer.length - this.fullBufferIdx) > sampleQtty){
			finalQtty = sampleQtty;
		}
		
		if (finalQtty == 0)
			return null;
		
		byte[] result = new byte[finalQtty];
		
		int untilIdx = fullBufferIdx + finalQtty;
		int resIdx = 0;
		int i = fullBufferIdx;
		for (; i < untilIdx; ++i){
			result[resIdx] = _fullBuffer[i];
			resIdx++;
		}
		fullBufferIdx = i;
		return result;
	}
		
	private byte[] runMicSampling (){
		
		final AudioFormat format = getFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = null;
		try {
			 line = (TargetDataLine) AudioSystem.getLine(info);
			 line.open(format);
			 line.start();
		} catch (LineUnavailableException e) {
			System.err.println("Line not available");
		}
		
		MicrophoneSampling gb = new MicrophoneSampling();
		gb.setSource(line);
		gb.setDuration(30);
		
		Thread th = new Thread(gb);
		th.run();
		
		return gb.getSample();
		
	}
	

	private AudioFormat getFormat(){
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
	}
	
	public class MicrophoneSampling implements Runnable {
		
		private TargetDataLine source = null;
		private int bufferSize = 4092;
		private OutputStream out;
		private int duration;
	

		public void setDuration(int duration) {
			this.duration = duration*1000;
		}

		public byte[] getSample() {
			return ((ByteArrayOutputStream)out).toByteArray();
		}

		public TargetDataLine getSource() {
			return source;
		}

		public void setSource(TargetDataLine source) {
			this.source = source;
		}

		public void run() {
			
			out = new ByteArrayOutputStream();
			boolean running = true;
			long startTime = System.currentTimeMillis();
			
			byte[] buffer = new byte[bufferSize];
		
			try{
				while (running){
					int count = source.read(buffer, 0, bufferSize);
					if (count > 0){
						out.write(buffer, 0, count);
					}
					
					long currentTime = System.currentTimeMillis();
					
					if (currentTime-startTime >= this.duration)
						running = false;
					
				}
			}
			catch (IOException e){
				System.err.println("I/O Error while recording");
			}
		}
}

	@Override
	public float getOutputSampleRate() {
		return 44100;
	}


	@Override
	public byte[] getSamples() {
		return getSamples(9216);
	}

}

