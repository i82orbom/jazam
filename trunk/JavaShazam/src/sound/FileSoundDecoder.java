package sound;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import sound.exceptions.UnsuportedSampleRateException;

public class FileSoundDecoder extends InputSound {

	private int BUFFER_SIZE;

	private String _inputFileName;
	private File _soundFile;
	private AudioInputStream _audioInputStream;
	private AudioFormat _audioInputFormat;
	private AudioFormat _decodedFormat;
	private AudioInputStream _audioInputDecodedStream;
	private byte[] _residualBuffer;
	
	

	public FileSoundDecoder(String fileName) throws UnsuportedSampleRateException{
		this._inputFileName = fileName;
		this._soundFile = new File(this._inputFileName);
		try{
			this._audioInputStream = AudioSystem.getAudioInputStream(this._soundFile);
		}
		catch (Exception e){
			e.printStackTrace();
			System.err.println("Could not open file: " + this._inputFileName);
			System.exit(1);
		}
		
		this._audioInputFormat = this._audioInputStream.getFormat();
		
		this._decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
		this._audioInputDecodedStream = AudioSystem.getAudioInputStream(this._decodedFormat, this._audioInputStream);
			
		/** Supported sample rates */
		switch((int)this._audioInputFormat.getSampleRate()){
			case 22050:
				 	this.BUFFER_SIZE = 2304;
				break;
			
			case 44100:
					this.BUFFER_SIZE = 4608;
				break;
				
			default:
				throw new UnsuportedSampleRateException((int)this._audioInputFormat.getSampleRate());
		}
		
//		System.out.println ("# Channels: " + this._decodedFormat.getChannels());
//		System.out.println ("Sample size (bits): " + this._decodedFormat.getSampleSizeInBits());
//		System.out.println ("Frame size: " + this._decodedFormat.getFrameSize());
//		System.out.println ("Frame rate: " + this._decodedFormat.getFrameRate());

	}
	
	public byte[] getSamples(){
		byte[] abData = new byte[this.BUFFER_SIZE];
		int bytesRead = 0;
		
		try{
			bytesRead = this._audioInputDecodedStream.read(abData,0,abData.length);
		}
		catch (Exception e){
			e.printStackTrace();
			System.err.println("Error getting samples from file: " + this._inputFileName);
			System.exit(1);
		}
		
		if (bytesRead > 0)
			return abData;
		else
			return null;
	}
	
	public byte[] getSamples(int sampleQtty){
		/** If there is still some residual data in the buffer, treat it */
		byte[] buffer = new byte[sampleQtty];
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
			
			byte[] mono = toMono(sample);
			/** Dump it to to buffer */
			/** Is there room for it? */
			if (mono.length < (buffer.length - bufferPointer)){
				/** Dump it! */
				for (int i = 0 ; i < mono.length; ++i){
					buffer[bufferPointer] = mono[i];
					bufferPointer++;
				}
			}
			else{
				/** Until where can we put?? */
				int limit = buffer.length - bufferPointer;

				if (limit > 0){
					int i = 0;
					for (i = 0; i < limit; ++i){
						buffer[bufferPointer] = mono[i];
						bufferPointer++;
					}

					/** From i till sample.length, dump it to the residual buffer */
					this._residualBuffer = new byte[mono.length - i];
					for (int j = 0 ; j < this._residualBuffer.length; ++j){
						this._residualBuffer[j] = mono[i];
						i++;
					}
				}
				
			done = true;
			}
		}
		return buffer;
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
	

	public float getOutputSampleRate(){
		return this._decodedFormat.getSampleRate();
	}
}
