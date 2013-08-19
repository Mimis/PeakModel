package org.peakModel.java.peakModel.burstiness;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peakModel.java.utils.Helper;

public class FeatureTemporalProfile {
	
	private double cutOffNorm;
	private LinkedHashMap<String,Double> movingAvgNormMap; //date to mov avg score
	private Map<String,Integer> featureDocFreqPerDayMap; //date to df
	private List<Burst> burstList; //contains the burst intervals of this feature
	/**
	 * @param cutOffNorm
	 * @param movingAvgNormMap
	 * @param featureDocFreqPerDayMap
	 */
	public FeatureTemporalProfile(double cutOffNorm,
			LinkedHashMap<String, Double> movingAvgNormMap,
			Map<String, Integer> featureDocFreqPerDayMap) {
		super();
		this.cutOffNorm = cutOffNorm;
		this.movingAvgNormMap = movingAvgNormMap;
		this.featureDocFreqPerDayMap = featureDocFreqPerDayMap;
		this.burstList = new ArrayList<Burst>();
		detectBurstPeriods();
	}
	
	
	/**
	 * Calculate for each Burst its total Number of Documents
	 */
	private void calculateTotalNrOfDocsInEachBurst(){
        for(Burst burst:burstList){
        	Set<String> burstYearSet = burst.getDateSet();
        	burst.setTotalNumberOfDocs(getTotalNumberOfDocsOnGivenInterval(burstYearSet));
        }	
	}
	
	/**
	 * Detect contiguous Bursts for this feature
	 */
	private void detectBurstPeriods(){
		List<Burst> burstList = new ArrayList<Burst>();
		String lastDate = "";
		for(Map.Entry<String, Double> entry : this.movingAvgNormMap.entrySet()){
			double burstiness = entry.getValue();
			if(burstiness >= this.cutOffNorm){
				String[] datesArray = entry.getKey().split(",");			
				if(Helper.arrayContainsGivenString(datesArray, lastDate)){
					Burst burst = burstList.get(burstList.size()-1);
					burst.addNextBurstInterval(datesArray, burstiness);
				}
				else
					burstList.add(new Burst(datesArray,burstiness));
				lastDate = datesArray[datesArray.length-1];
			}
		}
		this.burstList = burstList;
		calculateTotalNrOfDocsInEachBurst();
	}
	
	/**
	 * @return the burstList
	 */
	public List<Burst> getBurstList() {
		return burstList;
	}


	/**
	 * @param burstList the burstList to set
	 */
	public void setBurstList(List<Burst> burstList) {
		this.burstList = burstList;
	}


	/**
	 * @return the cutOffNorm
	 */
	public double getCutOffNorm() {
		return cutOffNorm;
	}
	/**
	 * @param cutOffNorm the cutOffNorm to set
	 */
	public void setCutOffNorm(double cutOffNorm) {
		this.cutOffNorm = cutOffNorm;
	}
	/**
	 * @return the movingAvgNormMap
	 */
	public LinkedHashMap<String, Double> getMovingAvgNormMap() {
		return movingAvgNormMap;
	}
	/**
	 * @param movingAvgNormMap the movingAvgNormMap to set
	 */
	public void setMovingAvgNormMap(LinkedHashMap<String, Double> movingAvgNormMap) {
		this.movingAvgNormMap = movingAvgNormMap;
	}
	/**
	 * @return the featureDocFreqPerDayMap
	 */
	public Map<String, Integer> getFeatureDocFreqPerDayMap() {
		return featureDocFreqPerDayMap;
	}
	/**
	 * @param featureDocFreqPerDayMap the featureDocFreqPerDayMap to set
	 */
	public void setFeatureDocFreqPerDayMap(
			Map<String, Integer> featureDocFreqPerDayMap) {
		this.featureDocFreqPerDayMap = featureDocFreqPerDayMap;
	}

	/**
	 * 
	 * @param datesArray
	 * @return the sum of the DF for each date that exist in the array
	 */
	public int getTotalNumberOfDocsOnGivenInterval(Set<String> burstYearSet){
		int totalDocOnInterval = 0;
		for(String date:burstYearSet){
			if(this.featureDocFreqPerDayMap.containsKey(date)){
				totalDocOnInterval += this.featureDocFreqPerDayMap.get(date);
//				System.out.println("\t\t"+featureDocFreqPerDayMap.get(date));
			}
		}
		return totalDocOnInterval;
	}
	

}
