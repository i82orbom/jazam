package FingerPrinting.computation;

import java.util.ArrayList;

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
	
	public HashableSound(String fileName, boolean mic) throws UnsuportedSampleRateException {
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
	 * @param iSecondStep specifies how many seconds would be in a single sample
	 */
	public ArrayList<Long> calculateHashesPerSecond(int iMiliseconds){
		
		int bytesToRetrieve = (iMiliseconds*this.requiredBytes1Second)/1000;
		
		if (_input != null){
			ArrayList<Long> hashes = new ArrayList<Long>();
			
			byte[] buff = null;
			while( (buff = _input.getSamples(bytesToRetrieve)) != null ){

				Complex[] fftResult = FastFourierTransform.fourierTransform(byteToShortArray(buff));
				hashes.add(filterAndHash(fftResult));
				fftResult = null;
				buff = null;
			}
			this._input = null;

			return hashes;
			
		}
		else
			throw new RuntimeException("No input specified.");
	}
	
	public short[] byteToShortArray(byte[] array){
		short[] result = new short[array.length/2];
		
		for(int i = 0; i < result.length;++i){
			byte lo = array[i*2];
			byte hi = array[i*2+1];
			
			short val=(short)( ((hi&0xFF)<<8) | (lo&0xFF) );

			result[i] = val;
		}
		
		return result;
	}
	


	private long filterAndHash(Complex[] fft){
		
		
		double[] highscores = new double[RANGE.length-1];
		int[] recordPoints = new int[RANGE.length-1];
		
		for (int i = 0; i < highscores.length; ++i){
			highscores[i] = -1;
			recordPoints[i] = -1;
		}
			
		for (int freq = LOWER_LIMIT; freq < UPPER_LIMIT-1; freq++){
			double mag = Math.log(fft[freq].abs() +1);
					
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
}
