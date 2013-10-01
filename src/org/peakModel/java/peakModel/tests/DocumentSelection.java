package org.peakModel.java.peakModel.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.PeakModeling2;
import org.peakModel.java.peakModel.burstiness.Burstiness;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;
import org.peakModel.java.utils.Helper;

public class DocumentSelection extends PeakModeling2{
	double avgNumberOfWordsInTitle = 0;
	double avgNumberOfWordsLengthInTitle = 0;
	double avgNumberOfDocs = 0;
	double avgDocsInPeaks = 0;
	double avgTopDocsInPeaks = 0;
	double avgDocsInPeaksPercent = 0;
	double avgTopDocsInPeaksPercent = 0;

	double avgPeakDays = 0;

	public DocumentSelection(
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
		final int minN = 2;
		final int maxN = 2;
		final int NUMBER_THREADS = 2;
		final int MAX_TITLE_LENGTH = 100;
		final int MIN_TITLE_LENGTH = 1;
		final String backgroundFile="background.txt";
		final String foregroundFile="foreground.txt";
		//==========================================================End Parameters==========================================================//

		
		
		//========================================================== Main ==========================================================//
		final boolean useForSearchOnlyTitle = true;
		//burst detection...
		final int burstTimeSpan = 7;
	    final double x = 2.0;
	    //nr of docs to retrieve
		final int MAX_DOCS = 15000;

		boolean scoreDocs=true;
		boolean useDocFreqForBurstDetection=true;
	    int N = 10;//how many documents should we consider? TOTAL number of DOcs / N = number of docs to consider
	    
	    
//	    for(int MAX_DOCS=100;MAX_DOCS<1500;MAX_DOCS+=50){
	    	
		    DocumentSelection peakModel = new DocumentSelection(useForSearchOnlyTitle, useStopWords, minN, maxN, NUMBER_THREADS, MAX_DOCS, MAX_TITLE_LENGTH, MIN_TITLE_LENGTH, burstTimeSpan);
			
		    
			for(Map.Entry<String, String> entry: getQueryList().entrySet()){
				System.out.println("\n"+entry.getKey()+"\t"+entry.getValue());
				FeatureTemporalProfile queryTemporalProfile = peakModel.runQuery(entry.getKey(),entry.getValue(),peakModel, x,scoreDocs,useDocFreqForBurstDetection);
				peakModel.stats(queryTemporalProfile, peakModel,N,entry.getKey(),entry.getValue());
				
	//			peakModel.calculateHeadlinesStatistics(peakModel);
				
	
	//			Helper.displayBurstsPeriods(queryTemporalProfile);
				//get number of docs per month
	//			long startTime = System.currentTimeMillis();
	//			peakModel.GetDocsPer15daysDistribution(entry.getKey(),entry.getValue());
	//	        System.out.println("#Total run time:"+ (System.currentTimeMillis()-startTime));
	
	//			displayQueryMovingAvg(queryTemporalProfile);
				//displayQueryDistribution(queryTemporalProfile,peakModel);
				//displayTopBottomDocsDistribution(peakModel, 145);
				//testParsimonious(queryTemporalProfile, peakModel,backgroundFile,foregroundFile);
			}	
			peakModel.displayAverageeStats(peakModel, getQueryList().size(),N);
			//Close Indexes
	        peakModel.closeIndexes();

//	    }
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
	
	public static void displayQueryMovingAvg(FeatureTemporalProfile queryTemporalProfile){
		for(Map.Entry<String, Double> entry:queryTemporalProfile.getMovingAvgNormMap().entrySet()){
			double burstDF = 0.0;
			if(queryTemporalProfile.getAllBurstDatesSet().contains(entry.getKey().split(",")[0]))
				burstDF=(double)entry.getValue();	
			System.out.println(entry.getKey().split(",")[0]+"\t"+(double)entry.getValue()+"\t"+burstDF);
		}


	}
	
	public static void displayQueryDistribution(FeatureTemporalProfile queryTemporalProfile, PeakModeling2 peakModel){
		Set<String> daysSet = new HashSet<String>();
		int c=1;
//		for(KbDocument kb:peakModel.getDocumentList()){
		KbDocument[] docs=peakModel.getDocumentList().toArray(new KbDocument[peakModel.getDocumentList().size()]);
		for(int i=docs.length-1;i>=0;i--){
			KbDocument kb=docs[i];

//			daysSet.add(kb.getDate());	
//			if(c % 100 == 0){
//				System.out.println(c+","+daysSet.size());
//				daysSet.clear();
//			}
			c++;			
			
//			System.out.println(kb.getTitle());
			if(c==20)
				break;
		}
		
		
		long totalFeatureFreq = Burstiness.totalFeatureFrequencies(queryTemporalProfile.getFeatureDocFreqPerDayMap());
		for(Map.Entry<String, Integer> entry:queryTemporalProfile.getFeatureDocFreqPerDayMap().entrySet()){
			double burstDF = 0.0;
			if(queryTemporalProfile.getAllBurstDatesSet().contains(entry.getKey()))
				burstDF=(double)entry.getValue()/totalFeatureFreq;	
			System.out.println(entry.getKey()+"\t"+(double)entry.getValue()/totalFeatureFreq+"\t"+burstDF);
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

	
	
	
	public  void stats(FeatureTemporalProfile queryTemporalProfile,PeakModeling2 peakModel, int N,String query,String date) throws ParseException, IOException{
		int countTOPHitOnBurstDays = 0;
		int countHitOnBurstDays = 0;
		int countTitleHitDocumentInBurst = 0;
		
		KbDocument[] docs=peakModel.getDocumentList().toArray(new KbDocument[peakModel.getDocumentList().size()]);
		System.out.println("Deafult Value:"+docs.length);
		int topBest=N;
		int count=1;
		
		//get top scored
//		for(int i=0;i<docs.length;i++){
			
			
		//get the last bottom from the whole document set
//		peakModel.getKbDocs(query, date, true,10000);
//		docs=peakModel.getDocumentList().toArray(new KbDocument[peakModel.getDocumentList().size()]);
//		System.out.println("All:"+docs.length);
		for(int i=docs.length-1;i>=0;i--){

			KbDocument doc=docs[i];
			System.out.println(doc.getTitle());

			if(queryTemporalProfile.getAllBurstDatesSet().contains(doc.getDate())){
				if(count <=topBest)
					countTOPHitOnBurstDays++;
				if(doc.getTitle().toLowerCase().contains(query.toLowerCase()))
					countTitleHitDocumentInBurst++;

				countHitOnBurstDays++;
			}
			count++;
			
			if(count>N)
				break;
		}

		double totalDocsPeaksPercent = (double)countHitOnBurstDays/peakModel.getTotalNumberOfRelevantDocuments()*100;
		double topDocsPeaksPercent = (double)countTOPHitOnBurstDays/topBest*100;

		this.avgNumberOfDocs += peakModel.getTotalNumberOfRelevantDocuments();
		this.avgDocsInPeaks += countHitOnBurstDays;
		this.avgTopDocsInPeaks += countTOPHitOnBurstDays;
		this.avgDocsInPeaksPercent += totalDocsPeaksPercent;
		this.avgTopDocsInPeaksPercent += topDocsPeaksPercent;

		
		this.avgPeakDays += (double) queryTemporalProfile.getAllBurstDatesSet().size() / queryTemporalProfile.getFeatureDocFreqPerDayMap().size()*100;
		
		//Helper.displayBurstsPeriods(queryTemporalProfile);
		System.out.println("#TotalDocs:"+peakModel.getTotalNumberOfRelevantDocuments());
	    System.out.println("#countTitleHitDocumentInBurst:"+countTitleHitDocumentInBurst);
	    System.out.println("#TopBestDocs:"+topBest);
		System.out.println(" #NrDaysWithDocs:"+queryTemporalProfile.getFeatureDocFreqPerDayMap().size());
		System.out.println(" #NrPeakDays:"+queryTemporalProfile.getAllBurstDatesSet().size());
		System.out.println(" #ALLDocsInPeaks:"+ countHitOnBurstDays+"("+ totalDocsPeaksPercent +")"+ "#TOP_DocsInPeaks:"+countTOPHitOnBurstDays+"("+topDocsPeaksPercent+")");
		System.out.println(peakModel.getTotalNumberOfRelevantDocuments()+" & "+countHitOnBurstDays+"("+ totalDocsPeaksPercent +")  & "+ countTOPHitOnBurstDays+"("+topDocsPeaksPercent+")");
	    System.out.println("##############################################################");

	    //DISPLAY THE PERCENT OF TOP OR BOOTOM N DOCS APPEAR IN BURST PERIODS
//	    try {
//			Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", topDocsPeaksPercent+"\t", true, false);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public  void displayAverageeStats(DocumentSelection peakModel,int numberofQueries,int N) throws IOException{
		double avgNumberOfDocs = peakModel.avgNumberOfDocs/numberofQueries;
		double avgDocsInPeaks = peakModel.avgDocsInPeaks/numberofQueries;
		double avgTopDocsInPeaks = peakModel.avgTopDocsInPeaks/numberofQueries;
		double avgDocsInPeaksPercent = peakModel.avgDocsInPeaksPercent/numberofQueries;
		double avgTopDocsInPeaksPercent = peakModel.avgTopDocsInPeaksPercent/numberofQueries;
		
		double avgWordsLengthTitle = peakModel.avgNumberOfWordsLengthInTitle/numberofQueries;
		double avgNrOfWordsTitle = peakModel.avgNumberOfWordsInTitle/numberofQueries;

		double avgPeakDaysPercent = peakModel.avgPeakDays/numberofQueries;

		System.out.println("N:"+N);
		System.out.println("avgNumberOfDocs:"+avgNumberOfDocs);
		System.out.println("avgPeakDaysPercent:"+avgPeakDaysPercent);
		System.out.println("avgDocsInPeaks:"+avgDocsInPeaks+"("+avgDocsInPeaksPercent+")");
		System.out.println("avgTopDocsInPeaks:"+avgTopDocsInPeaks+"("+avgTopDocsInPeaksPercent+")");
		System.out.println("avgWordsLengthInTitle:"+avgWordsLengthTitle+"\t avgNumberOfWordsInTitle:"+avgNrOfWordsTitle);
		System.out.println(avgNumberOfDocs+" & "+avgDocsInPeaks+"("+avgDocsInPeaksPercent+") & "+avgTopDocsInPeaks+"("+avgTopDocsInPeaksPercent+")");
		
		//Helper.writeLineToFile("/Users/mimis/Desktop/csvBurst.txt", avgTopDocsInPeaksPercent+"\n", true, false);
		//Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", avgTopDocsInPeaksPercent+"\t", true, true);
//		Helper.writeLineToFile("nrOfDocsForPeakDetection.txt", avgPeakDaysPercent+"\t", true, true);

	}
	
	
	
	
	
	public FeatureTemporalProfile runQuery(String query,String date,PeakModeling2 peakModel,double x,boolean scoreDocs,boolean useDocFreqForBurstDetection) throws ParseException, IOException, java.text.ParseException{
		//System.out.println("Nr of docs:"+peakModel.getNrOfDocs());
		long startTime = System.currentTimeMillis();

		/**
		 * Get documents based on given query
		 */
		peakModel.getKbDocs(query,date,scoreDocs,-1);//-1 to use the default value
		//calculate bursts based documents frequencies 
		FeatureTemporalProfile queryTemporalProfile = null;
		if(useDocFreqForBurstDetection)
			queryTemporalProfile = Burstiness.measureBurstinessForPeakYearMovingAverage(date, peakModel.getQueryDocFreqPerDayMap(), peakModel.getBurstTimeSpan(),x);
		//calculate bursts based documents scores
		else
			queryTemporalProfile = Burstiness.measureBurstinessWithDocScoreForPeakYearMovingAverage(date, peakModel.getQueryTotalDocScorePerDayMap(),peakModel.getQueryDocFreqPerDayMap(), peakModel.getBurstTimeSpan(),x);	
        
	    System.out.println("#Total Retrieve Documents run time:"+ (System.currentTimeMillis()-startTime));
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
	
	public void diaplsyQueryDistributionAndDocsInPeaks(FeatureTemporalProfile queryTemporalProfile,DocumentSelection peakModel){
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
