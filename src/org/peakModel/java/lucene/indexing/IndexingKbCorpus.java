package org.peakModel.java.lucene.indexing;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.peakModel.java.utils.HelperLucene;

public class IndexingKbCorpus {

	
	private  String indexKbCorpusFileName = null;
	private  String dutchStopWordFile = null;
	private FieldType titleFieldType = null;
	private FieldType contentFieldType = null;
	private IndexWriter indexWriter = null;
	

	/**
	 * @param indexKbCorpusFileName
	 * @param dutchStopWordFile
	 * @param titleFieldType
	 * @param contentFieldType
	 * @throws IOException 
	 */
	public IndexingKbCorpus(String indexKbCorpusFileName,String dutchStopWordFile,double setRAMBufferSizeMB,boolean createNewIndex) throws IOException {
		super();
		this.indexKbCorpusFileName = indexKbCorpusFileName;
		this.dutchStopWordFile = dutchStopWordFile;
		// get fieldTypes for title and content
		this.titleFieldType = getArticleTitleFieldType();
		this.contentFieldType = getArticleContentFieldType();
		this.indexWriter = getKbIndexWriter(setRAMBufferSizeMB,createNewIndex);
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		String indexKbCorpusFileName = "./index/test";
		String dutchStopWordFile = "./data/stopWords/dutch.txt";
		double setRAMBufferSizeMB = 1024;
		boolean createNewIndex = false;
		IndexingKbCorpus indexingKbCorpus = new IndexingKbCorpus(indexKbCorpusFileName, dutchStopWordFile,setRAMBufferSizeMB,createNewIndex);
		
		
		/*
		 * add TEST documents
		 */
		String date = "1987-12-12";
		String url = "http://kranten.kb.nl/view/article/id/ddd:011022115:mpeg21:p001:a0005";
		String title = " ROTTERDAM  second 1,5 1.66 aa ROTTERDAM Miljarden zwart-? geld &*_#in gokkast aan  1233422  4545malaka";
		String content = "ROTTERDAM — \"Per jaar verdwijnt er\" in ons land volgens";
		indexingKbCorpus.addDoc( date, url, title, content);
		
		
		//close index
		indexingKbCorpus.closeIndexWriter();
		
		//timer
        long endTime = System.currentTimeMillis();
	    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);
	}
	
	
	
	
	
	
	
	public void closeIndexWriter() throws IOException{
		HelperLucene.closeIndexWriter(this.getIndexWriter());
	}
	
	public IndexWriter getKbIndexWriter(double setRAMBufferSizeMB, boolean createNewIndex) throws IOException{
		/*
		 * Index Dir
		 */
        Directory indexDir = HelperLucene.getIndexDir(this.indexKbCorpusFileName);
        
		/*
		 * Analyzer
		 */
        Analyzer analyzer = HelperLucene.getKbAnalyzer(this.dutchStopWordFile);
        
        /*
         * InderWriter
         */
		IndexWriter indexWriter = HelperLucene.getIndexWriter(indexDir, analyzer, createNewIndex, setRAMBufferSizeMB);
	
		return indexWriter;
	}



	/**
	 * Fields:
		 * Date; format: YYYY-MM-DD
		 * Url
		 * Title
		 * Article’s Content
	 * @param indexWriter
	 * @param title
	 * @param isbn
	 */
	public  void addDoc(String date, String url, String title, String content)  {
		  Document doc = new Document();
          doc.add(new StringField("date", date, Field.Store.YES));
          doc.add(new StringField("url", url, Field.Store.YES));
          doc.add(new Field("title", title, titleFieldType));
          doc.add(new Field("content", content, contentFieldType));
		  try {
			  this.indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Article Content FieldType
	 * @return
	 */
	private  FieldType getArticleContentFieldType(){
		final FieldType contentOptions = new FieldType();
		contentOptions.setIndexed(true);
		//Indexes documents, frequencies and positions. This is a typical default for full-text search: full scoring is enabled and positional queries are supported.
		contentOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		contentOptions.setStored(false);
		contentOptions.setStoreTermVectors(true);
		contentOptions.setTokenized(true);
		contentOptions.freeze();
		return contentOptions;
	}

	/**
	 * Article Title FieldType
	 * @return
	 */
	private  FieldType getArticleTitleFieldType(){
		final FieldType contentOptions = new FieldType();
		contentOptions.setIndexed(true);
		//Indexes documents, frequencies and positions. This is a typical default for full-text search: full scoring is enabled and positional queries are supported.
		contentOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		contentOptions.setStored(true);
		contentOptions.setStoreTermVectors(true);
		contentOptions.setTokenized(true);
		contentOptions.freeze();
		return contentOptions;
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
	 * @return the dutchStopWordFile
	 */
	public String getDutchStopWordFile() {
		return dutchStopWordFile;
	}


	/**
	 * @param dutchStopWordFile the dutchStopWordFile to set
	 */
	public void setDutchStopWordFile(String dutchStopWordFile) {
		this.dutchStopWordFile = dutchStopWordFile;
	}


	/**
	 * @return the titleFieldType
	 */
	public FieldType getTitleFieldType() {
		return titleFieldType;
	}


	/**
	 * @param titleFieldType the titleFieldType to set
	 */
	public void setTitleFieldType(FieldType titleFieldType) {
		this.titleFieldType = titleFieldType;
	}


	/**
	 * @return the contentFieldType
	 */
	public FieldType getContentFieldType() {
		return contentFieldType;
	}


	/**
	 * @param contentFieldType the contentFieldType to set
	 */
	public void setContentFieldType(FieldType contentFieldType) {
		this.contentFieldType = contentFieldType;
	}


	/**
	 * @return the indexWriter
	 */
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}


	/**
	 * @param indexWriter the indexWriter to set
	 */
	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}

	
	
	
}
