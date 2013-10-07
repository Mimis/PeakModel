package org.peakModel.java.peakModel.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;

public class ExplanationGeneration extends FeatureSelection{
	double avgPrecision = 0;
	double avgNumberOfWordsLengthInTitle = 0;
	double avgNumberOfWordsInTitle = 0;

	public ExplanationGeneration(
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
		
		final int minN = 2;
		final int maxN = 2;
		final boolean useForSearchOnlyTitle = false;
		final int burstTimeSpan = 7;
	    final double x = 2.0;
		final int MAX_DOCS = 400;
		final boolean scoreDocs=true;
		final boolean useDocFreqForBurstDetection=true;
		final int topFeatures = 25;//how many documents should we consider? TOTAL number of DOcs / N = number of docs to consider
	    
	    
		/*
		 * Specific for Explanation Generation parameters..
		 */
		final int maxTitleHeadline = 10;
		final int minTitleHeadline = 3;
		final int topMHeadlines = 5;
	    
	    
	    
		
		
		
		//========================================================== Main ==========================================================//	    	
	    ExplanationGeneration peakModel = new ExplanationGeneration(useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan);

	    
	    
		for(Map.Entry<String, String> entry: getQueryList().entrySet()){
	        long startTime = System.currentTimeMillis();
	        String date = entry.getValue();
	        String query = entry.getKey();
			System.out.println("\n##############\n"+query+"\t"+date);
	
			/**
			 * Execute query..
			 */
			peakModel.getPerYearStats(entry.getValue());
			FeatureTemporalProfile queryTemporalProfile = peakModel.runQuery(entry.getKey(),entry.getValue(),peakModel, x,scoreDocs,useDocFreqForBurstDetection);
			
			
	        /**
	         * Create Language models for each class(Burst,NonBurst);Ngram Candidate lists form each document set with length 1 to 3
	         */
			int minLang = 1;int maxLang = 4;
	        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
			Set<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile);
			List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minLang, maxLang);//TODO CHANGE THAT
			//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
			Set<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
			List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minLang, maxLang);
			//ALL DOCUMENTS
			List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.getDocumentList(), minLang, maxLang);
			
		   
			
	    	/**
	    	 * Feature Scoring..Attention: Choose manualy the method!!!
	    	 */
			LanguageModel lang = peakModel.featureSelection(date, minN, allDocsLanguageModelList, burstLanguageModelList, noBurstLanguageModelList);
	    	
			
			/**
	    	 * Get top features(do not include the query!!)
	    	 */
		    final boolean skipStopWordsDurringFeatureSelection = true;
	    	List<NGram> bestFeaturesList = getBestFeatures(lang.getNgramList(), topFeatures,entry.getKey(), skipStopWordsDurringFeatureSelection, peakModel);
			
	    	for(NGram ng:bestFeaturesList)
	    		System.out.println(ng.getNgram()+"\t"+ng.getTf_query_peak()+"\t"+ng.getLOG_Likelyhood_burst());
	    	
	    	
	    	//######
	    	/**
	    	 * Explanation generation
	    	 * 	1.HITS
	    	 * 	2.Cosine based LOG LIKELIHOOD
	    	 * 	3.Cosine based on term frequencies for Baseline..
	    	 */
//			peakModel.explanationGenerationHITS(bestFeaturesList, burstDocList, maxTitleHeadline, minTitleHeadline, minN);
	    	peakModel.explanationGenerationCosineLOG(bestFeaturesList, burstDocList, lang, maxTitleHeadline, minTitleHeadline, minN);
//	    	peakModel.explanationGenerationCosineTF(bestFeaturesList, burstDocList, lang, maxTitleHeadline, minTitleHeadline, minN);

			/**
			 * Visualize Explanations
			 */
//			String method = "HITS";
			String method = "COSINE";
			peakModel.visualizeExplanations(burstDocList,topMHeadlines,method);
			
			
			
			

			//peakModel.calculateHeadlinesStatistics(peakModel);	    	
	    	System.out.println("#Total run time:"+ (System.currentTimeMillis()-startTime));
		}	
		//Close Indexes
        peakModel.closeIndexes();
        

        System.out.println("\n\n#########\nAveragePrecision:"+ (double) peakModel.avgPrecision/getQueryList().entrySet().size());
        peakModel.displayAverageeStats(peakModel, getQueryList().size());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Clauclate cosine between features and documents;weight the log likelihood scores
	 * @param bestFeaturesList
	 * @param docList
	 * @param lang
	 * @param maxTitleHeadline
	 * @param minTitleHeadline
	 * @param ngramLength
	 */
	public void explanationGenerationCosineLOG(List<NGram> bestFeaturesList,Set<KbDocument> docList, LanguageModel lang,int maxTitleHeadline,int minTitleHeadline, int ngramLength){		
		for(KbDocument doc : docList){
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			
			double cosine  = cosineDocWithBestFeaturesLOG(doc, bestFeaturesList, lang);
			doc.setCosineSimilarity(cosine);
		}
	}

	public void explanationGenerationCosineTF(List<NGram> bestFeaturesList,Set<KbDocument> docList, LanguageModel lang,int maxTitleHeadline,int minTitleHeadline, int ngramLength){		
		for(KbDocument doc : docList){
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			
			double cosine  = cosineDocWithBestFeaturesTFbaseline(doc, bestFeaturesList, lang);
			doc.setCosineSimilarity(cosine);
		}
	}

	/**
	 * 
	 * @param doc
	 * @param bestFeaturesList
	 * @return cosine similarity between given doc and best features; weight the logLikelihood scores
	 */
	public double cosineDocWithBestFeaturesLOG(KbDocument doc,List<NGram> bestFeaturesList, LanguageModel lang){
		List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),lang.getNgramLength());
		Set<String> allFeaturesSet = constructVectorWithAllUniqueFeatures(bestFeaturesList, titleTokens);
		double[] vector1 = new double[allFeaturesSet.size()];
		double[] vector2 = new double[allFeaturesSet.size()];
		
		//create vectors
		int index=0;
		for(String feature : allFeaturesSet){
			NGram ng = lang.getNgram(feature, "title");
			if(bestFeaturesList.contains(ng)) vector1[index] = ng.getLOG_Likelyhood_burst(); else vector1[index] = 0.0;
			if(titleTokens.contains(feature)) vector2[index] = ng.getLOG_Likelyhood_burst(); else vector2[index] = 0.0;
//			if(bestFeaturesList.contains(ng)) vector1[index] = ng.getLOG_Likelyhood_corpus(); else vector1[index] = 0.0;
//			if(titleTokens.contains(feature)) vector2[index] = ng.getLOG_Likelyhood_corpus(); else vector2[index] = 0.0;

			index++;
		}
		//compute numerator
		double num = 0.0;
		for(int i=0;i<vector1.length;i++)
			num += vector1[i] * vector2[i];
		//normalized vector
		double nv1 = getNormVector(vector1);
		double nv2 = getNormVector(vector2);
		//cosine
		return (double)num / (nv1*nv2);
	}
	
	/**
	 * 
	 * @param doc
	 * @param bestFeaturesList
	 * @return cosine similarity between given doc and best features; weight the logLikelihood scores
	 */
	public double cosineDocWithBestFeaturesTFbaseline(KbDocument doc,List<NGram> bestFeaturesList, LanguageModel lang){
		List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),lang.getNgramLength());
		Set<String> allFeaturesSet = constructVectorWithAllUniqueFeatures(bestFeaturesList, titleTokens);
		double[] vector1 = new double[allFeaturesSet.size()];
		double[] vector2 = new double[allFeaturesSet.size()];
		
		//create vectors
		int index=0;
		for(String feature : allFeaturesSet){
			NGram ng = lang.getNgram(feature, "title");
			if(bestFeaturesList.contains(ng)) vector1[index] = ng.getTf_query_peak(); else vector1[index] = 0.0;
			if(titleTokens.contains(feature)) vector2[index] = 1.0; else vector2[index] = 0.0;
			index++;
		}
		//compute numerator
		double num = 0.0;
		for(int i=0;i<vector1.length;i++)
			num += vector1[i] * vector2[i];
		//normalized vector
		double nv1 = getNormVector(vector1);
		double nv2 = getNormVector(vector2);
		//cosine
		return (double)num / (nv1*nv2);
	}
	
	
	
	
	/**
	 * Count how many times each headline includes the best features
	 * @param bestFeaturesList
	 * @param docList
	 * @param maxTitleHeadline
	 * @param minTitleHeadline
	 * @param ngramLength
	 */
	public void explanationGenerationHITS(List<NGram> bestFeaturesList,Set<KbDocument> docList, int maxTitleHeadline,int minTitleHeadline, int ngramLength){		
		int countHits = 0;
		for(KbDocument doc : docList){
			countHits = 0;
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			
			for(String token : titleTokens){
				if(bestFeaturesList.contains(new NGram(token,"title")))
					countHits++;
			}
			doc.setHitCounts(countHits);
		}
	}

	/**
	 * Visualize best headlines..
	 * @param burstDocList
	 * @param topMHeadlines
	 */
	public void visualizeExplanations(Set<KbDocument> burstDocList, int topMHeadlines,String method){
		List<KbDocument> docList = new ArrayList<KbDocument>(burstDocList);
		
		if(method.equals("HITS"))
			Collections.sort(docList,KbDocument.COMPARATOR_HITS);
		if(method.equals("COSINE"))
			Collections.sort(docList,KbDocument.COMPARATOR_COSINE);
		
		int c=0;
		for(KbDocument doc:docList){
			if(method.equals("HITS"))
				System.out.println(doc.getHitCounts()+"\t"+doc.getTitle()+"\t");
			if(method.equals("COSINE"))
				System.out.println(doc.getCosineSimilarity()+"\t"+doc.getTitle()+"\t");
			
			if(c++ > topMHeadlines) break;
		}
	}

	
	public static Set<String> constructVectorWithAllUniqueFeatures(List<NGram> bestFeaturesList,List<String> titleTokens){
		Set<String> all = new HashSet<String>();
		for(NGram ng:bestFeaturesList)
			all.add(ng.getNgram());
		for(String ng:titleTokens)
			all.add(ng);
		return all;
	}

	private static double getNormVector(double[] vector){
		double normalizedV = 0.0;
		for(Double v:vector)
			normalizedV+=Math.pow(v,2);
		return Math.sqrt(normalizedV);
	}

	
	
	public static Map<String,String> getQueryList(){
		Map<String,String> queryToDateMap = new HashMap<String,String>();
		queryToDateMap.put("lockheed", "1976");
		queryToDateMap.put("NSB", "1979");
		queryToDateMap.put("Haagse Post", "1974");
		queryToDateMap.put("Recessie", "1975");
		queryToDateMap.put("Krakers", "1981");
		queryToDateMap.put("Beatrix", "1965");
		
//		queryToDateMap.put("griekenland", "1967");
		return queryToDateMap;
	}

	
}
