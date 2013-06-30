package org.peakModel.java.peakModel;

import java.util.Comparator;

import org.peakModel.java.utils.Helper;


public class NGram {
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	

	//variables
	private String ngram;
	private int tf_query_peak;
	private int tf_peak;
	private int tf_corpus;
	private String field;
	//probabilities
	private double P_w;
	private double P_w_Given_query_peak;
	private double P_w_Given_time; 
	//statistical measures
	private double PMI_corpus;
	private double PMI_peak;
	private double PMI_peak_times_tf_query_peak;
	private double PMI_corpus_times_tf_query_peak;
	private double LOG_Likelyhood_corpus;
	private double LOG_Likelyhood_peak;
	private double PointwiseKL_corpus;
	private double PointwiseKL_peak;
	private double PointwiseKL_peak_corpus;

	/**
	 * @param ngram
	 * @param field
	 */
	public NGram(String ngram, String field) {
		super();
		this.ngram = ngram;
		this.field = field;
	}
	
	public void increaseTFpeakByone() {
		this.tf_query_peak += 1;
	}

	
	
	
	/**
	 * @return the pointwiseKL_corpus
	 */
	public double getPointwiseKL_corpus() {
		return PointwiseKL_corpus;
	}

	/**
	 * @param pointwiseKL_corpus the pointwiseKL_corpus to set
	 */
	public void setPointwiseKL_corpus(double pointwiseKL_corpus) {
		PointwiseKL_corpus = pointwiseKL_corpus;
	}

	/**
	 * @return the pointwiseKL_peak
	 */
	public double getPointwiseKL_peak() {
		return PointwiseKL_peak;
	}

	/**
	 * @param pointwiseKL_peak the pointwiseKL_peak to set
	 */
	public void setPointwiseKL_peak(double pointwiseKL_peak) {
		PointwiseKL_peak = pointwiseKL_peak;
	}

	/**
	 * @return the pointwiseKL_peak_corpus
	 */
	public double getPointwiseKL_peak_corpus() {
		return PointwiseKL_peak_corpus;
	}

	/**
	 * @param pointwiseKL_peak_corpus the pointwiseKL_peak_corpus to set
	 */
	public void setPointwiseKL_peak_corpus(double pointwiseKL_peak_corpus) {
		PointwiseKL_peak_corpus = pointwiseKL_peak_corpus;
	}

	/**
	 * @return the lOG_Likelyhood_corpus
	 */
	public double getLOG_Likelyhood_corpus() {
		return LOG_Likelyhood_corpus;
	}

	/**
	 * @param lOG_Likelyhood_corpus the lOG_Likelyhood_corpus to set
	 */
	public void setLOG_Likelyhood_corpus(double lOG_Likelyhood_corpus) {
		LOG_Likelyhood_corpus = lOG_Likelyhood_corpus;
	}

	/**
	 * @param pMI_corpus the pMI_corpus to set
	 */
	public void setPMI_corpus(double pMI_corpus) {
		PMI_corpus = pMI_corpus;
	}

	/**
	 * @return the pMI_peak_times_tf_query_peak
	 */
	public double getPMI_peak_times_tf_query_peak() {
		return PMI_peak_times_tf_query_peak;
	}

	/**
	 * @param pMI_peak_times_tf_query_peak the pMI_peak_times_tf_query_peak to set
	 */
	public void setPMI_peak_times_tf_query_peak(double pMI_peak_times_tf_query_peak) {
		PMI_peak_times_tf_query_peak = pMI_peak_times_tf_query_peak;
	}

	/**
	 * @return the p_w_Given_query_peak
	 */
	public double getP_w_Given_query_peak() {
		return P_w_Given_query_peak;
	}

	/**
	 * @param p_w_Given_query_peak the p_w_Given_query_peak to set
	 */
	public void setP_w_Given_query_peak(double p_w_Given_query_peak) {
		P_w_Given_query_peak = p_w_Given_query_peak;
	}

	/**
	 * @return the pMI_peak
	 */
	public double getPMI_peak() {
		return PMI_peak;
	}

	/**
	 * @param pMI_peak the pMI_peak to set
	 */
	public void setPMI_peak(double pMI_peak) {
		PMI_peak = pMI_peak;
	}

	/**
	 * @return the tf_peak
	 */
	public int getTf_peak() {
		return tf_peak;
	}

	/**
	 * @param tf_peak the tf_peak to set
	 */
	public void setTf_peak(int tf_peak) {
		this.tf_peak = tf_peak;
	}

	/**
	 * @return the tf_corpus
	 */
	public int getTf_corpus() {
		return tf_corpus;
	}

	/**
	 * @param tf_corpus the tf_corpus to set
	 */
	public void setTf_corpus(int tf_corpus) {
		this.tf_corpus = tf_corpus;
	}

	/**
	 * @return the tf_query_peak
	 */
	public int getTf_query_peak() {
		return tf_query_peak;
	}

	/**
	 * @param tf_query_peak the tf_query_peak to set
	 */
	public void setTf_query_peak(int tf_query_peak) {
		this.tf_query_peak = tf_query_peak;
	}

	
	/**
	 * @return the p_w
	 */
	public double getP_w() {
		return this.P_w;
	}

	/**
	 * @param p_w the p_w to set
	 */
	public void setP_w(double p_w) {
		this.P_w = p_w;
	}

	
	/**
	 * @return the p_w_Given_time
	 */
	public double getP_w_Given_time() {
		return this.P_w_Given_time;
	}

	/**
	 * @param p_w_Given_time the p_w_Given_time to set
	 */
	public void setP_w_Given_time(double p_w_Given_time) {
		this.P_w_Given_time = p_w_Given_time;
	}


	
	/**
	 * @return the pMI_corpus
	 */
	public double getPMI_corpus() {
		return this.PMI_corpus;
	}

	/**
	 * @param pMI_classic the pMI_corpus to set
	 */
	public void setPMI_classic(double PMI_corpus) {
		this.PMI_corpus = PMI_corpus;
	}

	/**
	 * @return the ngram
	 */
	public String getNgram() {
		return this.ngram;
	}
	/**
	 * @param ngram the ngram to set
	 */
	public void setNgram(String ngram) {
		this.ngram = ngram;
	}
	
	/**
	 * @return the field
	 */
	public String getField() {
		return this.field;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	
	
	
	/**
	 * @return the lOG_Likelyhood_peak
	 */
	public double getLOG_Likelyhood_peak() {
		return LOG_Likelyhood_peak;
	}

	/**
	 * @param lOG_Likelyhood_peak the lOG_Likelyhood_peak to set
	 */
	public void setLOG_Likelyhood_peak(double lOG_Likelyhood_peak) {
		LOG_Likelyhood_peak = lOG_Likelyhood_peak;
	}

	/**
	 * @return the pMI_classic_times_tf_query_peak
	 */
	public double getPMI_corpus_times_tf_query_peak() {
		return PMI_corpus_times_tf_query_peak;
	}

	/**
	 * @param pMI_classic_times_tf_query_peak the pMI_classic_times_tf_query_peak to set
	 */
	public void setPMI_corpus_times_tf_query_peak(
			double pMI_corpus_times_tf_query_peak) {
		PMI_corpus_times_tf_query_peak = pMI_corpus_times_tf_query_peak;
	}
	
	
	
	
	
	
	
	

	//========================================================  PROBABILITIES ======================================================================
	//P(w) = tf_w_corpus / N_corpus
	//P(w|query,peak) = tf_w_query_peak / |D|query
	//P(w|time_peak) = df_w_time_peak / |D|time_peak
	public void calculateProbabilities(long N_corpus, long N_query_peakPeriod,long N_peak){
		this.P_w = (double) this.tf_corpus / N_corpus;
		this.P_w_Given_time = (double) this.tf_peak / N_peak;
		this.P_w_Given_query_peak = (double) this.tf_query_peak / N_query_peakPeriod;
	}
	
	
	/**
	 * Calculate PMI_corpus  as below:
	 * 	PMI(w|query,peak) = log P(w|query,peak) - log P(w)
	 */
	public void computePMIcorpus() {
		this.PMI_corpus = Helper.log2(this.P_w_Given_query_peak) - Helper.log2(this.P_w);
	}
	
	/**
	 * Calculate PMI on Peak period as below:
	 * 	PMI(w|query,peak) = log P(w|query,peak) - log P(w|time_peak)
	 */
	public void computePMIpeak() {
		this.PMI_peak = Helper.log2(this.P_w_Given_query_peak) - Helper.log2(this.P_w_Given_time);
	}
	
	/**
	 * Calculate PMI on Peak period multiply by tf_query_peak:
	 * 	PMI(w|query,peak) = (log P(w|query,peak) - log P(w|time_peak)) * log(tf_query_peak)
	 */
	public void computePMIpeakTimesTf_query_peak() {
		this.PMI_peak_times_tf_query_peak = (Helper.log2(this.P_w_Given_query_peak) - Helper.log2(this.P_w_Given_time)) * Helper.log2(this.tf_query_peak);
	}
	
	/**
	 * Calculate PMI on Corpus period multiply by tf_query_peak:
	 * 	PMI(w|query,peak) = (log P(w|query,peak) - log P(w|time_peak)) * log(tf_query_peak)
	 */
	public void computePMI_corpus_times_tf_query_peak() {
		this.PMI_corpus_times_tf_query_peak = (Helper.log2(this.P_w_Given_query_peak) - Helper.log2(this.P_w)) * Helper.log2(this.tf_query_peak);
	}
	
	
	/**
	 * Calculate LOG_Likelyhood_corpus as below:
	 * 	LOG_Likelyhood_classic(w|query,peak) => 
	 * G2 = 2(a log(a) + b log(b) + c log(c) + d log(d)− (a + b)log(a + b) – (a + c)log(a + c)− (b + d)log(b + d) – (c + d)log(c + d)+ (a + b + c + d)log(a + b + c + d))
	 */
	public void computeLOGlikelyhoodCorpus(long N_query_peakPeriod,long N_corpus) {
		int a = this.tf_query_peak;
		int b = this.tf_corpus;
		long c = N_query_peakPeriod - a;
		long d = N_corpus - b;
		this.LOG_Likelyhood_corpus = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));
		
	}
	/**
	 * Calculate LOG_Likelyhood_peak as below:
	 * 	LOG_Likelyhood_classic(w|query,peak) => 
	 * G2 = 2(a log(a) + b log(b) + c log(c) + d log(d)− (a + b)log(a + b) – (a + c)log(a + c)− (b + d)log(b + d) – (c + d)log(c + d)+ (a + b + c + d)log(a + b + c + d))
	 */
	public void computeLOGlikelyhoodPeak(long N_query_peakPeriod,long N_peak) {
		int a = this.tf_query_peak;
		int b = this.tf_peak;
		long c = N_query_peakPeriod - a;
		long d = N_peak - b;
		this.LOG_Likelyhood_peak = 2 * (a * Helper.log2(a) + b * Helper.log2(b) + c * Helper.log2(c) + d * Helper.log2(d) - (a + b) * Helper.log2(a + b) - (a + c) * Helper.log2(a + c) - (b + d) * Helper.log2(b + d) - (c + d) * Helper.log2(c + d)+ (a + b + c + d)* Helper.log2(a + b + c + d));		
	}


	/**
	 * Calculate LOG_Likelyhood_peak as below:
	 * 	LOG_Likelyhood_classic(w|query,peak) => G2 = 2 P(w|N_query_peakPeriod) * ln (P(w|N_query_peakPeriod) / P(w|N_peak))
	 */
	public void computeLOGlikelyhoodPeak2(long N_query_peakPeriod,long N_peak) {
		this.LOG_Likelyhood_peak = 2 * this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w_Given_time) ;		
	}


	/**
	 * Calculate LOG_Likelyhood_corpus as below:
	 * 	LOG_Likelyhood_classic(w|query,peak) => G2 = 2 P(w|N_query_peakPeriod) * ln (P(w|N_query_peakPeriod) / P(w|N_corpus))
	 */
	public void computeLOGlikelyhoodCorpus2(long N_query_peakPeriod,long N_corpus) {
		this.LOG_Likelyhood_corpus = 2 * this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w) ;
	}


	/**
	 * Calculate PointwiseKL_peak as below:
	 * 	PointwiseKL_peak(w|query,peak) => P(w|N_query_peakPeriod) * log (P(w|N_query_peakPeriod) / P(w|N_peak))
	 */
	public void computePointwiseKLPeak() {
		this.PointwiseKL_peak = this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w_Given_time) ;		
	}


	/**
	 * Calculate PointwiseKL_corpus as below:
	 * 	PointwiseKL_corpus(w|query,peak) => P(w|N_query_peakPeriod) * log (P(w|N_query_peakPeriod) / P(w|N_corpus))
	 */
	public void computePointwiseKLCorpus() {
		this.PointwiseKL_corpus = this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w) ;
	}

	
	/**
	 * Calculate PointwiseKL_corpus_peak as below:
	 * 	PointwiseKL_corpus(w|query,peak) => P(w|N_query_peakPeriod) * log (P(w|N_query_peakPeriod) / P(w|N_corpus))
	 */
	public void computePointwiseKLCorpusPeak() {
		this.PointwiseKL_peak_corpus = 0.75 * (this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w)) + 
									   0.25 * (this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w_Given_time));
		
//		this.PointwiseKL_peak_corpus = this.P_w_Given_query_peak * Helper.log2(this.P_w_Given_query_peak / this.P_w)  * Helper.log2(this.P_w_Given_query_peak / this.P_w_Given_time);

	}
	
	//========================================================  END PROBABILITIES ======================================================================
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String toStringCompact() {
		return ngram + "(" + tf_query_peak + "," + tf_peak  + "," + tf_corpus + ")";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ngram + "," + tf_query_peak + "," + tf_peak + "," + tf_corpus + "," + field + "," + P_w + "," + P_w_Given_query_peak	+ "," + P_w_Given_time + ","
				+ PMI_corpus + "," + PMI_peak + ","	+ PMI_peak_times_tf_query_peak	+ "," + PMI_corpus_times_tf_query_peak + ","
				+ LOG_Likelyhood_corpus + "," + LOG_Likelyhood_peak ;
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NGram))
			return false;
		NGram other = (NGram) obj;
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
	public static Comparator<NGram> COMPARATOR_TOTAL_TF = new Comparator<NGram>()
    {
        public int compare(NGram o1, NGram o2){
            return o2.tf_query_peak - o1.tf_query_peak;
        }
    };

    /**
	 * Sort by PMI_peak_times_tf_query_peak
	 */
	public static Comparator<NGram> COMPARATOR_PMI_PEAK_TIMES_TF = new Comparator<NGram>()
    {
        public int compare(NGram o1, NGram o2){
            if(o2.PMI_peak_times_tf_query_peak > o1.PMI_peak_times_tf_query_peak )
            	return 1;
            else if(o2.PMI_peak_times_tf_query_peak < o1.PMI_peak_times_tf_query_peak )
            	return 0;
            else 
            	return 0;
        }
    };
    
    
    /**
	 * Sort by PMI_classic_times_tf_query_peak
	 */
	public static Comparator<NGram> COMPARATOR_PMI_CORPUS_TIMES_TF = new Comparator<NGram>()
    {
        public int compare(NGram o1, NGram o2){
            if(o2.PMI_corpus_times_tf_query_peak > o1.PMI_corpus_times_tf_query_peak )
            	return 1;
            else if(o2.PMI_corpus_times_tf_query_peak < o1.PMI_corpus_times_tf_query_peak )
            	return 0;
            else 
            	return 0;
        }
    };
    
	/**
	 * Sort by PMI classic
	 */
	public static Comparator<NGram> COMPARATOR_PMI_CORPUS = new Comparator<NGram>()
    {
        public int compare(NGram o1, NGram o2){
            if(o2.PMI_corpus > o1.PMI_corpus )
            	return 1;
            else if(o2.PMI_corpus < o1.PMI_corpus )
            	return 0;
            else 
            	return 0;
        }
    };
    
    /**
	 * Sort by PMI_peak_
	 */
	public static Comparator<NGram> COMPARATOR_PMI_PEAK = new Comparator<NGram>()
    {
        public int compare(NGram o1, NGram o2){
            if(o2.PMI_peak > o1.PMI_peak )
            	return 1;
            else if(o2.PMI_peak < o1.PMI_peak )
            	return 0;
            else 
            	return 0;
        }
    };

    /**
     * SORT BY LOG CLASSIC
     */
    public static Comparator<NGram> COMPARATOR_LOG_CORPUS = new Comparator<NGram>()
    {
    	 public int compare(NGram o1, NGram o2){
             if(o2.LOG_Likelyhood_corpus > o1.LOG_Likelyhood_corpus )
             	return 1;
             else if(o2.LOG_Likelyhood_corpus < o1.LOG_Likelyhood_corpus )
             	return 0;
             else 
             	return 0;
         }
    };
    
    /**
     * SORT BY LOG PEAK
     */
    public static Comparator<NGram> COMPARATOR_LOG_PEAK = new Comparator<NGram>()
    {
    	 public int compare(NGram o1, NGram o2){
             if(o2.LOG_Likelyhood_peak > o1.LOG_Likelyhood_peak )
             	return 1;
             else if(o2.LOG_Likelyhood_peak < o1.LOG_Likelyhood_peak )
             	return 0;
             else 
             	return 0;
         }
    };

    /**
     * SORT BY Pointwise KL peak
     */
    public static Comparator<NGram> COMPARATOR_POINTWISE_KL_PEAK = new Comparator<NGram>()
    {
    	 public int compare(NGram o1, NGram o2){
             if(o2.PointwiseKL_peak > o1.PointwiseKL_peak )
             	return 1;
             else if(o2.PointwiseKL_peak < o1.PointwiseKL_peak )
             	return 0;
             else 
             	return 0;
         }
    };
    
    /**
     * SORT BY Pointwise KL corpus
     */
    public static Comparator<NGram> COMPARATOR_POINTWISE_KL_CORPUS = new Comparator<NGram>()
    {
    	 public int compare(NGram o1, NGram o2){
             if(o2.PointwiseKL_corpus > o1.PointwiseKL_corpus )
             	return 1;
             else if(o2.PointwiseKL_corpus < o1.PointwiseKL_corpus )
             	return 0;
             else 
             	return 0;
         }
    };

    /**
     * SORT BY Pointwise KL corpus
     */
    public static Comparator<NGram> COMPARATOR_POINTWISE_KL_PEAK_CORPUS = new Comparator<NGram>()
    {
    	 public int compare(NGram o1, NGram o2){
             if(o2.PointwiseKL_peak_corpus > o1.PointwiseKL_peak_corpus )
             	return 1;
             else if(o2.PointwiseKL_peak_corpus < o1.PointwiseKL_peak_corpus )
             	return 0;
             else 
             	return 0;
         }
    };

}