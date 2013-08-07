package org.peakModel.java.peakModel.burstiness;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	 * Moving Average Vlachos et.all
	 * @param freqPerYear
	 * @param timeSpan
	 * @return
	 * @throws IOException 
	 */
	public static List<Burst> measureBurstinessMovingAverage(String freqPerYear, int timeSpan) throws IOException{
		int startYear=1800;
		int lastYear=1996;//exclusive
		List<String> movingAvgList = new ArrayList<String>();
		LinkedHashMap<Integer,Double> movingAvgOnlyList = new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> movingAvgNormOnlyList = new LinkedHashMap<Integer,Double>();

		long totalFeatureFreq = totalFeatureFrequencies(freqPerYear.split(","));
		Map<Integer,Integer> tfPerYearMap = Helper.importTfYearStringToMap(freqPerYear);
		double sumUpMovingAng = 0.0;
		double sumUpMovingAngNorm = 0.0;
		for(int year=startYear;year<lastYear-timeSpan+1;year++){
			int lastForwardIndex = (year+timeSpan) < lastYear ? (year+timeSpan) :lastYear ;
			
			
			int avg = Helper.getTfFromYearToTfMap(year, tfPerYearMap);
			double normalizedFreq = (double)avg / totalFeatureFreq;

			for(int movingForward=year+1;movingForward<lastForwardIndex;movingForward++){
				int currentTfYear = Helper.getTfFromYearToTfMap(movingForward, tfPerYearMap);
				avg += currentTfYear;
				normalizedFreq += (double)currentTfYear / totalFeatureFreq;
			}
			double currentMovingAvg = (double) avg / timeSpan;
			double currentMovingAvgNormalized = (double) normalizedFreq / timeSpan;
			sumUpMovingAng+=currentMovingAvg;
			sumUpMovingAngNorm+=currentMovingAvgNormalized;
			//String interval = year + "-" + (lastForwardIndex-1);
			//System.out.println("\tinterval:"+interval+"\tAvg:"+currentMovingAvg);	
			
			movingAvgList.add(year+":"+currentMovingAvg+":"+currentMovingAvgNormalized+":"+Helper.getTfFromYearToTfMap(year, tfPerYearMap)+":"+normalizedFreq);
			movingAvgOnlyList.put(year,currentMovingAvg);
			movingAvgNormOnlyList.put(year,currentMovingAvgNormalized);
		}
		
		//calculate cut-off
		int timeWindows = movingAvgList.size();
		double cutOff = getCutOff(sumUpMovingAng, timeWindows, movingAvgOnlyList);
		double cutOffNorm = getCutOff(sumUpMovingAngNorm, timeWindows, movingAvgNormOnlyList);
		//System.out.println("cutOffNorm:"+cutOffNorm);
		//detect bursts
		List<Burst> burstList = detectBurst(cutOffNorm, movingAvgNormOnlyList);


		//display birstiness
//		Helper.writeLineToFile("/Users/mimis/Desktop/csvBurst.txt", "year:burstiness:burstinessNormalized:tf:normalizedTf", false,true);
//        for(String burst:movingAvgList)
//        	Helper.writeLineToFile("/Users/mimis/Desktop/csvBurst.txt", burst, true,true);
		
		return burstList;
	}	
	/**
	 * Detect bursts
	 * @param cutOff
	 * @param movingAvgOnlyMap
	 * @return
	 */
	private static List<Burst> detectBurst(double cutOff,LinkedHashMap<Integer,Double> movingAvgOnlyMap){
		List<Burst> burstList = new ArrayList<Burst>();
		int lastBurstYear=-1;
		for(Map.Entry<Integer,Double> entry:movingAvgOnlyMap.entrySet()){
			int year = entry.getKey();
			double burstiness = entry.getValue();
			if(burstiness > cutOff){
				//System.out.println("year:"+year+"\tscore:"+burstiness);
				if(lastBurstYear == (year-1)){
					Burst burst = burstList.get(burstList.size()-1);
					burst.addYear(year, burstiness);
				}
				else
					burstList.add(new Burst(year,burstiness));
				lastBurstYear = year;
			}
		}
		//normalize scores
		for(Burst b:burstList)
			b.normalizeScore();
		return burstList;
	}
	
	private static double getCutOff(double sumUpMovingAng,int timeWindows,LinkedHashMap<Integer,Double> movingAvgOnlyMap){
		double mean = (double)sumUpMovingAng / timeWindows;
		double stand = getStandardDeviation(movingAvgOnlyMap, mean,timeWindows);
		double cutOff = mean + (2 * stand);
		return cutOff;
	}
	private static double getStandardDeviation(LinkedHashMap<Integer,Double> movingAvgOnlyMap,double mean,int timeWindows){
		double standard = 0.0;
		for(Map.Entry<Integer,Double> entry:movingAvgOnlyMap.entrySet())
			standard += Math.pow(entry.getValue() - mean,2);
		standard = (double)standard / timeWindows;
		return Math.sqrt(standard);
	}
}
