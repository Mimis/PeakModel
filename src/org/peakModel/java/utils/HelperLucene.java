package org.peakModel.java.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.peakModel.java.lucene.analyzer.KbAnalyzer;
import org.peakModel.java.ngram.NGram;

public class HelperLucene {
	
	/**
	 * 
	 * @param ngram
	 * @param date
	 * @param field
	 * @param queryParser
	 * @param searcher
	 * @return doc freq of given ngram in the given period
	 * @throws ParseException
	 * @throws IOException
	 */
	public static int getNgramDf(String ngram,String date,String field,QueryParser queryParser,IndexSearcher searcher) throws ParseException, IOException{
    	String queryNgramTime = field+":\"" + ngram + "\" AND " + date;
    	TotalHitCountCollector collectorOnlyForHitCount = new TotalHitCountCollector();
        HelperLucene.queryIndexWithCollector(queryParser,collectorOnlyForHitCount, searcher, queryNgramTime);
        return collectorOnlyForHitCount.getTotalHits();
	}
	
	public static void queryIndexWithCollector(QueryParser queryParser, Collector collector, IndexSearcher searcher, String queryText) throws  ParseException, IOException{
		Query query = queryParser.parse(queryText);
		searcher.search(query, collector);
	}
	
	public static TopDocs queryIndexGetTopDocs(QueryParser queryParser,IndexSearcher searcher, String queryText, int nrOfDocsToReturn) throws  ParseException, IOException{
		Query query = queryParser.parse(queryText);
		TopDocs topDocs = searcher.search(query, nrOfDocsToReturn);
		return topDocs;
	}
	
	public static void closeIndexWriter(IndexWriter indexWriter) throws IOException{
		indexWriter.close();
	}
	
	
	public static IndexWriter getIndexWriter(Directory indexDir, Analyzer analyzer, boolean createNewIndex,double setRAMBufferSizeMB) throws IOException{
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		indexWriterConfig.setRAMBufferSizeMB(setRAMBufferSizeMB);
		if(createNewIndex)
			indexWriterConfig.setOpenMode(OpenMode.CREATE); 
		IndexWriter indexWriter = new IndexWriter(indexDir, indexWriterConfig);
		return indexWriter;
	}
	
    public static Analyzer getKbAnalyzer(String dutchStopWordFile) throws IOException{
    	CharArraySet stopWordsSet = Helper.getStopWordsSet(dutchStopWordFile);
        KbAnalyzer analyzer = new KbAnalyzer(Version.LUCENE_43, stopWordsSet);
    	return analyzer;
	}

	
	
	public static Directory getIndexDir(String indexKbCorpusFileName) throws IOException{
		File indexFile = new File(indexKbCorpusFileName);
        Directory indexDir = FSDirectory.open(indexFile);
        return indexDir;
	}
	
	
	public static int getTotalNumberOfDocsInIndex(IndexReader reader){
		return reader.numDocs();
	}
	
	public static void displayTermVector(DirectoryReader reader,Terms terms,TermsEnum termsEnum,String field) throws IOException{
		if (terms != null) {
			termsEnum = terms.iterator(termsEnum);
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				final String term = text.utf8ToString();
				final int freq = (int) termsEnum.totalTermFreq();
				final int  df = reader.docFreq(new Term(field, term));
				System.out.println(term+"\ttf:"+freq + "\tdf:"+df);
			}
		}
	}

	public static void mapTermVectorToNGramList(DirectoryReader reader,Terms terms,TermsEnum termsEnum,String field,List<NGram> ngramList) throws IOException{
		if (terms != null) {
			termsEnum = terms.iterator(termsEnum);
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				final String term = text.utf8ToString();
				NGram newNGram = new NGram(term,field);
				//this we need it anyway to retrieve it in both cases
				final int total_tf_query = (int) termsEnum.totalTermFreq();
				int indexOfNgram = ngramList.indexOf(newNGram);
				if(indexOfNgram != -1){
					NGram ngram =ngramList.get(indexOfNgram);
					ngram.addTotal_tf_query(total_tf_query);
					ngram.increaseByOneDf_query();
				}
				else{
					ngramList.add(newNGram);
					final int df_corpus = reader.docFreq(new Term(field, term));
					newNGram.setDf_query(1);
					newNGram.setDf_corpus(df_corpus);
					newNGram.setTotal_tf_query(total_tf_query);
				}
			}
		}
	}
}
