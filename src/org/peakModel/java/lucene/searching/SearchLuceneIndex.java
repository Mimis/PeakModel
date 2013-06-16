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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGram;
import org.peakModel.java.utils.HelperLucene;

public class SearchLuceneIndex {

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
		String indexKbCorpusFileName = "./index/KbCorpus";
		String dutchStopWordFile = "./data/stopWords/dutch.txt";
		int topBestTerms = 3;
		//==========================================================//

        
		/*
		 * Variables
		 */
		//==========================================================//
        int totalNumberOfDocumentsWithTitle = -1;
        int totalNumberOfDocumentsWithContent = -1;
		int totalNumberOfRelevantDocuments = -1;
		int totalNumberOfDocumentsInGivenPeriod = -1;
		int totalNumberOfDocumentsWithTitleInGivenPeriod = -1;
		int totalNumberOfDocumentsWithContentInGivenPeriod = -1;
		List<NGram> ngramList = new ArrayList<NGram>();
		//==========================================================//
        
        
        /*
		 * Index Dir + Reader + Searcher + Analyzer + QueryParser
		 */
        Directory indexDir = HelperLucene.getIndexDir(indexKbCorpusFileName);
        DirectoryReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = HelperLucene.getKbAnalyzer(dutchStopWordFile);
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "title", analyzer);
        TotalHitCountCollector collectorOnlyForHitCount = new TotalHitCountCollector();
        
        
        
        
        /**
		 * Queries
		 */
        String query = "\""+ "griek" + "\"";
		String date = "date:[1980-05-05 TO 1980-05-05]";
		//This queries shoudl use to find out how many docs got content for each field separate in a specific period
		String titleDate = "title:{* TO *} AND " + date;
		String contentDate = "content:{* TO *} AND " + date;
        String wholeQuery = query + " AND " + date;


        
        
        /*
         * Get total number of document with title  or content text in the whole index
         * 	**THESE ARE CONTSTANTS!!!!**
         */
        totalNumberOfDocumentsWithTitle = reader.getDocCount("title");
        totalNumberOfDocumentsWithContent = reader.getDocCount("content");
        
		        
        
        /*
         *  1.Query Date Search: Get total Number Of Documents In Given Period
         */
        HelperLucene.queryIndexWithCollector(queryParser,collectorOnlyForHitCount, searcher, date);
        totalNumberOfDocumentsInGivenPeriod = collectorOnlyForHitCount.getTotalHits();
        
        
        
        /*
         * 2.1 Whole Query  Search: Get relevant docs to the given query, the total number of relevant docs and the terms with their info that exist in  
         */
		TopDocs topDocs = HelperLucene.queryIndexGetTopDocs(queryParser,searcher, wholeQuery,1000000000);

		
        /*
         * 2.2 Get the N-Grams with their Document Frequency and Total Frequency in each field!
         */
        if(totalNumberOfRelevantDocuments==0){
        	System.out.println("Zero Results :/");
        }else{
        	TermsEnum termsEnum = null;
			ScoreDoc[] hits = topDocs.scoreDocs;
			totalNumberOfRelevantDocuments = hits.length;
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				final Document doc = searcher.doc(docId);
				System.out.println("Doc id:" + docId + "\tTitle:"+ doc.get("title") + " Date:" + doc.get("date")	+ "\tUrl:" + doc.get("url"));
				
				final Terms titleTermVector = reader.getTermVector(docId,"title");
				HelperLucene.mapTermVectorToNGramList(reader, titleTermVector, termsEnum, "title", ngramList);
//				 final Terms contentTermVector = reader.getTermVector(docId,"content");
//				 HelperLucene.displayTermVector(reader,contentTermVector, termsEnum,"content");
			}
        }
        
        /*
         * 3. Calculate Statistical Measures
         */
        for(NGram ngram:ngramList){
        	//Title
        	ngram.setDf_time(HelperLucene.getNgramDf(ngram.getNgram(), date, "title", queryParser, searcher));
        	ngram.calculateProbabilities(totalNumberOfDocumentsWithTitle, totalNumberOfRelevantDocuments, totalNumberOfDocumentsInGivenPeriod);
        	ngram.computePMIClasic();
        	ngram.computePMItime();
        }

        /*
         * 4. Sort NGrams
         */
//        Collections.sort(ngramList, NGram.COMPARATOR_TOTAL_TF);
//        Collections.sort(ngramList, NGram.COMPARATOR_PMI_CLASSIC);
        Collections.sort(ngramList, NGram.COMPARATOR_PMI_TIME);
        
        
        
        
        /*
         * 5. Display results
         */
        System.out.println("====================================================================================================");
        System.out.println("Query:"+wholeQuery);
        System.out.println("====================================================================================================");
        System.out.println("TotalNumberOfRelevantDocuments:"+totalNumberOfRelevantDocuments);
        System.out.println("TotalNumberOfDocumentsInGivenPeriod:"+totalNumberOfDocumentsInGivenPeriod+"\tTotalNumberOfDocumentWithTitle:"+totalNumberOfDocumentsWithTitle + "\tTotalNumberOfDocumentWithContent:"+totalNumberOfDocumentsWithContent);
        System.out.println("====================================================================================================");
    	System.out.println("ngram ,total_tf_query,P_w,P_w_Given_query_time,P_w_Given_time,PMI_classic,PMI_time");
    	int countTerms = 1;
        for(NGram ngram:ngramList){
        	System.out.println(ngram.toStringCsvCompact());
        	if(countTerms++ >= topBestTerms)
        		break;
        }
        System.out.println("====================================================================================================");
        //timer
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
	}
}
