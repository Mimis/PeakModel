package org.peakModel.java.peakModel.clustering;

import java.util.ArrayList;
import java.util.List;

import org.peakModel.java.peakModel.document_process.KbDocument;

public class Clustering {

	/**
	 * clustering Based On NgramHits
	 * @param documentList
	 * @return
	 */
	public static List<Cluster> clusteringBasedOnNgramHits(List<KbDocument> documentList){
		List<Cluster> finalClusterList = new ArrayList<Cluster>();
		//1 step: cluster docs in the same cluster if they share identical NGram hits list
		List<Cluster> clusterList = clusterDocsBasedOnNGramHits(documentList);
		//2 step group two clusters together if they share common NGram hits
		List<Integer> GroupedClusterIndexes = new ArrayList<Integer>();
		for(int i=0;i<clusterList.size();i++){
			if(!GroupedClusterIndexes.contains(i)){
				Cluster cluster=clusterList.get(i);
				GroupedClusterIndexes.addAll(groupClusters(i, cluster, clusterList, new ArrayList<Integer>()));
				finalClusterList.add(cluster);
			}
		}
		
		for(Cluster cluster : finalClusterList)
			cluster.calculateAvgScore();
		return finalClusterList;
	}
	
	private static List<Integer> groupClusters(int currentIndex,Cluster initialCluster,List<Cluster> clusterList,List<Integer> previousGroupedClusterIndexes){
		boolean endedWithOtherCluster = false;
		for(int i=0;i<clusterList.size();i++){
			if(currentIndex!=i && !previousGroupedClusterIndexes.contains(i)){
				Cluster cluster=clusterList.get(i);
				if(initialCluster.shareAtLeastOneCommonNgramHit(cluster)){
					initialCluster.embedCluster(cluster);
					previousGroupedClusterIndexes.add(i);
					endedWithOtherCluster = true;
				}
			}
		}
		if(endedWithOtherCluster)
			groupClusters(currentIndex, initialCluster, clusterList, previousGroupedClusterIndexes);
		return previousGroupedClusterIndexes;
	}
	
	
	private static List<Cluster> clusterDocsBasedOnNGramHits(List<KbDocument> documentList){
		List<Cluster> clusterList = new ArrayList<Cluster>();
		for(KbDocument kb : documentList){
			
			Cluster tempCluster = new Cluster(kb.getNgramHitsList());
			int index = clusterList.indexOf(tempCluster);
			if(index != -1){
				Cluster oldCluster =  clusterList.get(index);
				oldCluster.adKbDoc(kb);
			}
			else{
				tempCluster.adKbDoc(kb);
				clusterList.add(tempCluster);
			}
		}
		return clusterList;
	}

}
