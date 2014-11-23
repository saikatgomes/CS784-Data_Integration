package com.walmart.productgenome.matching.models.audit;

import java.util.List;

import com.walmart.productgenome.matching.models.rules.Rule;

/**
 * Collects audit information about a single rule and all its comprising terms during the
 * matching process.
 *
 */
public class RuleAudit {
  
  private Rule rule;

  private MatchStatus status;

  private List<TermAudit> termAuditValues;

  public RuleAudit(Rule rule)
  {
    this.rule = rule;
  }

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public MatchStatus getStatus() {
    return status;
  }

  public void setStatus(MatchStatus status) {
    this.status = status;
  }

  public List<TermAudit> getTermAuditValues() {
    return termAuditValues;
  }

  public void setTermAuditValues(List<TermAudit> termAuditValues) {
    this.termAuditValues = termAuditValues;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n\n -------------------- RULE -------------------").append(rule)
        .append(", \tstatus=").append(status)
        .append(", \n\ntermAuditValues=").append(termAuditValues);
    return builder.toString();
  }

}
