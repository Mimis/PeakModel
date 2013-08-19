package org.peakModel.java.peakModel.document_process;

import java.util.List;
import java.util.Set;

import org.peakModel.java.peakModel.NGram;

public class AssignBurstyWeightToDocument {

	public static void AssignBurstyWeight(List<NGram> ngramList,KbDocument document){
		Set<String> tokenSet = document.getTokenSet();
		for(String token:tokenSet){
			String currentToken = token;
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
