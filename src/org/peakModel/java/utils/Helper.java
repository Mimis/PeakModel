package org.peakModel.java.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class Helper {

	public static CharArraySet getStopWordsSet(String stopWordFile){
		Set<String> stopWordsList = readFileLineByLineReturnListOfLineString(stopWordFile);
		CharArraySet stopWordsSet = new CharArraySet(Version.LUCENE_43, stopWordsList, false);
		return stopWordsSet;
	}
	
	public static Set<String> readFileLineByLineReturnListOfLineString(String fileToRead) {
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
	
	public static double log2( double a ){
		return Math.log(a) / Math.log(2);
	}
	
	public static void writeLineToFile(String filename, String text, boolean append, boolean addNewLine) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,append),"UTF8"));
		out.write(text);
		if(addNewLine)
			out.write("\n");
		out.close();
	}

}

