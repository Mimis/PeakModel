package org.peakModel.java.general_classes;

import java.util.List;

import org.peakModel.java.utils.Helper;

public class ProcessingExperimentsOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String experimentsFolder = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/baseline_setup_+_search_only_titles_4_docs";
		String experimentFile = "beatrix_[1965-01-01 TO 1965-12-31]_PMIclassic.csv";
		int returnTopN = 10;
		
		List<String> ngramStatsSetCsv =  Helper.readFileLineByLineReturnListOfLineString(experimentsFolder+"/"+experimentFile);
		int countNgrams=0;
		for(String ngramStatsCsv:ngramStatsSetCsv){
			String[] statsArray = ngramStatsCsv.split(",");
//			System.out.println(statsArray[0]+"\t"+statsArray[1]);
			System.out.println(statsArray[0]+"\t"+statsArray[5]+"\t"+statsArray[1]);
//			System.out.println(statsArray[0]+"\t"+statsArray[6]+"\t"+statsArray[1]);

			if(countNgrams++ >= returnTopN)
				break;
		}
	}

}
