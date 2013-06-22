package org.peakModel.java.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.peakModel.java.lucene.indexing.IndexingKbCorpus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseXml {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String kbDataFolderForIndexing = "/Users/mimis/Development/EclipseProject/PeakModel/data/KB/" + args[0];
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			SAXParser saxParser = factory.newSAXParser();

			
			DefaultHandler handler = new DefaultHandler() {
				/**
				 * Lucene...
				 */
				String indexKbCorpusFileName = "/Users/mimis/Development/EclipseProject/PeakModel/index/KB_1990";
				String dutchStopWordFile = "/Users/mimis/Development/EclipseProject/PeakModel/data/stopWords/dutch.txt";
				double setRAMBufferSizeMB = 1024;
				boolean createNewIndex = false;
				//updAte index;dont create from scratch
				IndexingKbCorpus indexingKbCorpus;
				//dont merge the index in every close command
				boolean executeIndexMerge = true;
				
				boolean date = false;
				String DateValue = null;
				boolean subject = false;
				String SubjectValue = null;
				boolean id = false;
				String IdValue = null;
				boolean source = false;
				String sourceValue = null;
				boolean title = false;
				String titleValue = null;
				boolean paragraph = false;
				StringBuilder paragraphValue;
				int docCounter = 0;
				
				
				// at the start open the index...
				public void startDocument() {
					System.out.println("Open index...");
					try {
						indexingKbCorpus = new IndexingKbCorpus(indexKbCorpusFileName, dutchStopWordFile,setRAMBufferSizeMB,createNewIndex);
					} catch (IOException e) {
						e.printStackTrace();
					}
					paragraphValue = new StringBuilder();
				}

				
				
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					if (qName.equalsIgnoreCase("dc:date")) {
						date = true;
					}

					if (qName.equalsIgnoreCase("dc:subject")) {
						subject = true;
					}

					if (qName.equalsIgnoreCase("dc:identifier")) {
						id = true;
					}
					if (qName.equalsIgnoreCase("title")) {
						title = true;
					}
					if (qName.equalsIgnoreCase("p")) {
						paragraph = true;
					}

					int length = attributes.getLength();
					// Each attribute
					for (int i = 0; i < length; i++) {
						// Get names and values to each attribute
						String name = attributes.getQName(i);
						if (name.equals("source")) {
							sourceValue = attributes.getValue(i);
						}
					}

				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {

					if (qName.equals("pm:root")) {
						// Skip advertisements
						if (!SubjectValue.equals("advertentie")) {

							// INDEX DOCUMENT
//							System.out.println("id : " + IdValue);
							// System.out.print("\tdate : "+ DateValue);
							// System.out.print("\tsubject : " + SubjectValue);
							// System.out.print("\tUrl : " + sourceValue );
							// System.out.print("\tTitle : " + titleValue );
							// System.out.print("\n\tParagraph : " +
							// paragraphValue.toString() );
							// System.out.println("");
							
							//add doc in the lucene index
							indexingKbCorpus.addDoc(DateValue, sourceValue,	titleValue, paragraphValue.toString());
							if(docCounter++ % 100000 == 0)
								System.out.println("docCounter : " + docCounter);
								
						}
						paragraphValue = new StringBuilder();
						paragraph = false;
					}

				}

				public void characters(char ch[], int start, int length)
						throws SAXException {

					if (date) {
						DateValue = new String(ch, start, length).trim();
						date = false;
					}

					if (subject) {
						SubjectValue = new String(ch, start, length).trim();
						subject = false;
					}

					if (id) {
						IdValue = new String(ch, start, length).trim();
						id = false;
					}

					if (title) {
						titleValue = new String(ch, start, length).trim();
						title = false;
					}
					if (paragraph) {
						paragraphValue.append(ch, start, length);
					}

					if (source) {
						sourceValue = null;
						source = false;
					}
				}

				
				// at the end close the index...
				public void endDocument() {
					System.out.println("Close index...");
					try {
						indexingKbCorpus.closeIndexWriter(executeIndexMerge);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			List<String> filesPaths = new ArrayList<String>();
			File file = new File(kbDataFolderForIndexing);
			Collection<File> files = FileUtils.listFiles(  file, new RegexFileFilter(".*\\.xml$"), DirectoryFileFilter.DIRECTORY);	
			for(File f: files)
				filesPaths.add(f.getAbsolutePath());
			for(String f: filesPaths){
				System.out.println(f);
				saxParser.parse(f, handler);	
			}

			
			//timer
	        long endTime = System.currentTimeMillis();
		    System.out.println("#Total Indexing run time:"+ (endTime-startTime)/1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}