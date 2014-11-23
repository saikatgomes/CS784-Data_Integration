package com.walmart.productgenome.matching.models.rules.functions;

import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;


// TODO: Sanjib..What is the logic behind this??

public class NormAbsDiff extends Function{

	public static final String NAME = "NORM_ABS_DIFF";
	public static final String DESCRIPTION = "Normalised Absolute Difference";
	public static final int NUM_ARGS = 2;
	
	public NormAbsDiff() {
		super(NAME, DESCRIPTION);
	}

	public NormAbsDiff(String name, String description){
		super(name, description);
	}
	
	@Override
	public Float compute(String[] args) throws IllegalArgumentException {
		if(args.length != 2){
			throw new IllegalArgumentException("Expected number of arguments: 2");
		}
		// TODO: Sanjib: review
		if (args[0] == null || args[1] == null) {
			return 0.0f;
		}
		Float res = null;
		try{
			Float f1 = Float.parseFloat(args[0]);
			Float f2 = Float.parseFloat(args[1]);
			Float min = 0.0f;
			if(f1 == 0.0f && f2 != 0.0f)
				min = f2;
			else if(f1 != 0.0f && f2 == 0.0f)
				min = f1;
			else if(f1 != 0.0f && f2 != 0.0f)
				min = Math.min(f1,f2);
			else{
				res = 0.0f;
				return res;
			}
			res = Math.abs(f1-f2)/min;
		}
		catch(NumberFormatException nfe){
			res = Float.NaN;
		}
		return res;
	}

  @Override
  public ArgType getArgType() {
    return ArgType.NUMERIC;
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
		sb.append(Float.class.getName());
		sb.append(",");
		sb.append(Float.class.getName());
		return sb.toString();
	}
	
}
