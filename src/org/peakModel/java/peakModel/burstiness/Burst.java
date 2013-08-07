package org.peakModel.java.peakModel.burstiness;

import java.util.ArrayList;
import java.util.List;

public class Burst {
	private List<Integer> yearList;
	private double score;
	
	public Burst(int year,double score){
		this.yearList = new ArrayList<Integer>();
		this.yearList.add(year);
		this.score = score;
	}

	public void normalizeScore() {
		this.score = (double)this.score / yearList.size();
	}

	
	public void addYear(int year,double score) {
		yearList.add(year);
		this.score+=score;
	}

	/**
	 * @return the yearList
	 */
	public List<Integer> getYearList() {
		return yearList;
	}

	/**
	 * @param yearList the yearList to set
	 */
	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
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
		return "Burst [yearList=" + yearList.toString() + ", score=" + score + "]";
	}
	
	
	
	
}
