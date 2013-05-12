import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import sound.exceptions.UnsuportedSampleRateException;
import FingerPrinting.computation.HashableSound;
import FingerPrinting.description.MPEG7Description;


public class Test {
	
	public static void main(String...args) throws UnsuportedSampleRateException, FileNotFoundException{
	//String inputFile = "Sehnsucht with Xavier Naidoo.mp3";
	//String outputXMLFile = "Sehnsucht with Xavier Naidoo.xml";
		
//String inputFile = "sub_(40-60)_Sehnsucht with Xavier Naidoo.mp3";
	//String outputXMLFile = "sub_(40-60)_Sehnsucht with Xavier Naidoo.xml";
	
   String inputFile = "Wait In Vain (Avicii Remix).m3";
   String outputXMLFile = "Wait In Vain (Avicii Remix).xml";
	
//String inputFile = "Wait In Vain (Avicii Remix)_crop.mp3";
//String outputXMLFile = "Wait In Vain (Avicii Remix)_crop.xml";
	
//		String inputFile = "rec.mp3";
	//	String outputXMLFile = "rec.xml";
		long currentTime = System.currentTimeMillis();

		HashableSound song = new HashableSound(inputFile,false);
		ArrayList<Long> hashes = song.computeHashes(); 
				
		System.out.println("Exec time: " + ((System.currentTimeMillis() - currentTime)/1000.0)/60 + " mins.");

		MPEG7Description description = new MPEG7Description();
		try {
			description.createNewFile(outputXMLFile);
			description.setAudioInfo("2", inputFile);
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
