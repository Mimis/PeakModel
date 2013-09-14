package org.peakModel.java.peakModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.peakModel.java.peakModel.document_process.KbDocument;

public class PeakModelWithDocumentSimilarity {
	
	public static double[][] computeSimMatrix(List<KbDocument> documentList,int topNgrams){
		int nrOfDocs = documentList.size();
		double[][] distances = new double[nrOfDocs][nrOfDocs];
		for(int i=0;i<nrOfDocs;i++){
			Set<NGram> ngramHitsList1 = documentList.get(i).getNgramHitsList();
			for(int y=0;y<nrOfDocs;y++){
				Set<NGram> ngramHitsList2 = documentList.get(y).getNgramHitsList();
				int hits = getNrOfEqualNGramsInGivenLists(ngramHitsList1, ngramHitsList2);
				distances[i][y] = (double) hits / topNgrams;
//				System.out.println(distances[i][y]+"\t"+ngramHitsList1.toString()+"\t"+ngramHitsList2.toString());
			}
		}
		return distances;
	}
	
	private static int getNrOfEqualNGramsInGivenLists(Set<NGram> ngramHitsList1,Set<NGram> ngramHitsList2){
		int countHits = 0;
		for(NGram ngram:ngramHitsList1){
			if(ngramHitsList2.contains(ngram))
				countHits++;
		}
		return countHits;
	}
	

	/**
	 * Calculate for each doc the cosine sim with the peak model and sort the array by this score
	 * @param ngramList
	 * @param documentList
	 * @param topNgramsForConsideration
	 */
	public static void cosineSimilarity(List<NGram> ngramList,List<KbDocument> documentList,int topNgramsForConsideration){
		List<NGram> peakModelNGramList = ngramList.subList(0, topNgramsForConsideration);
		double normalizedPeakModelVector = getNormalizedNGramVector(peakModelNGramList);
		double normalizedDocumentVector = 0.0;
		int numerator = 0;
		double cosineSimilarity = 0.0;
		for(KbDocument kbDocument : documentList){
			if(kbDocument.getTokenSet().isEmpty()){
				kbDocument.setCosineSimilarity(0.0);
				continue;
			}
			normalizedDocumentVector = getNormalizedDocVector(kbDocument);
			numerator = getNumeratorOfCosineSim(peakModelNGramList, kbDocument.getTokenSet());
			cosineSimilarity = numerator / (normalizedPeakModelVector * normalizedDocumentVector);
			kbDocument.setCosineSimilarity(cosineSimilarity);
		}
       	Collections.sort(documentList, KbDocument.COMPARATOR_COSINE);
	}
	private static int getNumeratorOfCosineSim(List<NGram> peakModelNGramList,Set<String> docTokenSet){
		int numerator = 0;
		for(NGram ng : peakModelNGramList){
			if(docTokenSet.contains(ng.getNgram())){
				numerator += ng.getTf_query_peak() * 1;
			}					
		}
		return numerator;
	}
	
	private static double getNormalizedNGramVector(List<NGram> peakModelNGramList){
		double normalizedPeakModelVector = 0.0;
		for(NGram ngram:peakModelNGramList)
			normalizedPeakModelVector += Math.pow(ngram.getTf_query_peak(), 2);
		return Math.sqrt(normalizedPeakModelVector);
		
	}
	private static double getNormalizedDocVector(KbDocument kbDocument){
		double normalizedDocument = 0.0;
		for(String token : kbDocument.getTokenSet())
			normalizedDocument += Math.pow(1,2);		
		return Math.sqrt(normalizedDocument);
	}
	
	
	/**
	 * %%%%%%%%%%%  compute similarity between documents  %%%%%%%%%%%
	 * cosine,tf-idf_peakYear,stopwords,unigrams
	 * @param documentList
	 * @param languageModelList
	 * @param stopWordsList
	 * @return
	 */
	public static double[][] computeDocumentsSimMatrix(List<KbDocument> documentList,LanguageModel languageModelList,List<String> stopWordsList){
		int nrOfDocs = documentList.size();
		double[][] distances = new double[nrOfDocs][nrOfDocs];
		for(int i=0;i<nrOfDocs;i++){
			Set<String> doc1 = documentList.get(i).getTokenSet();
			for(int y=0;y<nrOfDocs;y++){
				Set<String> doc2 = documentList.get(y).getTokenSet();
				double cosine = cosine(doc1, doc2, languageModelList,stopWordsList);
				distances[i][y] = cosine;
			}
		}
		return distances;
	}
	private static double cosine(Set<String> doc1,Set<String> doc2,LanguageModel languageModelList,List<String> stopWordsList){
		//Compute vectors...
		Set<String> all = new HashSet<String>();all.addAll(doc1);all.addAll(doc2);
		List<Double> v1 = new ArrayList<Double>();
		List<Double> v2 = new ArrayList<Double>();
		for(String ng:all){
			if(stopWordsList.contains(ng))
				continue;
			NGram ngram = languageModelList.getNgram(ng, "title");
			double tfidf=ngram.getTF_IDF_peak_year();
			if(doc1.contains(ng)) v1.add(tfidf); else v1.add(0.0);
			if(doc2.contains(ng)) v2.add(tfidf); else v2.add(0.0);
		}
		//compute numerator
		double num = 0.0;
		for(int i=0;i<v1.size();i++)
			num += v1.get(i) * v2.get(i);
		//normalized vector
		double nv1 = getNormVector(v1);
		double nv2 = getNormVector(v2);
		//cosine
		return (double)num / (nv1*nv2);
	}

	private static double getNormVector(List<Double> vector){
		double normalizedV = 0.0;
		for(Double v:vector)
			normalizedV+=Math.pow(v,2);
		return Math.sqrt(normalizedV);
	}
}
