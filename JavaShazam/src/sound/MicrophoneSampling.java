package sound;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.TargetDataLine;


public class MicrophoneSampling implements Runnable {
	
		private TargetDataLine source = null;
		private int bufferSize = 4092;
		private OutputStream out;
		private int duration = 1;
		
		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
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
			
			byte[] buffer = new byte[bufferSize];
		
			int times = 0;
			try{
				while (running){
					int count = source.read(buffer, 0, bufferSize);
					if (count > 0){
						out.write(buffer, 0, count);
					}
					
					times++;
					
					if (times == duration)
						running = false;
				}
			}
			catch (IOException e){
				System.err.println("I/O Error while recording");
			}
		}
}
