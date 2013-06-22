package org.peakModel.java.lucene.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.peakModel.java.utils.Helper;

public class CreatePeakPeriodIndex {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String fileWithTFperYear = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
		String ngramFileToRead = "/Users/mimis/Development/EclipseProject/PeakModel/data/ngrams/IndexKB1gram16-17-18-19Min10TimesSorted.tsv";
		HashMap<String,Integer> peakPeriodMap = new HashMap<String,Integer>();
		
		
		
		createPeakPeriodIndex(ngramFileToRead, peakPeriodMap);
		int totalNumberOfWords = 0;
		Helper.writeLineToFile(fileWithTFperYear, "", false, false);
		for(Map.Entry<String, Integer> entry:peakPeriodMap.entrySet()){
			System.out.println("Year:"+entry.getKey()+"\tTF:"+entry.getValue());
			Helper.writeLineToFile(fileWithTFperYear, entry.getKey()+","+entry.getValue(), true, true);
			totalNumberOfWords += entry.getValue();
		}
		Helper.writeLineToFile(fileWithTFperYear, "TotalWords,"+totalNumberOfWords, true, true);
		
		
	}
	
	public static void createPeakPeriodIndex(String ngramFileToRead,HashMap<String,Integer> peakPeriodMap){
		File file = new File(ngramFileToRead);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (!line.isEmpty()){
						String[] ngramInfo = line.split("\t");
						String[] tfPerYear = ngramInfo[3].split(",");
						     
						for(String tfYear:tfPerYear){
							String[] tfYearArray = tfYear.split(":");
							String year = tfYearArray[0];
							int tf = Integer.parseInt(tfYearArray[1]);
							
							if(peakPeriodMap.containsKey(year)){
								int tfOld = peakPeriodMap.get(year);
								peakPeriodMap.put(year, tfOld+tf);
							}
							else{
								peakPeriodMap.put(year, tf);
							}	
						}
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}


}
