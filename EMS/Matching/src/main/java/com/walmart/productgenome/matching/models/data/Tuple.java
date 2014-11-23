package com.walmart.productgenome.matching.models.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.base.Objects;

public class Tuple {

	private Map<Attribute,Object> data;
	
	
	// TODO: Sanjib, review. I added this constructor. It makes writing code easier.
  // First declare the item and then add the attribute values.
  public Tuple() {
    this.data = new HashMap<Attribute,Object>();
  }
	
	public Tuple(Map<Attribute,Object> data) throws IllegalArgumentException{
	  this();
		for(Map.Entry<Attribute, Object> entry : data.entrySet()){
			setAttributeValue(entry.getKey(),entry.getValue());
		}
	}
	
	public Object getAttributeValue(Attribute attribute){
		return data.get(attribute);
	}

	public void setAttributeValue(Attribute attribute, Object attributeValue) throws IllegalArgumentException{
		// check for type
		// System.out.println(attributeValue);
		Object attributeValueObj = null;
		if(null != attributeValue){
			if(attributeValue instanceof String && !((String) attributeValue).isEmpty()){
				attributeValueObj = attribute.convertValueToObject((String)attributeValue);
			}
			if(null != attributeValueObj){
				attribute.typeCheck(attributeValueObj);
			}
		}
		data.put(attribute, attributeValue); //accepting null values
	}
	
	public void removeAttr(Attribute attr) {
		data.remove(attr);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(Map.Entry<Attribute, Object> entry : data.entrySet()){
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static void main(String[] args){
		Map<Integer,String> map = new LinkedHashMap<Integer,String>();
		map.put(1, "Sanjib");
		map.put(2, "SKD");
		map.put(3, "Dada");
		map.put(4, "Topper");
		map.put(5, "Mr. Das");
		map.put(6, "Bebu");
		for(Integer i: map.keySet()){
			System.out.println(i);
		}
	}

  @Override
  public int hashCode(){
  	return Objects.hashCode(data);
  }
  
  @Override
  public boolean equals(Object object){
  	if (object instanceof Tuple) {
  		Tuple that = (Tuple) object;
  		return Objects.equal(this.data, that.data);
  	}
  	return false;
  }
	
	
}
