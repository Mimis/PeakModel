package org.peakModel.java.lucene.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		List<LanguageModel> languageModelList = new ArrayList<LanguageModel>();
		String initialNgram = "a b c d e f";
		
		
	}
	

	public static void calculateLogLikelihoofBasedOnBackOffModel(NGram ngram,List<LanguageModel> languageModelList){
		double finalLogLikelihood = 0.0;
		finalLogLikelihood = ngram.getLOG_Likelyhood_burst();
		
		String unigrams[] = ngram.getNgram().split(" ");
		for(int ngramLevel=unigrams.length-1;ngramLevel>0;ngramLevel--){
			LanguageModel langModel = languageModelList.get(languageModelList.indexOf(new LanguageModel(ngramLevel)));
			finalLogLikelihood += getLogLikeihoodOfSubNgrams(unigrams, langModel, ngramLevel);
		}
		ngram.setLOG_Likelyhood_burst(finalLogLikelihood);
	}
	
	public static double getLogLikeihoodOfSubNgrams(String unigrams[],LanguageModel langModel,int ngramLevel){
		double log_likelihood = 0.0;
		for(String ngramText:createNgrams(unigrams,ngramLevel)){
			NGram ngram = langModel.getNgram(ngramText, "title");
			log_likelihood += ngram.getLOG_Likelyhood_burst();
		}
		return log_likelihood;
	}
	
	public static List<String> createNgrams(String unigrams[],int ngramLevel){
		List<String> ngramList = new ArrayList<String>();
		for(int i=0;i<unigrams.length;i++){
			StringBuilder buf = new StringBuilder();
			buf.append(unigrams[i]);
			int lastIndex = i+ngramLevel <= unigrams.length ? i+ngramLevel : -1;
			if(lastIndex==-1)break;
			for(int y=i+1;y<lastIndex;y++){
				buf.append(" "+unigrams[y]);
			}
			ngramList.add(buf.toString());
		}
		return ngramList;
	}

}
