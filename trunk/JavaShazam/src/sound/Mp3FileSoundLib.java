package sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.converter.WaveFile;

public class Mp3FileSoundLib{
	
	private boolean DEBUG = true;

	private String _sInputFileName;
	private int _iSampleBufferSize;
	private float _iInputSampleRate;
	
	private int DECODED_AUDIO_SECOND_SIZE = 176375; /** bytes */

	private int _bytesPerPacket;
	
	private byte[] _residualBuffer;
	
	private File _input;

	private AudioFileFormat baseFileFormat;
	private AudioInputStream audioInputStream;
	
	private AudioFormat decodedFormat;
	private AudioInputStream audioDecodedInputStream;
	
	public Mp3FileSoundLib(String sFileName) throws UnsuportedSampleRateException, UnsupportedAudioFileException, IOException{
		this._sInputFileName = sFileName;
		this._iSampleBufferSize = 18432;
		this._residualBuffer = null;
		this._input = new File (this._sInputFileName);
		this.baseFileFormat = AudioSystem.getAudioFileFormat(this._input);
		this.audioInputStream = AudioSystem.getAudioInputStream(this._input);
		this._iInputSampleRate = this.baseFileFormat.getFormat().getSampleRate();

		
	/*	 float sampleRate = 44100;
	     int sampleSizeInBits = 16;
	     int channels = 2; //stereo
	     boolean signed = true;
	     boolean bigEndian = false;*/
	  
	 
	      
	    this.decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, this._iInputSampleRate, 16, 2, 2, this._iInputSampleRate, false);
		this.audioDecodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, this.audioInputStream);
		
		System.out.println ("# Channels: " + this.decodedFormat.getChannels());
		System.out.println ("Sample size (bits): " + this.decodedFormat.getSampleSizeInBits());
		System.out.println ("Frame size:: " + this.decodedFormat.getFrameSize());

		
		/** Supported sample rates */
		switch((int)this._iInputSampleRate){
			case 22050:
				 	this._bytesPerPacket = 2304;
				break;
			
			case 44100:
					this._bytesPerPacket = 4608;
				break;
				
			default:
				throw new UnsuportedSampleRateException((int) this._iInputSampleRate);
		}
		
	}
	
	public byte[] getSamples(){
		byte[] audioBytes = new byte[this._bytesPerPacket];
		try {
			if ( (this.audioDecodedInputStream.read(audioBytes)) != -1 ){

				return audioBytes;
			}
			else{
				return null;
			}
		} catch (IOException e) {
			System.err.println("I/O Error getSamples()");
			return null;
		}
	}
	
	
	public byte[] getSampleBySecond(float seconds){
		/** If there is still some residual data in the buffer, treat it */
		byte[] buffer = new byte[ (int)(seconds * this.DECODED_AUDIO_SECOND_SIZE)];
		int bufferPointer = 0;
		boolean done = false;
		
		/** It's supossed that the residual buffer is always less than the main buffer */
		if (this._residualBuffer != null){
			for (int i = 0; i < this._residualBuffer.length; ++i){
				buffer[bufferPointer] = this._residualBuffer[i];
				bufferPointer++;
			}
			/** 'Flush it' */
			this._residualBuffer = null;
		}
		
		while (!done){
			/** Get new sample */
			byte[] sample = getSamples();
			
			if (sample == null){ /** This will happen in the last one */
				done = true;
				if (bufferPointer > 0){
					byte[] rest = new byte[bufferPointer];
					for (int i = 0; i < rest.length; ++i){
						rest[i] = buffer[i];
					}
					return rest;
				}
				else
					return null;
			}
			
			/** Dump it to to buffer */
			/** Is there room for it? */
			if (sample.length < (buffer.length - bufferPointer)){
				/** Dump it! */
				for (int i = 0 ; i < sample.length; ++i){
					buffer[bufferPointer] = sample[i];
					bufferPointer++;
				}
			}
			else{
				/** Until where can we put?? */
				int limit = buffer.length - bufferPointer;

				if (limit > 0){
					int i = 0;
					for (i = 0; i < limit; ++i){
						buffer[bufferPointer] = sample[i];
						bufferPointer++;
					}

					/** From i till sample.length, dump it to the residual buffer */
					this._residualBuffer = new byte[sample.length - i];
					for (int j = 0 ; j < this._residualBuffer.length; ++j){
						this._residualBuffer[j] = sample[i];
						i++;
					}
				}
				
			done = true;
			}
		}
		return buffer;
	}
	
}