package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class SearchKbNgramIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		int a=319;
		int d=7347;
		long b = 35779420983L;
		double c = (double) a/b;
		double e = (double) d/b;
		double y = (double) 222/22386384356L;
		double x= c * e;
		double f = Helper.log2(y/x);
		System.out.println(f);
		
		
		
		int MAX_DOCS = 10000;
        Analyzer analyzer = new KeywordAnalyzer();
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "ngram", analyzer);

//		String indexKbNgramFileName = "./index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbNgramFileName = "./index/IndexKB2gramMin10PerYear1840-1995";
		
        Directory indexDir = HelperLucene.getIndexDir(indexKbNgramFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);

        String ngramText = "cariing?o'k";
		Query query = queryParser.parse(ngramText);
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


        for(int docId:docIds){
			final Document doc = searcher.doc(docId);
			final int tfCorpus =  Integer.parseInt(doc.get("totalFrequency"));
			final String freqPerYear = doc.get("freqPerYear");
			final String ng=doc.get("ngram");
			System.out.println(ng+"\t"+tfCorpus+"\t"+freqPerYear);
        }
        

	}
	
}
