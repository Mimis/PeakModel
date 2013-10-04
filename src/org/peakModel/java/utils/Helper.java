package org.peakModel.java.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.peakModel.burstiness.Burst;
import org.peakModel.java.peakModel.burstiness.FeatureTemporalProfile;
import org.peakModel.java.peakModel.document_process.KbDocument;

public class Helper {

	public static String replaceStopWordWitUnderScore(String feature,List<String> stopWordsList){
		String[] feaArr = feature.split("\\s");
		if(feaArr.length == 2){
			StringBuilder buf = new StringBuilder();
			for(String a:feaArr)
				if(stopWordsList.contains(a))
					buf.append("_ ");
				else
					buf.append(a+" ");
			return buf.toString();
		}
		else
			return feature;
	}

	public static void displayLanguageModelsByTFIDFpeak_year(List<LanguageModel> languageModelList,List<LanguageModel> negLanguageModelList, String name,List<String> stopWordsList,int minLength,int maxLength,int topN){
		for(int ngramLength=minLength;ngramLength<=maxLength;ngramLength++){
			LanguageModel lang = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLength)));

			System.out.println("\n\n#"+name+":"+lang.toString());
			for(NGram ng:lang.getTopTFIDFpeak_yearNgrams(topN)){

				if(stopWordsList!=null){
					if(!stopWordsList.contains(ng.getNgram()))
						System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tTFIDF:"+ng.getTF_IDF_peak_year());
				}
				else
					System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tTFIDF:"+ng.getTF_IDF_peak_year());

			}
		}
	}
	

	public static void displayLanguageModelsByFrequency(List<LanguageModel> languageModelList,List<LanguageModel> negLanguageModelList, String name,List<String> stopWordsList,int minLength,int maxLength,int topN){
		for(int ngramLength=minLength;ngramLength<=maxLength;ngramLength++){
			LanguageModel lang = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel negLang = negLanguageModelList.get(negLanguageModelList.indexOf(new LanguageModel(ngramLength)));

			System.out.println("\n\n#"+name+":"+lang.toString());
			for(NGram ng:lang.getTopFrequentNgrams(topN)){
				int negLMngram = 0;
				if(negLang.getNgramList().contains(ng))
					negLMngram = negLang.getNgramList().get(negLang.getNgramList().indexOf(ng)).getTf_query_peak();

				if(stopWordsList!=null){
					if(!stopWordsList.contains(ng.getNgram()))
						System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tnegTF:"+negLMngram+"\tLogBurst:"+ng.getLOG_Likelyhood_burst()+"\tLogCorpus:"+ng.getLOG_Likelyhood_corpus());
				}
				else
					System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tnegTF:"+negLMngram+"\tLogBurst:"+ng.getLOG_Likelyhood_burst()+"\tLogCorpus:"+ng.getLOG_Likelyhood_corpus());

			}
		}
	}
	public static void displayLanguageModelsByEntropy(List<LanguageModel> languageModelList,List<LanguageModel> negLanguageModelList,String name,int minLength,int maxLength,int topN){
		for(int ngramLength=minLength;ngramLength<=maxLength;ngramLength++){
			LanguageModel lang = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel negLang = negLanguageModelList.get(negLanguageModelList.indexOf(new LanguageModel(ngramLength)));
			System.out.println("\n\n#"+name+":"+lang.toString());
			for(NGram ng:lang.getTopEntropyNgrams(topN)){
				int negLMngram = 0;
				if(negLang.getNgramList().contains(ng))
					negLMngram = negLang.getNgramList().get(negLang.getNgramList().indexOf(ng)).getTf_query_peak();

				System.out.println(ng.getNgram()+"\t"+ng.getTf_query_peak()+"\t"+negLMngram+"\t"+ng.getP_w_language_model()+"\tEntropy:"+ng.getRelative_Entropy());
			}
		}
	}

	public static void displayLanguageModelsByLogLikelihoodBurst(List<LanguageModel> languageModelList,List<LanguageModel> negLanguageModelList,String name,int minLength,int maxLength,int topN){
		for(int ngramLength=minLength;ngramLength<=maxLength;ngramLength++){
			LanguageModel lang = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel negLang = negLanguageModelList.get(negLanguageModelList.indexOf(new LanguageModel(ngramLength)));

			System.out.println("\n\n#"+name+":"+lang.toString());
			for(NGram ng:lang.getTopLogBurstNgrams(topN)){
				int negLMngram = 0;
				if(negLang.getNgramList().contains(ng))
					negLMngram = negLang.getNgramList().get(negLang.getNgramList().indexOf(ng)).getTf_query_peak();
				System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tTFIDFpeak:"+ng.getTF_IDF_peak_year()+"\tnegTF:"+negLMngram+"\tLogBurst:"+ng.getLOG_Likelyhood_burst()+"\tLogCorpus:"+ng.getLOG_Likelyhood_corpus());
			}
		}
	}

	public static void displayLanguageModelsByLogLikelihoodCorpus(List<LanguageModel> languageModelList,List<LanguageModel> negLanguageModelList,String name,int minLength,int maxLength,int topN){
		for(int ngramLength=minLength;ngramLength<=maxLength;ngramLength++){
			LanguageModel lang = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLength)));
			LanguageModel negLang = negLanguageModelList.get(negLanguageModelList.indexOf(new LanguageModel(ngramLength)));

			System.out.println("\n\n#"+name+":"+lang.toString());
			for(NGram ng:lang.getTopLogCorpusNgrams(topN)){
				int negLMngram = 0;
				if(negLang.getNgramList().contains(ng))
					negLMngram = negLang.getNgramList().get(negLang.getNgramList().indexOf(ng)).getTf_query_peak();
				System.out.println(ng.getNgram()+"\tNrOfDaysAppear:"+ng.getDocFreqPerDayMap().size()+"\tTF:"+ng.getTf_query_peak()+"\tnegTF:"+negLMngram+"\tLogBurst:"+ng.getLOG_Likelyhood_burst()+"\tLogCorpus:"+ng.getLOG_Likelyhood_corpus());
			}
		}
	}

	public static void displayBurstsPeriods(FeatureTemporalProfile featureTemporalProfile){
		Collections.sort(featureTemporalProfile.getBurstList(), Burst.COMPARATOR_BURSTINESS);
        for(Burst burst:featureTemporalProfile.getBurstList()){
        	System.out.println("#"+burst.toString());
        }
	}

	
	public static void displayBurstsDocuments(FeatureTemporalProfile featureTemporalProfile,List<KbDocument> documentList){
		Collections.sort(featureTemporalProfile.getBurstList(), Burst.COMPARATOR_BURSTINESS);
        for(Burst burst:featureTemporalProfile.getBurstList()){
        	Set<String> burstYearSet = burst.getDateSet();
        	System.out.println("\n#"+burst.toString());
        	
			for(KbDocument kb : documentList){
				if(burstYearSet.contains(kb.getDate()))
					System.out.println("\t"+kb.getDate()+"\t"+kb.getTitle()+"\t"+kb.getTokenSet().toString());
			}
        }
	}
	public static void displayNoBurstsDocuments(FeatureTemporalProfile featureTemporalProfile,List<KbDocument> documentList){
		Set<String> allBurstYearSet = new HashSet<String>();
        for(Burst burst:featureTemporalProfile.getBurstList()){
        	allBurstYearSet.addAll(burst.getDateSet());
        }
    	System.out.println("\n#Non Burst Periods:");
    	for(KbDocument kb : documentList){
  			if(!allBurstYearSet.contains(kb.getDate()))
				System.out.println("\t"+kb.getDate()+"\t"+kb.getTitle()+"\t"+kb.getTokenSet().toString());
		}    
	}

	public static boolean arrayContainsGivenString(String[] datesArray,String date){
		for(String d:datesArray)
			if(d.equals(date))
				return true;
		return false;
	}

	public static Map<String,Integer> importTfYearStringToMap(String tfPerYear){
		Map<String,Integer> tfPerYearMap = new HashMap<String,Integer>();
		String[] tfYearArr = tfPerYear.split(",");
		for(String yearToFreq:tfYearArr){
			String[] yearToFreqArr = yearToFreq.split(":");
			String year = yearToFreqArr[0];
			tfPerYearMap.put(year, Integer.parseInt(yearToFreqArr[1]));
		}
		return tfPerYearMap;
	}

	public static int getTfFromYearToTfMap(int year,Map<Integer,Integer> tfPerYearMap){
		if(tfPerYearMap.containsKey(year))
			return tfPerYearMap.get(year);
		else
			return 0;
	}
	public static int getTfFromYearToTfMap(String year,Map<String,Integer> tfPerYearMap){
		if(tfPerYearMap.containsKey(year))
			return tfPerYearMap.get(year);
		else
			return 0;
	}
	
	public static float getScoreFromYearToTfMap(String year,Map<String,Float> scorePerYearMap){
		if(scorePerYearMap.containsKey(year))
			return scorePerYearMap.get(year);
		else
			return 0;
	}
	
	/**
	 * Group NGrams with a stop word by sum up their frequencies
	 * @param ngramList
	 * @param field
	 * @param stopWords
	 * @return List<NGram> of prunned ngram list
	 */
	public static List<NGram> NGramPruning(List<NGram> ngramList,String field,List<String> stopWords){
		List<NGram> ngramPruningList = new ArrayList<NGram>();
		List<NGram> ngramFinalList = new ArrayList<NGram>();

		for(NGram ngram:ngramList){
			String ngramString = ngram.getNgram();
			
			//its a BIGRAM
			if(ngramString.contains(" ")){
				String[] ngramArray = ngramString.split(" ");
				int indexOfStopWord = -1;
				int indexOfWord = -1;
				if(stopWords.contains(ngramArray[0]) && stopWords.contains(ngramArray[1])){
					indexOfStopWord=-10;
				}
				else if(stopWords.contains(ngramArray[0])){
					indexOfStopWord = 0;
					indexOfWord = 1;
				}
				else if(stopWords.contains(ngramArray[1])){
					indexOfStopWord = 1;
					indexOfWord = 0;
				}
				
				//got stop word so prun it
				if(indexOfStopWord != -1){
					String ngramReplaceString = null;
					if(indexOfStopWord != -10)
						ngramReplaceString = ngramArray[indexOfWord];
					else
						ngramReplaceString = "_";
						                                
					NGram tempNgram = new NGram(ngramReplaceString + " _",field);
					int indexOfpruningNgram = ngramPruningList.indexOf(tempNgram);
					//we have seen it before
					if(indexOfpruningNgram != -1){
						NGram pruningNgram = ngramPruningList.get(indexOfpruningNgram);
						SumUpFreq(pruningNgram, ngram);
					}
					//its a new prunnign ngram
					else{
						initializeFreq(tempNgram, ngram);
						ngramPruningList.add(tempNgram);
					}
				}
				//got no stop words;dont prun it
				else{
					ngramFinalList.add(ngram);
				}
			}
			//its a UNIGRAM
			else{
				if(stopWords.contains(ngramString)){
					NGram tempNgram = new NGram("_",field);
					int indexOfpruningNgram = ngramPruningList.indexOf(tempNgram);
					if(indexOfpruningNgram != -1){
						NGram pruningNgram = ngramPruningList.get(indexOfpruningNgram);
						SumUpFreq(pruningNgram, ngram);
					}
					//its a new prunnign ngram
					else{
						initializeFreq(tempNgram, ngram);
						ngramPruningList.add(tempNgram);
					}
				}
				else
					ngramFinalList.add(ngram);
			}
		}
		//add the prunning ngrams to pruned list
		for(NGram n:ngramPruningList){
			ngramFinalList.add(n);
		}
		return ngramFinalList;
	}
	private static void SumUpFreq(NGram a,NGram b){
		a.setTf_query_peak(a.getTf_query_peak() + b.getTf_query_peak());
		a.setTf_peak(a.getTf_peak() + b.getTf_peak());
		a.setTf_corpus(a.getTf_corpus() + b.getTf_corpus());						
		a.setNr_of_years_appearance(a.getNr_of_years_appearance() > b.getNr_of_years_appearance() ? a.getNr_of_years_appearance() : b.getNr_of_years_appearance());
	}
	
	private static void initializeFreq(NGram a,NGram b){
		a.setTf_query_peak(b.getTf_query_peak());
		a.setTf_peak(b.getTf_peak());
		a.setTf_corpus(b.getTf_corpus());
		a.setNr_of_years_appearance(b.getNr_of_years_appearance());
	}

	
	public static long removeNgramsWithNoOccurenceInNGramIndex(List<NGram> ngramList,List<NGram> finalNGramList){
		long N_query_peakPeriod=0;
        for(NGram ngram:ngramList){
        	if(ngram.getTf_corpus() != 0 && ngram.getTf_peak() != 0){
        		finalNGramList.add(ngram);
            	N_query_peakPeriod += ngram.getTf_query_peak();
        	}
        }
        return N_query_peakPeriod;
	}

	
	public static long getMaxTF_query_peak(List<NGram> ngramList){
		long max = 0;
        for(NGram ngram:ngramList){
        	if(max < ngram.getTf_query_peak())
        		max = ngram.getTf_query_peak();
        }
        return max;
	}
	
	public static void writeNgramToCsv(List<NGram> ngramList, String experimentsFile) throws IOException{
		if(ngramList.isEmpty())
			return;
		String csvExpnationOutput = "";
        Helper.writeLineToFile(experimentsFile,csvExpnationOutput, false,false);
        for(NGram ngram:ngramList){
        	String ngramToString = ngram.toStringBurstiness();
        	Helper.writeLineToFile(experimentsFile, ngramToString, true, true);
        }
	}


	public static void removeQueryTermsFromNgramList(String initialQuery,List<NGram> ngramList){
		String qArr[] = initialQuery.split("\\s+");
		List<NGram> queryNgrams = new ArrayList<NGram>();
		for(String q:qArr){
			queryNgrams.add(new NGram(q,"title"));
		}
		queryNgrams.add(new NGram(initialQuery,"title"));
		for(NGram ng:queryNgrams){
			boolean b = ngramList.remove(ng);
			System.out.println(b+"\t"+ng.getNgram());
		}
	}

	
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

	public static List<NGram> skipSmallLengthNgram(List<NGram> tokenList){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			int length = ngram.getNgram().length();
			if(length > 5)
				tokenNoStopWordsList.add(ngram);
//			else
//				System.out.println(ngram.getNgram());
		}
		return tokenNoStopWordsList;
	}

	public static List<NGram> skipSingletonNgramWithSingletonUnigrams(List<NGram> bigramList,List<NGram> unigramList,String field){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:bigramList){
			String bigram = ngram.getNgram();
			String[] tokensArray = bigram.split(" ");

			if(ngram.getTf_query_peak() != 1){
				tokenNoStopWordsList.add(ngram);				
			}
			else if(unigramList.get(unigramList.indexOf(new NGram(tokensArray[0],field))).getTf_query_peak() != 1  ||
					unigramList.get(unigramList.indexOf(new NGram(tokensArray[1],field))).getTf_query_peak() != 1)
					tokenNoStopWordsList.add(ngram);
			
		}
		return tokenNoStopWordsList;
	}

	
	public static List<NGram> skipSingletonNgram(List<NGram> tokenList){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			if(ngram.getTf_query_peak() != 1)
				tokenNoStopWordsList.add(ngram);
			else
				System.out.println(ngram.getNgram()+"\t"+ngram.getTf_query_peak()+"\t"+ngram.getNr_of_years_appearance());
			
		}
		return tokenNoStopWordsList;
	}

	/**
	 * 	 * Remove ngram with only numbers and function words
	 * @param tokenList
	 * @param stopWords
	 * @param query
	 * @return
	 */
	public static List<NGram> skipNgramWithNumberAndStopWord(List<NGram> tokenList,List<String> stopWords){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			String token = ngram.getNgram();
			if(token.contains(" ")){
				String[] tokensArray = token.split(" ");
				if((stopWords.contains(tokensArray[0]) && tokensArray[1].matches("\\d+"))  || (stopWords.contains(tokensArray[1]) && tokensArray[0].matches("\\d+"))){
//					System.out.println(token);
				}
				else
					tokenNoStopWordsList.add(ngram);
			}
		}
		return tokenNoStopWordsList;
	}

	
	/**
	 * Remove ngram  with the Query Keyword and a function word
	 * @param tokenList
	 * @param stopWords
	 * @return
	 */
	public static List<NGram> skipNgramWithQueryAndStopWord(List<NGram> tokenList,List<String> stopWords,String query ){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			String token = ngram.getNgram();
			if(token.contains(" ")){
				String[] tokensArray = token.split(" ");
				if((stopWords.contains(tokensArray[0]) && tokensArray[1].equals(query))  || (stopWords.contains(tokensArray[1]) && tokensArray[0].equals(query))){
//					System.out.println(token);
				}
				else
					tokenNoStopWordsList.add(ngram);
			}
		}
		return tokenNoStopWordsList;
	}


	public static List<NGram> keepNoCominationWithStopWordsFromList(List<NGram> tokenList,List<String> stopWords ){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			String token = ngram.getNgram();
			if(token.contains(" ")){
				String[] tokensArray = token.split(" ");
				if(!stopWords.contains(tokensArray[0]) && !stopWords.contains(tokensArray[1]))
					tokenNoStopWordsList.add(ngram);
//				else
//					System.out.println(token);
			}else{
				if(!stopWords.contains(token))
					tokenNoStopWordsList.add(ngram);
			}
		}
		return tokenNoStopWordsList;
	}

	public static List<NGram> keepNoStopWordsFromList(List<NGram> tokenList,List<String> stopWords ){
		List<NGram> tokenNoStopWordsList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			String token = ngram.getNgram();
			if(token.contains(" ")){
				String[] tokensArray = token.split(" ");
				if(!stopWords.contains(tokensArray[0]) || !stopWords.contains(tokensArray[1]))
					tokenNoStopWordsList.add(ngram);
//				else
//					System.out.println(token);
			}else{
				if(!stopWords.contains(token))
					tokenNoStopWordsList.add(ngram);
			}
		}
		return tokenNoStopWordsList;
	}

	public static List<NGram> keepNoNgramNumbersFromList(List<NGram> tokenList){
		List<NGram> tokenNoNgramNumbersList = new ArrayList<NGram>();
		for(NGram ngram:tokenList){
			String token = ngram.getNgram();
			if(!token.matches("(\\d\\s{0,1})+"))
				tokenNoNgramNumbersList.add(ngram);
//			else
//				System.out.println(token);
		}
		return tokenNoNgramNumbersList;
	}

	public static List<String> getGivenLengthNgramsFromList(Collection<String> tokenList,int ngramLength){
		List<String> tokenOnlyGivenLengthList = new ArrayList<String>();
		for(String token:tokenList){
			if(token.split(" ").length == ngramLength)
				tokenOnlyGivenLengthList.add(token);
		}
		return tokenOnlyGivenLengthList;
	}
	
	public static List<String> keepOnlyBigramsFromList(List<String> tokenList){
		List<String> tokenOnlyBiList = new ArrayList<String>();
		for(String token:tokenList){
			if(token.contains(" "))
				tokenOnlyBiList.add(token);
		}
		return tokenOnlyBiList;
	}
	public static List<String> keepOnlyUnigramsFromList(List<String> tokenList){
		List<String> tokenOnlyBiList = new ArrayList<String>();
		for(String token:tokenList){
			if(!token.contains(" "))
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
	
	public static void mapTokenListToNGramList(Collection<String> tokenList,String date, String field,List<NGram> ngramList) throws IOException{
		for(String token:tokenList){
			NGram newNGram = new NGram(token,field);
			int indexOfNgram = ngramList.indexOf(newNGram);
			if(indexOfNgram != -1){
				NGram ngram =ngramList.get(indexOfNgram);
				ngram.increaseTFpeakByone();
				ngram.addDateDocFrequency(date);
			}
			else{
				ngramList.add(newNGram);
				newNGram.setTf_query_peak(1);
				newNGram.addDateDocFrequency(date);
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

