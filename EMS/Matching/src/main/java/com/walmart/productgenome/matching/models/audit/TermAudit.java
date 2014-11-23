package com.walmart.productgenome.matching.models.audit;
import java.text.DecimalFormat;

import com.walmart.productgenome.matching.models.rules.Term;


/**
 * Collects audit information about a single term and all the participating attribute value tokens.
 */
public class TermAudit {
  
  private Term term;
  private MatchStatus status;
  private double calculatedScore;

  public TermAudit(Term term)
  {
    this.term = term;
  }

  public Term getTerm() {
    return term;
  }
  public void setTerm(Term term) {
    this.term = term;
  }
  
  public MatchStatus getStatus() {
    return status;
  }
  
  public void setStatus(MatchStatus status) {
    this.status = status;
  }

  public double getCalculatedScore() {
    return calculatedScore;
  }

  public void setCalculatedScore(double calculatedScore) {
    this.calculatedScore = calculatedScore;
  }

  @Override
  public String toString() {
    
    StringBuilder builder = new StringBuilder();
    builder .append("\n\n ----------------------- TERM --------------------------").append(term)
        .append(", \nstatus=").append(status)
        .append(", \tcalculatedScore=").append(formatDouble(calculatedScore));

    return builder.toString();
  }
  
  public static String formatDouble(double number) {
    DecimalFormat df = new DecimalFormat("####0.00");
    return df.format(number);
  }
  
}