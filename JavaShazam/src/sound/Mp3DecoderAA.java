package sound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import com.badlogic.audio.io.MP3Decoder;


public class Mp3DecoderAA {

	private MP3Decoder decoder;
	private String _inputFileName;
	private FileInputStream _inputStream;
	
	public Mp3DecoderAA(String inputFileName){
		this._inputFileName = inputFileName;
		try {
			this._inputStream = new FileInputStream(this._inputFileName);
			this.decoder = new MP3Decoder(this._inputStream);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public float[] getSamples(){
		float []samples = new float[4096];
		int readedSamples = this.decoder.readSamples(samples);
		if (readedSamples > 0)
			return samples;
		else
			return null;
	}
}
