import java.io.FileNotFoundException;
import java.util.List;

import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.computation.matching.Matcher;
import FingerPrinting.description.MPEG7Description;


public class MatchingTest {

	public static void main(String...args) throws FileNotFoundException{
		
		MPEG7Description original = new MPEG7Description();
		MPEG7Description record = new MPEG7Description();
		try {
			original.loadFromFile("Sehnsucht with Xavier Naidoo.xml");
			record.loadFromFile("rec.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<DataPoint> originalData = original.getData();
		List<DataPoint> recordData = record.getData();
		
		System.out.println("MATCHING SCORE: " + Matcher.match(originalData, recordData));
		
		
	}
}
