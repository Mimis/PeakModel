package org.peakModel.java.peakModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KbDocument {
	
	private final int id;
	private final String title;
	private final String date;
	private final String url;

	private final double score;
	private final Map<String,Integer> tokenMap;
	private double cosineSimilarity;
	private Set<NGram> ngramHitsList;

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
		this.tokenMap = mapListToMapWithTF(tokenList);
		this.cosineSimilarity = 0.0;
		this.score = score;
	}
	private Map<String,Integer> mapListToMapWithTF(List<String> tokenList){
		Map<String,Integer> tokenMap = new HashMap<String,Integer>();
		for(String token:tokenList){
			if(tokenMap.containsKey(token)){
				int tf = tokenMap.get(token);
				tokenMap.put(token, tf+1);
			}
			else
				tokenMap.put(token, 1);
		}
		return tokenMap;
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
	 * @return the tokenMap
	 */
	public Map<String,Integer> getTokenMap() {
		return tokenMap;
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

	

}
