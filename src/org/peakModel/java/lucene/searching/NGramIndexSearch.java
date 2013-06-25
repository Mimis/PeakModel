package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.peakModel.java.ngram.NGram;

public class NGramIndexSearch implements Runnable {
	  private final List<NGram> ngramList;
	  private final String year;
	  private final QueryParser queryParser;
	  private final IndexSearcher searcher;
	  private final int MAX_DOCS;
	  
	  public NGramIndexSearch(List<NGram> ngramList, String year,QueryParser queryParser, IndexSearcher searcher, int MAX_DOCS) {
		super();
		this.ngramList = ngramList;
		this.year = year;
		this.queryParser = queryParser;
		this.searcher = searcher;
		this.MAX_DOCS = MAX_DOCS;
	}

	@Override
	  public void run() {
		try {
			for(NGram ngram:this.ngramList)
				PeakModeling.getNgramTotalTfAndTFperYear(ngram, year, queryParser, searcher, MAX_DOCS);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	} 
