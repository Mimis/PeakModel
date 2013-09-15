package org.peakModel.java.lucene.searching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.utils.Helper;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
//?		van de	NrOfDaysAppear:8	TF:9	TFIDF:49.60577388390642
//		de jong	NrOfDaysAppear:5	TF:8	TFIDF:49.51859647104014

		int tf_query_peak_year = 8;
		int nr_days_appear_peak_year = 5;
		double idf = Helper.log2( ((double)365/nr_days_appear_peak_year));
		double tf_idf = (double) (tf_query_peak_year) * idf;

		System.out.println(tf_idf);
		
	}
}
