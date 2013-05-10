import java.util.List;

import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.computation.matching.Matcher;
import FingerPrinting.description.MPEG7Description;


public class MatchingTest {

	public static void main(String...args){
		
		MPEG7Description original = new MPEG7Description();
		MPEG7Description record = new MPEG7Description();
		try {
			original.loadFromFile("Wait In Vain (Avicii Remix).xml");
			record.loadFromFile("Sehnsucht with Xavier Naidoo.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<DataPoint> originalData = original.getData();
		List<DataPoint> recordData = record.getData();
		
		if(Matcher.match(originalData, recordData)){
			System.out.println("MATCH!");
		}
		else{
			System.out.println("NO MATCH AT ALL!");
		}
		
	}
}
