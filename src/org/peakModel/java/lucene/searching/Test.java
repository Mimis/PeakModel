package org.peakModel.java.lucene.searching;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
//		prinses beatrix(560,9951,102929)
//		huwelijk beatrix(78,13,44)
//		claus von(96,4541,8171)
		int N_query_peakPeriod = 8945;
		int N_peak = 132956608;

		int a = 96;
		int b = 4541;
		long c = N_query_peakPeriod - a;
		long d = N_peak - b;
		double LOG_Likelyhood_peak = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));

		System.out.println(LOG_Likelyhood_peak);
	}

}
