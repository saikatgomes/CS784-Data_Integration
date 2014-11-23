package com.walmart.productgenome.matching.models.rules.functions;


public class NormAbsSim extends Function{

	public static final String NAME = "NORM_ABS_SIM";
	public static final String DESCRIPTION = "1 - Normalised Absolute Difference";
	public static final int NUM_ARGS = 2;
	
	public NormAbsSim() {
		super(NAME, DESCRIPTION);
	}

	public NormAbsSim(String name, String description){
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
      /*System.out.println("XXXXXXX");
		  System.out.println(args[0]);
      System.out.println(args[1]);*/

			Float f1 = Float.parseFloat(args[0]);
			Float f2 = Float.parseFloat(args[1]);
			
		    
			Float max = 0.0f;
			if(f1 == 0.0f && f2 != 0.0f)
				max = f2;
			else if(f1 != 0.0f && f2 == 0.0f)
				max = f1;
			else if(f1 != 0.0f && f2 != 0.0f)
				max = Math.max(f1,f2);
			else{
				res = 0.0f;
				return res;
			}
			res = (float)Math.abs(f1-f2)/max;
		}
		catch(NumberFormatException nfe){
			res = Float.NaN;
		}
		
		//System.out.println(1-res);
		return (1 - res);
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
		sb.append(Float.class.getName());
		sb.append(",");
		sb.append(Float.class.getName());
		return sb.toString();
	}
	
}
