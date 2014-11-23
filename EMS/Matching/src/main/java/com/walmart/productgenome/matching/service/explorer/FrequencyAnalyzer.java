package com.walmart.productgenome.matching.service.explorer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Objects;
import com.walmart.productgenome.matching.models.Constants;

/*
 * Inserts a set of values to appropriate data structures and 
 * calculates summary statistics over them.
 * 
 * The summary statistics available are:
 * 1) Total number of values
 * 2) Total number of missing values
 * 3) Total number of unique values
 * 4) Total number of distinct value
 * 5) Values corresponding to top K frequency counts.
 * 6) Average length of values
 * 7) Average number of tokens in the values
 */

public class FrequencyAnalyzer {
	
  
  private static final String TOKEN_DELIMITER = " ";
	// Map of values to their frequencies.
	private Map<Object, Integer> valueToFreq;
	// Map of frequencies to set of values that have those frequencies.
	private Map<Integer, Set<Object>> freqToValues;
	// Total number of values entered in the maps.
	private int totalValues;
  private double avgLength;
  private double avgNumTokens;
	
	public FrequencyAnalyzer() {
		valueToFreq = new HashMap<Object, Integer>();
		freqToValues = new TreeMap<Integer, Set<Object>>(Collections.reverseOrder());
	}
	
	// Add a value to the list of values.
	public void addValue(Object value) {
		
		setTotalValues(totalValues + 1);
		
		Integer count = valueToFreq.get(value);
		if (count == null) {
			count = 0;
		}
		
    // Note: Will convert all types to String.
		// to get token length and average length.
		String strValue = String.valueOf(value);
		setAvgLength((avgLength*(totalValues - 1) + 
		              strValue.length())/
		              (double)totalValues);
		
		setAvgNumTokens((avgNumTokens*(totalValues - 1) + 
		                getTokens(strValue).length)/
		                (double)totalValues);
		
		updateValueToFreq(value, count);
		updateFreqToValues(value, count);
	}
	
	public int getTotalValues() {
		return totalValues;
	}
	
	public int getTotalDistinct() {
		return valueToFreq.size();
	}
	
	public int getTotalUnique() {
		return getValuesWithFreq(1).size();
	}
	
	public int getTotalMissing() {
	  if (valueToFreq.containsKey(Constants.MISSING_VALUE)) {
	    return valueToFreq.get(Constants.MISSING_VALUE);
	  }
	  return 0;
	}
	
	public int getFrequencyForValue(Object value) {
	  if (valueToFreq.containsKey(value)) {
	    return valueToFreq.get(value);
	  }
	  return 0;
	}
	
	public Set<Object> getValuesWithFreq(int freq) {
	  if (freqToValues.containsKey(freq)){
	    return freqToValues.get(freq);
	  }
	  return new HashSet<Object>();
	}
	
	public double getAvgLength() {
	  return avgLength;
	}
	
	public double getAvgNumTokens() {
	  return avgNumTokens;
	}
	
	/*
	 * Returns all the values that correspond to top k frequency
	 * counts for the values.
	 * 
	 * Note that the actual values returned may be more than k, 
	 * because we can have more than 1 value map to one frequency count.
	 * 
	 */
	public Map<Object, Integer> getTopKFrequencies(int k, int maxNumValues) {
		
		Map<Object, Integer> topKFrequencies = new LinkedHashMap<Object, Integer>();

		int countk = 0;
		int countm = 0;
				
		for(Map.Entry<Integer, Set<Object>> entry : freqToValues.entrySet()) {
		  // TODO: Why do I have emtpy sets? I should not.
		  if (!entry.getValue().isEmpty()) {
  			if (countk < k) {
  				for (Object value: entry.getValue()) {
  				  if (countm < maxNumValues) {
  				    topKFrequencies.put(value, entry.getKey());
              countm ++;
  				  } else {
  				    break;
  				  }
  				}
          countk ++;
  			} else {
  			  break;
  			}
		  }
		}
		
		return topKFrequencies;
	}
	
	private void updateValueToFreq(Object value, int count) {
		valueToFreq.put(value, count + 1);
	}
	
	private void updateFreqToValues(Object value, int count) {
		
		if (count > 0){
			freqToValues.get(count).remove(value);
		} 
		
		if (!freqToValues.containsKey(count + 1)) {
			Set<Object> valueSet = new HashSet<Object>();
			freqToValues.put(count + 1, valueSet);
		}
			
		freqToValues.get(count + 1).add(value);
	}
	
	private void setTotalValues(int value) {
		totalValues = value;
	}
	
	private void setAvgLength(double value) {
	  avgLength = value;
	}
	
	private void setAvgNumTokens(double value) {
	  avgNumTokens = value;
	}
	
	private String[] getTokens(String value) {
	  return value.split(TOKEN_DELIMITER);
	}

  @Override
  public String toString() {
  	return Objects.toStringHelper(this)
  		.add("valueToFreq", valueToFreq)
  		.toString();
  }
	
}
