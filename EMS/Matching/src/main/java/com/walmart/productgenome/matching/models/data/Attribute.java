package com.walmart.productgenome.matching.models.data;

import com.google.common.base.Objects;

public class Attribute {
	
	public enum Type{
		TEXT,
		INTEGER,
		LONG,
		FLOAT,
		BOOLEAN
	}
	
	private String name;
	private Type type;

	public String getName(){
		return name;
	}
	
	public Type getType(){
		return type;
	}
	
	public Attribute(String name, Type type){
		this.name = name;
		this.type = type;
	}

	public void typeCheck(Object attributeValue) throws IllegalArgumentException{
		switch(type){
		case TEXT:
			if(!(attributeValue instanceof String))
				throw new IllegalArgumentException("Attribute Type violation: String value expected for " + attributeValue);
			break;
		case INTEGER:
			if(!(attributeValue instanceof Integer))
				throw new IllegalArgumentException ("Attribute Type violation: Integer value expected for " + attributeValue);
			break;
		case LONG:
			if(!(attributeValue instanceof Long))
				throw new IllegalArgumentException ("Attribute Type violation: Long value expected for " + attributeValue);
			break;
		case FLOAT:
			if(!(attributeValue instanceof Float))
				throw new IllegalArgumentException ("Attribute Type violation: Float value expected for " + attributeValue);
			break;
		case BOOLEAN:
			if(!(attributeValue instanceof Boolean))
				throw new IllegalArgumentException ("Attribute Type violation: Boolean value expected for " + attributeValue);
			break;
		default:
			throw new IllegalArgumentException ("Attribute Type violation: Text/Integer/Long/Float/Boolean value expected for " + attributeValue);
		}
	}
	
	public Object convertValueToObject(String val) {
		if(null == val || val.isEmpty()){
			return null;
		}
		Object value = null;
		switch (type) {
		case TEXT:
			// System.out.println("Case: TEXT, val: " + val);
			value = val;
			break;
		case INTEGER:
			// System.out.println("Case: INTEGER, val: " + val);
			try{
				value = Integer.parseInt(val);
			}
			catch(NumberFormatException nfe){
				throw new IllegalArgumentException("Attribute Type violation: Integer value expected for " + val);
			}
			break;
		case LONG:
			// System.out.println("Case: LONG, val: " + val);
			try{
				value = Long.parseLong(val);
			}
			catch(NumberFormatException nfe){
				throw new IllegalArgumentException("Attribute Type violation: Long value expected for " + val);
			}
			break;
		case FLOAT:
			// System.out.println("Case: FLOAT, val: " + val);
			try{
				value = Float.parseFloat(val);
			}
			catch(NumberFormatException nfe){
				nfe.printStackTrace();
				throw new IllegalArgumentException("Attribute Type violation: Float value expected for " + val);
			}
			break;
		case BOOLEAN:
			// System.out.println("Case: BOOLEAN, val: " + val);
			if("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val) ){
				value =  Boolean.valueOf(val);
			}
			else{
				throw new IllegalArgumentException ("Attribute Type violation: Boolean value expected for " + val);
			}
			break;
		default:
			throw new IllegalArgumentException ("Attribute Type violation: Text/Integer/Long/Float/Boolean value expected for " + val);
		}
		return value;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(name);
		sb.append(", ");
		sb.append(type);
		sb.append("]");
		return sb.toString();
	}

  @Override
  public int hashCode(){
  	return Objects.hashCode(name, type);
  }
  
  @Override
  public boolean equals(Object object){
  	if (object instanceof Attribute) {
  		Attribute that = (Attribute) object;
  		return Objects.equal(this.name, that.name)
  			&& Objects.equal(this.type, that.type);
  	}
  	return false;
  }
	
}
