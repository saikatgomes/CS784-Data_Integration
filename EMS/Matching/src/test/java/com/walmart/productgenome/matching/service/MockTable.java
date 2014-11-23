package com.walmart.productgenome.matching.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.data.Table;

public class MockTable {
	
	private Table table;
	
	Attribute id = new Attribute("id", Type.INTEGER);
	Attribute title = new Attribute("title", Type.TEXT);
	Attribute year = new Attribute("year", Type.INTEGER);
	
	public MockTable(){
		
		Map<Attribute, Object> attrValueMap1 = new HashMap<Attribute, Object>();
		Map<Attribute, Object> attrValueMap2 = new HashMap<Attribute, Object>();
		Map<Attribute, Object> attrValueMap3 = new HashMap<Attribute, Object>();
		Map<Attribute, Object> attrValueMap4 = new HashMap<Attribute, Object>();

    attrValueMap1.put(id, 1);
		attrValueMap1.put(title, new String("Harry Potter 1"));
		attrValueMap1.put(year, 2008);
    
		attrValueMap2.put(id, 2);
		attrValueMap2.put(title, new String("Harry Potter 1"));
		attrValueMap2.put(year, 2009);
		
    attrValueMap3.put(id, 3);
		attrValueMap3.put(title, new String("Harry Potter 2"));
		attrValueMap3.put(year, null);
		
    attrValueMap4.put(id, 4);
		attrValueMap4.put(title, new String("Harry Potter 3"));
		attrValueMap4.put(year, 2009);
		
		Tuple item1 = new Tuple(attrValueMap1);
		Tuple item2 = new Tuple(attrValueMap2);
		Tuple item3 = new Tuple(attrValueMap3);
		Tuple item4 = new Tuple(attrValueMap4);
		
		List<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(title);
		attrs.add(year);
		
		table = new Table("mockTable", id, attrs, "dummyProj");
		table.addTuple(item1);
		table.addTuple(item2);
		table.addTuple(item3);
		table.addTuple(item4);

	}
	
	public Table getTable(){
		return table;
	}

}
