package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.burstiness.Burst;
import org.peakModel.java.peakModel.burstiness.Burstiness;

public class NGramIndexSearch implements Runnable {
	private final List<NGram> ngramList;
	private final String year;
	private final QueryParser queryParser;
	private final IndexSearcher searcher;
	private final int MAX_DOCS;
	private final long N_corpus;
	private final long N_peak;

	public NGramIndexSearch(List<NGram> ngramList, String year,
			QueryParser queryParser, IndexSearcher searcher, int MAX_DOCS,long N_corpus,long N_peak) {
		super();
		this.ngramList = ngramList;
		this.year = year;
		this.queryParser = queryParser;
		this.searcher = searcher;
		this.MAX_DOCS = MAX_DOCS;
		this.N_corpus=N_corpus;
		this.N_peak=N_peak;
	}

	@Override
	public void run() {
		try {
			int counter=0;
			for (NGram ngram : this.ngramList){
				getNgramTotalTfAndTFperYear(ngram, year,queryParser, searcher, MAX_DOCS,N_corpus,N_peak);
				if(counter++ %1000 ==0 )
					System.out.println("Thread:"+counter);
			}
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
	private  void getNgramTotalTfAndTFperYear(NGram ngram,String year,QueryParser queryParser,IndexSearcher searcher,int MAX_DOCS,long N_corpus,long N_peak) throws ParseException, IOException{
		String ngramText = ngram.getNgram().replace(" ", "?");//this is for bigrams
		if(ngramText.contains(":"))
			return;
		
		Query query = queryParser.parse(ngramText);
//		TopDocs topDocs = searcher.search(query, MAX_DOCS);
//		ScoreDoc[] hits = topDocs.scoreDocs;
		
		
		final List<Integer> docIds = new ArrayList<Integer>();
		searcher.search(query, new Collector() {
			   private int docBase;
			   
			   // ignore scorer
			   public void setScorer(Scorer scorer) {
			   }

			   // accept docs out of order (for a BitSet it doesn't matter)
			   public boolean acceptsDocsOutOfOrder() {
			     return true;
			   }
			 
			   public void collect(int doc) {
				   docIds.add(doc+docBase);
			   }
			 
			   public void setNextReader(AtomicReaderContext context) {
			     this.docBase = context.docBase;
			   }
			 });

		
		if(docIds.size()==0)
			return;
		else{
//			int docId = hits[0].doc;
			final Document doc = searcher.doc(docIds.get(0));
			final int tfCorpus =  Integer.parseInt(doc.get("totalFrequency"));
			final String freqPerYear = doc.get("freqPerYear");
			final int tfYear = getTfOfYear(year, freqPerYear);
			ngram.setTf_peak(tfYear);
			ngram.setTf_corpus(tfCorpus);
			ngram.setNr_of_years_appearance(freqPerYear.split(",").length);
			
			//this is based on year and not by the current documents set!!!!
//			final double burstiness = Burstiness.measureBurstinessCHI_SQUARE( tfYear, freqPerYear.split(","), N_corpus, N_peak);
			List<Burst> burstList = Burstiness.measureBurstinessMovingAverage(freqPerYear, 2);
			ngram.setBurstList(burstList);
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
