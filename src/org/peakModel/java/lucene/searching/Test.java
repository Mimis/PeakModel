package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocs;
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
		int a = 14;
		int b = 297;
		long c = 3157 - a;
		long d = 8410 - b;

		double E1 = (double)3157 * (a+b) / (3157+8410);
		double E2 = (double)8410 * (a+b) / (3157+8410);
		//@see http://ucrel.lancs.ac.uk/llwizard.html
		double firstParam =  (double)(a * Math.log((a/E1)));
		double secondParam =  (double)(b * Math.log((b/E2)));

		double LOG_Likelyhood_burst = (double) 2 * (firstParam + secondParam);

		double LOG_Likelyhood_corpus = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));

		System.out.println(E1+" "+E2+" "+firstParam+" "+secondParam+" "+LOG_Likelyhood_burst);
		System.out.println(LOG_Likelyhood_corpus);

//		beurs van	14	297	3143	8113	164.09989660582505
//		84.88173251491311 -25.23082206157192 80.98416877870636 111.50669343426888

//		beurs van	14	0.027131782945736434	111.50669343426888

	}

}
