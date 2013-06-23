package org.peakModel.java.lucene.searching;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.peakModel.java.ngram.NGram;

public class NGramIndexSearch implements Runnable {
	  private final  NGram ngram;
	  private final String year;
	  private final QueryParser queryParser;
	  private final IndexSearcher searcher;
	  private final int MAX_DOCS;
	  
	  public NGramIndexSearch(NGram ngram, String year,
			QueryParser queryParser, IndexSearcher searcher, int MAX_DOCS) {
		super();
		this.ngram = ngram;
		this.year = year;
		this.queryParser = queryParser;
		this.searcher = searcher;
		this.MAX_DOCS = MAX_DOCS;
	}

	@Override
	  public void run() {
		try {
			PeakModeling.getNgramTotalTfAndTFperYear(ngram, year, queryParser, searcher, MAX_DOCS);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	} 
