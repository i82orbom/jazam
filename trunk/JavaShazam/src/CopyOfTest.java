import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.Decoder.Params;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.OutputChannels;
import sound.Mp3FileSoundLib;
import sound.UnsuportedSampleRateException;
import sound.WAVReader;


public class CopyOfTest {
	
	public static void main(String...args) throws UnsuportedSampleRateException, UnsupportedAudioFileException, IOException, JavaLayerException{
		String inputFile = "Sehnsucht with Xavier Naidoo.mp3";
		String outputFile = "converted.wav";
		
		//	 Converter cv = new Converter();
		//Params par = new Params();
		//par.setOutputChannels(OutputChannels.fromInt(OutputChannels.DOWNMIX_CHANNELS));
		
	//	cv.convert(inputFile, outputFile, null, par);
		
	//	inputFile = outputFile;
		
		long currentTime = System.currentTimeMillis();

		Mp3FileSoundLib decod = new Mp3FileSoundLib(inputFile);
		byte[] samples;
		PrintWriter pw = new PrintWriter(new File("output1.txt"));
		
		ArrayList<byte[]> ar = new ArrayList<byte[]>();
		int qtty = 0;
		while ((samples =  decod.getSamples()) != null){
		/*	for (int i = 0; i < samples.length; ++i){
				pw.print(samples[i] + " ");
			}
			pw.println();*/
		//	ar.add(samples);
			
			byte[] mono = new byte[samples.length/2];
			for (int i = 0; i < mono.length/2; ++i){
				int left = (samples[i*4] << 8) | (samples[i*4 + 1] & 0xff);
				int right = (samples[i*4 + 2] << 8) | (samples[i*4 + 3] & 0xff);
				int avg = (left + right) / 2;
				short m = (short)avg;
				mono[i*2]= (byte)((short)(m>>8));
				mono[i*2 + 1] = (byte)(m & 0xff);
			
				
			}
			ar.add(mono);
			qtty += mono.length;
		}
		byte[] full = new byte[qtty];
		int pos = 0;
		for (int i = 0; i < ar.size(); ++i){
			byte[] temp = ar.get(i);
			for (int j = 0; j < temp.length; ++j){
				full[pos] = temp[j];
				pos++;
			}
		}
		
		WAVReader.writeWav(full, 44100, new File("dump.wav"));
		
		pw.close();
		System.out.println("Exec time: " + ((System.currentTimeMillis() - currentTime)/1000.0)/60 + " mins.");

	
		
		
	}
}
