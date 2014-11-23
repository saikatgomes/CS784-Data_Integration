package com.walmart.productgenome.matching.service.explorer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Table;

/*
 * Generate Table and Attribute Statistics.
 */

public class Explorer {

  
	public static TableStats getTableStats(Table table, int topKFreqs, int maxNumValues){
	  
	  Map<AttrComb, AttrStats> attrStatsMap = getAttrStatsMap(table, topKFreqs, maxNumValues);
		
		TableStats tableStats = new TableStats(attrStatsMap);
		
		return tableStats;
	}
	
	public static TableStats getTableStatsWithSelectedAttrs(Table table, int topKFreqs, 
	    int maxNumValues, int maxAttrCount) {
	  
	  TableStats tableStats = getTableStats(table, topKFreqs, maxNumValues);
    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, topKFreqs, maxNumValues, maxAttrCount);
    attrSelector.getBestAttrCombs();
	  
	  return tableStats;
	}
	
	private static Map<AttrComb, AttrStats> getAttrStatsMap(Table table, int topKFreqs, int maxNumValues) {
	  
	  Map<AttrComb, AttrStats> attrStatsMap = new HashMap<AttrComb, AttrStats>();
    
    for (Attribute attr: table.getAttributes()) {
      attrStatsMap.put(new AttrComb(attr), getAttrStats(table, attr, topKFreqs, maxNumValues));
    }
    return attrStatsMap;
	}
	
	public static AttrStats getAttrStats(Table table, Attribute attr, int topKFreqs, int maxNumValues){
		return getAttrStats(attr.getType(), table.getAllValuesForAttribute(attr), topKFreqs, maxNumValues);
	}
	
	public static AttrStats getAttrStats(Type type, List<Object> values, int topKFreqs, int maxNumValues) {
	  
	  FrequencyAnalyzer frequencyAnalyzer = getAttrValueFrequencyAnalysis(values);
    
    AttrStats attrStats = new AttrStats(type,
                                        frequencyAnalyzer.getTotalValues(), frequencyAnalyzer.getTotalMissing(),
                                        frequencyAnalyzer.getTotalUnique(), frequencyAnalyzer.getTotalDistinct(),
                                        frequencyAnalyzer.getTopKFrequencies(topKFreqs, maxNumValues),
                                        frequencyAnalyzer.getAvgLength(),
                                        frequencyAnalyzer.getAvgNumTokens());
    
    return attrStats;
	}
	
	/*
	 * Input all the attribute values to the frequencyAnalyzer.
	 */
	private static FrequencyAnalyzer getAttrValueFrequencyAnalysis(List<Object> attrValues){
		
		FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		
		for(Object value: attrValues){
			frequencyAnalyzer.addValue(value);
		}
		
		return frequencyAnalyzer;
	}

  public static AttrStats getAttrStats(List<Object> values, int topKFreq,
      int maxNumValues) {
    // Note that for Attribute combinations we will have an attribute with null type.
    return getAttrStats(null, values, topKFreq, maxNumValues);
  }

}
