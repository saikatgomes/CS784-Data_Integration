package com.walmart.productgenome.matching.models.audit;

import java.util.List;

import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.rules.Rule;

/**
 * Collects audit data about the matching of every item pair. This includes information like
 * whether the item pair matched or not, which clauses of rules were fired, what tokens did not
 * match for the mismatched item pairs etc.
 *
 */
public class ItemPairAudit {
  // the item pair whose match information has been captured for auditing
  private Tuple itemPair;

  // Whether matching succeeded for this item pair ?
  private MatchStatus status;

  // Audit information about all the constituent rules
  private List<RuleAudit> ruleAuditValues;
  private List<String> matchingRuleNames;

  public ItemPairAudit(Tuple itemPair)
  {
    this.itemPair = itemPair;
  }

  public Tuple getItemPair() {
    return itemPair;
  }

  public void setItemPair(Tuple itemPair) {
    this.itemPair = itemPair;
  }

  public MatchStatus getStatus() {
    return status;
  }

  public void setStatus(MatchStatus status) {
    this.status = status;
  }

  public List<RuleAudit> getRuleAuditValues() {
    return ruleAuditValues;
  }

  public void setRuleAuditValues(List<RuleAudit> ruleAuditValues) {
    this.ruleAuditValues = ruleAuditValues;
  }
  
  public void setMatchingRuleNames(List<String> matchingRuleNames) {
    this.matchingRuleNames = matchingRuleNames;
  }
  
  public List<String> getMatchingRuleNames() {
    return matchingRuleNames;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AuditDataCollector")
        .append("\n ============================================== \n")
            .append("\n itemPair=").append(itemPair)
        .append(", \nstatus=").append(status)
        .append(", \n\n RULE AUDIT VALUES =\n").append(ruleAuditValues)
        .append("\n ============================================== \n");
    return builder.toString();
  }

}