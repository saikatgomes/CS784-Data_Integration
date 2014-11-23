package models.data;

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

	@Override
	public boolean equals(Object obj) {
		Attribute attribute = (Attribute)obj;
		return attribute.getName().equals(name) && attribute.getType().equals(type);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() ^ type.hashCode()	;
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
	
}
