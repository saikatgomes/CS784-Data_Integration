package com.walmart.productgenome.matching.models.rules.functions;

import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;
import com.wcohen.ss.MongeElkan;

public class MongeElkanSimilarity extends Function {

	public static final String NAME = "MONGE_ELKAN";
	public static final String DESCRIPTION = "Normalised Monge Elkan Similarity";
	public static final int NUM_ARGS = 2;
	
	public MongeElkanSimilarity() {
		super(NAME, DESCRIPTION);
	}

	public MongeElkanSimilarity(String name, String description) {
		super(name, description);
	}

	@Override
	public Object compute(String[] args) throws IllegalArgumentException {
		if(args.length != NUM_ARGS){
			throw new IllegalArgumentException("Expected number of arguments: " + NUM_ARGS);
		}
		MongeElkan me = new MongeElkan();
		me.setScaling(true);
		return (float) me.score(args[0], args[1]);
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
