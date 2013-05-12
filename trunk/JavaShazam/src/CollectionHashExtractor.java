import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import sound.exceptions.UnsuportedSampleRateException;

import FingerPrinting.computation.HashableSound;
import FingerPrinting.description.MPEG7Description;


public class CollectionHashExtractor {

	public static void main (String...args) throws UnsuportedSampleRateException{
		String outputFolder = "hashes/";

		String inputFolder = "collection/";
		File rootDirectory = new File(inputFolder);
		String[] itemsInDirectory = rootDirectory.list();
		
		for (String itemInDirectory : itemsInDirectory){
			System.err.println("Analyzing: " + itemInDirectory);
			if (itemInDirectory.endsWith(".mp3")){
				long currentTime = System.currentTimeMillis();

				HashableSound song = new HashableSound("collection/" + itemInDirectory,false);
				ArrayList<Long> hashes = song.computeHashes();
						
				song = null;
				System.out.println("Exec time: " + ((System.currentTimeMillis() - currentTime)/1000.0)/60 + " mins.");

				MPEG7Description description = new MPEG7Description();
				try {
					description.createNewFile(outputFolder + itemInDirectory.substring(0, itemInDirectory.indexOf(".mp3"))+".xml");
					description.setAudioInfo("2", itemInDirectory);
					description.setTimeStampStep(400);
					description.setFingerPrint(hashes);
					hashes = null;
					description.write();
					description = null;
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
				Runtime.getRuntime().gc();
			}
			
		}
	}

	
}
