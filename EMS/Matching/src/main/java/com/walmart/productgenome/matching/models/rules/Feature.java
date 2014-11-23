package com.walmart.productgenome.matching.models.rules;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.google.common.base.Objects;

public class Feature {

	private String name;
	private Function function;
	private Attribute attribute1;
	private Attribute attribute2;
	private String table1Name;
	private String table2Name;
	private String projectName;

	public Feature(String name, Function function, String projectName,
			String table1Name, String table2Name, Attribute attribute1,
			Attribute attribute2) {
		this.name = name;
		this.function = function;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
		this.table1Name = table1Name;
		this.table2Name = table2Name;
		this.projectName = projectName;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function function) {
		this.function = function;
	}
	public Attribute getAttribute1() {
		return attribute1;
	}
	public void setAttribute1(Attribute attribute1) {
		this.attribute1 = attribute1;
	}
	public Attribute getAttribute2() {
		return attribute2;
	}
	public void setAttribute2(Attribute attribute2) {
		this.attribute2 = attribute2;
	}

	public String getTable1Name() {
		return table1Name;
	}

	public void setTable1Name(String table1Name) {
		this.table1Name = table1Name;
	}

	public String getTable2Name() {
		return table2Name;
	}

	public void setTable2Name(String table2Name) {
		this.table2Name = table2Name;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	// returns Float.NaN if the feature is not computable
	public float compute(Tuple tuple1, Tuple tuple2){
	  // TODO Sanjib: Why are you converting it to String? Shouldnt that it be Object
	  // Looks like some logic is wrong here.
	  if (name.equals("price_norm_sim")) {
	   /* System.out.println("t1 " + tuple1);

	    System.out.println(attribute1);
	    System.out.println(attribute2);
	  System.out.println("t1 " + tuple1.getAttributeValue(attribute1));
	  System.out.println("t2 " + tuple2.getAttributeValue(attribute2));*/
	  }
		String s1 = String.valueOf(tuple1.getAttributeValue(attribute1));
		String s2 = String.valueOf(tuple2.getAttributeValue(attribute2));
		String[] args = new String[2];
		args[0] = s1;
		args[1] = s2;
		return (Float) function.compute(args);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("function", function)
				.add("attribute1", attribute1)
				.add("attribute2", attribute2)
				.add("table1Name", table1Name)
				.add("table2Name", table2Name)
				.add("projectName", projectName)
				.toString();
	}

	public String getDisplayString() {
		StringBuilder sb = new StringBuilder();
		sb.append(function.getName());
		sb.append(",");
		sb.append(table1Name);
		sb.append(":");
		sb.append(attribute1.getName());
		sb.append(",");
		sb.append(table2Name);
		sb.append(":");
		sb.append(attribute2.getName());
		return sb.toString();
	}
}
