package com.walmart.productgenome.matching.service.explorer;

import java.util.ArrayList;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;

/*
 * Select the best attribute combinations using
 * heuristics.
 */

public class AttributeSelector {
  
  private final Table table;
  private final TableStats tableStats;
  private final int maxCombination;
  private final int topKFreq;
  private final int maxNumValues;
  private final List<Attribute> filteredAttrs;
    
  private int minUniqueForBest = ExplorerConstants.MIN_UNIQUE_FOR_BEST;
  private int maxMissing = ExplorerConstants.MAX_MISSING;
  private int minDistinct = ExplorerConstants.MIN_DISTINCT;
  
  public AttributeSelector(Table table2, TableStats tableStats, 
      int topKFreq, int maxNumValues, int maxCombination) {
    this.table = table2;
    this.tableStats = tableStats;
    this.maxCombination = maxCombination;
    this.topKFreq = topKFreq;
    this.maxNumValues = maxNumValues;
    filteredAttrs = getFilteredAttrs();
  }
  
  public void getBestAttrCombs() {
    for (int i = 1; i < maxCombination + 1; i++) {
      List<AttrComb> allAttrCombs = genAttrCombs(i);
      List<AttrComb> filteredAttrCombs = filterAlreadySeenCombs(allAttrCombs);
      getBestAttrCombs(i, filteredAttrCombs);
    } 
  }
  
  private void getBestAttrCombs(int attrCount, List<AttrComb> attrCombs) {
    
    for (AttrComb attrComb: attrCombs) {
      
      AttrStats attrStats = null;
      
      if (tableStats.hasAttrStats(attrComb)) {
        attrStats = tableStats.getAttrStats(attrComb);
      } else{
        attrStats = getAttrStatForComb(attrComb);
      }
      
      if (isCombEnoughForMatching(attrStats)) {        
        tableStats.putAttrStatsForComb(attrComb, attrStats);
        tableStats.putLayeredSelectedAttrs(attrCount, attrComb);
      }
      
    }
   
  }
  
  /* This is only called once at the start
   * to remove any attributes that we are sure
   * will not be useful for matching.
   */
  private List<Attribute> getFilteredAttrs() {
    
    List<Attribute> filteredAttrs = new ArrayList<Attribute>();
    
    for (Attribute attr: table.getAttributes()) {
      // Id attribute does not provide any value
      // for matching.
      if (attr.equals(table.getIdAttribute())) {
        continue;
      }
      
      AttrStats attrStats = tableStats.getAttrStats(new AttrComb(attr));
      if (shouldFilterAttribute(attrStats)) {
        continue;
      }
      
      filteredAttrs.add(attr);
      
    }
    
    return filteredAttrs;
  }
  
  private List<AttrComb> genAttrCombs(int setSize){
    
    List<AttrComb> attrCombs = new ArrayList<AttrComb>();
    
    // Create the initial vector
     ICombinatoricsVector<Attribute> initialVector = Factory.createVector(filteredAttrs);

    // Create a simple combination generator to generate 3-combinations of the initial vector
    Generator<Attribute> gen = Factory.createSimpleCombinationGenerator(initialVector, setSize);

    // Print all possible combinations
    for (ICombinatoricsVector<Attribute> combination : gen) {
       attrCombs.add(new AttrComb(combination));
    }
    
    return attrCombs;
    
  }
  
  private List<AttrComb> filterAlreadySeenCombs(
                                      List<AttrComb> combs) {
    List<AttrComb> filteredCombs =
                            new ArrayList<AttrComb>();
    for (AttrComb comb: combs) {
      if (!tableStats.isInBestCombs(comb)) {
        filteredCombs.add(comb);
      }
    }
    return filteredCombs;
  }
  
  private boolean isCombEnoughForMatching(AttrStats attrStats) {
    
    /* Heuristic condition for best attributes
     * that have very high percent of unique values.
     */
    if (attrStats.getPercentUnique() >= minUniqueForBest) {
      return true;
    }
    return false;
  }
  
  private boolean shouldFilterAttribute(AttrStats attrStats) {
    
    /* Heuristic condition for worst attributes
     * that should be filtered.
     * high number of missing values, and number of
     * distinct values less than 4. That means it has either
     * 3 distinct values; or it has 2 distinct values and 
     *  some may be missing.
     */
    
    if (attrStats.getPercentMissing() > maxMissing) {
      return true;
    }
    
    if (attrStats.getTotalDistinct() < minDistinct + 1) {
      return true;
    }
    
    return false;
  }
  
  public AttrStats getAttrStatForComb(AttrComb attrs) {
    
    List<List<Object>> attrValueLists = new ArrayList<List<Object>>();
    for (Attribute attr: attrs) {
      attrValueLists.add(table.getAllValuesForAttribute(attr));
    }
    
    List<List<Object>> zippedValueLists = zip(attrValueLists);
    
    List<Object> values = new ArrayList<Object>();
    
    for (List<Object> list: zippedValueLists) {
      String concatValues = concatStringsWSep(list, ExplorerConstants.ATTR_Value_Separator);
      if (concatValues.equals("")) {
        values.add(Constants.MISSING_VALUE);
      } else {
        values.add(concatValues);
      }
    }
    
    return Explorer.getAttrStats(values, topKFreq, maxNumValues);
  }
  
  private <T> List<List<T>> zip(List<List<T>> lists) {
    List<List<T>> zipped = new ArrayList<List<T>>();
    for (List<T> list : lists) {
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            List<T> list2;
            if (i >= zipped.size()) {
                zipped.add(list2 = new ArrayList<T>());
            } else {
                list2 = zipped.get(i);
            }
            list2.add(list.get(i));
        }
    }
    return zipped;
  }
  
  private String concatStringsWSep(List<Object> objects, String separator) {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for(Object s: objects) {
        // If at least one value is missing then the concatenation is missing.
        if (s.equals(Constants.MISSING_VALUE)) {
          return "";
        }
        sb.append(sep).append(s);
        sep = separator;
    }
    return sb.toString();                           
  }
  
}
