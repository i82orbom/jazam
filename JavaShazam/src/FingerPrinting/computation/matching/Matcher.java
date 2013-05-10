package FingerPrinting.computation.matching;

import java.util.ArrayList;
import java.util.List;

public class Matcher {

	public static boolean match(List<DataPoint> original, List<DataPoint> record){
		
		int iOriginalSize = original.size();
		int iRecordSize = record.size();
		int iOriginalIdx = 5; /** First 5 fingerptins are skipped */
		boolean done = false;
		
		int OFFSET_MATCH_THRESHOLD = 20;
		int iOriginalIdxPost = 0;
		int iRecordIdxPost = 0;
		
		for (int i = 5 ; i < iRecordSize - 1 && done == false; ++i){ /** First 5 fingerprints of record are skipped */
			
			/** First of all find two sucessive matches */
			int numMatches = 0;
			
			while (numMatches != 2 && iOriginalIdx < iOriginalSize){
				if (original.get(iOriginalIdx).getFingerprint() == record.get(i).getFingerprint()){
					numMatches++;
				}
				iOriginalIdx++;
				if (numMatches == 1){
					if (original.get(iOriginalIdx).getFingerprint() == record.get(i+1).getFingerprint()){
						numMatches++;
					}
					else
						numMatches = 0;
					
					iOriginalIdx++;
				}
			
			}
			if (numMatches == 2){
				/** At this point we have two matches */
			//	System.out.println("Found two consecutive matches...");
			//	System.out.println("Timestamp1(original) = " + original.get(iOriginalIdx-2).getTimestamp() + " // Timestamp1(record) = " + record.get(i).getTimestamp());
			//	System.out.println("Timestamp2(original) = " + original.get(iOriginalIdx-1).getTimestamp() + " // Timestamp2(record) = " + record.get(i+1).getTimestamp());
			//	System.out.println("OFFSET: " + (original.get(iOriginalIdx-2).getTimestamp()-record.get(i).getTimestamp()));

				/** KEEP LOOKING from iOriginalIdx */
				done = true;
				iOriginalIdxPost = iOriginalIdx;
				iRecordIdxPost = i;
			}
			else{
				/** No two consecutive matches */
			//	System.out.println("No two consecutive matches...\n NO MATCH AT ALL!");
			//	done = true;
			}
			
			iOriginalIdx = 5;
		}
		
		/** Once this part is done, we have in iOriginalIdxPost-1 the last index where the two consecutives matches were found */
		/** And in iRecordIdxPost the last index from original record where the fingerprint matched */
		
		/** Try to find OFFSET_MATCH_THRESHOLD matches with the same offset */
		ArrayList<Integer> listOffsets = new ArrayList<Integer>();
		ArrayList<Integer> offsetQtty = new ArrayList<Integer>();
		int j;
		done = false;
		for (int i = iRecordIdxPost; i < iRecordSize && done == false; ++i){
			/** If I go trough 50 fingerprints from the original list, without finding match, give up */
			long recordFingerPrint = record.get(i).getFingerprint();
			int recordTimeStamp = record.get(i).getTimestamp();
			
			for (j = iOriginalIdxPost; j < iOriginalSize && j < (iOriginalIdxPost+50) && done == false; ++j){
				long originalFingerPrint = original.get(j).getFingerprint();
				int originalTimeStamp = original.get(j).getTimestamp();
				
				int timeOffset = originalTimeStamp - recordTimeStamp;
				if (originalFingerPrint == recordFingerPrint){
					int idxValue = listOffsets.indexOf(timeOffset);
					if (idxValue < 0){ /** It's not in the list, yet */
						listOffsets.add(timeOffset);
						offsetQtty.add(1);
					}
					else{
						int currentOffsetQtty = offsetQtty.get(idxValue) + 1;
						offsetQtty.set(idxValue, currentOffsetQtty);
						
						if (currentOffsetQtty == OFFSET_MATCH_THRESHOLD){
							done = true;
							return true;
						}
					}
				}
			}
			//iOriginalIdxPost = j;
		}
		
		return false;
	}
}
