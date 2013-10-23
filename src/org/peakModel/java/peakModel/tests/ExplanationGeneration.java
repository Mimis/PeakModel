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
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;

public class ExplanationGeneration extends FeatureSelection{
	double avgDiversity = 0;
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
		final int MAX_DOCS = 175;
		final boolean scoreDocs=true;
		final boolean useDocFreqForBurstDetection=true;
		final int topFeatures = 10;//how many documents should we consider? TOTAL number of DOcs / N = number of docs to consider
	    // how many final document to get from preaks for further processing
	    final int N = 30;		

	    
		/*
		 * Specific for Explanation Generation parameters..
		 */
		final int maxTitleHeadline = 10;
		final int minTitleHeadline = 3;
		final int topMHeadlines = 5;
	    final double lamdaMMR = 0.7;
	    

	    final boolean skipStopWords = true;
	    final boolean skipFeaturesIncludeQuery = false;
	    final boolean skipFeaturesNumbers = true;

		
		
	    
	    
	    
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
			List<String> queryList = new ArrayList<String>(Arrays.asList(query.toLowerCase().split("\\s")));queryList.add(query.toLowerCase());
			int minLang = 1;int maxLang = 2;
	        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
			Set<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile,N);
			List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, skipFeaturesNumbers,queryList, peakModel.getStopWords(),query.toLowerCase());//TODO CHANGE THAT
			//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
			Set<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
			List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords,skipFeaturesNumbers, queryList, peakModel.getStopWords(),query.toLowerCase());
			//ALL DOCUMENTS
			List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.getDocumentList(), minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, skipFeaturesNumbers,queryList, peakModel.getStopWords(),query.toLowerCase());
			//Top N documents
			List<LanguageModel> pseudoRelevanceDocsLanguageModelList = createLanguageModels(peakModel.getTopNDocumentList(N), minLang, maxLang,skipFeaturesIncludeQuery,  skipStopWords, skipFeaturesNumbers,queryList, peakModel.getStopWords(),query.toLowerCase());

			
	    	/**
	    	 * Feature Scoring..Attention: Choose manualy the method!!!
	    	 */
			LanguageModel lang = peakModel.featureSelection(date, minN, allDocsLanguageModelList,pseudoRelevanceDocsLanguageModelList, burstLanguageModelList, noBurstLanguageModelList);
	    	
			
			/**
	    	 * Get top features(do not include the query!!)
	    	 */
	    	List<NGram> bestFeaturesList = getBestFeatures(lang.getNgramList(), topFeatures,entry.getKey());
	    	
	    	
			/**
			 * Hybrid
			 */
//	    	List<NGram> hybridBestFeaturesList2 = new ArrayList<NGram>();
//	    	peakModel.getNgramPerYearSTats(bestFeaturesList,25,date);
//	    	Collections.sort(bestFeaturesList, NGram.COMPARATOR_LOG_CORPUS);
//	    	int c=1;
//			for(NGram ng:bestFeaturesList){
//				hybridBestFeaturesList2.add(ng);
//				if(c++ >25) break;
//			}

	    	
	    	
	    	
	    	//######
	    	/**
	    	 * Explanation generation
	    	 * 	1.HITS
	    	 * 	2.Cosine based LOG LIKELIHOOD
	    	 * 	3.Cosine based on term frequencies for Baseline..
	    	 */
//			peakModel.explanationGenerationHITS(bestFeaturesList, peakModel.getDocumentList(), maxTitleHeadline, minTitleHeadline, minN,topMHeadlines);
//	    	peakModel.explanationGenerationCosineLOG(bestFeaturesList, peakModel.getDocumentList(), lang, maxTitleHeadline, minTitleHeadline, minN,topMHeadlines);
	    	peakModel.explanationGenerationMMRCosine(lamdaMMR, bestFeaturesList, peakModel.getDocumentList(), lang, maxTitleHeadline, minTitleHeadline, minN, topMHeadlines);
	    	
//	    	peakModel.explanationGenerationCosineTF(bestFeaturesList, burstDocList, lang, maxTitleHeadline, minTitleHeadline, minN,topMHeadlines);
			
			
			
			

			//peakModel.calculateHeadlinesStatistics(peakModel);	    	
	    	System.out.println("\t\t\t###Total run time:"+ (System.currentTimeMillis()-startTime));
		}	
		//Close Indexes
        peakModel.closeIndexes();
        

        System.out.println("\n\n#########\nAverageDiversity:"+ (double) peakModel.avgDiversity/getQueryList().entrySet().size());
		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", (double) peakModel.avgDiversity/getQueryList().entrySet().size()+"\t", true, true);

	}
	
	
	
	public double computeDiversity(List<KbDocument> docList){
		double allSimilarities = 0.0;
		int c=0;
		for(int i=0;i<docList.size();i++){
			for(int y=i+1;y<docList.size();y++){
				allSimilarities += cosineDocWithDoc(docList.get(i), docList.get(y));
				c++;
			}
		}
		return (double) allSimilarities / c;
	}
	
	/**
	 * MMR
	 * @param l
	 * @param bestFeaturesList
	 * @param docSet
	 * @param lang
	 * @param maxTitleHeadline
	 * @param minTitleHeadline
	 * @param ngramLength
	 * @param topMHeadlines
	 * @throws IOException 
	 */
	public void explanationGenerationMMRCosine(double l, List<NGram> bestFeaturesList,Set<KbDocument> docSet, LanguageModel lang,int maxTitleHeadline,int minTitleHeadline, int ngramLength,int topMHeadlines) throws IOException{		
		for(KbDocument doc : docSet){
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			double cosine  = cosineDocWithBestFeaturesLOG(doc, bestFeaturesList, lang);
			doc.setCosineSimilarity(cosine);
		}
		
		List<KbDocument> docList = new ArrayList<KbDocument>(docSet);
		Collections.sort(docList,KbDocument.COMPARATOR_COSINE);
		List<KbDocument> selectedDocs = new ArrayList<KbDocument>();
		
		List<Integer> selectedDocIndexes = new ArrayList<Integer>();
		for(int i=0;i<topMHeadlines;i++){
			if(selectedDocs.isEmpty()){
				selectedDocs.add(docList.get(i));
				selectedDocIndexes.add(i);
			}
			else{
				//calculate MMR for the next docs that got a cosine sim with the query
				double maxMMR=-1000.0;
				int indexOfMaxMMR=-1;
				for(int y=0;y<docList.size();y++){
					if(selectedDocIndexes.contains(y)) continue;
					KbDocument docToCheck = docList.get(y);
					if(docToCheck.getCosineSimilarity()==0) break;
					
					//CALCULATE DOC DIVERSITY BASED ON ALL FEATURES OR ONLY THE BEST ONESS
					double MMR =  (l*docToCheck.getCosineSimilarity()) - ((1-l) * maxSimFromPreviousSelected(docToCheck,selectedDocs,bestFeaturesList));
					//double xQuad = (l*docToCheck.getCosineSimilarity()) + ((1-l) * maxSimFromPreviousSelected(docToCheck,selectedDocs,bestFeaturesList));

					if(MMR>maxMMR){
						maxMMR = MMR;
						indexOfMaxMMR = y;
					}
				}
				//add the maximum MMR to selected
				if(indexOfMaxMMR==-1) break;
				selectedDocs.add(docList.get(indexOfMaxMMR));
				selectedDocIndexes.add(indexOfMaxMMR);
				if(selectedDocs.size()== topMHeadlines) break;
			}
		}
		//visualize
		for(KbDocument d:selectedDocs)
			System.out.println(d.getTitle());
		//diversityyy
		this.avgDiversity += computeDiversity(selectedDocs);
		System.out.println("##Diversity:"+ computeDiversity(selectedDocs));
		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", Helper.round(computeDiversity(selectedDocs),2)+"\t", true, true);

	}
//	public double xQuadDiversity(List<NGram> bestFeaturesList,List<KbDocument> selectedDocs){
//		double xQuad = 0.0;
//		for(NGram ng:bestFeaturesList){
//			xQuad += ng.getLOG_Likelyhood_corpus() * featurePenalization(selectedDocs, ng);
//		}
//		return xQuad;
//	}
//	public double featurePenalization(List<KbDocument> selectedDocs,NGram feature){
//		double featurePenalization = 0.0;
//		for(KbDocument d : selectedDocs){
//			featurePenalization*= (1-cosineDocWithBestFeaturesTFbaseline(doc, bestFeaturesList, lang));
//		}
//		return featurePenalization;
//	}

	public double maxSimFromPreviousSelected(KbDocument docToCheck, List<KbDocument> selectedDocs,List<NGram> bestFeaturesList){
		double maxSim = -100.0;
    	for(KbDocument d : selectedDocs){
    		//use all the terms as features
   			double cos=cosineDocWithDoc(docToCheck,d);
   			
   			//use onluy the beaset features as features
//   			double cos=cosineDocWithDocUseOnlyBestFeatures(docToCheck, d, bestFeaturesList);
   			
   			if(cos>maxSim)
   				maxSim = cos;
    	}
    	return maxSim;
	}
	
	
	
	
	
	
	/**
	 * Clauclate cosine between features and documents;weight the log likelihood scores
	 * @param bestFeaturesList
	 * @param docList
	 * @param lang
	 * @param maxTitleHeadline
	 * @param minTitleHeadline
	 * @param ngramLength
	 * @throws IOException 
	 */
	public void explanationGenerationCosineLOG(List<NGram> bestFeaturesList,Set<KbDocument> docList, LanguageModel lang,int maxTitleHeadline,int minTitleHeadline, int ngramLength,int topMHeadlines) throws IOException{		
		for(KbDocument doc : docList){
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			
			double cosine  = cosineDocWithBestFeaturesLOG(doc, bestFeaturesList, lang);
			doc.setCosineSimilarity(cosine);
		}
		visualizeExplanations(docList,topMHeadlines,"COSINE");
	}

	public void explanationGenerationCosineTF(List<NGram> bestFeaturesList,Set<KbDocument> docList, LanguageModel lang,int maxTitleHeadline,int minTitleHeadline, int ngramLength,int topMHeadlines) throws IOException{		
		for(KbDocument doc : docList){
			List<String> titleTokens = Helper.getGivenLengthNgramsFromList(doc.getTokenSet(),ngramLength);
			if(titleTokens.size() <= minTitleHeadline || titleTokens.size() >= maxTitleHeadline) continue;
			
			double cosine  = cosineDocWithBestFeaturesTFbaseline(doc, bestFeaturesList, lang);
			doc.setCosineSimilarity(cosine);
		}
		visualizeExplanations(docList,topMHeadlines,"COSINE");
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
			if(ng!=null){
//				if(bestFeaturesList.contains(ng)) vector1[index] = ng.getLOG_Likelyhood_burst(); else vector1[index] = 0.0;
//				if(titleTokens.contains(feature)) vector2[index] = ng.getLOG_Likelyhood_burst(); else vector2[index] = 0.0;
				if(bestFeaturesList.contains(ng)) vector1[index] = ng.getLOG_Likelyhood_corpus(); else vector1[index] = 0.0;
				if(titleTokens.contains(feature)) vector2[index] = ng.getLOG_Likelyhood_corpus(); else vector2[index] = 0.0;
				index++;
			}
		}
		

		//compute numerator
		double num = 0.0;
		for(int i=0;i<vector1.length;i++)
			num += vector1[i] * vector2[i];
		//normalized vector
		double nv1 = getNormVector(vector1);
		double nv2 = getNormVector(vector2);
		//cosine
		if(nv2==0) return 0.0; //if there are no any hits in the documents then is zero..
		return (double)num / (nv1*nv2);
	}

	
	public double cosineDocWithDoc(KbDocument doc1,KbDocument doc2){
		List<String> titleTokens1 = Helper.getGivenLengthNgramsFromList(doc1.getTokenSet(),1);
		List<String> titleTokens2 = Helper.getGivenLengthNgramsFromList(doc2.getTokenSet(),1);
		Set<String> allFeaturesSet = new HashSet<String>();allFeaturesSet.addAll(titleTokens1);allFeaturesSet.addAll(titleTokens2);

		double[] vector1 = new double[allFeaturesSet.size()];
		double[] vector2 = new double[allFeaturesSet.size()];
		
		//create vectors
		int index=0;
		for(String feature : allFeaturesSet){
			if(titleTokens1.contains(feature)) vector1[index] = 1; else vector1[index] = 0.0;
			if(titleTokens2.contains(feature)) vector2[index] = 1; else vector2[index] = 0.0;
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
		if(nv1==0 || nv2==0) return 0.0;
		return (double)num / (nv1*nv2);
	}

	public double cosineDocWithDocUseOnlyBestFeatures(KbDocument doc1,KbDocument doc2,List<NGram> bestFeaturesList){
		List<String> titleTokens1 = Helper.getGivenLengthNgramsFromList(doc1.getTokenSet(),1);
		List<String> titleTokens2 = Helper.getGivenLengthNgramsFromList(doc2.getTokenSet(),1);
		Set<String> allFeaturesSet = new HashSet<String>();allFeaturesSet.addAll(titleTokens1);allFeaturesSet.addAll(titleTokens2);

		double[] vector1 = new double[bestFeaturesList.size()];
		double[] vector2 = new double[bestFeaturesList.size()];
		
		//create vectors
		int index=0;
		for(NGram ng : bestFeaturesList){
			String feature = ng.getNgram();
			if(titleTokens1.contains(feature)) vector1[index] = 1; else vector1[index] = 0.0;
			if(titleTokens2.contains(feature)) vector2[index] = 1; else vector2[index] = 0.0;
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
		if(nv1==0 || nv2==0) return 0.0;
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
	 * @throws IOException 
	 */
	public void explanationGenerationHITS(List<NGram> bestFeaturesList,Set<KbDocument> docList, int maxTitleHeadline,int minTitleHeadline, int ngramLength,int topMHeadlines) throws IOException{		
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
		visualizeExplanations(docList,topMHeadlines,"HITS");
	}

	/**
	 * Visualize best headlines..
	 * @param burstDocList
	 * @param topMHeadlines
	 * @throws IOException 
	 */
	public void visualizeExplanations(Set<KbDocument> burstDocList, int topMHeadlines,String method) throws IOException{
		List<KbDocument> docList = new ArrayList<KbDocument>(burstDocList);
		List<KbDocument> bestdocList = new ArrayList<KbDocument>();

		if(method.equals("HITS"))
			Collections.sort(docList,KbDocument.COMPARATOR_HITS);
		if(method.equals("COSINE"))
			Collections.sort(docList,KbDocument.COMPARATOR_COSINE);
		
		int c=1;
		for(KbDocument doc:docList){
			if(method.equals("HITS") && doc.getHitCounts()==0)break;
			if(method.equals("COSINE") && doc.getCosineSimilarity()==0.0)break;	
			System.out.println(doc.getTitle());
			
			bestdocList.add(doc);
			if(c++ >= topMHeadlines) break;
		}
		
		//diversityyy
		this.avgDiversity += Helper.round(computeDiversity(bestdocList),2);
		System.out.println("##Diversity:"+ Helper.round(computeDiversity(bestdocList),2));
		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", Helper.round(computeDiversity(bestdocList),2)+"\t", true, true);

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
		
//		queryToDateMap.put("burgers", "1990");
		return queryToDateMap;
	}

	
}
