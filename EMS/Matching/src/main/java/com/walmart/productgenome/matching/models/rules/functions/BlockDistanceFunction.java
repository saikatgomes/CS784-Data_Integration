package com.walmart.productgenome.matching.models.rules.functions;

import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.BlockDistance;

import com.walmart.productgenome.matching.models.data.Attribute.Type;

public class BlockDistanceFunction extends Function {

	public static final String NAME = "BLOCK_DISTANCE";
	public static final String DESCRIPTION = "Normalised Block Distance";
	public static final int NUM_ARGS = 2;
	
	public BlockDistanceFunction() {
		super(NAME, DESCRIPTION);
	}

	public BlockDistanceFunction(String name, String description){
		super(name, description);
	}
		
	@Override
	public Float compute(String[] args) throws IllegalArgumentException{
		if(args.length != NUM_ARGS){
			throw new IllegalArgumentException("Expected number of arguments: " + NUM_ARGS);
		}
		AbstractStringMetric metric = new BlockDistance();
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
	
	public static void main(String[] args){
		Function bd = new BlockDistanceFunction();
		System.out.println(bd.getSignature());
	}

}
