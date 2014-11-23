package com.walmart.productgenome.matching.models.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.audit.RuleAudit;
import com.walmart.productgenome.matching.models.audit.TermAudit;
import com.walmart.productgenome.matching.models.data.Tuple;

public class Rule {
	
	private String name;
	private String projectName;
	private String table1Name;
	private String table2Name;
	private List<Term> terms;
	
	public Rule(String name, String projectName, String table1Name,
			String table2Name,List<Term> terms){
		this.name = name;
		this.projectName = projectName;
		this.table1Name = table1Name;
		this.table2Name = table2Name;
		this.terms = new ArrayList<Term>(terms);
	}
	
	public String getName(){
		return name;
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	public String getTable1Name(){
		return table1Name;
	}
	
	public String getTable2Name(){
		return table2Name;
	}
	
	public List<Term> getTerms() {
	  return terms;
	}
	
	public void setTerms(List<Term> terms) {
		this.terms = new ArrayList<Term>(terms);
	}
	
	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2) throws IOException {
		for (Term t : terms){
			//System.out.println("Evaluating term " + t);
			MatchStatus result = t.evaluate(tuple1, tuple2);
			if (result != MatchStatus.MATCH) {
				return result;
			}
		}
		return MatchStatus.MATCH;
	}
	
	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2, RuleAudit ruleAudit) throws IOException {
		
	  // This evaluates all terms for tuple because it wants to have
	  // complete audit info. It does not break at the term that did not apply
	  // to the item pair.
	  
	  MatchStatus result = MatchStatus.MATCH;
	  List<TermAudit> termAudits = new ArrayList<TermAudit>();
	  
	  for (Term t : terms){
	    TermAudit termAudit = new TermAudit(t);
	    //System.out.println("Evaluating term " + t);
	    MatchStatus rtmp = t.evaluate(tuple1, tuple2, termAudit);
	    termAudit.setStatus(rtmp);
	    if(rtmp == MatchStatus.NON_MATCH) {
	    	result = rtmp;
	    }
	    else if(rtmp != MatchStatus.MATCH && result == MatchStatus.MATCH) {
	    	// DECLINE_TO_PREDICT or UNSURE
	    	// do not override NON_MATCH
	    	result = rtmp;
	    }
      termAudits.add(termAudit);
    }
	  
	  ruleAudit.setTermAuditValues(termAudits);
	  
	  return result;
  }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < terms.size(); i++){
			Term t = terms.get(i);
			sb.append(t.toString());
			if(i != terms.size()-1){
				sb.append(Constants.TERM_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public String getRuleString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < terms.size(); i++){
			Term t = terms.get(i);
			sb.append(t.getTermString());
			if(i != terms.size()-1){
				sb.append(Constants.TERM_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
	public String getDisplayString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < terms.size(); i++){
			Term t = terms.get(i);
			sb.append(t.getDisplayString());
			if(i != terms.size()-1){
				sb.append(Constants.TERM_SEPARATOR);
			}
		}
		return sb.toString();
	}
}
