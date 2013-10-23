package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.peakModel.java.peakModel.PeakModeling;
import org.peakModel.java.peakModel.burstiness.Burst;
import org.peakModel.java.peakModel.burstiness.Burstiness;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class SearchKbNgramIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
//		String indexKbNgramFileName = "./index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbNgramFileName = "./index/IndexKB2gramMin10PerYear1840-1995";
//		String fileWithTotalTFperYear = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
		String fileWithTotalTFperYear = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";
		
		String firstLine = "year:";
		String ngramText = "verloving beatrix";
		String[] allArr = new String[195];
//		Set<String> ngramSet = Helper.readFileLineByLineReturnSetOfLineString("/Users/mimis/Desktop/csvBurst.txt");
//		for(String ngramText:ngramSet){
//			
			searchNgramIndex(ngramText, fileWithTotalTFperYear, indexKbNgramFileName,allArr);
			firstLine += ngramText+":";
			
//		}
	
		
		
		int startYear = 1800;
		System.out.println(firstLine);
		for(String a:allArr)
			System.out.println(startYear++ +":"+ a);
	}

	
	public static void searchNgramIndex(String ngramText, String fileWithTotalTFperYear,String indexKbNgramFileName,String[] allArr) throws IOException, ParseException{
		HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
        long totalNumberOfWords = PeakModeling.totalFrequencies(fileWithTotalTFperYear, peakPeriodMap);

//		int MAX_DOCS = 10000;
        Analyzer analyzer = new KeywordAnalyzer();
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "ngram", analyzer);
		
        Directory indexDir = HelperLucene.getIndexDir(indexKbNgramFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        ngramText = ngramText.replace(" ", "?");
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
			
//			Map<Integer,Integer> tfPerYearMap = Helper.importTfYearStringToMap(freqPerYear);
//			int c=0;
//			for(int year=1800;year<1995;year++){
//				int tf = tfPerYearMap.get(year) == null ? 0 : tfPerYearMap.get(year);
//				if(allArr[c] == null)
//					allArr[c] = ""+tf;
//				else
//					allArr[c] = allArr[c]+":"+tf;
//				c++;
//			}
			
			/**
			 * Burstiness
			 */
//			List<Burst> burstList = Burstiness.measureBurstinessForWholeCorpusMovingAverage(freqPerYear, 4);
//			for(Burst b:burstList)
//				System.out.println(b.toString());
						
        }

        indexDir.close();
        reader.close();
        
	}
	
	

}
