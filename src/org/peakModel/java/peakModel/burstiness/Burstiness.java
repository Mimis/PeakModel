package org.peakModel.java.peakModel.burstiness;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakModel.java.utils.DateIterator;
import org.peakModel.java.utils.Helper;

public class Burstiness {
	public static void main(String args[]) throws ParseException{
		String y="1981-07-07:4,1981-07-06:4,1981-03-10:8,1981-07-09:4,1981-03-11:6,1981-07-08:3,1981-03-12:9,1981-03-03:4,1981-07-14:3,1981-03-02:11,1981-07-15:5,1981-03-05:7,1981-03-04:4,1981-07-16:4,1981-03-07:10,1981-07-10:11,1981-03-06:4,1981-03-09:1,1981-07-11:7,1981-12-30:4,1981-12-29:3,1981-12-28:1,1981-12-24:3,1981-12-23:5,1981-12-22:5,1981-07-04:7,1981-07-02:5,1981-07-03:11,1981-07-01:6,1981-12-21:5,1981-12-12:3,1981-12-11:3,1981-12-14:6,1981-12-16:5,1981-12-15:6,1981-12-18:2,1981-12-17:1,1981-12-19:1,1981-12-10:7,1981-12-03:3,1981-12-02:6,1981-12-01:3,1981-12-07:5,1981-12-04:1,1981-12-09:9,1981-12-08:17,1981-10-14:3,1981-04-03:8,1981-04-04:11,1981-10-13:11,1981-10-16:12,1981-10-15:9,1981-04-06:11,1981-10-10:3,1981-01-21:9,1981-04-07:5,1981-04-08:12,1981-01-20:6,1981-04-09:5,1981-10-12:1,1981-10-17:19,1981-10-19:4,1981-01-19:6,1981-01-13:3,1981-01-14:3,1981-01-12:5,1981-04-11:1,1981-01-17:13,1981-04-10:1,1981-04-13:6,1981-01-15:8,1981-01-16:6,1981-10-05:7,1981-04-16:5,1981-04-17:1,1981-10-03:6,1981-04-14:5,1981-10-02:2,1981-04-15:6,1981-10-01:9,1981-01-30:6,1981-04-18:1,1981-01-31:8,1981-10-09:10,1981-10-08:4,1981-10-07:3,1981-10-06:3,1981-01-22:8,1981-01-23:16,1981-01-24:2,1981-01-26:2,1981-04-24:4,1981-01-27:5,1981-04-23:2,1981-01-28:3,1981-04-22:3,1981-04-21:1,1981-01-29:1,1981-03-31:7,1981-03-30:18,1981-03-28:7,1981-03-24:4,1981-03-25:7,1981-03-26:18,1981-03-27:14,1981-01-10:6,1981-03-23:6,1981-03-21:24,1981-03-20:6,1981-03-19:9,1981-03-17:6,1981-03-18:12,1981-01-08:8,1981-01-09:3,1981-03-16:3,1981-03-13:11,1981-03-14:5,1981-04-02:4,1981-04-01:4,1981-01-05:6,1981-01-06:8,1981-01-07:10,1981-01-02:3,1981-01-03:5,1981-11-07:2,1981-11-02:2,1981-11-03:9,1981-11-04:6,1981-11-05:2,1981-08-29:5,1981-08-31:3,1981-06-02:2,1981-06-01:2,1981-06-04:7,1981-06-03:2,1981-11-29:1,1981-04-30:3,1981-11-25:4,1981-11-26:7,1981-11-27:2,1981-11-28:5,1981-11-21:1,1981-11-23:3,1981-11-24:1,1981-10-31:4,1981-04-29:4,1981-04-28:6,1981-04-27:6,1981-04-25:5,1981-11-18:5,1981-10-30:2,1981-11-19:4,1981-11-16:3,1981-11-17:6,1981-11-14:2,1981-11-13:5,1981-11-10:3,1981-11-11:4,1981-11-20:2,1981-10-28:9,1981-10-29:1,1981-10-20:5,1981-10-21:12,1981-10-22:4,1981-10-23:6,1981-10-24:2,1981-10-26:2,1981-10-27:11,1981-02-17:12,1981-05-08:13,1981-05-09:9,1981-02-16:7,1981-09-05:1,1981-02-19:14,1981-02-18:7,1981-06-22:2,1981-09-04:1,1981-09-03:4,1981-05-04:1,1981-06-23:9,1981-02-13:4,1981-05-05:1,1981-06-24:5,1981-02-12:5,1981-09-02:4,1981-06-25:4,1981-09-01:4,1981-05-06:4,1981-06-26:8,1981-05-07:9,1981-02-14:9,1981-06-17:1,1981-05-12:8,1981-08-06:4,1981-08-05:3,1981-05-11:5,1981-02-20:8,1981-06-16:5,1981-05-14:3,1981-06-19:2,1981-02-21:12,1981-08-03:1,1981-05-13:4,1981-06-18:1,1981-08-01:1,1981-09-10:2,1981-02-09:10,1981-06-10:1,1981-02-07:10,1981-06-11:4,1981-09-15:6,1981-02-06:3,1981-09-18:6,1981-02-05:3,1981-09-17:5,1981-02-04:7,1981-06-15:1,1981-09-11:1,1981-02-03:9,1981-06-12:8,1981-09-14:4,1981-02-02:5,1981-06-13:1,1981-02-10:23,1981-02-11:15,1981-05-02:9,1981-05-01:16,1981-06-06:3,1981-06-05:2,1981-09-09:1,1981-06-09:1,1981-07-25:2,1981-07-24:3,1981-09-21:2,1981-07-20:1,1981-07-23:8,1981-07-22:2,1981-05-26:3,1981-09-25:8,1981-05-27:4,1981-09-24:22,1981-09-23:7,1981-05-29:4,1981-09-22:5,1981-08-19:2,1981-09-29:1,1981-09-28:3,1981-09-26:3,1981-08-24:3,1981-05-30:3,1981-09-19:2,1981-08-22:9,1981-08-21:8,1981-08-28:3,1981-08-27:6,1981-08-26:1,1981-08-25:5,1981-08-20:5,1981-07-17:5,1981-07-18:7,1981-09-30:5,1981-07-31:4,1981-08-07:1,1981-02-26:11,1981-08-08:2,1981-02-25:13,1981-05-18:2,1981-05-15:4,1981-02-24:21,1981-05-16:1,1981-02-23:16,1981-02-28:10,1981-06-30:3,1981-05-19:1,1981-02-27:8,1981-08-11:1,1981-05-21:4,1981-05-20:3,1981-08-12:4,1981-05-25:2,1981-08-15:2,1981-06-29:2,1981-05-23:7,1981-06-27:11,1981-05-22:3,1981-07-29:2,";
		measureBurstinessForPeakYearMovingAverage("1981", y, 3);
	}

	
	
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
	
	
	public static LinkedHashMap<String,Double> measureBurstinessForPeakYearMovingAverage(String peakYear,String freqPerYear, int timeSpan) throws ParseException {
		String startDate= peakYear+"-01-01";
		String endDate= peakYear+"-12-31";
		List<String> dateList = DateIterator.getDatesForGivenInterval(startDate, endDate);
		LinkedHashMap<String,Double> movingAvgNormOnlyList = new LinkedHashMap<String,Double>();

		long totalFeatureFreq = totalFeatureFrequencies(freqPerYear.split(","));
		Map<String,Integer> tfPerYearMap = Helper.importTfYearStringToMap(freqPerYear);

		double sumUpMovingAngNorm = 0.0;
		for(int i=0;i<dateList.size();i++){
			if(i+timeSpan > dateList.size())
				break;
			
			double normalizedFreq = (double) Helper.getTfFromYearToTfMap(dateList.get(i), tfPerYearMap) / totalFeatureFreq;
			for(int y=i+1;y<i+timeSpan;y++){
				normalizedFreq += (double)Helper.getTfFromYearToTfMap(dateList.get(y), tfPerYearMap) / totalFeatureFreq;
			}
			double currentMovingAvgNormalized = (double) normalizedFreq / timeSpan;
			sumUpMovingAngNorm+=currentMovingAvgNormalized;
			movingAvgNormOnlyList.put(dateList.get(i), currentMovingAvgNormalized);
		}

		int timeWindows = movingAvgNormOnlyList.size();
		double cutOffNorm = getCutOff(sumUpMovingAngNorm, timeWindows, movingAvgNormOnlyList);
		System.out.println("cutOffNorm:"+cutOffNorm);
//		for(Map.Entry<String, Double> entry:movingAvgNormOnlyList.entrySet()){
//			String date = entry.getKey();
//			double burstiness = entry.getValue();
//			if(burstiness > cutOffNorm)
//				System.out.println(date+"\t"+burstiness);
//		}
		return movingAvgNormOnlyList;
	}

	/**
	 * Calculate Burstiness for the whole corpus by Moving Average Vlachos et.all
	 * @param freqPerYear
	 * @param timeSpan
	 * @return
	 * @throws IOException 
	 */
//	public static List<Burst> measureBurstinessForWholeCorpusMovingAverage(String freqPerYear, int timeSpan) throws IOException{
//		int startYear=1800;
//		int lastYear=1996;//exclusive
//		List<String> movingAvgList = new ArrayList<String>();
//		LinkedHashMap<Integer,Double> movingAvgOnlyList = new LinkedHashMap<Integer,Double>();
//		LinkedHashMap<Integer,Double> movingAvgNormOnlyList = new LinkedHashMap<Integer,Double>();
//
//		long totalFeatureFreq = totalFeatureFrequencies(freqPerYear.split(","));
//		Map<Integer,Integer> tfPerYearMap = Helper.importTfYearStringToMap(freqPerYear);
//		double sumUpMovingAng = 0.0;
//		double sumUpMovingAngNorm = 0.0;
//		
//		
//		for(int year=startYear;year<lastYear-timeSpan+1;year++){
//			int lastForwardIndex = (year+timeSpan) < lastYear ? (year+timeSpan) :lastYear ;
//			
//			
//			int avg = Helper.getTfFromYearToTfMap(year, tfPerYearMap);
//			double normalizedFreq = (double)avg / totalFeatureFreq;
//
//			for(int movingForward=year+1;movingForward<lastForwardIndex;movingForward++){
//				int currentTfYear = Helper.getTfFromYearToTfMap(movingForward, tfPerYearMap);
//				avg += currentTfYear;
//				normalizedFreq += (double)currentTfYear / totalFeatureFreq;
//			}
//			double currentMovingAvg = (double) avg / timeSpan;
//			double currentMovingAvgNormalized = (double) normalizedFreq / timeSpan;
//			sumUpMovingAng+=currentMovingAvg;
//			sumUpMovingAngNorm+=currentMovingAvgNormalized;
//			//String interval = year + "-" + (lastForwardIndex-1);
//			//System.out.println("\tinterval:"+interval+"\tAvg:"+currentMovingAvg);	
//			
//			movingAvgList.add(year+":"+currentMovingAvg+":"+currentMovingAvgNormalized+":"+Helper.getTfFromYearToTfMap(year, tfPerYearMap)+":"+normalizedFreq);
//			movingAvgOnlyList.put(year,currentMovingAvg);
//			movingAvgNormOnlyList.put(year,currentMovingAvgNormalized);
//		}
//		
//		//calculate cut-off
//		int timeWindows = movingAvgList.size();
////		double cutOff = getCutOff(sumUpMovingAng, timeWindows, movingAvgOnlyList);
////		double cutOffNorm = getCutOff(sumUpMovingAngNorm, timeWindows, movingAvgNormOnlyList);
//		//System.out.println("cutOffNorm:"+cutOffNorm);
//		//detect bursts
////		List<Burst> burstList = detectBurst(cutOffNorm, movingAvgNormOnlyList);
//
//
//		//display birstiness
////		Helper.writeLineToFile("/Users/mimis/Desktop/csvBurst.txt", "year:burstiness:burstinessNormalized:tf:normalizedTf", false,true);
////        for(String burst:movingAvgList)
////        	Helper.writeLineToFile("/Users/mimis/Desktop/csvBurst.txt", burst, true,true);
//		
//		return null;
//	}	
	
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
	
	private static double getCutOff(double sumUpMovingAng,int timeWindows,LinkedHashMap<String,Double> movingAvgOnlyMap){
		double mean = (double)sumUpMovingAng / timeWindows;
		double stand = getStandardDeviation(movingAvgOnlyMap, mean,timeWindows);
		double cutOff = mean + (2 * stand);
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
