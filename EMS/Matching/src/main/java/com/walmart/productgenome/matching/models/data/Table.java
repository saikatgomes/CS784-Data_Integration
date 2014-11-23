package com.walmart.productgenome.matching.models.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class Table {

	private String name;
	private String description = "";
	private Attribute idAttrib;
	private List<Attribute> attributes;
	private Map<Object,Tuple> tuples;
	private String projectName;

	public Table(String name, Attribute idAttrib, List<Attribute> attributes, String projectName){
		this.name = name;
		this.idAttrib = idAttrib;
		this.attributes = new ArrayList<Attribute>(attributes);
		tuples = new LinkedHashMap<Object,Tuple>();
		this.projectName = projectName;
	}

	public Table(String name, Attribute idAttrib, List<Attribute> attributes,
			List<Tuple> tuples, String projectName){
		this(name,idAttrib,attributes,projectName);
		addAllTuples(tuples);
	}

	// copy constructor
	public Table(Table otherTable) {
		this.name = otherTable.getName();
		this.idAttrib = new Attribute(otherTable.getIdAttribute().getName(), otherTable.getIdAttribute().getType());
		this.attributes = new ArrayList<Attribute>(otherTable.getAttributes());
		this.tuples = new LinkedHashMap<Object, Tuple>(otherTable.tuples);
		this.projectName = otherTable.getProjectName();
	}

	// copy constructor for sampling
	public Table(Table otherTable, String tableName, List<Object> ids) {
		this.name = tableName;
		this.idAttrib = new Attribute(otherTable.getIdAttribute().getName(), otherTable.getIdAttribute().getType());
		this.attributes = new ArrayList<Attribute>(otherTable.getAttributes());
		this.tuples = new LinkedHashMap<Object, Tuple>();
		for(Object id: ids) {
			this.tuples.put(id, otherTable.getTuple(id));
		}
		this.projectName = otherTable.getProjectName();
	}

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attribute getIdAttribute(){
		return idAttrib;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Tuple getTuple(Object id){
		return tuples.get(id);
	}

	public List<Attribute> getAttributes(){
		return attributes;
	}

	public void addTuple(Tuple tuple){
		//System.out.println("tuple: " + tuple);
		Object tupleId = tuple.getAttributeValue(idAttrib);
		//System.out.println("tupleId: " + tupleId);
		tuples.put(tupleId,tuple);
	}

	public void addAllTuples(Collection<Tuple> tuples){
		for(Tuple tuple : tuples){
			addTuple(tuple);
		}
	}

	public void removeTuple(Object id){
		tuples.remove(id);
	}

	public boolean contains(Tuple tuple){
		Object tupleId = tuple.getAttributeValue(idAttrib);
		return tuples.containsKey(tupleId);
	}

	public boolean isEmpty(){
		return tuples.isEmpty();
	}

	public long getSize(){
		return tuples.size();
	}

	public Collection<Tuple> getAllTuples(){
		return tuples.values();
	}

	public List<Tuple> getAllTuplesInOrder(){
		List<Tuple> tupleList = new ArrayList<Tuple>();
		for(Tuple tuple : tuples.values()){
			tupleList.add(tuple);
		}
		return tupleList;
	}

	public List<Object> getAllIdsInOrder() {
		List<Object> ids = new ArrayList<Object>(tuples.keySet()); //keySet() returns the keys in order
		return ids;
	}

	public void clear() {
		tuples.clear();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public List<Object> getAllValuesForAttribute(Attribute attribute){
		List<Object> values = new ArrayList<Object>();
		for(Tuple tuple : tuples.values()){
			Object val = tuple.getAttributeValue(attribute);
			values.add(val);
		}
		return values;
	}

	// TODO: SANJIB. ALSO NEED TO GET ROW ID FOR EACH ITEM.
	// WHICH SPECIFIED ITS PLACE RELATIVE TO OTHERS
	// IN DATASET.

	public Attribute getAttributeByName(String attrName) {
		for (Attribute attr: attributes) {
			if (attrName.equals(attr.getName())) {
				return attr;
			}
		}
		return null;
	}

	// TODO: What if the table is large? We need to do external sorting.
	// Right now everything is in memory.
	public List<Object> sortTableByAttribute(List<Object> ids, Attribute attr, boolean ascending){
		Collections.sort(ids, new AttrComparator(attr));
		if (!ascending) {
			Collections.reverse(ids);
		}
		return ids;
	}

	//TODO: Need inverted indexes on attributes for efficient implementation
	public List<Object> getAllRowIdsWithAttrValue(Attribute attr, Object value){
		// type check the attribute value
		attr.typeCheck(value);
		List<Object> ids = new ArrayList<Object>();
		String strValue = String.valueOf(value);
		for(Tuple tuple : tuples.values()){
			String tupleVal = String.valueOf(tuple.getAttributeValue(attr));
			if (tupleVal.contains(strValue)) {
				ids.add(tuple.getAttributeValue(idAttrib));
			}
		}
		return ids;
	}

	private class AttrComparator implements Comparator<Object> {

		private Attribute attr;

		public AttrComparator(Attribute attr) {
			this.attr = attr;
		}

		public int compare(Object id1, Object id2) {

			Object value1 = getTuple(id1).getAttributeValue(attr);
			Object value2 = getTuple(id2).getAttributeValue(attr);
			
			if (value1 == null && value2 != null) {
			  return -1;
			}
			else if (value2 == null && value1 != null) {
        return 1;
      } else if (value2 == null && value1 == null) {
        return 0;
      }

			switch(attr.getType()){
			case TEXT:
				String sval1 = (String) value1;
				String sval2 = (String) value2;
				return sval1.compareTo(sval2);
			case INTEGER:
				Integer ival1 = (Integer) value1;
				Integer ival2 = (Integer) value2;
				return ival1.compareTo(ival2);
			case FLOAT:
				Float fval1 = (Float) value1;
				Float fval2 = (Float) value2;
				return fval1.compareTo(fval2);
			case BOOLEAN:
				Boolean bval1 = (Boolean) value1;
				Boolean bval2 = (Boolean) value2;
				return bval1.compareTo(bval2);
			default:
				throw new IllegalArgumentException ("Attribute Type violation: Text/Integer/Float/Boolean value expected for " + attr.getName());
			}
		}
	}

	// TODO: Sanjib. review
	public List<Object> sortTableByAttribute(List<Object> ids, Attribute attr, Boolean ascending) {

		Collections.sort(ids, new AttrComparator(attr));

		if (!ascending) {
			Collections.reverse(ids);
		}

		return ids;
	}	

	// TODO: Sanjib, review
	public List<Object> getAllRowsWithQueryCondition(
			List<Attribute> attrs, List<Object> values, List<QueryOps> ops) {

		List<Object> ids = new ArrayList<Object>();

		for(Tuple tuple : tuples.values()) {

			Boolean matches = true;
			for (int i = 0; i < attrs.size(); i++) {
				Attribute attr = attrs.get(i);
				Object value = values.get(i);
				QueryOps op = ops.get(i);

				if (!meetsQueryCondition(tuple, attr, value, op)) {
					matches = false;
					break;
				}
			}
			if (matches) {
				ids.add(tuple.getAttributeValue(getIdAttribute()));
			}
		}
		return ids; 
	}

	// TODO: Sanjib, review
	private Boolean meetsQueryCondition(Tuple tuple, Attribute attr, Object value, QueryOps op) {
		Object itemVal = tuple.getAttributeValue(attr);
		if (itemVal == null) {
		  return false;
		}
		if (compareUsingOps(attr.getType(), op, itemVal, value)) {
			return true;
		}
		return false;
	}

	// TODO: Sanjib, where does this belong to? I need to use it for doing comparisons in 
	// different places. It is definately not a property of the table.
	public static Boolean compareUsingOps(Type type, QueryOps op, Object val1, Object val2) {

		switch (op) {
		case GREATER_THAN:
			return customCompareTo(val1, val2) == 1;
		case LESS_THAN:
			return customCompareTo(val1, val2) == -1;
		case EQUALS:
			return customCompareTo(val1, val2) == 0;
		case GREATER_THAN_EQUALS:
			return customCompareTo(val1, val2) == 0 || customCompareTo(val1, val2) == 1;
		case LESS_THAN_EQUALS:
			return customCompareTo(val1, val2) == 0 || customCompareTo(val1, val2) == -1;
		case CONTAINS:
			String sval1 = String.valueOf(val1);
			String sval2 = String.valueOf(val2);
			return sval1.contains(sval2);
		default:
			//pass. Type check is done before getting here.
		}
		return null;
	}

	// TODO: Sanjib, review
	private static int customCompareTo(Object val1, Object val2) {

		if((val1 instanceof String) && (val2 instanceof String)) {
			return ((String)val1).compareTo((String)val2);
		}

		if((val1 instanceof Integer) && (val2 instanceof Integer)) {
			return ((Integer)val1).compareTo((Integer)val2);
		}

		if((val1 instanceof Float) && (val2 instanceof Float)) {
			return ((Float)val1).compareTo((Float)val2);
		}

		if((val1 instanceof Boolean) && (val2 instanceof Boolean)) {
			return ((Boolean)val1).compareTo((Boolean)val2);
		}

		throw new IllegalArgumentException ("Incompatible objects: Values to be compared are not compatible.");
	}

	// TODO: Sanjib, review
	public enum QueryOps {
		GREATER_THAN, LESS_THAN, EQUALS, GREATER_THAN_EQUALS, LESS_THAN_EQUALS, CONTAINS
	}

	// TODO: Sanjib, review
	//	public Object convertValueToObj(String val, Type type) {
	//        switch (type) {
	//          case TEXT:
	//            return (String) val;
	//          case INTEGER:
	//            return Integer.parseInt(val);
	//          case FLOAT:
	//            return Float.parseFloat(val);
	//          case BOOLEAN:
	//            return Boolean.valueOf(val);
	//          default:
	//            throw new IllegalArgumentException ("Invalid Type: conversion for this type is not supported");
	//        }
	//  }


	// TODO: Sanjib, review.
	public List<Object> getRandomSample(Long sampleSize) {

		Set<Object> sample = new HashSet<Object>();

		List<Object> ids = new ArrayList<Object>(tuples.keySet());
		Collections.shuffle(ids);

		for (int i = 0; i < sampleSize; i++) {
			sample.add(ids.get(i));
		}	 

		for (Object id: ids) {
			if (!sample.contains(id)) {
				tuples.remove(id);
			}
		}

		return new ArrayList<Object>(sample);
	}

	//TODO: Sanjib, review. Can we delete the id attr?
	// We may be able to, if we have our own internal identifier.
	public void removeAttr(Attribute attr) {
		attributes.remove(attr);
		for(Tuple tuple : tuples.values()){
			tuple.removeAttr(attr);
		}
	}

	public static void main(String[] args){
		Attribute idAttrib = new Attribute("id",Attribute.Type.TEXT);
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(idAttrib);
		attributes.add(new Attribute("title", Attribute.Type.TEXT));
		attributes.add(new Attribute("price", Attribute.Type.FLOAT));
		Table t = new Table("Bowker",idAttrib,attributes,"Products");
		//t.setDescription("Hello");
		System.out.println(JSONUtils.getTableJSON(t));
	}
}
