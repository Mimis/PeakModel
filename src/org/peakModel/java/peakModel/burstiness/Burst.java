package org.peakModel.java.peakModel.burstiness;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Burst {
	private Set<String> dateSet; //the dates where the burst exist
	private double score; //this the sum for all burst intervals burstiness score
	private int totalNumberOfDocs;
	
	public Burst(String[] datesArray,double score){
		this.dateSet = new LinkedHashSet<String>(Arrays.asList(datesArray));
		this.score = score;
		this.totalNumberOfDocs=0;
	}

	/**
	 * @return the dateSet
	 */
	public Set<String> getDateSet() {
		return dateSet;
	}

	/**
	 * @param dateSet the dateSet to set
	 */
	public void setDateSet(Set<String> dateSet) {
		this.dateSet = dateSet;
	}

	/**
	 * @return the totalNumberOfDocs
	 */
	public int getTotalNumberOfDocs() {
		return totalNumberOfDocs;
	}

	/**
	 * @param totalNumberOfDocs the totalNumberOfDocs to set
	 */
	public void setTotalNumberOfDocs(int totalNumberOfDocs) {
		this.totalNumberOfDocs = totalNumberOfDocs;
	}

	public void normalizeScore() {
		this.score = (double)this.score / dateSet.size();
	}

	
	public void addNextBurstInterval(String[] datesArray,double score) {
		dateSet.addAll(Arrays.asList(datesArray));
		this.score += score;
	}


	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Burst [dateSet=" + dateSet.toString() + ", score=" + score
				+ ", totalNumberOfDocs=" + totalNumberOfDocs + "]";
	}
	
    /**
     * SORT BY burstiness
     */
    public static Comparator<Burst> COMPARATOR_BURSTINESS = new Comparator<Burst>()
    {
    	 public int compare(Burst o1, Burst o2){
             if(o2.score > o1.score )
             	return 1;
             else if(o2.score < o1.score )
             	return 0;
             else 
             	return 0;
         }
    };

}
