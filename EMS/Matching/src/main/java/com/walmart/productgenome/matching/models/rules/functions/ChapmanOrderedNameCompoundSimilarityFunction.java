package com.walmart.productgenome.matching.models.rules.functions;

import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanOrderedNameCompoundSimilarity;

public class ChapmanOrderedNameCompoundSimilarityFunction extends Function {
	
	public static final String NAME = "CHAPMAN_ORDERED_NAME_COMPOUND_SIMILARITY";
	public static final String DESCRIPTION = "Chapman Ordered Name Compound Similarity";
	public static final int NUM_ARGS = 2;
	
	public ChapmanOrderedNameCompoundSimilarityFunction() {
		super(NAME, DESCRIPTION);
	}

	public ChapmanOrderedNameCompoundSimilarityFunction(String name, String description) {
		super(name, description);
	}
	
	@Override
	public Float compute(String[] args) throws IllegalArgumentException{
		if(args.length != NUM_ARGS){
			throw new IllegalArgumentException("Expected number of arguments: " + NUM_ARGS);
		}
		AbstractStringMetric metric = new ChapmanOrderedNameCompoundSimilarity();
		return metric.getSimilarity(args[0], args[1]);
	}

  @Override
  public ArgType getArgType() {
    return ArgType.STRING;
  }
  
	@Override
	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		sb.append(",");
		sb.append(this.getDescription());
		sb.append(",");
		sb.append(this.getClass().getName());
		sb.append(",");
		sb.append(Float.class.getName());
		sb.append(",");
		sb.append(NUM_ARGS);
		sb.append(",");
		sb.append(String.class.getName());
		sb.append(",");
		sb.append(String.class.getName());
		return sb.toString();
	}
}
