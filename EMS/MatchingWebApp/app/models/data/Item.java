package models.data;

import java.util.HashMap;
import java.util.Map;

public class Item {
	
	private Map<Attribute,Object> data;
	
	public Item(Map<Attribute,Object> data) throws IllegalArgumentException{
		this.data = new HashMap<Attribute,Object>();
		for(Map.Entry<Attribute, Object> entry : data.entrySet()){
			setAttributeValue(entry.getKey(),entry.getValue());
		}
	}
	
	public Object getAttributeValue(Attribute attribute){
		return data.get(attribute);
	}
	
	public void setAttributeValue(Attribute attribute, Object attributeValue) throws IllegalArgumentException{
		// check for type
		switch(attribute.getType()){
		case TEXT:
			if(!(attributeValue instanceof String))
				throw new IllegalArgumentException("Attribute Type violation: String value expected");
			break;
		case INTEGER:
			if(!(attributeValue instanceof Integer))
				throw new IllegalArgumentException ("Attribute Type violation: Integer value expected");
			break;
		case FLOAT:
			if(!(attributeValue instanceof Float))
				throw new IllegalArgumentException ("Attribute Type violation: Float value expected");
			break;
		case BOOLEAN:
			if(!(attributeValue instanceof Boolean))
				throw new IllegalArgumentException ("Attribute Type violation: Boolean value expected");
			break;
		default:
			throw new IllegalArgumentException ("Attribute Type violation: Text/Integer/Float/Boolean value expected");
		}
		data.put(attribute, attributeValue);
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

}
