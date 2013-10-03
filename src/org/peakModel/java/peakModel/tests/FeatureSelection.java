package org.peakModel.java.peakModel.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.PeakModeling2;
import org.peakModel.java.peakModel.burstiness.Burstiness;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;

public class FeatureSelection extends PeakModeling2{
	double avgNumberOfWordsInTitle = 0;
	double avgNumberOfWordsLengthInTitle = 0;
	double avgNumberOfDocs = 0;
	double avgDocsInPeaks = 0;
	double avgTopDocsInPeaks = 0;
	double avgDocsInPeaksPercent = 0;
	double avgTopDocsInPeaksPercent = 0;

	double avgPeakDays = 0;

	public FeatureSelection(
			boolean useForSearchOnlyTitle, boolean useStopWords, int minN,
			int maxN, int nUMBER_THREADS, int mAX_DOCS, int mAX_TITLE_LENGTH,
			int mIN_TITLE_LENGTH, int burstTimeSpan) throws IOException {
		super( useForSearchOnlyTitle, useStopWords, minN, maxN,
				nUMBER_THREADS, mAX_DOCS, mAX_TITLE_LENGTH, mIN_TITLE_LENGTH,
				burstTimeSpan);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws java.text.ParseException 
	 */
	public static void main(String[] args)  throws IOException, ParseException, java.text.ParseException {
		//========================================================== Input Parameters ==========================================================//
		final boolean useStopWords = true;
		final int NUMBER_THREADS = 2;
		final int MAX_TITLE_LENGTH = 100;
		final int MIN_TITLE_LENGTH = 1;
		final String backgroundFile="background.txt";
		final String foregroundFile="foreground.txt";
		//==========================================================End Parameters==========================================================//

		
		final int minN = 1;
		final int maxN = 1;
		final boolean useForSearchOnlyTitle = false;
		final int burstTimeSpan = 7;
	    final double x = 2.0;
		final int MAX_DOCS = 400;
		boolean scoreDocs=true;
		boolean useDocFreqForBurstDetection=true;
	    int topFeatures = 25;//how many documents should we consider? TOTAL number of DOcs / N = number of docs to consider
	    
	    
	    
	    
	    
	    
	    
	    
		//========================================================== Main ==========================================================//
//	    for(int MAX_DOCS=100;MAX_DOCS<1500;MAX_DOCS+=50){
	    	
		    FeatureSelection peakModel = new FeatureSelection(useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan);

		    
			for(Map.Entry<String, String> entry: getQueryList().entrySet()){
		        long startTime = System.currentTimeMillis();
		        String date = entry.getValue();
		        String query = entry.getKey();
				System.out.println("\n"+query+"\t"+date);
		
				peakModel.getPerYearStats(entry.getValue());
				FeatureTemporalProfile queryTemporalProfile = peakModel.runQuery(entry.getKey(),entry.getValue(),peakModel, x,scoreDocs,useDocFreqForBurstDetection);
				
				
		        /**
		         * Create Language models for each class(Burst,NonBurst);Ngram Candidate lists form each document set with length 1 to 3
		         */
		        //BURSTs DOCS:get all documents that are published on the burst period and extract Ngram Models
				Set<KbDocument> burstDocList = peakModel.getBurstsDocumentsList(queryTemporalProfile);
				List<LanguageModel> burstLanguageModelList = createLanguageModels(burstDocList, minN, maxN);
				//NON BURSTS DOCS:get all documents that are NOT published on the burst period and extract Ngram Models
				Set<KbDocument> nonBurstDocList = peakModel.getNonBurstsDocumentsList(queryTemporalProfile);
				List<LanguageModel> noBurstLanguageModelList = createLanguageModels(nonBurstDocList, minN, maxN);
				//ALL DOCUMENTS
				List<LanguageModel> allDocsLanguageModelList = createLanguageModels(peakModel.getDocumentList(), minN, maxN);
				

			    
			    
			    
				/**
				 * Global LOG likelihood
				 */
				LanguageModel lang = allDocsLanguageModelList.get(allDocsLanguageModelList.indexOf(new LanguageModel(minN)));
				peakModel.getNgramPerYearSTats(lang.getNgramList(),25,entry.getValue());
		    	Collections.sort(lang.getNgramList(), NGram.COMPARATOR_LOG_CORPUS);
				
		    	
		    	
		    	/**
		    	 * Get top features
		    	 */
		    	List<String> bestFeaturesList = getBestFeatures(lang.getNgramList(), topFeatures,entry.getKey());
				
				
		    	/**
		    	 * Check Burstiness overlaping between Query and Features
		    	 */
		    	peakModel.detectBurstPeriodsForBestFeatures(queryTemporalProfile.getAllBurstDatesSet(),bestFeaturesList, date, x, scoreDocs, useDocFreqForBurstDetection);
				
		    	
		    	
	//			peakModel.calculateHeadlinesStatistics(peakModel);
	//			Helper.displayBurstsPeriods(queryTemporalProfile);	
	//			displayQueryMovingAvg(queryTemporalProfile);
				//displayQueryDistribution(queryTemporalProfile,peakModel);
				//displayTopBottomDocsDistribution(peakModel, 145);
				//testParsimonious(queryTemporalProfile, peakModel,backgroundFile,foregroundFile);
				
			    System.out.println("#Total run time:"+ (System.currentTimeMillis()-startTime));
			}	
			//Close Indexes
	        peakModel.closeIndexes();

//	    }
	}
	
	
	public void detectBurstPeriodsForBestFeatures(Set<String> queryBurstDays,List<String> featureList, String date, double x,boolean scoreDocs,boolean useDocFreqForBurstDetection) throws ParseException, IOException, java.text.ParseException{
    	System.out.println("#Query Burst Days:"+queryBurstDays.size());
		for(String feature:featureList){
			FeatureTemporalProfile featureTemporalProfile = runQuery(feature,date,this, x,scoreDocs,useDocFreqForBurstDetection);
	    	Set<String> common = new HashSet<String>(queryBurstDays);
	    	common.retainAll(featureTemporalProfile.getAllBurstDatesSet());
	    	System.out.println("#Feature:"+feature+"\tBurst Days:"+featureTemporalProfile.getAllBurstDatesSet().size()+"\tCommon HITS:"+common.size());
		}
	}
	
	
	
	
	/**
	 * 
	 * @param NGramList
	 * @param topN
	 * @param query
	 */
	public static List<String> getBestFeatures(List<NGram> NGramList,int topN,String query){
		List<String> bestFeatureList = new ArrayList<String>();
		List<String> queryList = new ArrayList<String>(Arrays.asList(query.toLowerCase().split("\\s")));
//    	System.out.println("########################################");
    	int c=0;
       	for(NGram ng:NGramList){
       		if(queryList.contains(ng.getNgram())) 
       			continue;
//       		System.out.println(ng.getNgram()+"\t"+ng.getTf_query_peak());
       		bestFeatureList.add(ng.getNgram());
       		if(c++ > topN)
       			break;
       	}
//       	System.out.println("########################################");
       	return bestFeatureList;
	}
	
	
	
	
	
	
	
	
	
	public static void testParsimonious(FeatureTemporalProfile queryTemporalProfile, PeakModeling2 peakModel,String backgroundFile,String foregroundFile) throws IOException{
		StringBuilder PeakBuf = new StringBuilder();
		StringBuilder AllBuf = new StringBuilder();
		for(KbDocument doc:peakModel.getDocumentList()){
			
			if(queryTemporalProfile.getAllBurstDatesSet().contains(doc.getDate())){
				PeakBuf.append(doc.getTokenSet().toString()+" ");
			}
			AllBuf.append(doc.getTokenSet().toString()+" ");
		}
		Helper.writeLineToFile(backgroundFile, AllBuf.toString(), false, true);
		Helper.writeLineToFile(foregroundFile, PeakBuf.toString(), false, true);

		//Parsimonious
        ProcessBuilder builder = new ProcessBuilder("python2.7", "/Users/mimis/Development/EclipseProject/PeakModel/src/org/peakModel/java/peakModel/tests/parsy.py", 
        		backgroundFile,foregroundFile);

        builder.redirectErrorStream(true);
        Process p = builder.start();
        InputStream stdout = p.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));

        String line;
        while ((line = reader.readLine ()) != null) {
            System.out.println ("Stdout: " + line);
        }

	}
	
	
	
	
	
	/**
	 * 1.avgNrOfWords
	 * 2.avgWordLength
	 */
	public void calculateHeadlinesStatistics(PeakModeling2 peakModel){
		double avgNrOfWords = 0;
		double avgWordLength = 0;
		for(KbDocument doc:peakModel.getDocumentList()){
			int tempNrWords = doc.getTitle().split("\\s").length;
			avgWordLength += doc.getTitle().length() - (tempNrWords-1);
			avgNrOfWords += tempNrWords;
			//System.out.println("\t"+doc.getTitle());
		}
		avgWordLength = (double) avgWordLength / avgNrOfWords;
		avgNrOfWords = (double)avgNrOfWords / peakModel.getDocumentList().size();
		this.avgNumberOfWordsLengthInTitle += avgWordLength;
		this.avgNumberOfWordsInTitle += avgNrOfWords;
		System.out.println("avgWordLength:"+avgWordLength+"\tavgNrOfWords:"+avgNrOfWords);
	}

	
	
	
	
	
	
	
	public FeatureTemporalProfile runQuery(String query,String date,PeakModeling2 peakModel,double x,boolean scoreDocs,boolean useDocFreqForBurstDetection) throws ParseException, IOException, java.text.ParseException{
		//System.out.println("Nr of docs:"+peakModel.getNrOfDocs());
//		long startTime = System.currentTimeMillis();

		/**
		 * Get documents based on given query
		 */
		peakModel.getKbDocs(query,date,scoreDocs,-1);//-1 to use the default value
//	    System.out.println("Total Docs:"+peakModel.getDocumentList().size()+"\tTime with parsing:"+ (System.currentTimeMillis()-startTime));

		//calculate bursts based documents frequencies 
		FeatureTemporalProfile queryTemporalProfile = null;
		if(useDocFreqForBurstDetection)
			queryTemporalProfile = Burstiness.measureBurstinessForPeakYearMovingAverage(date, peakModel.getQueryDocFreqPerDayMap(), peakModel.getBurstTimeSpan(),x);
		//calculate bursts based documents scores
		else
			queryTemporalProfile = Burstiness.measureBurstinessWithDocScoreForPeakYearMovingAverage(date, peakModel.getQueryTotalDocScorePerDayMap(),peakModel.getQueryDocFreqPerDayMap(), peakModel.getBurstTimeSpan(),x);	
        
        return queryTemporalProfile;
	}

	public static Map<String,String> getQueryList(){
		Map<String,String> queryToDateMap = new HashMap<String,String>();
		queryToDateMap.put("lockheed", "1976");
		queryToDateMap.put("NSB", "1979");
		queryToDateMap.put("Haagse Post", "1974");
		queryToDateMap.put("Recessie", "1975");
		queryToDateMap.put("Krakers", "1981");
		queryToDateMap.put("Beatrix", "1965");
		
//		queryToDateMap.put("griekenland", "1967");

		return queryToDateMap;
	}

	
	
	/**
	 * Display query;s distribution together with the TOP scored documents.
	 * @param peakModel
	 * @param N
	 * @return
	 */
	public Map<String,Integer> getTopDocsDistribution(PeakModeling2 peakModel,int N){
		KbDocument[] docs=peakModel.getDocumentList().toArray(new KbDocument[peakModel.getDocumentList().size()]);
		Map<String,Integer> docDateToDfMap = new HashMap<String,Integer>();
		int last = N>docs.length ? docs.length:N;
//		for(int i=0;i<last;i++){
		for(int i=docs.length-1;i>=docs.length-N;i--){

			String date = docs[i].getDate();
			if(docDateToDfMap.containsKey(date))
				docDateToDfMap.put(date, docDateToDfMap.get(date)+1);
			else
				docDateToDfMap.put(date, 1);
		}
		return docDateToDfMap;
	}
	
	public void diaplsyQueryDistributionAndDocsInPeaks(FeatureTemporalProfile queryTemporalProfile,FeatureSelection peakModel){
		Map<String, Integer> topDocsDistribution = peakModel.getTopDocsDistribution(peakModel, 1000);
		for(Map.Entry<String, Integer> entry2: queryTemporalProfile.getFeatureDocFreqPerDayMap().entrySet()){
			int topD=0;
			if(topDocsDistribution.containsKey(entry2.getKey()))
				topD=topDocsDistribution.get(entry2.getKey());
			System.out.println(entry2.getKey()+","+entry2.getValue()+","+topD);
			
		}

	}

	public static Map<String,Integer> displayTopBottomDocsDistribution(PeakModeling2 peakModel,int N){
		Map<String,Integer> docDateToDfMap = new HashMap<String,Integer>();
		KbDocument[] docs=peakModel.getDocumentList().toArray(new KbDocument[peakModel.getDocumentList().size()]);
		int last = N>docs.length ? docs.length:N;
		for(int i=0;i<last;i++){
//		for(int i=docs.length-1;i>=docs.length-N;i--){
			KbDocument doc=docs[i];
			String date = docs[i].getDate();
			if(docDateToDfMap.containsKey(date))
				docDateToDfMap.put(date, docDateToDfMap.get(date)+1);
			else
				docDateToDfMap.put(date, 1);
		}
		for(Map.Entry<String, Integer> entry:docDateToDfMap.entrySet()){
			double norm= (double) entry.getValue()/N;
			System.out.println(entry.getKey()+","+norm);
		}
		return docDateToDfMap;
	}

}
