package com.walmart.productgenome.matching.service.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;
import com.walmart.productgenome.matching.models.data.Attribute;

/*
 * Stores summary statistics for a table.
 */

// TODO: Design decision.
// TableStats works with AttrCombinations because
// we had the attrSelector at some point.
// Do we want to still support that functionality?
// Otherwise, it can work with attributes and not Attribute
// combinations.

public class TableStats {
  
	private final Map<AttrComb, AttrStats> attrStatsMap;
	private Map<Integer, List<AttrComb>> layeredSelectedAttrs;
  private Map<AttrComb, Integer> attrCombsIdMap;
  private int id;

  
  public TableStats(Map<AttrComb, AttrStats> attrStatsMap) {
  	this.attrStatsMap = attrStatsMap;
  	this.layeredSelectedAttrs = new HashMap<Integer, List<AttrComb>>();
    attrCombsIdMap = new HashMap<AttrComb, Integer>();
    id = 1;
    
    for (AttrComb attrComb: attrStatsMap.keySet()) {
      attrCombsIdMap.put(attrComb, id);
      id ++;
    }
  }
  
  public Set<AttrComb> getAllAttrCombs() {
    return attrStatsMap.keySet();
  }
  
  public boolean hasAttrStats(AttrComb attrComb) {
    return attrStatsMap.containsKey(attrComb);
  }
  
  public void putAttrStatsForComb(AttrComb attrComb, AttrStats attrStats) {
    attrStatsMap.put(attrComb, attrStats);
    if (!attrCombsIdMap.containsKey(attrComb)) {
      attrCombsIdMap.put(attrComb, id);
      id ++;
    }
  }
  
  public AttrStats getAttrStats(AttrComb attrComb) {
    return attrStatsMap.get(attrComb);
  }
  
  public List<AttrComb> getAttributeCombsForLayer(int attrCount) {
    if (layeredSelectedAttrs.containsKey(attrCount)) {
      return layeredSelectedAttrs.get(attrCount);
    }
    return null;
  }
  
  public void putLayeredSelectedAttrs(int attrCount, AttrComb attrComb) {
    if (layeredSelectedAttrs.containsKey(attrCount)) {
      layeredSelectedAttrs.get(attrCount).add(attrComb);
    } else {
      List<AttrComb> attrCombs =  new ArrayList<AttrComb>();
      attrCombs.add(attrComb);
      layeredSelectedAttrs.put(attrCount, attrCombs);
    }
  }
  
  public Integer getAttrCombId(AttrComb attrComb) {
    return attrCombsIdMap.get(attrComb);
  }
  
  public AttrComb getAttrCombById(Integer id) {
    // using a for loop instead of additional data structures
    // since I am assuming that there are not many
    // attrCombs.
        
    for(Map.Entry<AttrComb, Integer> entry: attrCombsIdMap.entrySet()) {
      if (id.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    
    return null;
  }
  
  public boolean isInBestCombs(AttrComb inComb) {
      
      /*  Check to see if subset of inComb was already
       *  output as best combination. In that case
       *  we do not to evaluate this combination.
       */
      for(Map.Entry<Integer, List<AttrComb>> entry: 
                          layeredSelectedAttrs.entrySet()) {
        for(AttrComb attrComb: entry.getValue()) {
            boolean isSubset = true;
            for (Attribute attr: attrComb) {
              if (!inComb.contains(attr)) {
                isSubset = false;
              }
            }
            if (isSubset) {
              return true;
            }
          }
        }
      
      return false;
    }
  
  public String printLayeredCombs() {
  
    String text = "";
    
    for(Entry<Integer, List<AttrComb>> entry: 
      layeredSelectedAttrs.entrySet()) {
      text += "===== " + entry.getKey() + " =====\n";
      for(AttrComb attrComb: entry.getValue()) {
        
        text += attrComb + "\n";
      }
    }
    return text;
  }

  @Override
  public String toString() {
  	return Objects.toStringHelper(this)
  		.add("attrStatsMap", attrStatsMap)
  		.toString();
  }

}
