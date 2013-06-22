package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGramWithDF;
import org.peakModel.java.utils.Helper;
import org.peakModel.java.utils.HelperLucene;

public class SearchLuceneIndexWithTermVectors {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		long startTime = System.currentTimeMillis();
		
		
		/*
		 * Input Parameters
		 */
		//==========================================================//
		String indexKbCorpusFileName = "../index/test";
		String dutchStopWordFile = "../data/stopWords/dutch.txt";
		int topBestTerms = 100000;
		int MAX_DOCS = 1000;
		
		/*
		 * Queries
		 */
        String query = "\""+ args[0] + "\"";
		String date = args[1];//"date:[1976-01-01 TO 1976-12-30]";
		//This queries shoudl use to find out how many docs got content for each field separate in a specific period
//		String titleDate = "title:{* TO *} AND " + date;
//		String contentDate = "content:{* TO *} AND " + date;
		if(!date.equals("null"))
			query = query + " AND " + date;
		//get Ngram Statistics
		boolean getNgramStats = Boolean.parseBoolean(args[2]);
		String ngram_type = args[3];//uni,bi,mix
		boolean compactNgramToStringOutput = Boolean.parseBoolean(args[4]);
		boolean useForSearchOnlyTitle = Boolean.parseBoolean(args[5]);
		
		//experiment output files
		String experimentsFileTF = "../experiments/"+query+"TF.csv";
		String experimentsFilePMIclassic = "../experiments/"+query+"PMIclassic.csv";
		String experimentsFilePMIpeak = "../experiments/"+query+"PMIpeak.csv";
		//==========================================================//
		
        
		/*
		 * Variables
		 */
		//==========================================================//
		int totalNumberOfDocuments = -1;
        int totalNumberOfDocumentsWithTitle = -1;
        int totalNumberOfDocumentsWithContent = -1;
		int totalNumberOfRelevantDocuments = -1;
		int totalNumberOfDocumentsInGivenPeriod = -1;
//		int totalNumberOfDocumentsWithTitleInGivenPeriod = -1;
//		int totalNumberOfDocumentsWithContentInGivenPeriod = -1;
		List<NGramWithDF> ngramList = new ArrayList<NGramWithDF>();
		//==========================================================//
        
        
        /*
		 * Index Dir + Reader + Searcher + Analyzer + QueryParser
		 */
        Directory indexDir = HelperLucene.getIndexDir(indexKbCorpusFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = HelperLucene.getKbAnalyzer(dutchStopWordFile);
        TotalHitCountCollector collectorOnlyForHitCount = new TotalHitCountCollector();
        
        
        /*
         * QueryParser
         */
        QueryParser queryParser = null;
        if(useForSearchOnlyTitle)
        	queryParser = new QueryParser(Version.LUCENE_43, "title", analyzer);
        else
        	queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},analyzer);
        
        
        

        
        
        /*
         * Get total number of document with title  or content text in the whole index
         * 	**THESE ARE CONTSTANTS!!!!**
         */
        totalNumberOfDocumentsWithTitle = 19376917;//reader.getDocCount("title");
        totalNumberOfDocumentsWithContent = 19102728;//reader.getDocCount("content");
        totalNumberOfDocuments = 19442493;//reader.numDocs();
		        
        
        /*
         *  1.Query Date Search: Get total Number Of Documents In Given Period
         */
        HelperLucene.queryIndexWithCollector(queryParser,collectorOnlyForHitCount, searcher, date);
        totalNumberOfDocumentsInGivenPeriod = collectorOnlyForHitCount.getTotalHits();
        
        
        
        /*
         * 2.1 Whole Query  Search: Get relevant docs to the given query, the total number of relevant docs and the terms with their info that exist in  
         */
		TopDocs topDocs = HelperLucene.queryIndexGetTopDocs(queryParser,searcher, query,MAX_DOCS);
		ScoreDoc[] hits = topDocs.scoreDocs;
		totalNumberOfRelevantDocuments = hits.length;

		
        /*
         * 2.2 Get the N-Grams with their Document Frequency and Total Frequency in each field!
         */
        System.out.println("HITS:"+totalNumberOfRelevantDocuments);
        int countProcessDocs = 0;
        if(totalNumberOfRelevantDocuments==0){
        	System.out.println("Zero Results :/");
        }else{
        	TermsEnum termsEnum = null;
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				final Document doc = searcher.doc(docId);
				System.out.println(countProcessDocs++ +"\tDoc id:" + docId + "\tTitle:"+ doc.get("title") + " Date:" + doc.get("date")	+ "\tUrl:" + doc.get("url"));
				
				if(getNgramStats){
					final Terms titleTermVector = reader.getTermVector(docId,"title");
					//TODO keep only unigram or bigrams or all of them based on the input
					HelperLucene.mapTermVectorToNGramList(reader, titleTermVector, termsEnum, "title", ngramList,ngram_type);
				    
	//				 final Terms contentTermVector = reader.getTermVector(docId,"content");
	//				 HelperLucene.displayTermVector(reader,contentTermVector, termsEnum,"content");
					System.out.println("Unique Ngrams:"+ngramList.size());
				}
			}
        }
	
		
		
        /*
         * 3. Calculate Statistical Measures
         */
		System.out.println("Calculate Statistical Measures for NGrams....");
        for(NGramWithDF ngram:ngramList){
        	//Title
        	ngram.setDf_time(HelperLucene.getNgramDf(ngram.getNgram(), date, "title", queryParser, searcher));
        	ngram.calculateProbabilities(totalNumberOfDocumentsWithTitle, totalNumberOfRelevantDocuments, totalNumberOfDocumentsInGivenPeriod);
        	ngram.computePMIClasic();
        	ngram.computePMItime();
        }
        
        
        

        /*
         * 4. Sort NGrams By:
         * 	a.TF
         *  b.PMI_classic
         *  c.PMI_time
         */
        if(getNgramStats){
	       	Collections.sort(ngramList, NGramWithDF.COMPARATOR_TOTAL_TF);
	       	writeNgramToCsv(ngramList, experimentsFileTF, topBestTerms,compactNgramToStringOutput);
	       	Collections.sort(ngramList, NGramWithDF.COMPARATOR_PMI_CLASSIC);
	       	writeNgramToCsv(ngramList, experimentsFilePMIclassic, topBestTerms,compactNgramToStringOutput);
	       	Collections.sort(ngramList, NGramWithDF.COMPARATOR_PMI_TIME);
	       	writeNgramToCsv(ngramList, experimentsFilePMIpeak, topBestTerms,compactNgramToStringOutput);
        }        
       	
        
       	
       	
        
        /*
         * 5. Display results
         */
        System.out.println("====================================================================================================");
        System.out.println("Query:"+query);
        System.out.println("====================================================================================================");
        System.out.println("TotalNumberOfRelevantDocuments:"+totalNumberOfRelevantDocuments);
        System.out.println("TotalNumberOfDocumentsInGivenPeriod:"+totalNumberOfDocumentsInGivenPeriod+"\tTotalNumberOfDocumentWithTitle:"+totalNumberOfDocumentsWithTitle + "\tTotalNumberOfDocumentWithContent:"+totalNumberOfDocumentsWithContent+"\tTotalDocs:"+totalNumberOfDocuments);
        System.out.println("====================================================================================================");
        
        
        
        
        //timer
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
	}
	
	
	public static void writeNgramToCsv(List<NGramWithDF> ngramList, String experimentsFile, int topBestTerms, boolean compactView) throws IOException{
		if(ngramList.isEmpty())
			return;
		String csvExpnationOutput = null;
		if(compactView)
			csvExpnationOutput = "ngram ,total_tf_query,P_w,P_w_Given_query_time,P_w_Given_time,PMI_classic,PMI_peak";
    	else
    		csvExpnationOutput = "ngram ,total_tf_query,df_query,df_corpus,df_time,P_w,P_w_Given_query_time,P_w_Given_time,PMI_classic,PMI_peak";
    	System.out.println(csvExpnationOutput);
    	
    	
        Helper.writeLineToFile(experimentsFile,csvExpnationOutput, false,true);
    	int countTerms = 1;
        for(NGramWithDF ngram:ngramList){
        	String ngramToString = null;
        	if(compactView)
        		ngramToString = ngram.toStringCsvCompact();
        	else
        		ngramToString = ngram.toStringCsvAll();
        	System.out.println(ngramToString);
        	Helper.writeLineToFile(experimentsFile, ngramToString, true, true);
        	if(countTerms++ >= topBestTerms)
        		break;
        }
	}
}
