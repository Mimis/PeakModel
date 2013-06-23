package org.peakModel.java.lucene.searching;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGram;
import org.peakModel.java.utils.HelperLucene;

public class SearchKbNgramIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		int MAX_DOCS = 100;
        Analyzer analyzer = new KeywordAnalyzer();
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "ngram", analyzer);

//		String indexKbNgramFileName = "./index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbNgramFileName = "./index/IndexKB2gramMin10PerYear1840-1995";
		
        Directory indexDir = HelperLucene.getIndexDir(indexKbNgramFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);

        String query = "eerste?klasse";
        String year = "1974";
        
        PeakModeling.getNgramTotalTfAndTFperYear(new NGram(query,"title"), year, queryParser, searcher, MAX_DOCS);
        

	}
	
}
