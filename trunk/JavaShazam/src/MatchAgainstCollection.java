import java.io.File;
import java.util.List;

import sound.exceptions.UnsuportedSampleRateException;
import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.computation.matching.Matcher;
import FingerPrinting.description.MPEG7Description;


public class MatchAgainstCollection {
	
	private static List<DataPoint> recordData;

	public static void main (String...args) throws UnsuportedSampleRateException{
		
		String inputFolder = "hashes/";
		
		MPEG7Description record = new MPEG7Description();
		try {
			record.loadFromFile("rec.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		recordData = record.getData();
		System.err.println("SEARCHING MATCHES FOR: " + record.getTitle());
		
		match(new File(inputFolder));
	}
	

	public static void match(File rootDirectory) throws UnsuportedSampleRateException{
		String[] itemsInDirectory = rootDirectory.list();
		
		for (String itemInDirectory:itemsInDirectory){
			if (itemInDirectory.endsWith(".xml")){
				MPEG7Description original = new MPEG7Description();
				try {
					original.loadFromFile("hashes/" + itemInDirectory);
				}
				catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				List<DataPoint> originalData = original.getData();

				System.out.println("Match with: " + original.getTitle() + "\n\tSCORE: " + Matcher.match(originalData, recordData));

				
			}
			
		}
	}
}
