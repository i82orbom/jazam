package sound;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CopyOfMp3FileSoundLib {

	public static void main (String...args){
		String inputFile = "sub_(40-60)_Sehnsucht with Xavier Naidoo.mp3";
		File input = new File (inputFile);
		
		AudioFileFormat baseFileFormat;
			try {
				baseFileFormat = AudioSystem.getAudioFileFormat(input);
				Map properties = baseFileFormat.properties();
				Long duration = (Long) properties.get("duration");
				double dur = duration /1000000.0;
				System.out.println("Duration >>" + dur);

				AudioInputStream in = AudioSystem.getAudioInputStream(input);
				AudioInputStream din = null;
				AudioFormat baseFormat = in.getFormat();
				 	 float sampleRate = 44100;
				     int sampleSizeInBits = 8;
				     int channels = 2; //stereo
				     boolean signed = true;
				     boolean bigEndian = true;
				 
				      
				AudioFormat decodedFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
				din = AudioSystem.getAudioInputStream(decodedFormat, in);
				
				int readBytes = 0;
				byte[] audioBytes = new byte[4608];
				int totalBytes = 0;
				int packets = 0;
				while ( (readBytes = din.read(audioBytes)) != -1 ){
					totalBytes += readBytes;
					packets++;
					System.out.println("Actual read bytes : " + readBytes);
				}
				
				System.out.println("Read bytes: " + totalBytes + " PACKS: " + packets );
				
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
	
	}
	
}
