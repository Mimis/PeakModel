package org.peakModel.java.general_classes;

import java.util.List;

import org.peakModel.java.utils.Helper;

public class ProcessingExperimentsOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		String a = "100";
		if(a.matches("(\\d\\s{0,1})+"))
			System.out.println(a);
		else
			System.out.println("nooo");

			
//		String file = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";
//		List<String> ngramStatsSetCsv =  Helper.readFileLineByLineReturnListOfLineString(file);
//		long countNgrams=0;
//		for(String ngramStatsCsv:ngramStatsSetCsv){
//			String[] statsArray = ngramStatsCsv.split(",");
//			if(!statsArray[0].startsWith("TotalWords")){
//				System.out.println(statsArray[0]+"\t"+statsArray[1]);
//				countNgrams+=Integer.parseInt(statsArray[1]);
//				System.out.println(countNgrams);
//			}
//		}
//		System.out.println(countNgrams);
	}

}
