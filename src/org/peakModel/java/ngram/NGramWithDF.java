package org.peakModel.java.ngram;

import java.util.Comparator;

import org.peakModel.java.utils.Helper;


public class NGramWithDF {
	//variables
	private String ngram;
	private int total_tf_query;
	private int df_query;
	private int df_corpus;
	private int df_time;
	private String field;
	//probabilities
	private double P_w;
	private double P_w_Given_query;
	private double P_w_Given_time; 
	//statistical measures
	private double PMI_classic;
	private double PMI_time;
	
	/**
	 * @param ngram
	 * @param field
	 */
	public NGramWithDF(String ngram, String field) {
		super();
		this.ngram = ngram;
		this.field = field;
	}
	
	public void addTotal_tf_query(int total_tf_query){
		this.total_tf_query += total_tf_query;
	}
	
	
	/**
	 * @return the pMI_time
	 */
	public double getPMI_time() {
		return PMI_time;
	}

	/**
	 * @param pMI_time the pMI_time to set
	 */
	public void setPMI_time(double pMI_time) {
		PMI_time = pMI_time;
	}

	/**
	 * @return the p_w
	 */
	public double getP_w() {
		return P_w;
	}

	/**
	 * @param p_w the p_w to set
	 */
	public void setP_w(double p_w) {
		P_w = p_w;
	}

	/**
	 * @return the p_w_Given_query
	 */
	public double getP_w_Given_query() {
		return P_w_Given_query;
	}

	/**
	 * @param p_w_Given_query the p_w_Given_query to set
	 */
	public void setP_w_Given_query(double p_w_Given_query) {
		P_w_Given_query = p_w_Given_query;
	}

	/**
	 * @return the p_w_Given_time
	 */
	public double getP_w_Given_time() {
		return P_w_Given_time;
	}

	/**
	 * @param p_w_Given_time the p_w_Given_time to set
	 */
	public void setP_w_Given_time(double p_w_Given_time) {
		P_w_Given_time = p_w_Given_time;
	}

	/**
	 * @return the df_query
	 */
	public int getDf_query() {
		return df_query;
	}

	/**
	 * @param df_query the df_query to set
	 */
	public void setDf_query(int df_query) {
		this.df_query = df_query;
	}

	public void increaseByOneDf_query() {
		this.df_query += 1;
	}

	
	/**
	 * @return the pMI_classic
	 */
	public double getPMI_classic() {
		return PMI_classic;
	}

	/**
	 * @param pMI_classic the pMI_classic to set
	 */
	public void setPMI_classic(double pMI_classic) {
		PMI_classic = pMI_classic;
	}

	/**
	 * @return the ngram
	 */
	public String getNgram() {
		return ngram;
	}
	/**
	 * @param ngram the ngram to set
	 */
	public void setNgram(String ngram) {
		this.ngram = ngram;
	}
	
	/**
	 * @return the total_tf_query
	 */
	public int getTotal_tf_query() {
		return total_tf_query;
	}
	/**
	 * @param total_tf_query the total_tf_query to set
	 */
	public void setTotal_tf_query(int total_tf_query) {
		this.total_tf_query = total_tf_query;
	}
	/**
	 * @return the df_corpus
	 */
	public int getDf_corpus() {
		return df_corpus;
	}
	/**
	 * @param df_corpus the df_corpus to set
	 */
	public void setDf_corpus(int df_corpus) {
		this.df_corpus = df_corpus;
	}
	/**
	 * @return the df_time
	 */
	public int getDf_time() {
		return df_time;
	}
	/**
	 * @param df_time the df_time to set
	 */
	public void setDf_time(int df_time) {
		this.df_time = df_time;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	
	//P(w) = df_w_corpus / |D|corpus
	//P(w|query) = df_w_query / |D|query
	//P(w|time_peak) = df_w_time_peak / |D|time_peak
	public void calculateProbabilities(int totalNumberOfDocuments, int totalNumberOfRelevantDocuments,int totalNumberOfDocumentsInGivenPeriod){
		this.P_w = (double) this.df_corpus / totalNumberOfDocuments;
		this.P_w_Given_query = (double) this.df_query / totalNumberOfRelevantDocuments;
		this.P_w_Given_time = (double) this.df_time / totalNumberOfDocumentsInGivenPeriod;
	}
	
	
	/**
	 * Calculate classic PMI as below:
	 * 	PMI(w|query) = log P(w|query) - log P(w)
	 */
	public void computePMIClasic() {
		this.PMI_classic = Helper.log2(this.P_w_Given_query) - Helper.log2(this.P_w);
	}
	
	/**
	 * Calculate PMI on Peak period as below:
	 * 	PMI(w|query) = log P(w|query) - log P(w|time_peak)
	 */
	public void computePMItime() {
		this.PMI_time = Helper.log2(this.P_w_Given_query) - Helper.log2(this.P_w_Given_time);
	}
	
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((ngram == null) ? 0 : ngram.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NGram [ngram=" + ngram + ", total_tf_query=" + total_tf_query
				+ ", df_query=" + df_query + ", df_corpus=" + df_corpus
				+ ", df_time=" + df_time + ", field=" + field + ", P_w=" + P_w
				+ ", P_w_Given_query=" + P_w_Given_query + ", P_w_Given_time="
				+ P_w_Given_time + ", PMI_classic=" + PMI_classic
				+ ", PMI_time=" + PMI_time + "]";
	}

	/**
	 * Now for each experiment, create a csv file with the top 100 terms, their TF , their log P(W|query, time_peak), 
	 * their log prior probability, their log prior probability in the peak period, and their PMI. Order them by TF and then PMI. 
	 */
	public String toStringCsvCompact() {
		return  ngram + "," + total_tf_query + "," + P_w + "," + P_w_Given_query + "," + P_w_Given_time + "," + PMI_classic + "," + PMI_time;	
	}

	public String toStringCsvAll() {
		return  ngram + "," + total_tf_query + "," + df_query + "," + df_corpus + "," + df_time + "," + P_w + "," + P_w_Given_query + "," + P_w_Given_time + "," + PMI_classic + "," + PMI_time;	
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
		if (!(obj instanceof NGramWithDF))
			return false;
		NGramWithDF other = (NGramWithDF) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (ngram == null) {
			if (other.ngram != null)
				return false;
		} else if (!ngram.equals(other.ngram))
			return false;
		return true;
	}
	
	//***** COMPARATORS sorting*****//
	/**
	 * Sort by Total Term Frequency in the result documents
	 */
	public static Comparator<NGramWithDF> COMPARATOR_TOTAL_TF = new Comparator<NGramWithDF>()
    {
        public int compare(NGramWithDF o1, NGramWithDF o2){
            return o2.total_tf_query - o1.total_tf_query;
        }
    };

	/**
	 * Sort by Total Term Frequency in the result documents
	 */
	public static Comparator<NGramWithDF> COMPARATOR_PMI_CLASSIC = new Comparator<NGramWithDF>()
    {
        public int compare(NGramWithDF o1, NGramWithDF o2){
            if(o2.PMI_classic > o1.PMI_classic )
            	return 1;
            else if(o2.PMI_classic < o1.PMI_classic )
            	return 0;
            else 
            	return 0;
        }
    };
    
    /**
	 * Sort by Total Term Frequency in the result documents
	 */
	public static Comparator<NGramWithDF> COMPARATOR_PMI_TIME = new Comparator<NGramWithDF>()
    {
        public int compare(NGramWithDF o1, NGramWithDF o2){
            if(o2.PMI_time > o1.PMI_time )
            	return 1;
            else if(o2.PMI_time < o1.PMI_time )
            	return 0;
            else 
            	return 0;
        }
    };

}
