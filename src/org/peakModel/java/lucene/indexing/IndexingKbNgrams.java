package org.peakModel.java.lucene.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.peakModel.java.utils.HelperLucene;

public class IndexingKbNgrams {

	
	private  String indexKbCorpusFileName = null;
	private  String dutchStopWordFile = null;
	private IndexWriter indexWriter = null;
	

	/**
	 * @param indexKbCorpusFileName
	 * @param dutchStopWordFile
	 * @param titleFieldType
	 * @param contentFieldType
	 * @throws IOException 
	 */
	public IndexingKbNgrams(String indexKbCorpusFileName,String dutchStopWordFile,double setRAMBufferSizeMB,boolean createNewIndex) throws IOException {
		super();
		this.indexKbCorpusFileName = indexKbCorpusFileName;
		this.dutchStopWordFile = dutchStopWordFile;
		// get fieldTypes for title and content
		this.indexWriter = getKbIndexWriter(setRAMBufferSizeMB,createNewIndex);
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		
		String ngramFile="/Users/mimis/Development/EclipseProject/PeakModel/data/ngrams/IndexKB1gram16-17-18-19Min10TimesSorted.tsv";
		String indexKbNgramFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/IndexKB1gram16-17-18-19Min10TimesSorted";
		String dutchStopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/empty.txt";
		double setRAMBufferSizeMB = 1024;
		boolean createNewIndex = true;
		IndexingKbNgrams indexingKbCorpus = new IndexingKbNgrams(indexKbNgramFileName, dutchStopWordFile,setRAMBufferSizeMB,createNewIndex);
		

		//index KB ngrams
		indexingKbCorpus.indexNgramFile(ngramFile);
		
		
		//close index
		indexingKbCorpus.closeIndexWriter(true);
		
		//timer
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
	}
	
	public void indexNgramFile(String ngramFile){
		int count = 0;
		File file = new File(ngramFile);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (!line.isEmpty()){
						String[] ngramInfo = line.split("\t");
						this.addDoc(ngramInfo[0], ngramInfo[2], ngramInfo[3]);
						if(count++ % 100000 == 0)
							System.out.println(line);
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void closeIndexWriter(boolean executeIndexMerge) throws IOException{
		HelperLucene.closeIndexWriter(this.getIndexWriter(),executeIndexMerge);
	}
	
	public IndexWriter getKbIndexWriter(double setRAMBufferSizeMB, boolean createNewIndex) throws IOException{
		/*
		 * Index Dir
		 */
        Directory indexDir = HelperLucene.getIndexDir(this.indexKbCorpusFileName);
        
		/*
		 * Analyzer
		 */
        
    	//CharArraySet stopWordsSet = Helper.getStopWordsSet(this.dutchStopWordFile);
        Analyzer analyzer = new KeywordAnalyzer();

        /*
         * InderWriter
         */
		IndexWriter indexWriter = HelperLucene.getIndexWriter(indexDir, analyzer, createNewIndex, setRAMBufferSizeMB);
	
		return indexWriter;
	}



	/**
	 * Fields:
		 * ngram
		 * totalFrequency in the corpus
		 * freqPerYear => 16yy:4090,17yy:46914,1800:23306
	 */
	public  void addDoc(String ngram, String totalFrequency, String freqPerYear)  {
		  Document doc = new Document();
          doc.add(new StringField("ngram", ngram, Field.Store.YES));
          doc.add(new StringField("totalFrequency", totalFrequency, Field.Store.YES));
          doc.add(new StringField("freqPerYear", freqPerYear, Field.Store.YES));
		  try {
			  this.indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @return the indexKbCorpusFileName
	 */
	public String getIndexKbCorpusFileName() {
		return indexKbCorpusFileName;
	}


	/**
	 * @param indexKbCorpusFileName the indexKbCorpusFileName to set
	 */
	public void setIndexKbCorpusFileName(String indexKbCorpusFileName) {
		this.indexKbCorpusFileName = indexKbCorpusFileName;
	}


	/**
	 * @return the indexWriter
	 */
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	
}
