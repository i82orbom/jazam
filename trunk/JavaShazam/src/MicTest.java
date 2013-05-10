import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import sound.RunSampling;
import sound.exceptions.UnsuportedSampleRateException;
import sound.wav.WAVReader;
import FingerPrinting.computation.HashableSound;
import FingerPrinting.description.MPEG7Description;


public class MicTest {

	public static void main (String...args) throws UnsuportedSampleRateException{
		byte[] micData = RunSampling.runSampling();
		
	/*	for (int i = 0 ; i < micData.length; ++i){
			System.out.println(micData[i]);
		}
		*/
		WAVReader.writeWav(micData, 44100, new File("rec.wav"));
		
		HashableSound hs = new HashableSound("rec.wav");
		
		ArrayList<Long> hashes = hs.calculateHashesPerSecond(400); /** 400 ms */
		String outputXMLFile = "rec.xml";
		MPEG7Description description = new MPEG7Description();
		try {
			description.createNewFile(outputXMLFile);
			description.setAudioInfo("2", "rec.wav");
			description.setTimeStampStep(400);
			description.setFingerPrint(hashes);
			description.write();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		
	}
}
