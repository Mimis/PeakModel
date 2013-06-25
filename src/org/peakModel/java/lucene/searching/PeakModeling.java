package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGram;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class PeakModeling {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		long startTime = System.currentTimeMillis();
		
		
		//========================================================== Input Parameters ==========================================================//
		String query = "\""+ args[0] + "\"";
		String date = args[1];//year
		if(!date.equals("null"))
			query = query + " AND date:["+date+"-01-01 TO "+date+"-12-30]";
		boolean useForSearchOnlyTitle = Boolean.parseBoolean(args[2]);
		final int minN = Integer.parseInt(args[3]);
		final int maxN = Integer.parseInt(args[4]);
		
		/*
		 * Variables
		 */
		final int NUMBER_THREADS = 100;
		final int MAX_DOCS = 10000;
		List<NGram> ngramList = new ArrayList<NGram>();
		//after removing the non exost in Ngram index
		List<NGram> finalNGramList = new ArrayList<NGram>();

		HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
		int totalNumberOfRelevantDocuments = 0;
		long N_peak = 0;//total number of words in peak period(uni,bi or mix)
		long N_corpus = 0;//total number of words in coprus(uni,bi or mix)
		long N_query_peakPeriod = 0;//total number of words in query + peak period
		
		//FILES
		String dutchStopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";
		
		String indexKbCorpusFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/KB_1950_1995";
		String indexKbUnigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbBigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB2gramMin10PerYear1840-1995";
		String fileWithTotalTFperYearUnigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
		String fileWithTotalTFperYearBigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";
		
		//experiment output files
		String experimentsFileTF = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_TF_"+minN+"gram.csv";
		String experimentsFilePMIclassic = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_PMIclassic_"+minN+"gram.csv";
		String experimentsFilePMIpeak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_PMIpeak_"+minN+"gram.csv";
		String experimentsFilePMIpeakTF_query_peak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_PMIpeak_times_TF_"+minN+"gram.csv";
		String experimentsFileLOGclassic = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_LOGclassic_"+minN+"gram.csv";
		String experimentsFileLOGpeak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+args[0]+"_"+date+"_LOGpeak_"+minN+"gram.csv";
		//==========================================================End Parameters==========================================================//

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//========================================================== Lucene  ==========================================================//

		
        /*
         * Corpus and Ngram indexes
		 * Index Dir + Reader + Searcher + Analyzer + QueryParser
		 */
        Directory indexNgramDir = null;
        IndexSearcher nGramSearcher = null;

        Directory indexDir = HelperLucene.getIndexDir(indexKbCorpusFileName);
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
        Analyzer queryAnalyzer = HelperLucene.getKbAnalyzer(dutchStopWordFile);
        Analyzer NgramAnalyzerForTokenization = HelperLucene.getNGramAnalyzer(dutchStopWordFile,minN,maxN);
        

        /*
         * QueryParser based on the input parameter 'use only title' or not
         */
        QueryParser queryParser = null;
        if(useForSearchOnlyTitle)
        	queryParser = new QueryParser(Version.LUCENE_43, "title", queryAnalyzer);
        else
        	queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},queryAnalyzer);
      //==========================================================ENd LUCENE===================================================================//
        
        
        
        
        
        
        
        
        
        
        
        
        
		//========================================================== MAIN  ==========================================================//

        /*
		 * Get peak period Maps:  
		 * 		N_peak_period
		 * 		N_corpus(Year:TotalWords)
		 */
        if(minN==1){
			Helper.getPeakPeriodIndex(fileWithTotalTFperYearUnigram, peakPeriodMap);
			N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
			N_corpus = peakPeriodMap.get("TotalWords");
	        indexNgramDir = HelperLucene.getIndexDir(indexKbUnigramFileName);
	        nGramSearcher = new IndexSearcher(DirectoryReader.open(indexNgramDir));
        }else{
        	Helper.getPeakPeriodIndex(fileWithTotalTFperYearBigram, peakPeriodMap);
			N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
			N_corpus = peakPeriodMap.get("TotalWords");
	        indexNgramDir = HelperLucene.getIndexDir(indexKbBigramFileName);
	        nGramSearcher = new IndexSearcher(DirectoryReader.open(indexNgramDir));
        }
       
        
        /*
         * 2.1 Whole Query  Search: Get relevant docs to the given query
         * 2.2 Get the N-Grams Stats: 
         * 		C(w_i)_query_and_peak_period 
         */
        TopDocs topDocs = HelperLucene.queryIndexGetTopDocs(queryParser,searcher, query,MAX_DOCS);
		ScoreDoc[] hits = topDocs.scoreDocs;
		totalNumberOfRelevantDocuments = hits.length;
        if(totalNumberOfRelevantDocuments==0){
        	System.out.println("Zero Results :/");
        }else{
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				final Document doc = searcher.doc(docId);
				final String title = doc.get("title");				
				List<String> tokenList = HelperLucene.tokenizeString(NgramAnalyzerForTokenization, title);
				if(minN == 2)
					tokenList = Helper.keepOnlyBigramsFromList(tokenList);
				Helper.mapTokenListToNGramList(tokenList, "title", ngramList);
				//System.out.println("\tDoc id:" + docId + "\tTitle:"+ title + " Date:" + doc.get("date")	+ "\tUrl:" + doc.get("url"));
			}
        }
	
        
        
        
        /*
         * Compute => 
         * 		N_query_peakPeriod
         * 		C(w_i)_peakPeriod
         * 		C(w_i)_corpus
         */
		long startTime2 = System.currentTimeMillis();
	    List<Thread> threads = new ArrayList<Thread>();
	    int countThreads = 1;
        for(NGram ngram:ngramList){
            QueryParser ngramIndexQueryParser = new QueryParser(Version.LUCENE_43, "ngram", new KeywordAnalyzer());
        	N_query_peakPeriod += ngram.getTf_query_peak();
        	//linear processing
//        	getNgramTotalTfAndTFperYear(ngram, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);
        	//multihreading
        	PeakModeling.NgramSearchMultyThread(countThreads++, NUMBER_THREADS, threads, ngram, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);
        }
	    System.out.println("#Query Ngram index time:"+ (System.currentTimeMillis()-startTime2)/1000);

        //remove ngramsthat dont exist in NGram index
        for(NGram ngram:ngramList){
        	if(ngram.getTf_corpus() != 0 && ngram.getTf_peak() != 0)
        		finalNGramList.add(ngram);
        }
        
        
        
        /*
         * Compute Probabilities and Statistical Measures
         */
        for(NGram ngram:finalNGramList){
        	ngram.calculateProbabilities(N_corpus, N_query_peakPeriod, N_peak);
        	ngram.computePMIClassic();
        	ngram.computePMIpeak();
        	ngram.computePMIpeakTimesTf_query_peak();
        	ngram.computeLOGlikelyhoodClassic();
        	ngram.computeLOGlikelyhoodPeak();
        }
    	
       	
        /*
         * Sort NGrams By:
         * 	a.TF
         *  b.PMI_classic
         *  c.PMI_time
         */
       	Collections.sort(finalNGramList, NGram.COMPARATOR_TOTAL_TF);
       	writeNgramToCsv(finalNGramList, experimentsFileTF);
       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_CLASSIC);
      	writeNgramToCsv(finalNGramList, experimentsFilePMIclassic);
       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_PEAK);
       	writeNgramToCsv(finalNGramList, experimentsFilePMIpeak);
       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_PEAK_TIMES_TF);
       	writeNgramToCsv(finalNGramList, experimentsFilePMIpeakTF_query_peak);
       	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_CLASSIC);
       	writeNgramToCsv(finalNGramList, experimentsFileLOGclassic);
       	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_PEAK);
       	writeNgramToCsv(finalNGramList, experimentsFileLOGpeak);

      //==========================================================End MAIN===================================================================//
       	
       	
       	
       	
       	
       	
        /*
         * 5. Display results
         */
        System.out.println("====================================================================================================");
        System.out.println("Query:"+query);
        System.out.println("====================================================================================================");
        System.out.println("TotalNumberOfRelevantDocuments:"+totalNumberOfRelevantDocuments);
        System.out.println("====================================================================================================");
        System.out.println("N_coprus:"+N_corpus+"\tN_peakPeriod:"+N_peak+"\tN_query_peakPeriod:"+N_query_peakPeriod);
        System.out.println("====================================================================================================");
        
        
        
        
        //timer
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
	}
	public static void NgramSearchMultyThread(int countThreads,int NUMBER_THREADS, List<Thread> threads,NGram ngram,String date,QueryParser ngramIndexQueryParser,IndexSearcher nGramSearcher, int MAX_DOCS){
    	NGramIndexSearch task = new NGramIndexSearch(ngram, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);
        Thread worker = new Thread(task);
        worker.start();
        threads.add(worker);
        if(countThreads % NUMBER_THREADS == 0){
        	Helper.waitThreadsToFinish(threads);
        	System.out.println("wait to finish threads....");
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
	public static void getNgramTotalTfAndTFperYear(NGram ngram,String year,QueryParser queryParser,IndexSearcher searcher,int MAX_DOCS) throws ParseException, IOException{
		String ngramText = ngram.getNgram().replace(" ", "?");//this is for bigrams
		TopDocs topDocs = HelperLucene.queryIndexGetTopDocs(queryParser,searcher, ngramText,MAX_DOCS);
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
	
	public static int getTfOfYear(String year,String tfPerYear){
		String[] tfYearArray = tfPerYear.split(",");
		for(String tfYear:tfYearArray){
			if(tfYear.startsWith(year)){
				return Integer.parseInt(tfYear.split(":")[1]);
			}
		}
		return 0;
	}

	
	public static void writeNgramToCsv(List<NGram> ngramList, String experimentsFile) throws IOException{
		if(ngramList.isEmpty())
			return;
		String csvExpnationOutput = "ngram ,total_tf_query,df_query,df_corpus,df_time,P_w,P_w_Given_query_time,P_w_Given_time,PMI_classic,PMI_peak";
    	System.out.println(csvExpnationOutput);
        Helper.writeLineToFile(experimentsFile,csvExpnationOutput, false,true);
        for(NGram ngram:ngramList){
        	String ngramToString = ngram.toStringCompact();
        	//System.out.println(ngramToString);
        	Helper.writeLineToFile(experimentsFile, ngramToString, true, true);
        }
	}
}