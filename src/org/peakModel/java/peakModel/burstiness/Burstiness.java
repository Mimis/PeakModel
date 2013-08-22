package org.peakModel.java.peakModel.burstiness;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakModel.java.utils.DateIterator;
import org.peakModel.java.utils.Helper;

public class Burstiness {

	
	
	public static double measureBurstinessCHI_SQUARE(int tfYear, String[] tfYearArr, long totalNumberOfWords,long totalTfYear){
        long totalFeatureFreq = totalFeatureFrequencies(tfYearArr);

		long a = tfYear;
		long b = totalTfYear - tfYear;
		long c = totalFeatureFreq - tfYear;
		long d = totalNumberOfWords - totalTfYear - (totalFeatureFreq - tfYear);

		double e = (double)Math.pow((a*b-b*c),2);
		double chi_square = (double) (((a+b+c+d)*e)/ ((a+b)*(a+c)*(b+c)*(b+d)));
		return chi_square;
	}

	public static double measureBurstinessBAI(int tfYear, String[] tfYearArr, long totalNumberOfWords,long totalTfYear){

        long totalFeatureFreq = totalFeatureFrequencies(tfYearArr);
        double P_j = (double) totalFeatureFreq / totalNumberOfWords;
        double F_j = (double) tfYear / totalTfYear;
		double burstiness = (double)F_j/P_j;
		return burstiness;
	}

	public static long totalFeatureFrequencies(String[] tfYearArr){
		long totalTf = 0;
		for(String tfYear : tfYearArr){
			String[] tfArray = tfYear.split(":");
			String year = tfArray[0];
			int tf = Integer.parseInt(tfArray[1]);
			if(!year.equals("17yy") && !year.equals("16yy"))
				totalTf += tf;
		}
		return totalTf;
	}

	public static long totalFeatureFrequencies(Map<String,Integer> featureDocFreqPerDayMap){
		long totalTf = 0;
		for(Map.Entry<String,Integer> entry : featureDocFreqPerDayMap.entrySet()){
			totalTf += entry.getValue();
		}
		return totalTf;
	}
	
	public static Map<String,Double> measureBurstinessForAllYears(String freqPerYear, long totalNumberOfWords,HashMap<String,Long> peakPeriodMap){

		Map<String,Double> burstMap = new HashMap<String,Double>();
		String[] tfYearArr = freqPerYear.split(",");

		for(String tfYear : tfYearArr){
			String[] tfArray = tfYear.split(":");
			String year = tfArray[0];
			int tf = Integer.parseInt(tfArray[1]);
			
			if(!year.equals("17yy") && !year.equals("16yy")){
				long totalTfYear = peakPeriodMap.get(year);
//		        double burstiness = measureBurstinessBAI(tf, tfYearArr, totalNumberOfWords, totalTfYear);
		        double burstiness = measureBurstinessCHI_SQUARE(tf, tfYearArr, totalNumberOfWords, totalTfYear);
		        NumberFormat formatter = new DecimalFormat("###.#####");  
		        
		        String f = formatter.format(burstiness);  
		        System.out.println(year+"\t"+f);
		        if(burstiness > 7.88)
		        	burstMap.put(year, burstiness);
			}
		}
		return burstMap;
	}
	
	/**
	 * Measure burstiness based on Moving Average and cut off threshold based on Vlachos et.al
	 * @param peakYear
	 * @param featureDocFreqPerDayMap
	 * @param timeSpan
	 * @return
	 * @throws ParseException
	 */
	public static FeatureTemporalProfile measureBurstinessForPeakYearMovingAverage(String peakYear,Map<String,Integer> featureDocFreqPerDayMap, int timeSpan,double x) throws ParseException {
		String startDate= peakYear+"-01-01";
		String endDate= peakYear+"-12-31";
		List<String> dateList = DateIterator.getDatesForGivenInterval(startDate, endDate);
		LinkedHashMap<String,Double> movingAvgNormMap = new LinkedHashMap<String,Double>();

		long totalFeatureFreq = totalFeatureFrequencies(featureDocFreqPerDayMap);

		double sumUpMovingAngNorm = 0.0;
		for(int i=0;i<dateList.size();i++){
			if(i+timeSpan > dateList.size())
				break;
			StringBuilder currentDateInterval = new StringBuilder();
			currentDateInterval.append(dateList.get(i));
			
			double normalizedFreq = (double) Helper.getTfFromYearToTfMap(dateList.get(i), featureDocFreqPerDayMap) / totalFeatureFreq;
			for(int y=i+1;y<i+timeSpan;y++){
				normalizedFreq += (double)Helper.getTfFromYearToTfMap(dateList.get(y), featureDocFreqPerDayMap) / totalFeatureFreq;
				currentDateInterval.append(","+dateList.get(y));
			}
			double currentMovingAvgNormalized = (double) normalizedFreq / timeSpan;
			sumUpMovingAngNorm+=currentMovingAvgNormalized;
			movingAvgNormMap.put(currentDateInterval.toString(), currentMovingAvgNormalized);
		}
		int timeWindows = movingAvgNormMap.size();
		double cutOffNorm = getCutOff(sumUpMovingAngNorm, timeWindows, movingAvgNormMap,x);
		return new FeatureTemporalProfile(cutOffNorm, movingAvgNormMap, featureDocFreqPerDayMap);
	}

	
	private static double getCutOff(double sumUpMovingAng,int timeWindows,LinkedHashMap<String,Double> movingAvgOnlyMap,double x){
		double mean = (double)sumUpMovingAng / timeWindows;
		double stand = getStandardDeviation(movingAvgOnlyMap, mean,timeWindows);
		double cutOff = mean + (x * stand);
		return cutOff;
	}
	private static double getStandardDeviation(LinkedHashMap<String,Double> movingAvgOnlyMap,double mean,int timeWindows){
		double standard = 0.0;
		for(Map.Entry<String,Double> entry:movingAvgOnlyMap.entrySet())
			standard += Math.pow(entry.getValue() - mean,2);
		standard = (double)standard / timeWindows;
		return Math.sqrt(standard);
	}
}
