package FingerPrinting.computation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import sound.Mp3DecoderAA;
import sound.Mp3FileSoundLib;
import sound.UnsuportedSampleRateException;

public class HashableSound {

	/** 
	 * Hashing variables
	 * 
	 */
	private final int LOWER_LIMIT = 80;
	private final int UPPER_LIMIT = 300;
	private final int FUZ_FACTOR = 2;

	private int[] RANGE = new int[]{LOWER_LIMIT,120,180, UPPER_LIMIT+1};

	
	private Mp3FileSoundLib _input = null;
	
	public HashableSound(String fileName) throws UnsuportedSampleRateException {
		try {
			this._input = new Mp3FileSoundLib(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * Calculate _input sound hashes
	 * @param iSecondStep specifies how many seconds would be in a single sample
	 */
	public ArrayList<ArrayList<Long>> calculateHashesPerSecond(int iSecondStep){
		if (_input != null){
			ArrayList<ArrayList<Long>> hashes = new ArrayList<ArrayList<Long>>();
			
			byte[] buff = null;
			while( (buff = _input.getSamples()) != null ){
				
				/** Convert to mono */
				
				byte[] mono = new byte[buff.length/2];
				for (int i = 0; i < mono.length/2; ++i){
					int left = (buff[i*4] << 8) | (buff[i*4 + 1] & 0xff);
					int right = (buff[i*4 + 2] << 8) | (buff[i*4 + 3] & 0xff);
					int avg = (left + right) / 2;
					short m = (short)avg;
					mono[i*2]= (byte)((short)(m>>8));
					mono[i*2 + 1] = (byte)(m & 0xff);
				}
				
				
				FastFourierTransform fftize = new FastFourierTransform(mono);
				Complex[][] fftResult = fftize.getFFTResult();	
				//System.out.println("FFTResult length: " + fftResult.length);
				hashes.add(filterAndHash(fftResult));
			}
			return hashes;
		}
		else
			throw new RuntimeException("No input specified.");
	}
	

	private ArrayList<Long> filterAndHash(Complex[][] fft){
		
		double[] highscores = new double[RANGE.length-1];
		int[] recordPoints = new int[RANGE.length-1];
		
		for (int i = 0; i < highscores.length; ++i){
			highscores[i] = -1;
			recordPoints[i] = -1;
		}
		
		
		ArrayList<Long> hashes = new ArrayList<Long>();
		for (int i = 0; i < fft.length; ++i){
		
			//System.out.println("Freq amount: " + fft[i].length);
				
			for (int freq = LOWER_LIMIT; freq < UPPER_LIMIT-1; freq++){
				double mag = Math.log(fft[i][freq].abs() +1);
					
				int index = getIndex(freq);
			//	System.out.println("Freq: " + freq + " index: " + index);
					
				if (mag > highscores[index]){
					highscores[index] = mag;
					recordPoints[index] = freq;
				}
							
			}

			hashes.add(hash(recordPoints));

		}
		return hashes;
	}

	
	private long hash(int []p){
		int multiplyFactor = 100;
		int currentMultiplier = 1;
		long sum = 0;
		
		for (int i = 0; i < p.length; ++i){
			sum += (p[i] - (p[i]%FUZ_FACTOR)) * currentMultiplier;
			currentMultiplier *= multiplyFactor;
		}
		return sum;
	}
	
	private int getIndex(int freq){
		int i = 0;
		while (i < RANGE.length-1){
			if (freq >= RANGE[i] && freq < RANGE[i+1])
				return i;
			i++;
		}
		return i;	
	}
}
