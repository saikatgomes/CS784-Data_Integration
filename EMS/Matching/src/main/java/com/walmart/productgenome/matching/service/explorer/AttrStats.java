package com.walmart.productgenome.matching.service.explorer;

import java.util.Map;

import com.google.common.base.Objects;
import com.walmart.productgenome.matching.models.data.Attribute.Type;

/*
 * Store summary attribute statistics.
 */

public class AttrStats {
	
  private final Type type;
  private final int totalValues;
  private final int totalMissing;
  private final int totalUnique;
  private final int totalDistinct;
  private final Map<Object, Integer> topKFrequencies;
  private final double avgLength;
  private final double avgNumTokens;
 
	public AttrStats(Type type, int totalValues, int totalMissing, 
          					int totalUnique, int totalDistinct, 
          					Map<Object, Integer> topKFrequencies, 
          					double avgLength,
          					double avgNumTokens) {
		this.type = type;
		this.totalValues = totalValues;
		this.totalMissing = totalMissing;
		this.totalUnique = totalUnique;
		this.totalDistinct = totalDistinct;
		this.topKFrequencies = topKFrequencies;
		this.avgLength = avgLength;
		this.avgNumTokens = avgNumTokens;
	}

	public int getTotalValues() {
		return totalValues;
	}

	public int getTotalMissing() {
		return totalMissing;
	}

	public int getTotalUnique() {
		return totalUnique;
	}

	public int getTotalDistinct() {
		return totalDistinct;
	}

	public Map<Object, Integer> getTopKFrequencies() {
		return topKFrequencies;
	}
	
	public double getPercentUnique() {
		return 100 * getTotalUnique() / (float) (getTotalValues() - getTotalMissing());
	}
	
	public double getPercentMissing() {
		return 100 * getTotalMissing() / (float) getTotalValues();
	}
	
	public double getAvgLength() {
	  return avgLength;
	}
	
	public double getAvgNumTokens() {
	  return avgNumTokens;
	}
	
	public Type getType() {
	  return type;
	}

  @Override
  public String toString() {
  	return Objects.toStringHelper(this)
  		.add("totalValues", totalValues)
  		.add("totalMissing", totalMissing)
  		.add("totalUnique", totalUnique)
  		.add("totalDistinct", totalDistinct)
  		.add("topKFrequencies", topKFrequencies)
  		.toString();
  }

}
