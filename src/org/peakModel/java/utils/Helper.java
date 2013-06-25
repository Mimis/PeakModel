package org.peakModel.java.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.peakModel.java.ngram.NGram;

public class Helper {

	public static void waitThreadsToFinish(List<Thread> threads){
        int running = 0;
        do {
          running = 0;
          for (Thread thread : threads) {
            if (thread.isAlive()) {
              running++;
            }
          }
        } while (running > 0);
	}

	
	public static List<String> keepOnlyBigramsFromList(List<String> tokenList){
		List<String> tokenOnlyBiList = new ArrayList<String>();
		for(String token:tokenList){
			if(token.contains(" "))
				tokenOnlyBiList.add(token);
		}
		return tokenOnlyBiList;
	}

	public static void getPeakPeriodIndex(String fileWithTFperYear,HashMap<String,Long> peakPeriodMap){
		File file = new File(fileWithTFperYear);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (!line.isEmpty()){
						String[] tfPerYear = line.split(",");
						peakPeriodMap.put(tfPerYear[0],Long.parseLong(tfPerYear[1]));
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	public static void mapTokenListToNGramList(List<String> tokenList,String field,List<NGram> ngramList) throws IOException{
		for(String token:tokenList){
			NGram newNGram = new NGram(token,field);
			int indexOfNgram = ngramList.indexOf(newNGram);
			if(indexOfNgram != -1){
				NGram ngram =ngramList.get(indexOfNgram);
				ngram.increaseTFpeakByone();
			}
			else{
				ngramList.add(newNGram);
				newNGram.setTf_query_peak(1);
			}
		}
	}

	
	public static CharArraySet getStopWordsSet(String stopWordFile){
		Set<String> stopWordsList = readFileLineByLineReturnSetOfLineString(stopWordFile);
		CharArraySet stopWordsSet = new CharArraySet(Version.LUCENE_43, stopWordsList, false);
		return stopWordsSet;
	}
	


	public static double log2( double a ){
		return (double) Math.log(a) / Math.log(2);
	}
	
	public static void writeLineToFile(String filename, String text, boolean append, boolean addNewLine) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,append),"UTF8"));
		out.write(text);
		if(addNewLine)
			out.write("\n");
		out.close();
	}
	
	
	public static Set<String> readFileLineByLineReturnSetOfLineString(String fileToRead) {
		Set<String> lineWords = new HashSet<String>();
		File file = new File(fileToRead);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (line.length() > 0)
						lineWords.add(line.trim().replaceAll("\\n", ""));
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineWords;
	}

	public static List<String> readFileLineByLineReturnListOfLineString(String fileToRead) {
		List<String> lineWords = new ArrayList<String>();
		File file = new File(fileToRead);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (line.length() > 0)
						lineWords.add(line.trim().replaceAll("\\n", ""));
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineWords;
	}

}

