package sound;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class Mp3FileXuggler {

	private boolean DEBUG = true;
	
	private String _sInputFileName;
	private IContainer _inputContainer;
	private int _iBitRate;
	private IPacket _packet;
	private int _iAudioStreamId;
	private IStreamCoder _audioCoder;
	
	private int _iSampleBufferSize;
	private int _iInputSampleRate;
	
	private static SourceDataLine mLine;

	private int DECODED_AUDIO_SECOND_SIZE = 176375; /** bytes */
	private int _bytesPerPacket;
	
	private byte[] _residualBuffer;
	
	/**
	 * Constructor, prepares stream to be readed
	 * @param input input File
	 * @throws UnsuportedSampleRateException 
	 */
	public Mp3FileXuggler(String sFileName) throws UnsuportedSampleRateException{
		this._sInputFileName = sFileName;
		this._inputContainer = IContainer.make();
		this._iSampleBufferSize = 18432;
		this._residualBuffer = null;
		
		/** Open container **/
		if (this._inputContainer.open(this._sInputFileName, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Could not read the file: " + this._sInputFileName);
		
		/** How many streams does the file actually have */
		int iNumStreams = this._inputContainer.getNumStreams();
		this._iBitRate = this._inputContainer.getBitRate();
		
		if (DEBUG) System.out.println("Bitrate: " + this._iBitRate);
		
		/** Iterate the streams to find the first audio stream */
		this._iAudioStreamId = -1;
		this._audioCoder = null;
		boolean bFound = false;
		int i = 0;
		while (i < iNumStreams && bFound == false){
			
			/** Find the stream object */
			IStream stream = this._inputContainer.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			
			/** If the stream is audio, stop looking */
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO){
				this._iAudioStreamId = i;
				this._audioCoder = coder;
				this._iInputSampleRate = coder.getSampleRate();
				bFound = true;
			}
			++i;
		}
		
		/** If none was found */
		if (this._iAudioStreamId == -1)
			throw new RuntimeException("Could not find audio stream in container: " + this._sInputFileName);
		
		/** Otherwise, open audiocoder */
		
		if (this._audioCoder.open(null,null) < 0)
			throw new RuntimeException("could not open audio decoder for container: " + this._sInputFileName);
		
		this._packet = IPacket.make();
		
	    //openJavaSound(this._audioCoder);

	    /** Dummy read one packet to avoid problems in some audio files */
		this._inputContainer.readNextPacket(this._packet);
		

		/** Supported sample rates */
		switch(this._iInputSampleRate){
			case 22050:
				 	this._bytesPerPacket = 2304;
				break;
			
			case 44100:
					this._bytesPerPacket = 4608;
				break;
				
			default:
				throw new UnsuportedSampleRateException(this._iInputSampleRate);
		}
		
	}

	public byte[] getSamples(){
		byte[] rawBytes = null;
		
			
			/** Go to the correct packet */
			while (this._inputContainer.readNextPacket(this._packet) >= 0){
				//System.out.println(this._packet.getDuration());
				/** Once we have a packet, let's see if it belongs to the audio stream */

				if (this._packet.getStreamIndex() == this._iAudioStreamId){
					IAudioSamples samples = IAudioSamples.make(this._iSampleBufferSize, this._audioCoder.getChannels());


					//	System.out.println(">> " + samples.toString());
					/** Because a packet can contain multiple set of samples (frames of samples). We may need to call
					 * decode audio multiple times at different offsets in the packet's data */

					int iCurrentOffset = 0;
					while(iCurrentOffset < this._packet.getSize()){

						int iBytesDecoded = this._audioCoder.decodeAudio(samples, this._packet, iCurrentOffset);
						iCurrentOffset += iBytesDecoded;

						if (samples.isComplete()){
							rawBytes = samples.getData().getByteArray(0, samples.getSize());

							//playJavaSound(samples);
						}
					}
					return rawBytes;
				}
				else{
					/** Otherwise drop it */
					do{}while(false); /** WTF? **/
				}
			}
		
		return rawBytes; /** This will return null at this point */
	}
	
	public byte[] getSampleBySecond(int seconds){
		/** If there is still some residual data in the buffer, treat it */
		byte[] buffer = new byte[seconds * this.DECODED_AUDIO_SECOND_SIZE];
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
	
	private static void playJavaSound(IAudioSamples aSamples)
	  {
	    /**
	     * We're just going to dump all the samples into the line.
	     */
	    byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
	    mLine.write(rawBytes, 0, aSamples.getSize());
	  }

	private static void closeJavaSound()
	  {
	    if (mLine != null)
	    {
	      /*
	       * Wait for the line to finish playing
	       */
	      mLine.drain();
	      /*
	       * Close the line.
	       */
	      mLine.close();
	      mLine=null;
	    }
	  }
	  
	private static void openJavaSound(IStreamCoder aAudioCoder)
	  {
	    AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
	        (int)IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
	        aAudioCoder.getChannels(),
	        true, /* xuggler defaults to signed 16 bit samples */
	        false);
	    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	    try
	    {
	      mLine = (SourceDataLine) AudioSystem.getLine(info);
	      /**
	       * if that succeeded, try opening the line.
	       */
	      mLine.open(audioFormat);
	      /**
	       * And if that succeed, start the line.
	       */
	      mLine.start();
	    }
	    catch (LineUnavailableException e)
	    {
	      throw new RuntimeException("could not open audio line");
	    }
	    
	    
	  }

}
