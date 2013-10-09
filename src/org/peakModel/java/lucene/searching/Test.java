package org.peakModel.java.lucene.searching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
		long totCorpus=17889710492l;
		int a = 8;
		int b = 1690025;
		long c = 1940 - a;
		long d = totCorpus - b;
		
//		double LOG_Likelyhood_corpus = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));

		double E1 = (double)1940 * (a+b) / (1940+totCorpus);
		double E2 = (double)totCorpus * (a+b) / (1940+totCorpus);
		double LOG_Likelyhood_corpus=(double) 2 * ((a * Math.log((a/E1))) + (b * Math.log((b/E2))));

		System.out.println(LOG_Likelyhood_corpus);
	}
	
	
	public static boolean includeQuery(String ngram,List<String> queryUnigrmasList){
		for(String ng:ngram.split("\\s"))
			if(queryUnigrmasList.contains(ng))
				return true;
		return false;
	}
	
	public static boolean includeStopWord(String ngram,List<String> stopWordsList){
		for(String ng:ngram.split("\\s"))
			if(stopWordsList.contains(ng))
				return true;
		return false;
	}

}
