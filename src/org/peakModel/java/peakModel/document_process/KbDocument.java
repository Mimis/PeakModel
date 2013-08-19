package org.peakModel.java.peakModel.document_process;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.peakModel.java.peakModel.NGram;

public class KbDocument {
	
	private final int id;
	private final String title;
	private final String date;
	private final String url;

	private final double score;
	private final Set<String> tokenSet; //set because we wanna only unique tokens;we count document frequency only
	private double cosineSimilarity;
	private Set<NGram> ngramHitsList;
	private Set<NGram> ngramBurstyList;

	/**
	 * @param id
	 * @param tokenList
	 * @param cosineSimilarity
	 */
	public KbDocument(int id,String title, List<String> tokenList,String date,String url,double score) {
		super();
		this.title = title;
		this.id = id;
		this.date = date;
		this.url = url;
		this.tokenSet = new HashSet<String>(tokenList);
		this.cosineSimilarity = 0.0;
		this.score = score;
		this.ngramBurstyList = new HashSet<NGram>();
	}
	
	
	
	/**
	 * @return the tokenSet
	 */
	public Set<String> getTokenSet() {
		return tokenSet;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @return the ngramHitsList
	 */
	public Set<NGram> getNgramHitsList() {
		return ngramHitsList;
	}
	/**
	 * @param ngramHitsList the ngramHitsList to set
	 */
	public void setNgramHitsList(Set<NGram> ngramHitsList) {
		this.ngramHitsList = ngramHitsList;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the cosineSimilarity
	 */
	public double getCosineSimilarity() {
		return cosineSimilarity;
	}
	/**
	 * @param cosineSimilarity the cosineSimilarity to set
	 */
	public void setCosineSimilarity(double cosineSimilarity) {
		this.cosineSimilarity = cosineSimilarity;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the ngramBurstyList
	 */
	public Set<NGram> getNgramBurstyList() {
		return ngramBurstyList;
	}
	/**
	 * @param add bursty ngram to the ngramBurstyList to set
	 */
	public void addNgramBurstyList(NGram ngram) {
		this.ngramBurstyList.add(ngram);
	}

	public static Comparator<KbDocument> COMPARATOR_COSINE = new Comparator<KbDocument>()
    {
        public int compare(KbDocument o1, KbDocument o2){
            if(o2.cosineSimilarity > o1.cosineSimilarity )
            	return 1;
            else if(o2.cosineSimilarity < o1.cosineSimilarity )
            	return 0;
            else 
            	return 0;
        }
    };

	public static Comparator<KbDocument> COMPARATOR_BURSTINESS = new Comparator<KbDocument>()
    {
        public int compare(KbDocument o1, KbDocument o2){
            if(o2.ngramBurstyList.size() > o1.ngramBurstyList.size() )
            	return 1;
            else if(o2.ngramBurstyList.size() < o1.ngramBurstyList.size() )
            	return 0;
            else 
            	return 0;
        }
    };
	

}