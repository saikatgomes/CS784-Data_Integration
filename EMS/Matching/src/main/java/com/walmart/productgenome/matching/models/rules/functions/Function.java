package com.walmart.productgenome.matching.models.rules.functions;

import com.google.common.base.Objects;

public abstract class Function {

	private String name;
	private String description;
	
	public Function(String name, String description){
		this.name = name;
		this.description = description;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getReturnType(){
		return "Float";
	}
	
	public String getArguments(){
		return "String, String";
	}
	
	public abstract ArgType getArgType();
	
	public enum ArgType
  {
    STRING,
    NUMERIC
  }
	
	public abstract Object compute(String[] args) throws IllegalArgumentException;
	
	public abstract String getSignature();

  @Override
  public int hashCode(){
  	return Objects.hashCode(name);
  }
  
  @Override
  public boolean equals(Object object){
  	if (object instanceof Function) {
  		Function that = (Function) object;
  		return Objects.equal(this.name, that.name);
  	}
  	return false;
  }

}
