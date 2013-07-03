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
		String indexKbUnigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
//		String indexKbBigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB2gramMin10PerYear1840-1995";

		Directory  indexNgramDir = HelperLucene.getIndexDir(indexKbUnigramFileName);
	    DirectoryReader ngramIndexReader = DirectoryReader.open(indexNgramDir);
	    IndexSearcher nGramSearcher = new IndexSearcher(ngramIndexReader);
        QueryParser ngramIndexQueryParser = new QueryParser(Version.LUCENE_43, "ngram", new KeywordAnalyzer());


        List<String> qList = Helper.readFileLineByLineReturnListOfLineString("/Users/mimis/Development/EclipseProject/PeakModel/experiments/baseline_setup/beatrix_1965_LOG_corpus_1gram.csv");
		long startTime = System.currentTimeMillis();

        for(String ngramText:qList){
        	ngramText = ngramText.split(",")[0];
			ngramText = ngramText.replace(" ", "?");//this is for bigrams
	        System.out.println(ngramText);

			Query query = ngramIndexQueryParser.parse(ngramText);
			
			
			final List<Integer> docIds = new ArrayList<Integer>();
			nGramSearcher.search(query, new Collector() {
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
					   return;
				   }
				 
				   public void setNextReader(AtomicReaderContext context) {
				     this.docBase = context.docBase;
				   }
				 });
	
	        System.out.println(ngramText+"\tLength:"+docIds.size());
//	        for ( int id:docIds) {
//				final Document doc = nGramSearcher.doc(id);
//				System.out.println("\t"+id+"\t"+doc.get("ngram")+"\t"+doc.get("freqPerYear"));
//	        }		
        }
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);

//		TopDocs topDocs = nGramSearcher.search(query, 1000);
//		ScoreDoc[] hits = topDocs.scoreDocs;
//        System.out.println(hits.length);
//        for(int i=0;i<hits.length;i++){
//        	int docId = hits[i].doc;
//			final Document doc = ngramIndexReader.document(docId);
//			System.out.println(docId+"\t"+doc.get("ngram")+"\t"+doc.get("freqPerYear"));
//        }
        
        
        indexNgramDir.close();
        ngramIndexReader.close();
	}

}
