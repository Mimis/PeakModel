package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.peakModel.java.peakModel.NGram;

public class NGramIndexSearch implements Runnable {
	private final List<NGram> ngramList;
	private final String year;
	private final QueryParser queryParser;
	private final IndexSearcher searcher;
	private final int MAX_DOCS;

	public NGramIndexSearch(List<NGram> ngramList, String year,
			QueryParser queryParser, IndexSearcher searcher, int MAX_DOCS) {
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
			for (NGram ngram : this.ngramList)
				getNgramTotalTfAndTFperYear(ngram, year,queryParser, searcher, MAX_DOCS);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get:
	 	 * 		C(w_i)_peakPeriod
         * 		C(w_i)_corpus
	 * @param ngram
	 * @param year
	 * @param queryParser
	 * @param searcher
	 * @param MAX_DOCS
	 * @throws ParseException
	 * @throws IOException
	 */
	private  void getNgramTotalTfAndTFperYear(NGram ngram,String year,QueryParser queryParser,IndexSearcher searcher,int MAX_DOCS) throws ParseException, IOException{
		String ngramText = ngram.getNgram().replace(" ", "?");//this is for bigrams
		if(ngramText.contains(":"))
			return;
		Query query = queryParser.parse(ngramText);
		TopDocs topDocs = searcher.search(query, MAX_DOCS);

		ScoreDoc[] hits = topDocs.scoreDocs;
		if(hits.length==0)
			return;
		else{
			int docId = hits[0].doc;
			final Document doc = searcher.doc(docId);
			final int tfCorpus =  Integer.parseInt(doc.get("totalFrequency"));
			final String freqPerYear = doc.get("freqPerYear");
			final int tfYear = getTfOfYear(year, freqPerYear);
			ngram.setTf_peak(tfYear);
			ngram.setTf_corpus(tfCorpus);
		}
	}
	private  int getTfOfYear(String year,String tfPerYear){
		String[] tfYearArray = tfPerYear.split(",");
		for(String tfYear:tfYearArray){
			if(tfYear.startsWith(year)){
				return Integer.parseInt(tfYear.split(":")[1]);
			}
		}
		return 0;
	}

}
