package models.rules;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;

import com.wcohen.ss.MongeElkan;

public enum Function {
	BLOCK_DISTANCE, 
	CHAPMAN_LENGTH_DEVIATION, 
	CHAPMAN_MEAN_LENGTH, 
	CHAPMAN_ORDERED_NAME_COMPOUND_SIMILARITY,
	COSINE,
	DICE, 
	EUCLIDEAN,
	EXACT_MATCH,
	JACCARD,
	JARO,
	JARO_WINKLER,
	LEVENSHTEIN,
	MATCHING_COEFFICIENT,
	MONGE_ELKAN,
	NEEDLEMAN_WUNCH,
	OVERLAP_COEFFICIENT,
	QGRAM,
	SMITH_WATERMAN,
	SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE,
	SOUNDEX,
	TAG_LINK,
	TAG_LINK_TOKEN,
	NUM_SCORE,
	IS_NULL;
	
	public float getValue(String s1, String s2){
		AbstractStringMetric metric = null;
		float res = Float.NaN;
		switch(this){
		case COSINE:
			metric = new CosineSimilarity();
			break;
		case JACCARD:
			metric = new JaccardSimilarity();
			break;
		case JARO:
			metric = new Jaro();
			break;
		case JARO_WINKLER:
			metric = new JaroWinkler();
			break;
		case LEVENSHTEIN:
			metric = new Levenshtein();
			break;
		case MONGE_ELKAN:
			metric = null;
			MongeElkan me = new MongeElkan();
			me.setScaling(true);
			res = (float) me.score(s1,s2);
			break;
		case QGRAM:
			metric = new QGramsDistance();
			break;
		case SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE:
			metric = new SmithWatermanGotohWindowedAffine();
			break;
		case NUM_SCORE:
			try{
				Float f1 = Float.parseFloat(s1);
				Float f2 = Float.parseFloat(s2);
				Float min = 0.0f;
				if(f1 == 0.0f && f2 != 0.0f)
					min = f2;
				else if(f1 != 0.0f && f2 == 0.0f)
					min = f1;
				else if(f1 != 0.0f && f2 != 0.0f)
					min = Math.min(f1,f2);
				else{
					res = 0.0f;
					break;
				}
				res = Math.abs(f1-f2)/min;
			}
			catch(NumberFormatException nfe){
				//do nothing
			}
			break;
		case EXACT_MATCH:
			if(s1.equals(s2))
				res = 1.0f;
			else
				res = 0.0f;
			break;
		}
		if(null != metric)
			res = metric.getSimilarity(s1, s2);
		return res;
	}
}
