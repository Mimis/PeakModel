package org.peakModel.java.peakModel.document_process;

import java.util.List;
import java.util.Map;

import org.peakModel.java.peakModel.NGram;

public class AssignBurstyWeightToDocument {

	public static void AssignBurstyWeight(List<NGram> ngramList,KbDocument document){
		Map<String,Integer> tokenMap = document.getTokenMap();
		for(Map.Entry<String,Integer> entry:tokenMap.entrySet()){
			String currentToken = entry.getKey();
			NGram newNGram = new NGram(currentToken,"title");
			int indexOfNgram = ngramList.indexOf(newNGram);
			if(indexOfNgram != -1){
				NGram ngram =ngramList.get(indexOfNgram);
				if(ngram.isBurstyOnPeakDate())
					document.addNgramBurstyList(newNGram);
			}
		}
	}
}
