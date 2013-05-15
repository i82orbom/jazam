package FingerPrinting.computation;

import java.util.ArrayList;
import java.util.Arrays;

import sound.FileSoundDecoder;
import sound.InputSound;
import sound.MicSoundDecoder;
import sound.exceptions.UnsuportedSampleRateException;

public class HashableSound {

	/** 
	 * Hashing variables
	 * 
	 */
	private final int LOWER_LIMIT = 40;
	private final int UPPER_LIMIT = 300;
	private final int FUZ_FACTOR = 2;

	private int[] RANGE = new int[]{LOWER_LIMIT,80,120,180, UPPER_LIMIT+1};

	private int requiredBytes1Second;
	
	private InputSound _input = null;
	
	private double[] fourier;
	
	public HashableSound(String fileName, boolean mic) throws UnsuportedSampleRateException {
		fourier = new double[4096];
		Arrays.fill(fourier, 0);
		try {
			if (mic)
				this._input = new MicSoundDecoder();
			else
				this._input = new FileSoundDecoder(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch((int)this._input.getOutputSampleRate()){
		
		case 44100:
				this.requiredBytes1Second = 176375;
			break;
			
		case 22050:
				this.requiredBytes1Second = 88187;
			break;
			
		default:
			throw new  UnsuportedSampleRateException((int)this._input.getOutputSampleRate());
		}
	}
	
	/** 
	 * Calculate _input sound hashes
	 */
	public ArrayList<Long> computeHashes(){
		
		
		if (_input != null){
			ArrayList<Long> hashes = new ArrayList<Long>();
			
			byte[] buff = null;
			while( (buff = _input.getSamples(8192)) != null ){
				//System.out.println("Buff size: " + buff.length);
				if (buff.length >= 8192){
					FFT fft = new FFT(FFT.FFT_FORWARD, 4096, FFT.WND_HAMMING);
					double im[] = new double[buff.length];
					Arrays.fill(im, 0);
					double re[] = byteToDouble(buff);
					fft.transform(re, im);
			//		fillFourierArray(re, im);
					hashes.add(filterAndHash(re,im));		
				}
			}

			return hashes;
		}
		else
			throw new RuntimeException("No input specified.");
	}
	
	public double[] byteToDouble(byte[] array){
		double[] result = new double[array.length/2];
		
		for(int i = 0; i < result.length;++i){
			byte lo = array[i*2];
			byte hi = array[i*2+1];
			
			short val=(short)( ((hi&0xFF)<<8) | (lo&0xFF) );

			result[i] = val;
		}
		
		return result;
	}
	


	private long filterAndHash(double[] re, double[] im){
		
		
		double[] highscores = new double[RANGE.length-1];
		int[] recordPoints = new int[RANGE.length-1];
		
		for (int i = 0; i < highscores.length; ++i){
			highscores[i] = -1;
			recordPoints[i] = -1;
		}
			
		for (int freq = LOWER_LIMIT; freq < UPPER_LIMIT-1; freq++){
			double mag = Math.log(Math.hypot(re[freq],im[freq])+1);
					
			int index = getIndex(freq);
					
			if (mag > highscores[index]){
				highscores[index] = mag;
				recordPoints[index] = freq;
			}
							
		}
		
		return hash(recordPoints);
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
	
	public double[] getFourierArray(){
		return this.fourier;
	}
}
