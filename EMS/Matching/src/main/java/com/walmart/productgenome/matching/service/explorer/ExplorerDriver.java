package com.walmart.productgenome.matching.service.explorer;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;

/* 
 * Called from the explorer interface.
 */

public class ExplorerDriver {
	
	public static TableStats getTableStats(Table table, int topKFreqs, int maxNumValues){
		return Explorer.getTableStats(table, topKFreqs, maxNumValues);
	}
	
	public static TableStats getTableStatsWithSelectedAttrs(Table table, int topKFreqs, 
	                                int maxNumValues, int maxAttrCount) {
	  
	  return Explorer.getTableStatsWithSelectedAttrs(
	                      table, topKFreqs, maxNumValues, maxAttrCount);
	  
	}
	
	public static AttrStats getAttrStats(Table table, Attribute attr, int topKFreqs, int maxNumValues) {
	  return Explorer.getAttrStats(table, attr, topKFreqs, maxNumValues);
	}

}



