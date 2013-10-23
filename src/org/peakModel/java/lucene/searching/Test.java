package org.peakModel.java.lucene.searching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.utils.Helper;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		String token="1";
   		if(token.matches("(\\w+\\s\\d+|\\d+\\s\\w+|\\d+\\s\\d+|\\d+)")) 
   			System.out.println("yes");
   		else
   			System.out.println("nooo");
	}
	
	
	public static boolean includeQuery(String ngram,List<String> queryUnigrmasList){
		for(String ng:ngram.split("\\s"))
			if(queryUnigrmasList.contains(ng))
				return true;
		return false;
	}
	
	public static boolean includeStopWord(String ngram,List<String> stopWordsList){
		for(String ng:ngram.split("\\s"))
			if(stopWordsList.contains(ng))
				return true;
		return false;
	}

}
