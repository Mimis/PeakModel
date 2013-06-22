package org.peakModel.java.lucene.searching;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGram;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class SearchKbNgramIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		int MAX_DOCS = 100;
		String stopWordFile = "./data/stopWords/empty.txt";
    	CharArraySet stopWordsSet = Helper.getStopWordsSet(stopWordFile);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43, stopWordsSet);
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "ngram", analyzer);

		String indexKbNgramFileName = "./index/IndexKB1gram16-17-18-19Min10TimesSorted";
        Directory indexDir = HelperLucene.getIndexDir(indexKbNgramFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        
        String query = "grieks";
        String year = "1974";
        
        SearchLuceneIndex.getNgramTotalTfAndTFperYear(new NGram("title",query), year, queryParser, searcher, MAX_DOCS);
	}
	
}
