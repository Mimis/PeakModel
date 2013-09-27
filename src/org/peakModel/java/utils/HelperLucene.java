package org.peakModel.java.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.peakModel.java.lucene.analyzer.KbAnalyzer;
import org.peakModel.java.lucene.analyzer.NGramAnalyzer;
import org.peakModel.java.peakModel.NGramWithDF;

public class HelperLucene {
	
	public static List<String> tokenizeString(Analyzer analyzer, String textToTokenize)	throws IOException {
		List<String> ngrams = new ArrayList<String>();
		TokenStream ts = analyzer.tokenStream("title", new StringReader(textToTokenize));

		try {
			ts.reset(); // Resets this stream to the beginning. (Required)
			while (ts.incrementToken()) {
				ngrams.add(ts.getAttribute(CharTermAttribute.class).toString());
			}
			ts.end(); // Perform end-of-stream operations, e.g. set the final
						// offset.
		} finally {
			ts.close(); // Release resources associated with this stream.
		}
		return ngrams;
	}	
	
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

	public static TopDocs queryIndexGetTopDocsBoolean(QueryParser queryParser,IndexSearcher searcher, String queryText,String year,String startD,String endD, int nrOfDocsToReturn) throws  ParseException, IOException{
		FieldCacheRangeFilter<String> filter = null;
		if(startD !=null)
			filter = FieldCacheRangeFilter.newStringRange("date",year+""+startD, year+""+endD, true, true);
		else
			filter = FieldCacheRangeFilter.newStringRange("date",year+"-01-01", year+"-12-31", true, true);
		
		BooleanQuery b = new BooleanQuery();
		b.add(new BooleanClause(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD));
		b.add(new TermQuery(new Term("content", queryText)), BooleanClause.Occur.MUST);
		b.add(new TermQuery(new Term("title", queryText)), BooleanClause.Occur.MUST_NOT);

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);

		Query query = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"content", "title"},analyzer).parse(b.toString());
		//System.out.println(query.toString()+"\t"+filter.toString());
		TopDocs topDocs = searcher.search(query,filter, nrOfDocsToReturn);
		return topDocs;
	}

	public static TopDocs queryIndexGetDocsWithoutScoring(QueryParser queryParser,IndexSearcher searcher, String queryText, int nrOfDocsToReturn) throws  ParseException, IOException{
		Query query = queryParser.parse(queryText);
		TopDocs topDocs = searcher.search(new ConstantScoreQuery(query), nrOfDocsToReturn);
		return topDocs;
	}

	
	
	public static void closeIndexWriter(IndexWriter indexWriter,boolean executeIndexMerge) throws IOException{
		indexWriter.close(executeIndexMerge);
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

    public static Analyzer getNGramAnalyzer(String dutchStopWordFile,int minN,int maxN) throws IOException{
    	CharArraySet stopWordsSet = Helper.getStopWordsSet(dutchStopWordFile);
        NGramAnalyzer analyzer = new NGramAnalyzer(Version.LUCENE_43, stopWordsSet,minN, maxN);
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

	public static void mapTermVectorToNGramList(DirectoryReader reader,Terms terms,TermsEnum termsEnum,String field,List<NGramWithDF> ngramList,String ngram_type) throws IOException{
		if (terms != null) {
			termsEnum = terms.iterator(termsEnum);
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				
				final String term = text.utf8ToString();
				String currentNgram_type = HelperLucene.getNgramType(term);
				if(!ngram_type.equals(currentNgram_type) && !ngram_type.equals("MIX"))
					continue;
				
				NGramWithDF newNGram = new NGramWithDF(term,field);
				//this we need it anyway to retrieve it in both cases
				final int total_tf_query = (int) termsEnum.totalTermFreq();			
				
				int indexOfNgram = ngramList.indexOf(newNGram);
				if(indexOfNgram != -1){
					NGramWithDF ngram =ngramList.get(indexOfNgram);
					ngram.addTotal_tf_query(total_tf_query);
					ngram.increaseByOneDf_query();
				}
				else{
					ngramList.add(newNGram);
					//THIS THE PER	FORMANCE BOTTLENECK
					final int df_corpus = reader.docFreq(new Term(field, term));					
//					System.out.println("df:"+df_corpus+"\tNewDF:"+total_ff_query);
					newNGram.setDf_query(1);
					newNGram.setDf_corpus(df_corpus);
					newNGram.setTotal_tf_query(total_tf_query);
				}
			}
		}
	}
	
	private static String getNgramType(String ngram){
		int type = ngram.split(" ").length;
		if(type==1)
			return "UNI";
		else if(type==2)
			return "BI";
		else if(type==3)
			return "TRI";
		return null;
	}
}
