import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import sound.exceptions.UnsuportedSampleRateException;
import FingerPrinting.computation.HashableSound;
import FingerPrinting.description.MPEG7Description;


public class MicTest {

	public static void main (String...args) throws UnsuportedSampleRateException{
		
	
		HashableSound hs = new HashableSound(null,true);
		
		ArrayList<Long> hashes = hs.computeHashes(); 
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
