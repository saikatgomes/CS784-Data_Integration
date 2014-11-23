package com.walmart.productgenome.matching.models.audit;

public enum MatchStatus {
  MATCH (1),
  NON_MATCH (0),
  DECLINE_TO_PREDICT (-1),
  UNSURE (-2);
  
  private int label;
  
  private MatchStatus(int label) {
	  this.label = label;
  }
  
  public int getLabel() {
	  return label;
  }
}
