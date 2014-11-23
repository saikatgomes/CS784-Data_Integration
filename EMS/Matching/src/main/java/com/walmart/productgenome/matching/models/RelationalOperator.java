package com.walmart.productgenome.matching.models;

import java.util.ArrayList;
import java.util.List;

public enum RelationalOperator {
	EQUALS("=="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUALS(">="),
	LESS_THAN("<"),
	LESS_THAN_EQUALS("<="),
	NOT_EQUALS("!=");
	
	private String name;
	
	private RelationalOperator(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static List<String> getOperatorNames() {
		List<String> operatorNames = new ArrayList<String>();
		for(RelationalOperator relop : RelationalOperator.values()) {
			operatorNames.add(relop.getName());
		}
		return operatorNames;
	}
	
	public static RelationalOperator valueOfFromName(String name) {
		if("==".equals(name)) {
			return EQUALS;
		}
		else if(">".equals(name)) {
			return GREATER_THAN;
		}
		else if(">=".equals(name)) {
			return GREATER_THAN_EQUALS;
		}
		else if("<".equals(name)) {
			return LESS_THAN;
		}
		else if("<=".equals(name)) {
			return LESS_THAN_EQUALS;
		}
		else if("!=".equals(name)) {
			return NOT_EQUALS;
		}
		else {
			throw new IllegalArgumentException("Invalid relational operator");
		}
	}
}
