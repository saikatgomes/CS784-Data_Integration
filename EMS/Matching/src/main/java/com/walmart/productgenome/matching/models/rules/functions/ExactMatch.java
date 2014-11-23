package com.walmart.productgenome.matching.models.rules.functions;

import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;

public class ExactMatch extends Function {

	public static final String NAME = "EXACT_MATCH";
	public static final String DESCRIPTION = "Returns 1 if two strings match otherwise 0";
	public static final int NUM_ARGS = 2;
	
	public ExactMatch() {
		super(NAME, DESCRIPTION);
	}

	public ExactMatch(String name, String description) {
		super(name, description);
	}

	@Override
	public Object compute(String[] args) throws IllegalArgumentException {
		if(args.length != NUM_ARGS){
			throw new IllegalArgumentException("Expected number of arguments: " + NUM_ARGS);
		}
		Float res = null;
		// TODO Sanjib: What is the default behavior when either of the arguments
		// are null? I am assuming that we will return 0 similarity score.
		if (args[0] == null || args[1] == null) {
		  res = 0.0f;
		}
		else if(args[0].equals(args[1])) {
			res = 1.0f;
		}
		else {
			res = 0.0f;
		}
		return res;
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
