package com.walmart.productgenome.matching.models.rules.functions;

import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

public class JaroWinklerSimilarityFunction extends Function {
	
	public static final String NAME = "JARO_WINKLER_SIMILARITY";
	public static final String DESCRIPTION = "Jaro Winkler similarity";
	public static final int NUM_ARGS = 2;
	
	public JaroWinklerSimilarityFunction() {
		super(NAME, DESCRIPTION);
	}

	public JaroWinklerSimilarityFunction(String name, String description){
		super(name, description);
	}
	
	@Override
	public Float compute(String[] args) throws IllegalArgumentException{
		if(args.length != NUM_ARGS){
			throw new IllegalArgumentException("Expected number of arguments: " + NUM_ARGS);
		}
		// TODO: Sanjib review.
    if (args[0] == null || args[1] == null) {
      return 0.0f;
    }
    if (args[0].toLowerCase().equals("null") || args[1].toLowerCase().equals("null")) {
      return 0.0f;
    }
    if (args[0].isEmpty() || args[1].isEmpty()) {
      return 0.0f;
    }
    
    String newArg0 = args[0].toLowerCase();
    String newArg1 = args[1].toLowerCase();
    
		AbstractStringMetric metric = new JaroWinkler();
		return metric.getSimilarity(newArg0, newArg1);
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
