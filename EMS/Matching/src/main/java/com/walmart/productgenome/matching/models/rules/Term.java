package com.walmart.productgenome.matching.models.rules;

import java.io.IOException;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.RelationalOperator;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.audit.TermAudit;
import com.walmart.productgenome.matching.models.data.Tuple;

public class Term {

	private Feature feature1;
	private RelationalOperator relop;
	private float value;
	private Feature feature2;

	public Term(Feature feature, RelationalOperator relop, float value) {
		this.feature1 = feature;
		this.relop = relop;
		this.value = value;
	}

	public Term(Feature feature1, RelationalOperator relop, Feature feature2) {
		this.feature1 = feature1;
		this.relop = relop;
		this.feature2 = feature2;
	}
	
	public Feature getFeature1() {
	  return feature1;
	}
	
	public Feature getFeature2() {
    return feature2;
  }

	public RelationalOperator getRelop() {
		return relop;
	}

	public float getValue() {
		return value;
	}

	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2) throws IOException{
		float lvalue = feature1.compute(tuple1,tuple2);
		float rvalue = value;
		if(null != feature2){
			rvalue = feature2.compute(tuple1, tuple2);
		}
		if(lvalue == Float.NaN || rvalue == Float.NaN){
			return MatchStatus.DECLINE_TO_PREDICT;
		}
		else{	
			switch(relop){
			case LESS_THAN:
				if(lvalue < rvalue){
					return MatchStatus.MATCH;
				}
				break;
			case LESS_THAN_EQUALS:
				if(lvalue <= rvalue){
					return MatchStatus.MATCH;
				}
				break;
			case GREATER_THAN:
				if(lvalue > rvalue){
					return MatchStatus.MATCH;
				}
				break;
			case GREATER_THAN_EQUALS:
				if(lvalue >= rvalue){
					return MatchStatus.MATCH;
				}
				break;
			case EQUALS:
				if(lvalue == rvalue){
					return MatchStatus.MATCH;
				}
				break;
			}
			return MatchStatus.NON_MATCH;
		}
	}
	
	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2, TermAudit termAudit) throws IOException {
	  // This is a more detailed evaluation for collecting TermAudit
	  float lvalue = feature1.compute(tuple1,tuple2);
	  termAudit.setCalculatedScore(lvalue);
	  
	  return evaluate(tuple1, tuple2);
  }
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(feature1.getName());
		sb.append(Constants.OPERAND_SEPARATOR);
		sb.append(relop.getName());
		sb.append(Constants.OPERAND_SEPARATOR);
		if(null != feature2){
			sb.append(feature2.getName());
		}
		else{
			sb.append(value);
		}
		return sb.toString();
	}
  
	public String getTermString() {
		StringBuilder sb = new StringBuilder();
		sb.append(feature1.getName());
		sb.append(Constants.OPERAND_SEPARATOR);
		sb.append(relop.name());
		sb.append(Constants.OPERAND_SEPARATOR);
		if(null != feature2){
			sb.append(feature2.getName());
		}
		else{
			sb.append(value);
		}
		return sb.toString();
	}
	
	public String getDisplayString() {
		StringBuilder sb = new StringBuilder();
		sb.append(feature1.getName());
		sb.append(" ");
		sb.append(relop.getName());
		sb.append(" ");
		if(null != feature2){
			sb.append(feature2.getName());
		}
		else{
			sb.append(value);
		}
		return sb.toString();
	}
}
