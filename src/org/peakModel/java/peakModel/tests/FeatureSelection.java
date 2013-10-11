package org.peakModel.java.peakModel.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.PeakModeling2;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;

public class FeatureSelection extends DocumentSelection{
	double avgPrecision = 0;
	double avgNumberOfWordsLengthInTitle = 0;
	double avgNumberOfWordsInTitle = 0;

	public FeatureSelection(
			boolean useForSearchOnlyTitle, boolean useStopWords, int minN,
			int maxN, int nUMBER_THREADS, int mAX_DOCS, int mAX_TITLE_LENGTH,
			int mIN_TITLE_LENGTH, int burstTimeSpan) throws IOException {
		super( useForSearchOnlyTitle, useStopWords, minN, maxN,
				nUMBER_THREADS, mAX_DOCS, mAX_TITLE_LENGTH, mIN_TITLE_LENGTH,
				burstTimeSpan);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws java.text.ParseException 
	 */
	public static void main(String[] args)  throws IOException, ParseException, java.text.ParseException {
		//========================================================== Input Parameters ==========================================================//
		final boolean useStopWords = false;
		final int NUMBER_THREADS = 2;
		final int MAX_TITLE_LENGTH = 100;
		final int MIN_TITLE_LENGTH = 1;
		final String backgroundFile="background.txt";
		final String foregroundFile="foreground.txt";
		//==========================================================End Parameters==========================================================//

		
		final int minN = 1;
		final int maxN = 1;
		final boolean useForSearchOnlyTitle = false;
		final int burstTimeSpan = 7;
	    final double x = 2.0;
		final int MAX_DOCS = 400;
		final boolean scoreDocs=true;
		final boolean useDocFreqForBurstDetection=true;
		final int topFeatures = 25;//how many documents should we consider? TOTAL number of DOcs / N = number of docs to consider
	    final double similarityProportionThreshold = 0.25;//if the common peak days is higher than this threshold then the feature is relevant

	    final boolean skipStopWords = true;
	    final boolean skipFeaturesIncludeQuery = true;

	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
		//========================================================== Main ==========================================================//	    	
	    FeatureSelection peakModel = new FeatureSelection(useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan);

        long startTime = System.currentTimeMillis();

		for(Map.Entry<String, String> entry: getQueryList().entrySet()){
	        String date = entry.getValue();
	        String query = entry.getKey();
			System.out.println("\n##############\n"+query+"\t"+date);
	
			
			/**
			 * Execute query
			 */
			peakModel.getPerYearStats(entry.getValue());
			FeatureTemporalProfile queryTemporalProfile = peakModel.runQuery(entry.getKey(),entry.getValue(),peakModel, x,scoreDocs,useDocFreqForBurstDetection);
			
			
			

			
	        /**
	         * Create Language models for each class(Burst,NonBurst);Ngram Candidate lists form each document set with length 1 to 3
	         */
			List<String> queryList = new ArrayList<String>(Arrays.asList(query.toLowerCase().split("\\s")));queryList.add(query.toLowerCase());
			int minLang = 1;int maxLang = 2;
	        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
			Set<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile);
			List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, queryList, peakModel.getStopWords());//TODO CHANGE THAT
			//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
			Set<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
			List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, queryList, peakModel.getStopWords());
			//ALL DOCUMENTS
			List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.getDocumentList(), minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, queryList, peakModel.getStopWords());
			


		    	    	
	    	/**
	    	 * Feature Scoring..
	    	 */
			LanguageModel lang = peakModel.featureSelection(date, minN, allDocsLanguageModelList, burstLanguageModelList, noBurstLanguageModelList);
	    	/**
	    	 * Get top features(do not include the query!!)
	    	 */
	    	List<NGram> bestFeaturesList = getBestFeatures(lang.getNgramList(), topFeatures,entry.getKey());
			
			//display
//	    	for(NGram ng:bestFeaturesList)
//				System.out.println(ng.getNgram()+"("+Helper.round(ng.getLOG_Likelyhood_corpus(),2)+")"+"\t"+ng.getTf_query_peak()+","+ng.getTf_peak()+","+ng.getTf_corpus());

	    	
	    	//testtt
	    	List<NGram> hybridBestFeaturesList2 = new ArrayList<NGram>();
	    	System.out.println("\n\n########final:");
	    	peakModel.getNgramPerYearSTats(bestFeaturesList,25,date);
	    	Collections.sort(bestFeaturesList, NGram.COMPARATOR_LOG_CORPUS);
	    	System.out.println(peakModel.getN_query_peakPeriod()+"\t"+peakModel.getN_corpus());

	    	int c=1;
			for(NGram ng:bestFeaturesList){
				System.out.println(ng.getNgram()+"\tburst:"+ng.getLOG_Likelyhood_burst()+"\tcorpus:"+ng.getLOG_Likelyhood_corpus()+"\t"+ng.getTf_query_peak()+","+ng.getTf_corpus());
				hybridBestFeaturesList2.add(ng);
				if(c++ >25) break;
			}

	    	
	    	
	    	/**
	    	 * #EVALUATION...
	    	 * Check Burstiness overlaping between Query and Features
	    	 */
//	    	peakModel.evaluateBestFeatures(query.toLowerCase(),queryTemporalProfile.getAllBurstDatesSet(),bestFeaturesList, date, x, scoreDocs, useDocFreqForBurstDetection,similarityProportionThreshold);
			
	    	
	    		    	
//			peakModel.calculateHeadlinesStatistics(peakModel);
		}	
		//Close Indexes
        peakModel.closeIndexes();
        
	    System.out.println("#Total run time:"+ (System.currentTimeMillis()-startTime));

        ///
		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", (double) peakModel.avgPrecision/getQueryList().entrySet().size()+"\t", true, true);
        System.out.println("\n\n#########\nAveragePrecision:"+ (double) peakModel.avgPrecision/getQueryList().entrySet().size());
        peakModel.displayAverageeStats(peakModel, getQueryList().size());

	}
	
	

	/**
	 * Feature scoring..
	 * @param date
	 * @param featureLevel
	 * @param allDocsLanguageModelList
	 * @param burstLanguageModelList
	 * @param noBurstLanguageModelList
	 * @return
	 * @throws IOException 
	 */
	public LanguageModel featureSelection(String date,int featureLevel,List<LanguageModel> allDocsLanguageModelList,List<LanguageModel> burstLanguageModelList,List<LanguageModel> noBurstLanguageModelList) throws IOException{
		/**
		 * 1. Baseline: term frequency
		 */
		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_TOTAL_TF);
		
		
		
		
		
		/**
		 * 2. Global LOG likelihood
		 */
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
//		getNgramPerYearSTats(lang.getNgramList(),25,date);
//    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_CORPUS);
//    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_PEAK);

		
		
		
		
		/**
		 * 2.1. Global LOG likelihood - BackOff Model - ATTENTION:need to chnage the ngram.calculateLogLikelihoofBasedOnBackOffModel to use log_corpus±±!!!
		 */
//		for(int ngramLength=1;ngramLength<=2;ngramLength++){
//			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			getPerYearStats(date,ngramLength);
//			getNgramPerYearSTats(m1.getNgramList(),25,date);
//		}
//		measureSignificanceBasedOnBackOffModel(burstLanguageModelList, 2, 2,1);
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
//    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_CORPUS);
		
		
		
		
		
		/**
		 * 3. Log compare Burst against non Burst
		 */
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
////		LanguageModel m2 = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
//		LanguageModel m2 = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(featureLevel)));
//		measureSignificanceOfTermsInBurstAgainstNonBurstDocs(lang, m2);
//		Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_LIKELIHOOD_BURST);
		
		
		
		
    	/**
    	 * 4. Back Off model - needs to assign which Lang Model to use with **featureLevel > 1**
    	 */
//		for(int ngramLength=1;ngramLength<=2;ngramLength++){
//			LanguageModel m1 = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			LanguageModel m2 = noBurstLanguageModelList.get(noBurstLanguageModelList.indexOf(new LanguageModel(ngramLength)));
////			LanguageModel m2 = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(ngramLength)));
//			measureSignificanceOfTermsInBurstAgainstNonBurstDocs(m1, m2);
//		}
//		measureSignificanceBasedOnBackOffModel(burstLanguageModelList, 2, 2,1);
//		LanguageModel lang = burstLanguageModelList.get(burstLanguageModelList.indexOf(new LanguageModel(featureLevel)));
//    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_LIKELIHOOD_BURST);
    	
//		System.out.println("Total number of feature candidates:"+lang.getNgramList().size());
    	return lang;
	}
	
	
	
	/**
	 * Evaluate features based on the common number of peak days with the query ones.
	 * @param queryBurstDays
	 * @param featureList
	 * @param date
	 * @param x
	 * @param scoreDocs
	 * @param useDocFreqForBurstDetection
	 * @throws ParseException
	 * @throws IOException
	 * @throws java.text.ParseException
	 */
	public void evaluateBestFeatures(String query,Set<String> queryBurstDays,List<NGram> featureList, String date, double x,boolean scoreDocs,boolean useDocFreqForBurstDetection,double similarityProportionThreshold) throws ParseException, IOException, java.text.ParseException{
		List<NGram> relevantFeatures = new ArrayList<NGram>();
		List<NGram> nonRelevantFeatures = new ArrayList<NGram>();
		for(NGram feature:featureList){
			//execute query
			FeatureTemporalProfile featureTemporalProfile = runQuery(Helper.replaceStopWordWitUnderScore(feature.getNgram(), getStopWords()),date,this, x,scoreDocs,useDocFreqForBurstDetection);
	    	
			/**
			 * Relevant if got common Peak Days...
			 */
			Set<String> common = new HashSet<String>(queryBurstDays);
	    	common.retainAll(featureTemporalProfile.getAllBurstDatesSet());
	    	double hitsRatio = (double) common.size() / featureTemporalProfile.getAllBurstDatesSet().size();
//	    	if(hitsRatio >= similarityProportionThreshold)
//	    		relevantFeatures.add(feature);
//	    	else
//	    		nonRelevantFeatures.add(feature);
	    	
	    	/**
			 * Relevant if feature's top docs include the query..
			 */
	    	int countQueryOccurncesInTopDocs = 0;
	    	for(KbDocument doc:getDocumentList()){
//	    		System.out.println(doc.getScore()+"\t"+doc.getTitle());
	    		if(doc.getTitle().toLowerCase().contains(query))countQueryOccurncesInTopDocs++;
	    		if(doc.getRank()>25)break;
	    	}
//	    	if(countQueryOccurncesInTopDocs > 0)
//	    		relevantFeatures.add(feature);
//	    	else
//	    		nonRelevantFeatures.add(feature);

	    	
	    	
	    	if(hitsRatio >= similarityProportionThreshold && countQueryOccurncesInTopDocs > 0 )//|| hitsRatio >= 0.5 || countQueryOccurncesInTopDocs > 12)
	    		relevantFeatures.add(feature);
	    	else
	    		nonRelevantFeatures.add(feature);

	    	//display hits on peak peridos and top N documents
	    	System.out.println("#Feature:"+feature.getNgram()+"("+this.getDocumentList().size()+")\tBurst Days:"+featureTemporalProfile.getAllBurstDatesSet().size()+"\tCommon HITS:"+common.size()+"("+hitsRatio+")"+"\tcountQueryOccurncesInTopDocs:"+countQueryOccurncesInTopDocs+"\n");
		}
		double precision = (double)relevantFeatures.size()/featureList.size();
		avgPrecision+=precision;
		
		
		//visualization...
		System.out.println("\n#Relevant Features(precision):"+precision);
		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", precision+"\t", true, true);
		for(NGram f:relevantFeatures)
			System.out.println("\t"+f.getNgram() + "\t" + f.getLOG_Likelyhood_burst()+"\t tf_q_peak:"+f.getTf_query_peak()+"\t tf_peak:"+f.getTf_peak()+"\t tf_corpus:"+f.getTf_corpus());
		
		System.out.println("\n#NOT Relevant Features:");
		for(NGram f:nonRelevantFeatures)
			System.out.println("\t"+f.getNgram() + "\t" + f.getLOG_Likelyhood_burst()+"\t tf_q_peak:"+f.getTf_query_peak()+"\t tf_peak:"+f.getTf_peak()+"\t tf_corpus:"+f.getTf_corpus());
	}
	
	
	
	
	/**
	 * 
	 * @param NGramList
	 * @param topN
	 * @param query
	 */
	public static List<NGram> getBestFeatures(List<NGram> NGramList,int topN,String query){
		List<NGram> bestFeatureList = new ArrayList<NGram>();
    	int c=1;
       	for(NGram ng:NGramList){
//       		System.out.println(ng.getNgram()+"\t"+ng.getTf_query_peak());
       		bestFeatureList.add(ng);
       		if(c++ >= topN)	break;
       	}
       	return bestFeatureList;
	}
	
	
	


		
	
	/**
	 * 1.avgNrOfWords
	 * 2.avgWordLength
	 */
	public void calculateHeadlinesStatistics(PeakModeling2 peakModel){
		double avgNrOfWords = 0;
		double avgWordLength = 0;
		for(KbDocument doc:peakModel.getDocumentList()){
			int tempNrWords = doc.getTitle().split("\\s").length;
			avgWordLength += doc.getTitle().length() - (tempNrWords-1);
			avgNrOfWords += tempNrWords;
			//System.out.println("\t"+doc.getTitle());
		}
		avgWordLength = (double) avgWordLength / avgNrOfWords;
		avgNrOfWords = (double)avgNrOfWords / peakModel.getDocumentList().size();
		this.avgNumberOfWordsLengthInTitle += avgWordLength;
		this.avgNumberOfWordsInTitle += avgNrOfWords;
		System.out.println("avgWordLength:"+avgWordLength+"\tavgNrOfWords:"+avgNrOfWords);
	}
	public void displayAverageeStats(PeakModeling2 peakModel,int numberofQueries){
		double avgWordsLengthTitle = avgNumberOfWordsLengthInTitle/numberofQueries;
		double avgNrOfWordsTitle = avgNumberOfWordsInTitle/numberofQueries;
		System.out.println("avgWordsLengthTitle:"+avgWordsLengthTitle+"\tavgNrOfWordsTitle:"+avgNrOfWordsTitle);
	}

	

	public static Map<String,String> getQueryList(){
		Map<String,String> queryToDateMap = new HashMap<String,String>();
//		queryToDateMap.put("lockheed", "1976");
//		queryToDateMap.put("NSB", "1979");
//		queryToDateMap.put("Haagse Post", "1974");
//		queryToDateMap.put("Recessie", "1975");
//		queryToDateMap.put("Krakers", "1981");
		queryToDateMap.put("Beatrix", "1965");
		
//		queryToDateMap.put("griekenland", "1967");
		return queryToDateMap;
	}

	
}
