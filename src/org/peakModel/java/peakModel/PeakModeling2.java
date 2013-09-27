package org.peakModel.java.peakModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
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
import org.peakModel.java.peakModel.burstiness.Burst;
import org.peakModel.java.peakModel.burstiness.Burstiness;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class PeakModeling2 {
	
	public static void main(String args[]) throws IOException, ParseException, java.text.ParseException{
		
		//========================================================== Input Parameters ==========================================================//
		final String query = args[0];
		final String date = args[1].equals("null") ? null : args[1] ;//year
		final boolean useForSearchOnlyTitle = Boolean.parseBoolean(args[2]);
		final boolean useStopWords = Boolean.parseBoolean(args[3]);
		final int minN = Integer.parseInt(args[4]);
		final int maxN = Integer.parseInt(args[5]);
		final int NUMBER_THREADS = Integer.parseInt(args[6]);
		final int MAX_DOCS = 300000;
		final int MAX_TITLE_LENGTH = 100;
		final int MIN_TITLE_LENGTH = 1;
		//burst detection...
		final int burstTimeSpan = 7;
	    final double x = 2.0;
		//==========================================================End Parameters==========================================================//

		
		
		
		    
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//========================================================== Main ==========================================================//
		PeakModeling2 peakModel = new PeakModeling2(useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan);
		peakModel.getPerYearStats(date);

		//System.out.println("Nr of docs:"+peakModel.getNrOfDocs());
		long startTime = System.currentTimeMillis();

		/**
		 * Get documents based on given query
		 */
		int max_number_of_documents = -1;//-1 for using the default value
		peakModel.getKbDocs(query,date,true,max_number_of_documents);
	    System.out.println("#Total Retrieve Documents run time:"+ (System.currentTimeMillis()-startTime)/1000);

	    
		/**
		 * Construct Query Temporal Profile
		 * 	1) detect burst periods based on querys temporal distribution
		 */
        FeatureTemporalProfile queryTemporalProfile = Burstiness.measureBurstinessForPeakYearMovingAverage(date, peakModel.queryDocFreqPerDayMap, peakModel.burstTimeSpan,x);
        System.out.println("#Total queryTemporalProfile run time:"+ (System.currentTimeMillis()-startTime)/1000);

        
        
        
        /**
         * Create Language models for each class(Burst,NonBurst);Ngram Candidate lists form each document set with length 1 to 3
         */
        int minNGramLengthLM = 1;
        int maxNGramLengthLM = 4;
        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
		Set<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile);
		List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minNGramLengthLM, maxNGramLengthLM);
		
		//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
		Set<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
		List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minNGramLengthLM, maxNGramLengthLM);
				
		//ALL DOCUMENTS
		List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.documentList, minNGramLengthLM, maxNGramLengthLM);
        System.out.println("#Total LanguageModels run time:"+ (System.currentTimeMillis()-startTime)/1000);
		

		/**
		 * Statistical Measures between burst against non burst features
		 * 	HERE I CAN CALCULATE THE LOGS AS MAARTEN DE RIJK FOR FACETS BY COMPARING THE BURSTS NGRAMS WITH THE WHOLE RELEVANT DOCUMENT SET
		 */
		for(int ngramLength=minNGramLengthLM;ngramLength<=maxNGramLengthLM;ngramLength++){
			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel m2 = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			LanguageModel m2 = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			peakModel.measureRelativeEntropy(m1,m2);
			peakModel.measureSignificanceOfTermsInBurstAgainstNonBurstDocs(m1, m2);
		}
		//measure Tf Idf based on the number of days appera durring the peak year
		peakModel.measureTF_IDf_basedOnDaysInPeakYear(minNGramLengthLM, maxNGramLengthLM, allDocsLanguageModelList, burstLanguageModelList);
        System.out.println("#Total Statistical Measures run time:"+ (System.currentTimeMillis()-startTime)/1000);

		
        
        
		/**
		 * Statistical Measures against the whole corpus
		 * Get best ngrams based on Log_corpus measure
		 */
		LanguageModel lang = allDocsLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(minN)));
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(minN)));
//		LanguageModel lang = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(minN)));
		peakModel.getNgramPerYearSTats(lang.getNgramList(),25,date);
		
		

		
		
		
		/*
		 * VISUALIZATION BURSTS ...
		 */
        visualization(peakModel, queryTemporalProfile, burstDocList, nonBurstDocList, burstLanguageModelList, noBurstLanguageModelList,allDocsLanguageModelList, minNGramLengthLM, maxNGramLengthLM);
        
        
		/**
		 * Back off model log likelihood
		 * for a fourgram we take in account the trigrams and bigrams weights that include in if minNgramLevelForScoreConsideration==2
		 * for a bigram we cant use any unigram if minNgramLevelForScoreConsideration==2
		 */
//		int minNgramLevelForScoreConsideration = 1; //use till bigrams for back off model;that means we recalculate weights only for greater than bigrams models!
//		peakModel.measureSignificanceBasedOnBackOffModel(burstLanguageModelList, maxNGramLengthLM, 2,minNgramLevelForScoreConsideration);
//		Helper.displayLanguageModelsByLogLikelihoodBurst(burstLanguageModelList,noBurstLanguageModelList, "BackOffBurst",minNGramLengthLM,maxNGramLengthLM,10);



		
		
		
        /**
         * 1. HITS count Explanation Generation: top N UNIGRAMS, no StopWords and Query Keywords
         */
//		int featureType = 1;
		
		//*CHOOSE FROM WHICH MODEL WE WILL GENERATE EXPLANATION
//		LanguageModel LM = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(featureType)));
//		LanguageModel LM = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureType)));
		
		//*CHOOSE TOP FEATURES BASED ON GIVEN MEASURE(for log_corpus we need global feature statistics)
//		List<String> topFeatureList = peakModel.getTopFeatures(LM, 25,NGram.COMPARATOR_LOG_LIKELIHOOD_BURST);
//		List<String> topFeatureList = peakModel.getTopFeatures(LM, 25,NGram.COMPARATOR_TF_IDF_peak_year);
//		List<String> topFeatureList = peakModel.getTopFeatures(LM, 25,NGram.COMPARATOR_LOG_CORPUS);

		//generate explanations...
//		peakModel.explanationGenerationHITS(topFeatureList,queryTemporalProfile);
		
		//display
//		Collections.sort(peakModel.documentList,KbDocument.COMPARATOR_HITS);
//		for(KbDocument doc:peakModel.documentList)
//			System.out.println(doc.getHitCounts()+"\t"+doc.getTitle());
		
		
		
		
		
		
		/**
		 * Cluster Documents: UNIGRAMS, Cosine, TFxIDF_peak_year, Hierachical Aglomerative clustering
		 */
//        double[][] docSimMatrix = PeakModelWithDocumentSimilarity.computeDocumentsSimMatrix(peakModel.documentList, allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(1))), peakModel.stopWords);

        //Close Indexes
        peakModel.closeIndexes();
        
        
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
        //========================================================== End Main ==========================================================//
	}

	
	
	/**
	 * Explanation Generation..
	 * @param LM
	 * @param topNFeatures
	 * @param comparator
	 * @return
	 */
	public List<String> getTopFeatures(String query,LanguageModel LM,int topNFeatures,Comparator<NGram> comparator){
		//Get the top N features without stopWords and query tokens
        List<String> topFeatureList = new ArrayList<String>();
        List<String> queryTokens = new ArrayList<String>(Arrays.asList(query.split(" ")));
        queryTokens.add(query);
		Collections.sort(LM.getNgramList(),comparator);
		for(NGram ng:LM.getNgramList()){
			String currentNgram = ng.getNgram();
			if(!this.stopWords.contains(currentNgram) && !queryTokens.contains(currentNgram)){
				System.out.println("top::"+currentNgram);
				topFeatureList.add(currentNgram);
				if(topFeatureList.size()==topNFeatures)
					break;
			}
		}
		return topFeatureList;
	}
	public void explanationGenerationHITS(List<String> topFeatureList,FeatureTemporalProfile queryTemporalProfile){		
		//count hits
		Set<String> burstDates = queryTemporalProfile.getAllBurstDatesSet();
		for(KbDocument doc : documentList){
			int countHits = 0;
			String docDate = doc.getDate();
//			if(burstDates.contains(docDate)){
				for(String docToken : doc.getTokenSet()){
					if(topFeatureList.contains(docToken))
						countHits++;
				}
//			}
			doc.setHitCounts(countHits);
		}
	}
	
	
	

	
	
	
	/**
	 * Measure tf-idf based on the numbr of days appear durring the Peak Year
	 * @param minNGramLengthLM
	 * @param maxNGramLengthLM
	 * @param allDocsLanguageModelList
	 */
	public void measureTF_IDf_basedOnDaysInPeakYear(int minNGramLengthLM,int maxNGramLengthLM,List<LanguageModel> allDocsLanguageModelList,List<LanguageModel> burstLanguageModelList){
		for(int ngramLength=minNGramLengthLM;ngramLength<=maxNGramLengthLM;ngramLength++){
			LanguageModel allLM = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel burstLM = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));

			for(NGram ng:allLM.getNgramList()){
				int tf_query_peak_year = ng.getTf_query_peak();
				int nr_days_appear_peak_year = ng.getDocFreqPerDayMap().size();
				double idf = Helper.log2( ((double)365/nr_days_appear_peak_year));
				double tf_idf = (double) (tf_query_peak_year) * idf;
				ng.setTF_IDF_peak_year(tf_idf);
				//Assign the tfidf into burst Ngram model..
				NGram burstNgram = burstLM.getNgram(ng.getNgram(), "title");
				if(burstNgram!=null){
					burstNgram.setTF_IDF_peak_year(tf_idf);
					//experimet measure multiply tf idf with logBurst
//					burstNgram.setTF_IDF_peak_year(tf_idf * burstNgram.getLOG_Likelyhood_burst());
				}
			}
		}
	}
	
	/**
	 * Vizualize Top Features..
	 * @param peakModel
	 * @param queryTemporalProfile
	 * @param burstDocList
	 * @param nonBurstDocList
	 */
	public static void visualization(PeakModeling2 peakModel,FeatureTemporalProfile queryTemporalProfile,Set<KbDocument> burstDocList,Set<KbDocument> nonBurstDocList,List<LanguageModel> burstLanguageModelList,List<LanguageModel> noBurstLanguageModelList,List<LanguageModel> allDocsLanguageModelList,int minNGramLengthLM,int maxNGramLengthLM){
		Helper.displayBurstsPeriods(queryTemporalProfile);
		System.out.println("total Docs:"+peakModel.totalNumberOfRelevantDocuments+"\tBurstsDocs:"+burstDocList.size()+"\tNonBurstsDocs:"+nonBurstDocList.size()+"\tCuttoff:"+queryTemporalProfile.getCutOffNorm());
//		Helper.displayLanguageModelsByEntropy(burstLanguageModelList,noBurstLanguageModelList, "BurstEntropy",minNGramLengthLM,maxNGramLengthLM,20);
//		Helper.displayLanguageModelsByLogLikelihoodBurst(burstLanguageModelList,noBurstLanguageModelList, "BurstLogLikelihood",minNGramLengthLM,maxNGramLengthLM,25);
		Helper.displayLanguageModelsByLogLikelihoodCorpus(allDocsLanguageModelList,noBurstLanguageModelList, "ALL",minNGramLengthLM,maxNGramLengthLM,100);
		
//		Helper.displayLanguageModelsByFrequency(burstLanguageModelList, noBurstLanguageModelList,"BurstFrequency",peakModel.stopWords, minNGramLengthLM,maxNGramLengthLM,40);
//		Helper.displayLanguageModelsByFrequency(noBurstLanguageModelList, burstLanguageModelList,"Non Burst",peakModel.stopWords,minNGramLengthLM,maxNGramLengthLM,40);
//		Helper.displayLanguageModelsByFrequency(allDocsLanguageModelList, noBurstLanguageModelList,"ALL",peakModel.stopWords, minNGramLengthLM,maxNGramLengthLM,40);
//		Helper.displayLanguageModelsByTFIDFpeak_year(allDocsLanguageModelList, noBurstLanguageModelList,"ALL",peakModel.stopWords, minNGramLengthLM,maxNGramLengthLM,250);
		
//        Helper.displayBurstsDocuments(queryTemporalProfile, peakModel.documentList);
//        Helper.displayNoBurstsDocuments(queryTemporalProfile, peakModel.documentList);
//		displayQueryNormFreqPerDayAndDetectedBurstPeriods(allDocsLanguageModelList, queryTemporalProfile);

	}
	
	
	/**
	 * Measure ngram significance based on sub-ngrams scores that includes(back-off model)
	 * @param burstLanguageModelList
	 * @param maxNGramLengthLM
	 * @param minNGramLengthLM
	 */
	public void measureSignificanceBasedOnBackOffModel(List<LanguageModel> burstLanguageModelList , int maxNGramLengthLM,int minNGramLengthLM,int minNgramLevelForScoreConsideration){
		//Test Back OFF model; we need to start from the bigger one to smaller one model!!!Otherwise we use the new feature weights!
		for(int ngramLength=maxNGramLengthLM;ngramLength>=minNGramLengthLM;ngramLength--){
			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			for(NGram ngram:m1.getNgramList())
				ngram.calculateLogLikelihoofBasedOnBackOffModel( burstLanguageModelList,minNgramLevelForScoreConsideration);
		}
	}
	
	/**
	 * Measure for each ngram in burst model its relative entropy to non-burst model
	 * @param M1
	 * @param M2
	 */
	public void measureRelativeEntropy(LanguageModel M1,LanguageModel M2){
		List<NGram> m2NGramList = M2.getNgramList();
		for(NGram ngram : M1.getNgramList()){
			double p_w_m1 = ngram.getP_w_language_model();
			double p_w_m2 = 0.0;
			int indexOfCurrentNGramInM2 = m2NGramList.indexOf(ngram);
			if(indexOfCurrentNGramInM2 != -1)
				p_w_m2 = m2NGramList.get(indexOfCurrentNGramInM2).getP_w_language_model();
			double D_m1_m2 = p_w_m1 * Helper.log2((double) p_w_m2 / p_w_m1);
			ngram.setRelative_Entropy(D_m1_m2);
		}
	}
	
	/**
	 * Measure the log likelihood for each term in burst doc set against the non burst doc set
	 * @see http://ucrel.lancs.ac.uk/llwizard.html
	 * @param M1
	 * @param M2
	 */
	public void measureSignificanceOfTermsInBurstAgainstNonBurstDocs(LanguageModel M1,LanguageModel M2){
		int totalNgramFreqM1 = M1.getTotalNGramFrequency();
		int totalNgramFreqM2 = M2.getTotalNGramFrequency();
		int totalNgramFreqBothModels = totalNgramFreqM1 + totalNgramFreqM2;
		List<NGram> m2NGramList = M2.getNgramList();
		for(NGram ngram : M1.getNgramList()){
			double p_w1 = ngram.getP_w_language_model();
			double p_w2 = 0.0;
			int a = ngram.getTf_query_peak();
			int b = 0;
			int indexOfCurrentNGramInM2 = m2NGramList.indexOf(ngram);
			if(indexOfCurrentNGramInM2 != -1){
				NGram ng2=m2NGramList.get(indexOfCurrentNGramInM2);
				b = ng2.getTf_query_peak();
				p_w2 = ng2.getP_w_language_model();
			}

			double E1 = (double)totalNgramFreqM1 * (a+b) / totalNgramFreqBothModels;
			double E2 = (double)totalNgramFreqM2 * (a+b) / totalNgramFreqBothModels;
			//@see http://ucrel.lancs.ac.uk/llwizard.html
			double secondParam = 0.0;
			if(b!=0)
				secondParam = (double)(b * Math.log((b/E2)));
			double LOG_Likelyhood_burst = (double) 2 * ((a * Math.log((a/E1))) + secondParam);
			if(p_w1<p_w2)
				LOG_Likelyhood_burst *= -1;

			//other way to compute it..
//			int c = totalNgramFreqM1 - a;
//			int d = totalNgramFreqM2 - b;
//			double LOG_Likelyhood_burst = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));

			ngram.setLOG_Likelyhood_burst(LOG_Likelyhood_burst);
		}
	}

	/**
	 * 
	 * @param ngramList
	 */
	public void getNgramPerYearSTats(List<NGram> ngramList,int  topN,String date){
//        ngramList = Helper.keepNoStopWordsFromList(ngramList, stopWords);
//        ngramList = Helper.skipNgramWithQueryAndStopWord(ngramList, stopWords, query);
//        ngramList = Helper.keepNoNgramNumbersFromList(ngramList);
//        ngramList = Helper.skipNgramWithNumberAndStopWord(ngramList, stopWords);
//        ngramList = Helper.keepNoCominationWithStopWordsFromList(ngramList, stopWords);

        List<NGram> finalNGramList = new ArrayList<NGram>();

    	PeakModeling.NgramSearchMultiThread(ngramList, NUMBER_THREADS, date, nGramSearcher, 1,N_corpus,N_peak); //multihreading
    	this.N_query_peakPeriod = Helper.removeNgramsWithNoOccurenceInNGramIndex(ngramList, finalNGramList);
    	PeakModeling.calculateProbabilitiesAndMeasures(finalNGramList, null, minN, N_corpus, N_query_peakPeriod, N_peak, N_years, maxTF_query_peak);

    	//Display top N based on Log Corpus
    	System.out.println("### top ngrams cpmapred whole corpus ####");
    	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_CORPUS);
    	int c=0;
       	for(NGram ng:finalNGramList){
       		System.out.println(ng.getNgram()+"\t"+ng.getLOG_Likelyhood_corpus());
       		if(c++ > topN)
       			break;
       	}
       	System.out.println("########################################");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Input Paramaters
	 */
	private final boolean useForSearchOnlyTitle;
	private final boolean useStopWords;
	private final int minN;
	private final int maxN;
	private final int NUMBER_THREADS;
	private final int MAX_DOCS;
	private final int MAX_TITLE_LENGTH;
	private final int MIN_TITLE_LENGTH;
	private final int burstTimeSpan;
	
	/*
	 * Variables
	 */
	private Set<KbDocument> documentList = new LinkedHashSet<KbDocument>();
	private HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
	private List<String> stopWords = new ArrayList<String>();
    //keep the doc frew that query got durring the peak year
	private HashMap<String,Integer> queryDocFreqPerDayMap = new HashMap<String,Integer>();
	private HashMap<String,Float> queryTotalDocScorePerDayMap = new HashMap<String,Float>();
	
	private int totalNumberOfRelevantDocuments = 0;
	private long maxTF_query_peak = 0;
	private long N_peak = 0;//total number of words in peak period(uni,bi or mix)
	private long N_corpus = 0;//total number of words in coprus(uni,bi or mix)
	private long N_query_peakPeriod = 0;//total number of words in query + peak period
	private long N_years = 0;//total number of years

	//FILES
	String dutchStopWordsFile =  "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/dutch.txt";
	private final String stopWordFile;
    private final String indexKbCorpusFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/KB_1950_1995";
    private final String indexKbUnigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
    private final String indexKbBigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB2gramMin10PerYear1840-1995";
    private final String fileWithTotalTFperYearUnigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
    private final String fileWithTotalTFperYearBigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";

    /*
     * Lucene
     */
    //KB indexes and searches
    private Directory indexKbDir = null;
    private IndexReader kbIndexReader = null;
    private IndexSearcher kbSearcher = null;
    private Analyzer NgramAnalyzerForTokenization = null;       
    private QueryParser queryParser = null;
    private Analyzer queryAnalyzer = null;
    //Ngram index
    private Directory indexNgramDir = null;
    private DirectoryReader ngramIndexReader = null;
    private IndexSearcher nGramSearcher = null;

    
	/**
	 * @param query
	 * @param date
	 * @param useForSearchOnlyTitle
	 * @param useStopWords
	 * @param minN
	 * @param maxN
	 * @param nUMBER_THREADS
	 * @param ngramList
	 * @param documentList
	 * @param peakPeriodMap
	 * @param stopWords
	 * @param totalNumberOfRelevantDocuments
	 * @param maxTF_query_peak
	 * @param n_peak
	 * @param n_corpus
	 * @param n_query_peakPeriod
	 * @param n_years
	 * @param dutchStopWordsFile
	 * @param stopWordFile
	 * @param indexKbCorpusFileName
	 * @param indexKbUnigramFileName
	 * @param indexKbBigramFileName
	 * @param fileWithTotalTFperYearUnigram
	 * @param fileWithTotalTFperYearBigram
	 * @throws IOException 
	 */
	public PeakModeling2(boolean useForSearchOnlyTitle, boolean useStopWords, int minN,	int maxN, int nUMBER_THREADS, int mAX_DOCS,int mAX_TITLE_LENGTH,int mIN_TITLE_LENGTH, int burstTimeSpan) throws IOException {
		super();
		this.useForSearchOnlyTitle = useForSearchOnlyTitle;
		this.useStopWords = useStopWords;
		this.minN = minN;
		this.maxN = maxN;
		this.NUMBER_THREADS = nUMBER_THREADS;
		this.MAX_DOCS = mAX_DOCS;
		this.MAX_TITLE_LENGTH = mAX_TITLE_LENGTH;
		this.MIN_TITLE_LENGTH = mIN_TITLE_LENGTH;
		this.burstTimeSpan = burstTimeSpan;
		
		if(useStopWords)
			this.stopWordFile = this.dutchStopWordsFile;
		else
			this.stopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";

		
		/*
		 * initialize stop words
		 */
		this.stopWords = Helper.readFileLineByLineReturnListOfLineString(dutchStopWordsFile);

		/*
		 * initialize lucene
		 */
		initializeLucene();
	}
	
	/**
	 * Create a list of language models with min and max given length; Each model consists from a list of ngrams together with their  frequencies
	 * @param documentList
	 * @param minNGramLength
	 * @param maxNGramLength
	 * @return list of language model
	 * @throws IOException
	 */
	public static List<LanguageModel> createLanguageModels(Set<KbDocument> documentList,int minNGramLength,int maxNGramLength) throws IOException{
		List<LanguageModel> languageModelList = new ArrayList<LanguageModel>();
		for(int ngramLength=minNGramLength;ngramLength<=maxNGramLength;ngramLength++)
			languageModelList.add(createLanguageModel(documentList, ngramLength));
		return languageModelList;
	}
	public static LanguageModel createLanguageModel(Set<KbDocument> documentList,int ngramLength) throws IOException{
		List<NGram> ngramList = new ArrayList<NGram>();
		for(KbDocument kb : documentList)
			Helper.mapTokenListToNGramList(Helper.getGivenLengthNgramsFromList(kb.getTokenSet(),ngramLength), kb.getDate(), "title", ngramList);
		
		LanguageModel languageModel = new LanguageModel(ngramLength,ngramList,documentList.size());
		return languageModel;
	}

	/**
	 * TODO this needs refactoring to be more efficient
	 * @param featureTemporalProfile
	 * @param documentList
	 * @return list of documents that are published durring query;s burst periods
	 */
	public  Set<KbDocument> getBurstsDocumentsList(FeatureTemporalProfile featureTemporalProfile){
		Set<KbDocument> documentBurstList = new HashSet<KbDocument>();
		Set<String> allBurstYearSet = new HashSet<String>();
        for(Burst burst:featureTemporalProfile.getBurstList())
        	allBurstYearSet.addAll(burst.getDateSet());
//        allBurstYearSet.add("1965-06-28");
		for(KbDocument kb : this.documentList){
			if(allBurstYearSet.contains(kb.getDate()))
				documentBurstList.add(kb);
		}
        return documentBurstList;
	}
	/**
	 * 
	 * @param featureTemporalProfile
	 * @param documentList
	 * @return list of documents that are NOT published durring query;s burst periods
	 */
	public  Set<KbDocument> getNonBurstsDocumentsList(FeatureTemporalProfile featureTemporalProfile){
		Set<KbDocument> documentBurstList = new HashSet<KbDocument>();
		Set<String> allBurstYearSet = new HashSet<String>();
        for(Burst burst:featureTemporalProfile.getBurstList())
        	allBurstYearSet.addAll(burst.getDateSet());
    	for(KbDocument kb : this.documentList){
  			if(!allBurstYearSet.contains(kb.getDate()))
  				documentBurstList.add(kb);
		}
        return documentBurstList;
	}


	/**
	 * Close Lucene indexes
	 * @throws IOException
	 */
	public void closeIndexes() throws IOException{
        this.kbIndexReader.close();
        this.indexKbDir.close();

        if(this.ngramIndexReader != null){
	        this.ngramIndexReader.close();
	        this.indexNgramDir.close();
        }
	}
	
	
	/**
	 * @return the queryTotalDocScorePerDayMap
	 */
	public HashMap<String, Float> getQueryTotalDocScorePerDayMap() {
		return queryTotalDocScorePerDayMap;
	}



	/**
	 * @param queryTotalDocScorePerDayMap the queryTotalDocScorePerDayMap to set
	 */
	public void setQueryTotalDocScorePerDayMap(
			HashMap<String, Float> queryTotalDocScorePerDayMap) {
		this.queryTotalDocScorePerDayMap = queryTotalDocScorePerDayMap;
	}



	/**
	 * @return the totalNumberOfRelevantDocuments
	 */
	public int getTotalNumberOfRelevantDocuments() {
		return totalNumberOfRelevantDocuments;
	}

	/**
	 * @param totalNumberOfRelevantDocuments the totalNumberOfRelevantDocuments to set
	 */
	public void setTotalNumberOfRelevantDocuments(int totalNumberOfRelevantDocuments) {
		this.totalNumberOfRelevantDocuments = totalNumberOfRelevantDocuments;
	}

	public void GetDocsPer15daysDistribution(String query,String date) throws ParseException, IOException{
		Map<String,Integer> dateToDf = new LinkedHashMap<String,Integer>();
		String[] daysArr = new String[2];
		daysArr[0] = "01-15";
		daysArr[1] = "16-31";
		int totalHits=0;
		for(int i=1;i<=12;i++){
			
			String month = null;
			if(i<10) month = "0" +i;
			else month = Integer.toString(i);

			
			for(String days:daysArr){
				String[] startEndDays = days.split("-");
//				TopDocs topDocs = HelperLucene.queryIndexGetTopDocsBoolean(queryParser, kbSearcher, "\""+query+"\"", date, "-"+month+"-"+startEndDays[0],"-"+month+"-"+startEndDays[1], this.MAX_DOCS);
				TopDocs topDocs = HelperLucene.queryIndexGetDocsWithoutScoring(this.queryParser,this.kbSearcher, constructQuery15Days(query, date, i,startEndDays[0],startEndDays[1]),this.MAX_DOCS);
				int hits = topDocs.totalHits;
				//System.out.println(date+"-"+i+"-"+startEndDays[0]+","+hits);
				totalHits+=hits;
				dateToDf.put(date+"-"+i+"-"+startEndDays[0], hits);
			}
		}
		
//		for(Map.Entry<String, Integer> entry:dateToDf.entrySet())
//			System.out.println(entry.getKey()+"\t"+(double)entry.getValue()/totalHits);
		
		calculateMA(dateToDf, 2,2);
	}
	
	public void calculateMA(Map<String,Integer> dateToDf,int timeSpan,double standardDeviationCutOff){
		LinkedHashMap<String,Double> movingAvgNormMap = new LinkedHashMap<String,Double>();
		double sumUpMovingAngNorm = 0.0;

		double totalFeatureFreq = Burstiness.totalFeatureFrequencies(dateToDf);

		String[] datesArr = dateToDf.keySet().toArray(new String[dateToDf.size()]);
		for(int i=0;i<datesArr.length;i++){
//			if(i+timeSpan > datesArr.length)
//				break;

			double currentMA = (double)dateToDf.get(datesArr[i]) / totalFeatureFreq;
//			for(int y=i+1;y<i+timeSpan;y++){
//				currentMA += (double)dateToDf.get(datesArr[y])/totalFeatureFreq;
//			}
			movingAvgNormMap.put(datesArr[i], currentMA);
			sumUpMovingAngNorm +=currentMA;
		}
		
		int timeWindows = movingAvgNormMap.size();
		double cutOffNorm = Burstiness.getCutOff(sumUpMovingAngNorm, timeWindows, movingAvgNormMap,standardDeviationCutOff);
		
		System.out.println("cutOffNorm:"+cutOffNorm+"\tsumUpMovingAngNorm:"+sumUpMovingAngNorm);
		for(Map.Entry<String, Double> entry:movingAvgNormMap.entrySet()){
			if(entry.getValue() > cutOffNorm)
				System.out.println("BURST\t"+entry.getKey()+"\t"+entry.getValue());
			else
				System.out.println(entry.getKey()+"\t"+entry.getValue());
		}
		
	}


	/**
	 * Retrieve the docs that the query bring and save the query doc freq durring the peak year
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getKbDocs(String query,String date,boolean scoreDocs,int max_documents) throws ParseException, IOException{
		TopDocs topDocs = null;
		if(scoreDocs){
			topDocs = HelperLucene.queryIndexGetTopDocs(this.queryParser,this.kbSearcher, constructQuery(query,date),max_documents!=-1?max_documents:this.MAX_DOCS);
//			topDocs = HelperLucene.queryIndexGetTopDocsBoolean(queryParser, kbSearcher, "\""+query+"\"", date,null,null, this.MAX_DOCS);
		}
		else
			topDocs = HelperLucene.queryIndexGetDocsWithoutScoring(this.queryParser,this.kbSearcher, constructQuery(query,date),max_documents!=-1?max_documents:this.MAX_DOCS);

		ScoreDoc[] hits = topDocs.scoreDocs;
		queryDocFreqPerDayMap = new HashMap<String,Integer>();
		documentList = new LinkedHashSet<KbDocument>();
		

        if(hits.length==0){
        	System.out.println("Zero Results :/");
        }else{
        	int rank=1;
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				final Document doc = this.kbSearcher.doc(docId);
				
				/*
				 * Title
				 */
				final String title = doc.get("title");				
				final String docDate = doc.get("date");
				final String url = doc.get("url");				
				
				//Skip larger than N titles
				List<String> tokenList = HelperLucene.tokenizeString(this.NgramAnalyzerForTokenization, title);
//				int titleTokenSize = title.split("\\s+").length;
//				if(titleTokenSize > this.MAX_TITLE_LENGTH || titleTokenSize <= this.MIN_TITLE_LENGTH)
//					continue;


				
				//save current doc
				boolean isNewDoc = this.documentList.add(new KbDocument(docId, title, tokenList, docDate, url, hits[i].score,rank));
				if(isNewDoc){
					//Save query doc frequencies
					if(this.queryDocFreqPerDayMap.containsKey(docDate))
						this.queryDocFreqPerDayMap.put(docDate, this.queryDocFreqPerDayMap.get(docDate)+1);
					else
						this.queryDocFreqPerDayMap.put(docDate, 1);
					
					//save doc scores
					if(this.queryTotalDocScorePerDayMap.containsKey(docDate)){
						this.queryTotalDocScorePerDayMap.put(docDate, this.queryTotalDocScorePerDayMap.get(docDate)+hits[i].score);
					}
					else
						this.queryTotalDocScorePerDayMap.put(docDate, hits[i].score);
					
					
					rank++;
				}		
				
				
				
				//Save document rank per day
//				int TopK = 5;
//				if(this.queryTopKDocRankPerDayMap.containsKey(docDate)){
//					List<Integer> topKDocRankList = this.queryTopKDocRankPerDayMap.get(docDate);
//					if(topKDocRankList.size() <= TopK)
//						topKDocRankList.add(i+1);
//				}
//				else{
//					this.queryTopKDocRankPerDayMap.put(docDate, new ArrayList<Integer>(Arrays.asList(i+1)));
//				}
				
			}
			this.totalNumberOfRelevantDocuments = this.documentList.size();

        }

	}

	private void initializeLucene() throws IOException{
	    /*
         * KB index
         */
        this.indexKbDir = HelperLucene.getIndexDir(this.indexKbCorpusFileName);
        this.kbIndexReader = DirectoryReader.open(this.indexKbDir);
        this.kbSearcher = new IndexSearcher(this.kbIndexReader);
        this.NgramAnalyzerForTokenization = HelperLucene.getNGramAnalyzer(this.stopWordFile,this.minN,this.maxN);       
        
        /*
         * QueryParser based on the input parameter 'use only title' or not
         */
        this.queryAnalyzer = HelperLucene.getKbAnalyzer(this.stopWordFile);
        if(this.useForSearchOnlyTitle)
        	this.queryParser = new QueryParser(Version.LUCENE_43, "title", this.queryAnalyzer);
        else
        	this.queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},this.queryAnalyzer);
	}
	
	
	/**
	 * @return the documentList
	 */
	public Set<KbDocument> getDocumentList() {
		return documentList;
	}



	/**
	 * @param documentList the documentList to set
	 */
	public void setDocumentList(Set<KbDocument> documentList) {
		this.documentList = documentList;
	}



	public int getNrOfDocs(){
		return this.kbIndexReader.numDocs();
	}

	
	/**
	 * Get per Year Stats(N_peak,N_corpus,N_years) and NGram Index/Searcher
	 * @throws IOException
	 */
	private void getPerYearStats(String date) throws IOException{
        /**
		 * Get peak period Maps:  
		 * 		N_peak_period
		 * 		N_corpus(Year:TotalWords)
		 */
        if(date != null && !date.equals("null")){
	        if(this.minN==1){
				Helper.getPeakPeriodIndex(this.fileWithTotalTFperYearUnigram, this.peakPeriodMap);
				this.N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
				this.N_corpus = peakPeriodMap.get("TotalWords");
//				N_corpus = PeakModeling.totalFrequencies(fileWithTotalTFperYearUnigram, peakPeriodMap);

				this.indexNgramDir = HelperLucene.getIndexDir(this.indexKbUnigramFileName);
				this.ngramIndexReader = DirectoryReader.open(this.indexNgramDir);
				this.nGramSearcher = new IndexSearcher(this.ngramIndexReader);
				this.N_years = this.peakPeriodMap.size() - 1;
	        }else{
	        	Helper.getPeakPeriodIndex(fileWithTotalTFperYearBigram, peakPeriodMap);
	        	this.N_peak = peakPeriodMap.get(date);//TODO this may return null pointer exception
	        	this.N_corpus = peakPeriodMap.get("TotalWords");
//				N_corpus = PeakModeling.totalFrequencies(fileWithTotalTFperYearBigram, peakPeriodMap);

	        	this.indexNgramDir = HelperLucene.getIndexDir(this.indexKbBigramFileName);
	        	this.ngramIndexReader = DirectoryReader.open(this.indexNgramDir);
	        	this.nGramSearcher = new IndexSearcher(this.ngramIndexReader);
	        	this.N_years = this.peakPeriodMap.size() - 1 ;
	        }
        }        

	}
	
	private String constructQuery(String query,String dateGiven){
		String queryFinal = "\""+ query + "\"";
		String date = dateGiven.equals("null") ? null : dateGiven ;//year
		if(date != null)
			queryFinal = queryFinal + " AND date:["+date+"-01-01 TO "+date+"-12-31]";
		return queryFinal;
	}

	private String constructQuery15Days(String query,String dateGiven,int m,String startDay,String EndDay){		
		String month = null;
		if(m<10) month = "0" +m;
		else month = Integer.toString(m);
			
		String queryFinal = "\""+ query + "\"";
		String date = dateGiven.equals("null") ? null : dateGiven ;//year
		if(date != null)
			queryFinal = queryFinal + " AND date:["+date+"-"+month+"-"+startDay+" TO "+date+"-"+month+"-"+EndDay+"]";
		return queryFinal;
	}

	public static void displayQueryNormFreqPerDayAndDetectedBurstPeriods(List<LanguageModel> allDocsLanguageModelList, FeatureTemporalProfile queryTemporalProfile){

		Set<String> allBurstYearSet = queryTemporalProfile.getAllBurstDatesSet();
		LinkedHashMap<String, Double> maMap = queryTemporalProfile.getMovingAvgNormMap();
		Map<String,Integer> queryDofFreqMap = queryTemporalProfile.getFeatureDocFreqPerDayMap();
		long totalQueryFr = Burstiness.totalFeatureFrequencies(queryDofFreqMap);
		for(Map.Entry<String,Double> entry:maMap.entrySet()){
			String date = entry.getKey();
			double MA = 0.0;
			double nonBurst =0.0;
			double queryDF=0.0;
			if(queryDofFreqMap.containsKey(date.split(",")[0]))
				queryDF = (double)queryDofFreqMap.get(date.split(",")[0])/totalQueryFr;
			if(allBurstYearSet.contains(date.split(",")[0]))
				MA = entry.getValue();
			else
				nonBurst = queryDF;
			System.out.println(date.split(",")[0]+"\t"+queryDF+"\t"+MA+"\t"+nonBurst);
		}

	}



	/**
	 * @return the queryDocFreqPerDayMap
	 */
	public HashMap<String, Integer> getQueryDocFreqPerDayMap() {
		return queryDocFreqPerDayMap;
	}



	/**
	 * @param queryDocFreqPerDayMap the queryDocFreqPerDayMap to set
	 */
	public void setQueryDocFreqPerDayMap(
			HashMap<String, Integer> queryDocFreqPerDayMap) {
		this.queryDocFreqPerDayMap = queryDocFreqPerDayMap;
	}



	/**
	 * @return the burstTimeSpan
	 */
	public int getBurstTimeSpan() {
		return burstTimeSpan;
	}
	
	

}
