package org.peakModel.java.peakModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			if(kbDocument.getTokenMap().isEmpty()){
				kbDocument.setCosineSimilarity(0.0);
				continue;
			}
			normalizedDocumentVector = getNormalizedDocVector(kbDocument);
			numerator = getNumeratorOfCosineSim(peakModelNGramList, kbDocument.getTokenMap());
			cosineSimilarity = numerator / (normalizedPeakModelVector * normalizedDocumentVector);
			kbDocument.setCosineSimilarity(cosineSimilarity);
		}
       	Collections.sort(documentList, KbDocument.COMPARATOR_COSINE);
	}
	private static int getNumeratorOfCosineSim(List<NGram> peakModelNGramList,Map<String,Integer> docTokenMap){
		int numerator = 0;
		for(NGram ng : peakModelNGramList){
			if(docTokenMap.containsKey(ng.getNgram())){
				numerator += ng.getTf_query_peak() * docTokenMap.get(ng.getNgram());
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
		for(Map.Entry<String, Integer> entry : kbDocument.getTokenMap().entrySet())
			normalizedDocument += Math.pow(entry.getValue(),2);		
		return Math.sqrt(normalizedDocument);
	}
}
