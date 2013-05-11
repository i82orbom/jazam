package FingerPrinting.computation.matching;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Matcher {

	public static int match(List<DataPoint> original, List<DataPoint> record){
		
		int highestOffset = -1;
		int amountOffset = 0;
		
		HashMap<Integer,Integer> offsetList = new HashMap<Integer, Integer>();
		
		// Assume original list it's bigger than record 
		int iOriginalSize = original.size();
		int iRecordSize = record.size();
		
		for (int i = 0; i < iOriginalSize; ++i){
			long cOriginalFP = original.get(i).getFingerprint();
			int cOriginalTS = original.get(i).getTimestamp();
			
			for (int j = 0; j < iRecordSize; ++j){
				long rFP = record.get(j).getFingerprint();
				int rTS = record.get(j).getTimestamp();
				
				if (rFP == cOriginalFP){
					int offset = cOriginalTS - rTS;
					
					Integer iteratedAmountOff = offsetList.get(offset);
					if (iteratedAmountOff == null){
						offsetList.put(offset, 1);
						if (amountOffset < 1){
							highestOffset = offset;
							amountOffset = 1;
						}
					}
					else{
						int newOffsetAmount = offsetList.get(offset)+1;
						offsetList.put(offset, newOffsetAmount);
						if (newOffsetAmount > amountOffset){
							highestOffset = offset;
							amountOffset = newOffsetAmount;
						}
						
					}
					
				}
			}
		}
		
		Set<Integer> keys = offsetList.keySet();
		
//		for (Integer key : keys){
//			System.out.println("OFFSET: " + key + " >> AMOUNT: " + offsetList.get(key));
//		}
		
		return amountOffset;
	}
	
}
