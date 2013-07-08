package org.peakModel.java.general_classes;

import java.util.ArrayList;
import java.util.List;

import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.utils.Helper;

public class ProcessingExperimentsOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dutchStopWordsFile =  "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/dutch.txt";
        List<String> stopWords = Helper.readFileLineByLineReturnListOfLineString(dutchStopWordsFile);
        String field = "title";
        
		List<NGram> ngramList = new ArrayList<NGram>();
		NGram n1 = new NGram("aan met","title");n1.setTf_query_peak(1);n1.setTf_peak(2);n1.setTf_corpus(1);n1.setNr_of_years_appearance(10);
		NGram n2 = new NGram("met al","title");n2.setTf_query_peak(10);n2.setTf_peak(1);n2.setTf_corpus(2);n2.setNr_of_years_appearance(1);
		
		NGram n3 = new NGram("a a","title");n3.setTf_query_peak(10);n3.setTf_peak(1);n3.setTf_corpus(2);n3.setNr_of_years_appearance(11);
		NGram n4 = new NGram("a met","title");n4.setTf_query_peak(11);n4.setTf_peak(1);n4.setTf_corpus(3);n4.setNr_of_years_appearance(100);
		
		
		NGram n5 = new NGram("to a","title");n5.setTf_query_peak(11);n5.setTf_peak(2);n5.setTf_corpus(3);n5.setNr_of_years_appearance(1);
//		NGram n6 = new NGram("b met","title");n6.setTf_query_peak(1);n6.setTf_peak(1);n6.setTf_corpus(1);n6.setNr_of_years_appearance(110);
		
//		NGram n7 = new NGram("a b","title");n7.setTf_query_peak(11);n7.setTf_peak(1);n7.setTf_corpus(1);n7.setNr_of_years_appearance(101);
//		NGram n8 = new NGram("c a","title");n8.setTf_query_peak(1);n8.setTf_peak(1);n8.setTf_corpus(1);n8.setNr_of_years_appearance(103);
//		
//		NGram n9 = new NGram("c aan","title");n9.setTf_query_peak(11);n9.setTf_peak(1);n9.setTf_corpus(1);n9.setNr_of_years_appearance(130);
		
//		NGram n10 = new NGram("a c","title");n10.setTf_query_peak(11);n10.setTf_peak(1);n10.setTf_corpus(1);n10.setNr_of_years_appearance(410);
		ngramList.add(n1);ngramList.add(n2);ngramList.add(n3);ngramList.add(n4);ngramList.add(n5);
		//ngramList.add(n6);ngramList.add(n7);ngramList.add(n8);ngramList.add(n9);ngramList.add(n10);
		
		
		
		
		
		
		ngramList = Helper.NGramPruning(ngramList, field, stopWords);
		
		
		
		
		
		
		
		
		
		
		
		
					
		for(NGram n:ngramList){
			System.out.println(n.toString());
		}
			
		
		
		
		
		
		
		
		
			
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
