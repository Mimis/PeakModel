package org.peakModel.java.peakModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import org.peakModel.java.lucene.searching.Test;
import org.peakModel.java.peakModel.burstiness.Burst;
import org.peakModel.java.peakModel.burstiness.Burstiness;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class PeakModeling2 {
	
	public static void main(String args[]) throws IOException, ParseException, java.text.ParseException{
		
		//========================================================== Input Parameters ==========================================================//
		final String initialQuery = args[0];
		final String date = args[1].equals("null") ? null : args[1] ;//year
		final boolean useForSearchOnlyTitle = Boolean.parseBoolean(args[2]);
		final boolean useStopWords = Boolean.parseBoolean(args[3]);
		final int minN = Integer.parseInt(args[4]);
		final int maxN = Integer.parseInt(args[5]);
		final int NUMBER_THREADS = Integer.parseInt(args[6]);
		final int MAX_DOCS = 30000;
		final int MAX_TITLE_LENGTH = 100;
		final int MIN_TITLE_LENGTH = 1;
		final int burstTimeSpan = 7;
	    final double x = 2.0;

		//FILES
		String dutchStopWordsFile =  "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/dutch.txt";
		String stopWordFile = null;
		if(useStopWords)
			stopWordFile = dutchStopWordsFile;
		else
			stopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";
		String indexKbCorpusFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/KB_1950_1995";
		String indexKbUnigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String indexKbBigramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB2gramMin10PerYear1840-1995";
		String fileWithTotalTFperYearUnigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFunigrams.tsv";
		String fileWithTotalTFperYearBigram = "/Users/mimis/Development/EclipseProject/PeakModel/index/PeakPeriodTFIndex/peakPeriodTFbigrams.tsv";
		//==========================================================End Parameters==========================================================//

		
		
		
		    
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//========================================================== Main ==========================================================//
		PeakModeling2 peakModel = new PeakModeling2(initialQuery, date, useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan, dutchStopWordsFile, stopWordFile, indexKbCorpusFileName, indexKbUnigramFileName, indexKbBigramFileName, fileWithTotalTFperYearUnigram, fileWithTotalTFperYearBigram);
		long startTime = System.currentTimeMillis();

		/*
		 * Get documents based on given query
		 */
		peakModel.getKbDocs();
	    System.out.println("#Total Retrieve Documents run time:"+ (System.currentTimeMillis()-startTime)/1000);

		/*
		 * Construct Query Temporal Profile
		 * 	1) detect burst periods based on querys temporal distribution
		 */
        FeatureTemporalProfile queryTemporalProfile = Burstiness.measureBurstinessForPeakYearMovingAverage(peakModel.date, peakModel.queryDocFreqPerDayMap, peakModel.burstTimeSpan,x);
        
        
        /*
         * Create Language models for each class(Burst,NonBurst);Ngram Candidate lists form each document set with length 1 to 3
         */
        int minNGramLengthLM = 1;
        int maxNGramLengthLM = 4;
        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
		List<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile);
		List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minNGramLengthLM, maxNGramLengthLM);
		//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
		List<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
		List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minNGramLengthLM, maxNGramLengthLM);
		//ALL DOCUMENTS
//		List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.documentList, minNGramLengthLM, maxNGramLengthLM);

		

		/**
		 * Statistical Measures between burst against non burst features
		 */
		for(int ngramLength=minNGramLengthLM;ngramLength<=maxNGramLengthLM;ngramLength++){
			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel m2 = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			peakModel.measureRelativeEntropy(m1,m2);
			peakModel.measureSignificanceOfTermsInBurstAgainstNonBurstDocs(m1, m2);
		}
		
		
		/**
		 * Statistical Measures against the whole corpus
		 * Get best ngrams based on Log_corpus measure
		 */
//		LanguageModel lang = allDocsLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(2)));
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(2)));
//		LanguageModel lang = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(2)));
//		peakModel.getNgramPerYearSTats(lang.getNgramList());
		
		

		
		
		
		/*
		 * VISUALIZATION BURSTS ...
		 */
		Helper.displayBurstsPeriods(queryTemporalProfile);
		System.out.println("total Docs:"+peakModel.totalNumberOfRelevantDocuments+"\tBurstsDocs:"+burstDocList.size()+"\tNonBurstsDocs:"+nonBurstDocList.size()+"\tCuttoff:"+queryTemporalProfile.getCutOffNorm());
//		Helper.displayLanguageModelsByEntropy(burstLanguageModelList, "Burst",minNGramLengthLM,maxNGramLengthLM,20);
		Helper.displayLanguageModelsByLogLikelihoodBurst(burstLanguageModelList,noBurstLanguageModelList, "Burst",minNGramLengthLM,maxNGramLengthLM,200);
//		Helper.displayLanguageModelsByFrequency(burstLanguageModelList, "Burst",null,minNGramLengthLM,maxNGramLengthLM,200);
//		Helper.displayLanguageModelsByFrequency(noBurstLanguageModelList, "Non Burst",minNGramLengthLM,maxNGramLengthLM,2000);
//		Helper.displayLanguageModelsByFrequency(allDocsLanguageModelList, "ALL",peakModel.stopWords, minNGramLengthLM,maxNGramLengthLM,2000);
		
//        Helper.displayBurstsDocuments(queryTemporalProfile, peakModel.documentList);
//        Helper.displayNoBurstsDocuments(queryTemporalProfile, peakModel.documentList);
//		displayGeneral(allDocsLanguageModelList, queryTemporalProfile);

		
		/**
		 * Back off model log likelihood
		 */
		peakModel.measureSignificanceBasedOnBackOffModel(burstLanguageModelList, maxNGramLengthLM, minNGramLengthLM);
		Helper.displayLanguageModelsByLogLikelihoodBurst(burstLanguageModelList,noBurstLanguageModelList, "Burst",minNGramLengthLM,maxNGramLengthLM,200);


		
		
		
        
        //Close Indexes
        peakModel.closeIndexes();
        
        
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
        //========================================================== End Main ==========================================================//
	}
	
	/**
	 * Measure ngram significance based on sub-ngrams scores that includes(back-off model)
	 * @param burstLanguageModelList
	 * @param maxNGramLengthLM
	 * @param minNGramLengthLM
	 */
	public void measureSignificanceBasedOnBackOffModel(List<LanguageModel> burstLanguageModelList , int maxNGramLengthLM,int minNGramLengthLM){
		//Test Back OFF model; we need to start from the bigger one to smaller one model!!!Otherwise we use the new feature weights!
		for(int ngramLength=maxNGramLengthLM;ngramLength>=minNGramLengthLM;ngramLength--){
			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			for(NGram ngram:m1.getNgramList())
				ngram.calculateLogLikelihoofBasedOnBackOffModel( burstLanguageModelList);
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
			
//			int c = totalNgramFreqM1 - a;
//			int d = totalNgramFreqM2 - b;
//			double LOG_Likelyhood_burst = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));
			if(p_w1<p_w2)
				LOG_Likelyhood_burst *= -1;
			ngram.setLOG_Likelyhood_burst(LOG_Likelyhood_burst);
		}
	}

	/**
	 * 
	 * @param ngramList
	 */
	public void getNgramPerYearSTats(List<NGram> ngramList){
        ngramList = Helper.keepNoStopWordsFromList(ngramList, stopWords);
        ngramList = Helper.skipNgramWithQueryAndStopWord(ngramList, stopWords, query);
        ngramList = Helper.keepNoNgramNumbersFromList(ngramList);
        ngramList = Helper.skipNgramWithNumberAndStopWord(ngramList, stopWords);
        ngramList = Helper.keepNoCominationWithStopWordsFromList(ngramList, stopWords);

        List<NGram> finalNGramList = new ArrayList<NGram>();

    	PeakModeling.NgramSearchMultiThread(ngramList, NUMBER_THREADS, date, nGramSearcher, 1,N_corpus,N_peak); //multihreading
    	this.N_query_peakPeriod = Helper.removeNgramsWithNoOccurenceInNGramIndex(ngramList, finalNGramList);
    	PeakModeling.calculateProbabilitiesAndMeasures(finalNGramList, null, minN, N_corpus, N_query_peakPeriod, N_peak, N_years, maxTF_query_peak);

    	//Display top N based on Log Corpus
    	Collections.sort(finalNGramList, NGram.COMPARATOR_LOG_CORPUS);
    	int c=0;
       	for(NGram ng:finalNGramList){
       		System.out.println(ng.getNgram()+"\t"+ng.getTf_query_peak()+"\t"+ng.getLOG_Likelyhood_corpus());
       		if(c++ > 200)
       			break;
       	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Input Paramaters
	 */
	private final String query;
	private final String date;//year
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
	private List<KbDocument> documentList = new ArrayList<KbDocument>();
	private HashMap<String,Long> peakPeriodMap = new HashMap<String,Long>();
	private List<String> stopWords = new ArrayList<String>();
    //keep the doc frew that query got durring the peak year
	private HashMap<String,Integer> queryDocFreqPerDayMap = new HashMap<String,Integer>();
	
	
	private int totalNumberOfRelevantDocuments = 0;
	private long maxTF_query_peak = 0;
	private long N_peak = 0;//total number of words in peak period(uni,bi or mix)
	private long N_corpus = 0;//total number of words in coprus(uni,bi or mix)
	private long N_query_peakPeriod = 0;//total number of words in query + peak period
	private long N_years = 0;//total number of years

	//FILES
	private final String stopWordFile;
    private final String  indexKbCorpusFileName;
    private final String  indexKbUnigramFileName;
    private final String  indexKbBigramFileName;
    private final String  fileWithTotalTFperYearUnigram;
    private final String  fileWithTotalTFperYearBigram;
    
    
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
	public PeakModeling2(String query, String date,	boolean useForSearchOnlyTitle, boolean useStopWords, int minN,	int maxN, int nUMBER_THREADS, int mAX_DOCS,int mAX_TITLE_LENGTH,int mIN_TITLE_LENGTH, int burstTimeSpan,
			 String dutchStopWordsFile,
			String stopWordFile, String indexKbCorpusFileName,
			String indexKbUnigramFileName, String indexKbBigramFileName,
			String fileWithTotalTFperYearUnigram,
			String fileWithTotalTFperYearBigram) throws IOException {
		super();
		this.query = query;
		this.date = date;
		this.useForSearchOnlyTitle = useForSearchOnlyTitle;
		this.useStopWords = useStopWords;
		this.minN = minN;
		this.maxN = maxN;
		this.NUMBER_THREADS = nUMBER_THREADS;
		this.MAX_DOCS = mAX_DOCS;
		this.MAX_TITLE_LENGTH = mAX_TITLE_LENGTH;
		this.MIN_TITLE_LENGTH = mIN_TITLE_LENGTH;
		this.burstTimeSpan = burstTimeSpan;
		
		
		this.stopWordFile = stopWordFile;
		this.indexKbCorpusFileName = indexKbCorpusFileName;
		this.indexKbUnigramFileName = indexKbUnigramFileName;
		this.indexKbBigramFileName = indexKbBigramFileName;
		this.fileWithTotalTFperYearUnigram = fileWithTotalTFperYearUnigram;
		this.fileWithTotalTFperYearBigram = fileWithTotalTFperYearBigram;
		
		/*
		 * initialize stop words
		 */
		this.stopWords = Helper.readFileLineByLineReturnListOfLineString(dutchStopWordsFile);
		//this is for lucene;if we use stopwords use the dutch file, otherwise an empty one
		if(this.useStopWords)
			stopWordFile = dutchStopWordsFile;
		else
			stopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";

		/*
		 * initialize lucene
		 */
		initializeLucene();
		/*
		 * Get Per Year Stats and Searcher
		 */
		getPerYearStats();
	}
	
	/**
	 * Create a list of language models with min and max given length; Each model consists from a list of ngrams together with their  frequencies
	 * @param documentList
	 * @param minNGramLength
	 * @param maxNGramLength
	 * @return list of language model
	 * @throws IOException
	 */
	public static List<LanguageModel> createLanguageModels(List<KbDocument> documentList,int minNGramLength,int maxNGramLength) throws IOException{
		List<LanguageModel> languageModelList = new ArrayList<LanguageModel>();
		for(int ngramLength=minNGramLength;ngramLength<=maxNGramLength;ngramLength++)
			languageModelList.add(createLanguageModel(documentList, ngramLength));
		return languageModelList;
	}
	public static LanguageModel createLanguageModel(List<KbDocument> documentList,int ngramLength) throws IOException{
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
	public  List<KbDocument> getBurstsDocumentsList(FeatureTemporalProfile featureTemporalProfile){
		List<KbDocument> documentBurstList = new ArrayList<KbDocument>();
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
	public  List<KbDocument> getNonBurstsDocumentsList(FeatureTemporalProfile featureTemporalProfile){
		List<KbDocument> documentBurstList = new ArrayList<KbDocument>();
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
        this.ngramIndexReader.close();
        this.kbIndexReader.close();
        this.indexNgramDir.close();
        this.indexKbDir.close();
	}
	
	
	/**
	 * Retrieve the docs that the query bring and save the query doc freq durring the peak year
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getKbDocs() throws ParseException, IOException{
        TopDocs topDocs = HelperLucene.queryIndexGetTopDocs(this.queryParser,this.kbSearcher, constructQuery(),this.MAX_DOCS);
		ScoreDoc[] hits = topDocs.scoreDocs;
		
        if(hits.length==0){
        	System.out.println("Zero Results :/");
        }else{
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				final Document doc = this.kbSearcher.doc(docId);
				
				/*
				 * Title
				 */
				final String title = doc.get("title");				
				final String docDate = doc.get("date");
				final String url = doc.get("url");				
				//System.out.println(docDate+"\t"+title+"\tScore:"+hits[i].score);
				
				//Skip larger than N titles
				List<String> tokenList = HelperLucene.tokenizeString(this.NgramAnalyzerForTokenization, title);
				if(tokenList.size() > this.MAX_TITLE_LENGTH || tokenList.size() <= this.MIN_TITLE_LENGTH)
					continue;
				
				//save current doc
				this.documentList.add(new KbDocument(docId, title, tokenList, docDate, url, hits[i].score));
				this.totalNumberOfRelevantDocuments++;
				//Save query doc frequencies
				if(this.queryDocFreqPerDayMap.containsKey(docDate))
					this.queryDocFreqPerDayMap.put(docDate, this.queryDocFreqPerDayMap.get(docDate)+1);
				else
					this.queryDocFreqPerDayMap.put(docDate, 1);
			}
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
	 * Get per Year Stats(N_peak,N_corpus,N_years) and NGram Index/Searcher
	 * @throws IOException
	 */
	private void getPerYearStats() throws IOException{
        /**
		 * Get peak period Maps:  
		 * 		N_peak_period
		 * 		N_corpus(Year:TotalWords)
		 */
        if(this.date != null){
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
	
	private String constructQuery(){
		String queryFinal = "\""+ this.query + "\"";
		String date = this.date.equals("null") ? null : this.date ;//year
		if(date != null)
			queryFinal = queryFinal + " AND date:["+date+"-01-01 TO "+date+"-12-31]";
		return queryFinal;
	}
	
	public static void displayGeneral(List<LanguageModel> allDocsLanguageModelList, FeatureTemporalProfile queryTemporalProfile){

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

}
