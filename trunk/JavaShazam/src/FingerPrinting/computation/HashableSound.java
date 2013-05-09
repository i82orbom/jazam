package FingerPrinting.computation;

import java.util.ArrayList;

import sound.InputSoundDecoder;
import sound.exceptions.UnsuportedSampleRateException;

public class HashableSound {

	/** 
	 * Hashing variables
	 * 
	 */
	private final int LOWER_LIMIT = 80;
	private final int UPPER_LIMIT = 300;
	private final int FUZ_FACTOR = 2;

	private int[] RANGE = new int[]{LOWER_LIMIT,120,180, UPPER_LIMIT+1};

	
	private InputSoundDecoder _input = null;
	
	public HashableSound(String fileName) throws UnsuportedSampleRateException {
		try {
			this._input = new InputSoundDecoder(fileName);
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
			while( (buff = _input.getSampleBySecond((float) 1.0)) != null ){
				
		
				FastFourierTransform fftize = new FastFourierTransform(buff);
				Complex[][] fftResult = fftize.getFFTResult();	
				//System.out.println("FFTResult length: " + fftResult.length);
				hashes.add(filterAndHash(fftResult));
			}
			return hashes;
		}
		else
			throw new RuntimeException("No input specified.");
	}
	
	public byte[] arrayUnion(byte[] a1, byte[] a2){
		byte[] total = new byte[a1.length+a2.length];
		int idx = 0;
		for (int i = 0 ; i < a1.length; ++i){
			total[idx] = a1[i];
			idx++;
		}
		for (int i = 0 ; i < a2.length; ++i){
			total[idx] = a2[i];
			idx++;
		}
		return total;
	}
	
	public byte[] toMono(byte[] stereo){
		byte[] mono = new byte[stereo.length/2];
		
		int HI = 1; int LO = 0; /** Big endian, or little endian, who knows */

		for (int i = 0 ; i < mono.length/2; ++i){
			int left = (stereo[i * 4 + HI] << 8) | (stereo[i * 4 + LO] & 0xff);
	        int right = (stereo[i * 4 + 2 + HI] <<8) | (stereo[i * 4 + 2 + LO] & 0xff);
	        int avg = (left + right) / 2;
	        mono[i * 2 + HI] = (byte)((avg >> 8) & 0xff);
	        mono[i * 2 + LO] = (byte)(avg & 0xff);
		}
		
		return mono;
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
