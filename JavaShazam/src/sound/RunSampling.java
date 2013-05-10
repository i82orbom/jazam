package sound;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class RunSampling {
	
	public static byte[] runSampling (){
		
		final AudioFormat format = getFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = null;
		try {
			 line = (TargetDataLine) AudioSystem.getLine(info);
			 line.open(format);
			 line.start();
		} catch (LineUnavailableException e) {
			System.err.println("L’nea disponible");
		}
		
		MicrophoneSampling gb = new MicrophoneSampling();
		gb.setSource(line);
		gb.setDuration(100);
		
		Thread th = new Thread(gb);
		th.run();
		
		return gb.getSample();
		
	}
	

	private static AudioFormat getFormat(){
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
	}
}
