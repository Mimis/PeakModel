package org.peakModel.java.peakModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.lucene.searching.NGramIndexSearch;
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
		String initialQuery = args[0];
		String date = args[1];//year
		if(!date.equals("null"))
			query = query + " AND date:["+date+"-01-01 TO "+date+"-12-30]";
		boolean useForSearchOnlyTitle = Boolean.parseBoolean(args[2]);
		boolean useStopWords = Boolean.parseBoolean(args[3]);
		final int minN = Integer.parseInt(args[4]);
		final int maxN = Integer.parseInt(args[5]);
		final int NUMBER_THREADS = Integer.parseInt(args[6]);
		int topNgramsForConsideration = 10;//for cosine similarity
		final int MAX_DOCS = 30000;
		
		/*
		 * Variables
		 */
		List<NGram> ngramList = new ArrayList<NGram>();
		//this is for phraseness measure when we encounter bi-grams
		List<NGram> unigramList = new ArrayList<NGram>();
		List<KbDocument> documentList = new ArrayList<KbDocument>();
		HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
		int totalNumberOfRelevantDocuments = 0;
		long maxTF_query_peak = 0;
		long N_peak = 0;//total number of words in peak period(uni,bi or mix)
		long N_corpus = 0;//total number of words in coprus(uni,bi or mix)
		long N_query_peakPeriod = 0;//total number of words in query + peak period
		long N_years = 0;//total number of years

		//FILES
		String dutchStopWordsFile =  "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/dutch.txt";
		String stopWordFile = null;
		if(useStopWords)
			stopWordFile = dutchStopWordsFile;
		else
			stopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";
        List<String> stopWords = Helper.readFileLineByLineReturnListOfLineString(dutchStopWordsFile);
		String indexKbCorpusFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/KB_1950_1995";
		String indexKbUnigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbBigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB2gramMin10PerYear1840-1995";
		String fileWithTotalTFperYearUnigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
		String fileWithTotalTFperYearBigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";
		//==========================================================End Parameters==========================================================//

		
		
		
		
		//========================================================== Lucene  Initialization ==========================================================//		
        /*
         * Ngram index
		 */
        Directory indexNgramDir = null;
        DirectoryReader ngramIndexReader = null;
        IndexSearcher nGramSearcher = null;
        /*
         * KB index
         */
        Directory indexKbDir = HelperLucene.getIndexDir(indexKbCorpusFileName);
        IndexReader kbIndexReader = DirectoryReader.open(indexKbDir);
        IndexSearcher searcher = new IndexSearcher(kbIndexReader);
        Analyzer NgramAnalyzerForTokenization = HelperLucene.getNGramAnalyzer(stopWordFile,minN,maxN);       
        
        /*
         * QueryParser based on the input parameter 'use only title' or not
         */
        QueryParser queryParser = null;
        Analyzer queryAnalyzer = HelperLucene.getKbAnalyzer(stopWordFile);
        if(useForSearchOnlyTitle)
        	queryParser = new QueryParser(Version.LUCENE_43, "title", queryAnalyzer);
        else
        	queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},queryAnalyzer);
      //==========================================================ENd LUCENE Initialization===================================================================//
        
        
        
        
        
        
        
        
        
        
        
        
        
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
	        ngramIndexReader = DirectoryReader.open(indexNgramDir);
	        nGramSearcher = new IndexSearcher(ngramIndexReader);
	        N_years = peakPeriodMap.size() - 1;
        }else{
        	Helper.getPeakPeriodIndex(fileWithTotalTFperYearBigram, peakPeriodMap);
			N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
			N_corpus = peakPeriodMap.get("TotalWords");
	        indexNgramDir = HelperLucene.getIndexDir(indexKbBigramFileName);
	        ngramIndexReader = DirectoryReader.open(indexNgramDir);
	        nGramSearcher = new IndexSearcher(ngramIndexReader);
	        N_years = peakPeriodMap.size() - 1 ;
        }
        
        
                
        /**
         * GET NGRAM CANDIDATES!!!
         * 
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
				
				/*
				 * Title
				 */
				final String title = doc.get("title");				
				//System.out.println(title+"\t"+doc.get("url"));
				List<String> tokenList = HelperLucene.tokenizeString(NgramAnalyzerForTokenization, title);
				
		        
				if(minN == 2){
					//this is for phraseness
					List<String> unigramTokenList = Helper.keepOnlyUnigramsFromList(tokenList);				
					Helper.mapTokenListToNGramList(unigramTokenList, "title", unigramList);
					//this the candidate keyphrases
					tokenList = Helper.keepOnlyBigramsFromList(tokenList);				
				}
				documentList.add(new KbDocument(docId, title,tokenList));
				Helper.mapTokenListToNGramList(tokenList, "title", ngramList);
			}
        }
	
        
        /**
         * FILTER OUT CANDIDATES!
         * 	1. ngrams with only stop words
         *  2. TODO ngram with the Query Keyword and a function word
         *  3. ngram with only numbers
         *  4. TODO ngram with only numbers and function words
         */
//        ngramList = Helper.keepNoStopWordsFromList(ngramList, stopWords);
//        ngramList = Helper.keepNoNgramNumbersFromList(ngramList);
        
        
        
        
        /*
         * Compute => 
         * 		N_query_peakPeriod
         * 		C(w_i)_peakPeriod
         * 		C(w_i)_corpus
         */
	    System.out.println("#ngramList before removing empty:"+ ngramList.size());		
    	//linear processing
	    //PeakModeling.NgramSearchLinearProcessing(ngramList, nGramSearcher, date, 1);
    	//multihreading
    	PeakModeling.NgramSearchMultiThread(ngramList, NUMBER_THREADS, date, nGramSearcher, 1);


    	
    	
        /*
         * remove ngrams that dont exist in NGram index
         */
    	List<NGram> finalNGramList = new ArrayList<NGram>();
    	N_query_peakPeriod = removeNgramsWithNoOccurenceInNGramIndex(ngramList, finalNGramList);
	    System.out.println("#Unique Ngrams(after removing):"+ finalNGramList.size());

	    
		/*
		 * find maximum frequency in the foreground corpus ; We use it for TF-IDF score
		 */
	    maxTF_query_peak = Helper.getMaxTF_query_peak(finalNGramList);
	    
        /*
         * compute 
         * 	1)N_unigram_peak for phraseness
         *  2)P(w)_query_peak
         */
	    computeProb_For_Calculate_Phraseness(unigramList, minN, NUMBER_THREADS, date, fileWithTotalTFperYearUnigram, indexKbUnigramFileName);
	    
	    
	    
        /*
         * Compute Probabilities and Statistical Measures
         */
		calculateProbabilitiesAndMeasures(finalNGramList, unigramList, minN, N_corpus, N_query_peakPeriod, N_peak,N_years,maxTF_query_peak);
		
		
        /*
         * 1.Sort NGrams By Measure
         * 2.Get top N ngrams after Remove from ngrams the initial query
         * 3.Calculate Cosine Similarity between Top NGrams and Headlines
         */
        Helper.removeQueryTermsFromNgramList(initialQuery, finalNGramList);
        displayBestNgramsForEachMeasure(useStopWords, finalNGramList, topNgramsForConsideration, documentList, initialQuery, date, minN);
                
        /*
         * close indexes
         */
        ngramIndexReader.close();
        kbIndexReader.close();
        indexNgramDir.close();
        indexKbDir.close();
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
	
	
	
	
	
	
	/**
         * compute 
         * 	1)N_unigram_peak for phraseness
         *  2)P(w)_query_peak
         *  3)P(w)_peak
         *  4)P(w)_corpus
	 * @param unigramList
	 * @param minN
	 * @throws IOException 
	 */
	public static void computeProb_For_Calculate_Phraseness(List<NGram> unigramList,int minN,int NUMBER_THREADS, String date, String fileWithTotalTFperYearUnigram,String indexKbUnigramFileName) throws IOException{
		if(minN == 2){
			//Get statistics for each Unigram!!!
			HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
			Helper.getPeakPeriodIndex(fileWithTotalTFperYearUnigram, peakPeriodMap);
			long N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
			long N_corpus = peakPeriodMap.get("TotalWords");
	        Directory indexNgramDir = HelperLucene.getIndexDir(indexKbUnigramFileName);
	        DirectoryReader ngramIndexReader = DirectoryReader.open(indexNgramDir);
	        IndexSearcher nGramSearcher = new IndexSearcher(ngramIndexReader);
	        long N_years = peakPeriodMap.size() - 1;
	    	PeakModeling.NgramSearchMultiThread(unigramList, NUMBER_THREADS, date, nGramSearcher, 1);
	    	
			long N_unigram_peak = 0;//total number of words in peak period; THIS IS FOR PHRASENESS
			for(NGram uni:unigramList)
				N_unigram_peak += uni.getTf_query_peak();				
			
	        for(NGram ngram:unigramList)
	        	ngram.calculateProbabilities(N_corpus, N_unigram_peak, N_peak);

//			for(NGram uni:unigramList)
//				uni.calculateP_query_peakPeriod(N_unigram_peak);				
	        
	        indexNgramDir.close();
	        ngramIndexReader.close();
		}
	}
	
	
	
	public static long removeNgramsWithNoOccurenceInNGramIndex(List<NGram> ngramList,List<NGram> finalNGramList){
		long N_query_peakPeriod=0;
        for(NGram ngram:ngramList){
        	if(ngram.getTf_corpus() != 0 && ngram.getTf_peak() != 0){
        		finalNGramList.add(ngram);
            	N_query_peakPeriod += ngram.getTf_query_peak();
        	}
        }
        return N_query_peakPeriod;
	}
	
	
	/**
	 * Calculate Probabilities and Statistical Measures
	 * @param finalNGramList
	 * @param unigramList
	 * @param minN
	 * @param N_corpus
	 * @param N_query_peakPeriod
	 * @param N_peak
	 */
	public static void calculateProbabilitiesAndMeasures(List<NGram> finalNGramList,List<NGram> unigramList, int minN, long N_corpus,long N_query_peakPeriod,long N_peak,long N_years,long maxTF_query_peak){
        for(NGram ngram:finalNGramList){
        	ngram.calculateProbabilities(N_corpus, N_query_peakPeriod, N_peak);
    		if(minN == 2){
//    			ngram.computePhrasenessPKLForeground(unigramList);
//    			ngram.computePhrasenessPMIForeground(unigramList);
//    			ngram.computePhrasenessPKLBackgroundPeak(unigramList);
//    			ngram.computePhrasenessPMIBackgroundPeak(unigramList);
    			ngram.computePhrasenessPKLBackgroundCorpus(unigramList);
//    			ngram.computePhrasenessPMIBackgroundCorpus(unigramList);
    		}
        	ngram.computePMIcorpus();
        	ngram.computePMIpeak();
        	ngram.computePMIpeakTimesTf_query_peak();
        	ngram.computePMI_corpus_times_tf_query_peak();
        	ngram.computeLOGlikelyhoodPeak( N_query_peakPeriod, N_peak);
        	ngram.computeLOGlikelyhoodCorpus(N_query_peakPeriod, N_corpus);
        	ngram.computePointwiseKLCorpus();
        	ngram.computePointwiseKLPeak();
        	ngram.computePointwiseKLCorpusPeak();
        	ngram.computeDiceCorpus(N_query_peakPeriod);
        	ngram.computeDicePeak(N_query_peakPeriod);
        	ngram.computePhiSquareCorpus(N_query_peakPeriod, N_corpus);
        	ngram.computePhiSquarePeak(N_query_peakPeriod, N_peak);
        	ngram.computeTF_IDF(N_years,maxTF_query_peak);
        	ngram.computeIDF_Phraseness_Informativeness();
        }
	}
	
	/**
	 * Cosine Similarity 
	 * @param measure
	 * @param finalNGramList
	 * @param documentList
	 * @param topNgramsForConsideration
	 */
	public static void DisplayBestTitlesViaCosineSim(String measure,List<NGram> finalNGramList,List<KbDocument> documentList, int topNgramsForConsideration ){
       	PeakModelWithDocumentSimilarity.cosineSimilarity(finalNGramList, documentList, topNgramsForConsideration);
       	System.out.println("\n\n##"+measure+" best titles:");
       	int topN = documentList.size() > 10 ? documentList.size() : documentList.size();
       	for(int i=0;i<topN;i++){
       		KbDocument kbDoc = documentList.get(i);
       		if(kbDoc.getCosineSimilarity()==0.0)
       			break;
//       		if(kbDoc.getTitle().length()>50)
//       			System.out.println(kbDoc.getTitle().substring(0,50)+"....");
//       		else
       			System.out.println(kbDoc.getTitle()+"\t"+kbDoc.getCosineSimilarity());
       	}
	}
	
	
	/**
	 * Split the given ngra, list in N threads and retrieve their NGram statistics!!!
	 * @param ngramList
	 * @param NUMBER_THREADS
	 * @param date
	 * @param nGramSearcher
	 * @param MAX_DOCS
	 */
	public static void NgramSearchMultiThread(List<NGram> ngramList, int NUMBER_THREADS,String date,IndexSearcher nGramSearcher, int MAX_DOCS){
		int ngramSize = ngramList.size();
		int ngramsPerThread = ngramSize / NUMBER_THREADS;
	    List<Thread> threads = new ArrayList<Thread>();

		for(int i=0;i<NUMBER_THREADS;i++){
			int fromIndex = i * ngramsPerThread;
			int toIndex = (i + 1) * ngramsPerThread;			
			toIndex = i == NUMBER_THREADS-1 ? ngramSize : toIndex;			
			List<NGram> ngramSubList = ngramList.subList(fromIndex, toIndex);			
            QueryParser ngramIndexQueryParser = new QueryParser(Version.LUCENE_43, "ngram", new KeywordAnalyzer());
			startWorker(ngramSubList, threads, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);
		}
		System.out.println("wait for Threads to finish...");
        Helper.waitThreadsToFinish(threads);
	}
	public static void startWorker(List<NGram> ngramList,  List<Thread> threads,String date,QueryParser ngramIndexQueryParser,IndexSearcher nGramSearcher, int MAX_DOCS){
    	NGramIndexSearch task = new NGramIndexSearch(ngramList, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);
        Thread worker = new Thread(task);
        worker.start();
        threads.add(worker);
	}
	
	
	public static void displayBestNgramsForEachMeasure(boolean useStopWords, List<NGram> finalNGramList,int topNgramsForConsideration, List<KbDocument> documentList,String initialQuery,String date,int minN) throws IOException{
		String experimentsFileTF = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_TF_"+minN+"gram.csv";
		String experimentsFilePMIcorpus = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PMI_corpus_"+minN+"gram.csv";
		String experimentsFilePMIpeak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PMI_peak_"+minN+"gram.csv";
		String experimentsFilePMIpeakTF_query_peak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PMI_TF_peak_"+minN+"gram.csv";
		String experimentsFilePMIcorpusTF_query_peak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PMI_TF_corpus_"+minN+"gram.csv";
		String experimentsFileLOGcorpus = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_LOG_corpus_"+minN+"gram.csv";
		String experimentsFileLOGpeak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_LOG_peak_"+minN+"gram.csv";
		String experimentsFilePointwiseKLpeak = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PointKL_peak_"+minN+"gram.csv";
		String experimentsFilePointwiseKLcorpus = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PointKL_corpus_"+minN+"gram.csv";
		String experimentsFilePointwiseKL_peak_corpus = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PointKL_peak_corpus_"+minN+"gram.csv";
		String experimentsFileTF_IDF = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_TFIDF_"+minN+"gram.csv";
		String experimentsFileIDF = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_IDF_"+minN+"gram.csv";
		String experimentsFilePHRASENESS = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_PHRASENESS_"+minN+"gram.csv";
		String experimentsFileIDF_Phraseness_Informativeness = "/Users/mimis/Development/EclipseProject/PeakModel/experiments/"+initialQuery+"_"+date+"_IDF_Phraseness_Informativeness_"+minN+"gram.csv";
		
//		if(useStopWords){
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_TOTAL_TF);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileTF);
//	       	DisplayBestTitlesViaCosineSim("TF",finalNGramList, documentList, topNgramsForConsideration);
//	       	
//        }else{
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_CORPUS);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePMIcorpus);
//	       	DisplayBestTitlesViaCosineSim("PMI_CORPUS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_PEAK);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePMIpeak);
//	       	DisplayBestTitlesViaCosineSim("PMI_PEAK",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_PEAK_TIMES_TF);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePMIpeakTF_query_peak);
//	       	DisplayBestTitlesViaCosineSim("PMI_TF_PEAK",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_PMI_CORPUS_TIMES_TF);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePMIcorpusTF_query_peak);
//	       	DisplayBestTitlesViaCosineSim("PMI_TF_CORPUS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_CORPUS);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileLOGcorpus);
//	       	//DisplayBestTitlesViaCosineSim("LOG_CORPUS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_PEAK);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileLOGpeak);
//	       	//DisplayBestTitlesViaCosineSim("LOG_PEAK",finalNGramList, documentList, topNgramsForConsideration);
//	       	
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_POINTWISE_KL_PEAK);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePointwiseKLpeak);
//	       	DisplayBestTitlesViaCosineSim("POINTWISE_KL_PEAK",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_POINTWISE_KL_CORPUS);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePointwiseKLcorpus);
//	       	//DisplayBestTitlesViaCosineSim("POINTWISE_KL_CORPUS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_POINTWISE_KL_PEAK_CORPUS);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePointwiseKL_peak_corpus);
//	       	DisplayBestTitlesViaCosineSim("POINTWISE_KL_PEAK_CORPUS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_TF_IDF);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileTF_IDF);
	       	//DisplayBestTitlesViaCosineSim("TF-IDF",finalNGramList, documentList, topNgramsForConsideration);

//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_IDF);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileIDF);
	       	//DisplayBestTitlesViaCosineSim("IDF",finalNGramList, documentList, topNgramsForConsideration);

	       	Collections.sort(finalNGramList, NGram.COMPARATOR_PHRASENESS);
	       	Helper.writeNgramToCsv(finalNGramList, experimentsFilePHRASENESS);
//	       	//DisplayBestTitlesViaCosineSim("PHRASENESS",finalNGramList, documentList, topNgramsForConsideration);
//
//	       	
//	       	Collections.sort(finalNGramList, NGram.COMPARATOR_IDF_Phraseness_Informativeness);
//	       	Helper.writeNgramToCsv(finalNGramList, experimentsFileIDF_Phraseness_Informativeness);
	       	//DisplayBestTitlesViaCosineSim("PHRASENESS",finalNGramList, documentList, topNgramsForConsideration);

//        }
	}
	public static void NgramSearchLinearProcessing(List<NGram> ngramList,IndexSearcher nGramSearcher,String date,int MAX_DOCS){
//      QueryParser ngramIndexQueryParser = new QueryParser(Version.LUCENE_43, "ngram", new KeywordAnalyzer());
//      for(NGram ngram:ngramList)
//    	  NGramIndexSearch.getNgramTotalTfAndTFperYear(ngram, date, ngramIndexQueryParser, nGramSearcher, MAX_DOCS);

	}

}
