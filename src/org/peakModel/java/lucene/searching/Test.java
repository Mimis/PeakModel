package org.peakModel.java.lucene.searching;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.utils.HelperLucene;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
//		  RANK,ngram ,total_tf_query,df_query,df_corpus,df_time
//        1,lshiguro,2,1,1,1
//        143,bernhard's,1,1,10,1
//        668,bernhard,23,23,12935,322

		
		String indexKbCorpusFileName = "./index/test";
        Directory indexDir = HelperLucene.getIndexDir(indexKbCorpusFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
		String dutchStopWordFile = "./data/stopWords/dutch.txt";
        Analyzer analyzer = HelperLucene.getKbAnalyzer(dutchStopWordFile);
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},analyzer);

		
		
		String ngram = "ROTTERDAM";
		String date = "date:[1956-01-01 TO 1996-12-31]";
		
		final int df_corpus = reader.docFreq(new Term("title", ngram));					
		int df_time = HelperLucene.getNgramDf(ngram, date, "title", queryParser, searcher);
		System.out.println("df_corpus:"+df_corpus+"\tdf_time:"+df_time);
		
		
		}

}
