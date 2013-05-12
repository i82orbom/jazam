import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import sound.FileSoundDecoder;
import sound.exceptions.UnsuportedSampleRateException;
import sound.wav.WAVReader;


public class CopyOfTest {
	
	public static void main(String...args) throws UnsuportedSampleRateException, UnsupportedAudioFileException, IOException, JavaLayerException{
		String inputFile = "Sehnsucht with Xavier Naidoo.mp3";
		
		//	 Converter cv = new Converter();
		//Params par = new Params();
		//par.setOutputChannels(OutputChannels.fromInt(OutputChannels.DOWNMIX_CHANNELS));
		
	//	cv.convert(inputFile, outputFile, null, par);
		
	//	inputFile = outputFile;
		
		long currentTime = System.currentTimeMillis();

		FileSoundDecoder decod = new FileSoundDecoder(inputFile);
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
		//	if (samples[0] != 0)
	//			System.out.println("");
			int HI = 1; int LO = 0;

			byte[] mono = new byte[samples.length/2];
			for (int i = 0; i < mono.length/2; ++i){
				

					int left = (samples[i * 4 + HI] << 8) | (samples[i * 4 + LO] & 0xff);
			        int right = (samples[i * 4 + 2 + HI] <<8) | (samples[i * 4 + 2 + LO] & 0xff);
			        int avg = (left + right) / 2;
			        mono[i * 2 + HI] = (byte)((avg >> 8) & 0xff);
			        mono[i * 2 + LO] = (byte)(avg & 0xff);
				
				
			      
							
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
