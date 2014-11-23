package com.walmart.productgenome.matching.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;

public class BlockingService {

	private static Attribute getPairIdAttribute() {
		return new Attribute("pairId", Attribute.Type.INTEGER);
	} 
	
	private static void addAdditionalAttributes(List<Attribute> attributes,
			List<Attribute> additionalAttributes, String tableName) {
		if(null != additionalAttributes) {
			for(Attribute attribute : additionalAttributes) {
				attributes.add(new Attribute(tableName + "." +
									attribute.getName(), attribute.getType()));
			}
		}
	}
	
	private static Map<Object, List<Object>> createIndex(Table table,
			Attribute blockingAttribute, Attribute idAttribute) {
		Map<Object, List<Object>> index = new HashMap<Object, List<Object>>();
		for (Tuple tuple : table.getAllTuples()){
			Object value = tuple.getAttributeValue(blockingAttribute);
			Object tupleId = tuple.getAttributeValue(idAttribute);
			List<Object> postingsList;
			if(!index.containsKey(value)) {
				postingsList = new ArrayList<Object>();
			}
			else {
				postingsList = index.get(value);
			}
			postingsList.add(tupleId);
			index.put(value, postingsList);
		}
		return index;
	}
	
	public static Table block(Table table1, Table table2, Attribute table1BlockingAttribute,
			Attribute table2BlockingAttribute, List<Attribute> table1AdditionalAttributes,
			List<Attribute> table2AdditionalAttributes, String candsetName,
			String projectName) {
	
		if(table1BlockingAttribute.getType() != table2BlockingAttribute.getType()){
			throw new InputMismatchException("[EMS] blocking attributes must be of the same type");
		}
		
		Attribute idAttribute1 = table1.getIdAttribute();
		// index all the values for blocking attribute from table1
		Map<Object, List<Object>> index = createIndex(table1,
										table1BlockingAttribute, idAttribute1);
		
		String id1Name = table1.getName() + "." + idAttribute1.getName();
		Attribute id1 = new Attribute(id1Name,idAttribute1.getType());
		
		Attribute idAttribute2 = table2.getIdAttribute();
		String id2Name = table2.getName() + "." + idAttribute2.getName();
		Attribute id2 = new Attribute(id2Name,idAttribute2.getType());
				
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute pairIdAttribute = getPairIdAttribute(); 
		attributes.add(pairIdAttribute);
		attributes.add(id1);
		attributes.add(id2);
		
		// add table 1 additional attributes
		addAdditionalAttributes(attributes, table1AdditionalAttributes, table1.getName());
		
		// add table 2 additional attributes
		addAdditionalAttributes(attributes, table2AdditionalAttributes, table2.getName());
		
		Table candset = new Table(candsetName, pairIdAttribute, attributes,
									projectName);
		
		Map<Attribute,Object> data = new HashMap<Attribute,Object>();
		int pairId = 1;
		// iterate through all the values for blocking attribute from table2
		for (Tuple table2Tuple : table2.getAllTuplesInOrder()) {
			Object value = table2Tuple.getAttributeValue(table2BlockingAttribute);
			Object tupleId = table2Tuple.getAttributeValue(idAttribute2);
			if(index.containsKey(value)) {
				List<Object> id1Values = index.get(value);
				for(Object id1Val : id1Values) {
					data.put(pairIdAttribute, pairId);
					data.put(id1, id1Val);
					data.put(id2, tupleId);
			
					// put table 1 additional attribute values
					int i = 3;
					Tuple table1Tuple = table1.getTuple(id1Val);
					if(null != table1AdditionalAttributes){
						for(Attribute attribute : table1AdditionalAttributes) {
							Object attributeValue = table1Tuple.getAttributeValue(attribute);
							data.put(attributes.get(i), attributeValue);
							i++;
						}
					}
				
					// put table 2 additional attribute values
					if(null != table2AdditionalAttributes) {
						for(Attribute attribute : table2AdditionalAttributes) {
							Object attributeValue = table2Tuple.getAttributeValue(attribute);
							data.put(attributes.get(i), attributeValue);
							i++;
						}
					}
				
					Tuple pair = new Tuple(data);
					candset.addTuple(pair);
					//System.out.println("Found candidate pair: " + pairId + ", " +
					//						id1Val + ", " + tupleId);
					pairId++;
					data.clear();
				}
			}
		}
		//System.out.println(JSONUtils.getTableJSON(candset));
		return candset;
	}
}
