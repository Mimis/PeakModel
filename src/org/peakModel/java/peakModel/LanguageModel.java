package org.peakModel.java.peakModel;

import java.util.Collections;
import java.util.List;

public class LanguageModel {
	private int ngramLength;
	private List<NGram> ngramList;
	private int totalNumberNgrams;
	private int totalNumberOfDocuments;
	private int totalNGramFrequency;
	
	
	
	/**
	 * @param ngramLength
	 * @param ngramList
	 */
	public LanguageModel(int ngramLength, List<NGram> ngramList, int totalNumberOfDocuments) {
		super();
		this.ngramLength = ngramLength;
		this.ngramList = ngramList;
		this.totalNumberNgrams = ngramList.size();
		this.totalNumberOfDocuments = totalNumberOfDocuments;
		for(NGram ng : ngramList){
			this.totalNGramFrequency += ng.getTf_query_peak();
			//calculate the probability to find this ngram if we select by random an item from the document set that we extract it
			ng.setP_w_language_model((double)ng.getTf_query_peak() / this.totalNumberOfDocuments);
		}
	}

	public LanguageModel(int ngramLength) {
		super();
		this.ngramLength = ngramLength;
	}

	/**
	 * @return the ngram that is equal to given one,otherwise null
	 */
	public NGram getNgram(String ngramText,String field) {
		int index = ngramList.indexOf(new NGram(ngramText,field));
		if(index == -1)
			return null;
		else
			return ngramList.get(index);
	}


	/**
	 * 
	 * @param topN
	 * @return list of top frequent Ngrams
	 */
	public List<NGram> getTopFrequentNgrams(int topN) {
		Collections.sort(this.ngramList,NGram.COMPARATOR_TOTAL_TF);
		return this.ngramList.subList(0, this.ngramList.size() < topN ? this.ngramList.size() : topN);
	}
	
	public List<NGram> getTopEntropyNgrams(int topN) {
		Collections.sort(this.ngramList,NGram.COMPARATOR_ENTROPY);
		return this.ngramList.subList(0, this.ngramList.size() < topN ? this.ngramList.size() : topN);
	}
	public List<NGram> getTopLogBurstNgrams(int topN) {
		Collections.sort(this.ngramList,NGram.COMPARATOR_LOG_LIKELIHOOD_BURST);
		return this.ngramList.subList(0, this.ngramList.size() < topN ? this.ngramList.size() : topN);
	}

	/**
	 * 
	 * @param topN
	 * @return list of top frequent Ngrams
	 */
	public List<NGram> getTopProbableNgrams(int topN) {
		Collections.sort(this.ngramList,NGram.COMPARATOR_PROBABILITY);
		return this.ngramList.subList(0, this.ngramList.size() < topN ? this.ngramList.size() : topN);
	}

	/**
	 * @return the totalNumberOfDocuments
	 */
	public int getTotalNumberOfDocuments() {
		return totalNumberOfDocuments;
	}

	/**
	 * @param totalNumberOfDocuments the totalNumberOfDocuments to set
	 */
	public void setTotalNumberOfDocuments(int totalNumberOfDocuments) {
		this.totalNumberOfDocuments = totalNumberOfDocuments;
	}

	/**
	 * @return the ngramLength
	 */
	public int getNgramLength() {
		return ngramLength;
	}
	/**
	 * @param ngramLength the ngramLength to set
	 */
	public void setNgramLength(int ngramLength) {
		this.ngramLength = ngramLength;
	}
	/**
	 * @return the ngramList
	 */
	public List<NGram> getNgramList() {
		return ngramList;
	}
	/**
	 * @param ngramList the ngramList to set
	 */
	public void setNgramList(List<NGram> ngramList) {
		this.ngramList = ngramList;
	}
	/**
	 * @return the totalNumberNgrams
	 */
	public int getTotalNumberNgrams() {
		return totalNumberNgrams;
	}
	/**
	 * @param totalNumberNgrams the totalNumberNgrams to set
	 */
	public void setTotalNumberNgrams(int totalNumberNgrams) {
		this.totalNumberNgrams = totalNumberNgrams;
	}
	/**
	 * @return the totalNGramFrequency
	 */
	public int getTotalNGramFrequency() {
		return totalNGramFrequency;
	}
	/**
	 * @param totalNGramFrequency the totalNGramFrequency to set
	 */
	public void setTotalNGramFrequency(int totalNGramFrequency) {
		this.totalNGramFrequency = totalNGramFrequency;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ngramLength;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LanguageModel))
			return false;
		LanguageModel other = (LanguageModel) obj;
		if (ngramLength != other.ngramLength)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LanguageModel [ngramLength=" + ngramLength
				+ ", totalNumberNgrams=" + totalNumberNgrams
				+ ", totalNumberOfDocuments=" + totalNumberOfDocuments
				+ ", totalNGramFrequency=" + totalNGramFrequency + "]";
	}

}
