package org.peakModel.java.peakModel.clustering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.document_process.KbDocument;

public class Cluster {
	
	private List<KbDocument> kbDocList;
	private final Set<NGram> ngramHits;
	private double avgDocScore;
	/**
	 * @param kbDocList
	 * @param ngramHits
	 */
	public Cluster(Set<NGram> ngramHits) {
		super();
		this.ngramHits = ngramHits;
		this.kbDocList = new ArrayList<KbDocument>();
	}
	
	
	/**
	 * @return the ngramHits
	 */
	public boolean shareAtLeastOneCommonNgramHit(Cluster cluster) {
		Set<NGram>  nramHitsListToCheck = cluster.getNgramHits();
		for(NGram ngram:ngramHits)
			if(nramHitsListToCheck.contains(ngram))
				return true;
		return false;
	}
	
	
	public void embedCluster(Cluster cluster){
		kbDocList.addAll(cluster.getKbDocList());
		ngramHits.addAll(cluster.getNgramHits());
	}
	
	/**
	 * @return the kbDocList
	 */
	public List<KbDocument> getKbDocList() {
		return kbDocList;
	}
	/**
	 * @param kbDocList the kbDocList to set
	 */
	public void setKbDocList(List<KbDocument> kbDocList) {
		this.kbDocList = kbDocList;
	}
	
	public void adKbDoc(KbDocument kbDoc) {
		this.kbDocList.add(kbDoc);
	}

	
	/**
	 * @return the avgDocScore
	 */
	public double getAvgDocScore() {
		return avgDocScore;
	}
	/**
	 * @param avgDocScore the avgDocScore to set
	 */
	public void setAvgDocScore(double avgDocScore) {
		this.avgDocScore = avgDocScore;
	}
	public void calculateAvgScore(){
		int totalDocsInCluster = kbDocList.size();
		double sumScores = 0.0;
		for(KbDocument doc:kbDocList)
			sumScores += doc.getScore();
		this.avgDocScore = (double) sumScores / totalDocsInCluster;
	}
	
	
	/**
	 * @return the ngramHits
	 */
	public Set<NGram> getNgramHits() {
		return ngramHits;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ngramHits == null) ? 0 : ngramHits.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cluster))
			return false;
		Cluster other = (Cluster) obj;
		if (ngramHits == null) {
			if (other.ngramHits != null)
				return false;
		} else if (!ngramHits.equals(other.ngramHits))
			return false;
		return true;
	}
	
    /**
     * SORT BY Phraseness
     */
    public static Comparator<Cluster> COMPARATOR_SIZE = new Comparator<Cluster>()
    {
    	 public int compare(Cluster o1, Cluster o2){
             if(o2.kbDocList.size() > o1.kbDocList.size() )
             	return 1;
             else if(o2.kbDocList.size() < o1.kbDocList.size() )
             	return 0;
             else 
             	return 0;
         }
    };
    
    /**
     * SORT BY avg documents score
     */
    public static Comparator<Cluster> COMPARATOR_AVG_DOCS_SCORE = new Comparator<Cluster>()
    {
    	 public int compare(Cluster o1, Cluster o2){
             if(o2.avgDocScore > o1.avgDocScore )
             	return 1;
             else if(o2.avgDocScore < o1.avgDocScore )
             	return 0;
             else 
             	return 0;
         }
    };


}
