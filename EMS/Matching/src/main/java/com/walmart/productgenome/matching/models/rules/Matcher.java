package com.walmart.productgenome.matching.models.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.audit.RuleAudit;
import com.walmart.productgenome.matching.models.data.Tuple;

public class Matcher {

	private String name;
	private String projectName;
	private String table1Name;
	private String table2Name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTable1Name() {
		return table1Name;
	}

	public void setTable1Name(String table1Name) {
		this.table1Name = table1Name;
	}

	public String getTable2Name() {
		return table2Name;
	}

	public void setTable2Name(String table2Name) {
		this.table2Name = table2Name;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = new ArrayList<Rule>(rules);
	}

	private List<Rule> rules;

	public Matcher(String name, String projectName, String table1Name,
			String table2Name){
		this.name = name;
		this.projectName = projectName;
		this.table1Name = table1Name;
		this.table2Name = table2Name;
	}

	public Matcher(String name, String projectName, String table1Name,
			String table2Name, List<Rule> rules){
		this.name = name;
		this.projectName = projectName;
		this.table1Name = table1Name;
		this.table2Name = table2Name;
		this.rules = new ArrayList<Rule>(rules);
	}

	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2) throws IOException{
		for (Rule r : rules){
			//System.out.println("Evaluating rule " + r.getName());
			MatchStatus result = r.evaluate(tuple1, tuple2);
			if (result == MatchStatus.MATCH){
				return result;
			}
		}
		return MatchStatus.NON_MATCH;
	}

	public MatchStatus evaluate(Tuple tuple1, Tuple tuple2, ItemPairAudit itemPairAudit) throws IOException {

		// This evaluates all rules for tuple because it want to have complete
		// audit info. It does not break at the rule that matched the item pair.

		List<RuleAudit> ruleAudits = new ArrayList<RuleAudit>();
		List<String> matchingRuleNames = new ArrayList<String>();

		MatchStatus result = MatchStatus.NON_MATCH;

		for (Rule r : rules) {

			RuleAudit ruleAudit = new RuleAudit(r);

			//System.out.println("Evaluating rule " + r.getName());

			MatchStatus rtmp = r.evaluate(tuple1, tuple2, ruleAudit);
			ruleAudit.setStatus(rtmp);

			if(rtmp == MatchStatus.MATCH) {
				matchingRuleNames.add(r.getName());
				result = MatchStatus.MATCH;
			}
			else if(rtmp != MatchStatus.NON_MATCH && result != MatchStatus.MATCH){
				result = rtmp;
			}

			ruleAudits.add(ruleAudit);
		}

		itemPairAudit.setMatchingRuleNames(matchingRuleNames);
		itemPairAudit.setRuleAuditValues(ruleAudits);

		return result;
	}

	public int getNumRules(){
		return rules.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		for(int i = 0; i < rules.size(); i++){
			Rule r = rules.get(i);
			sb.append(r.toString());
			if(i != rules.size()-1){
				sb.append(Constants.RULE_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public String getShortDisplayString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < rules.size(); i++){
			Rule r = rules.get(i);
			sb.append(r.getName());
			if(i != rules.size()-1){
				sb.append(Constants.RULE_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public String getDisplayString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < rules.size(); i++){
			Rule r = rules.get(i);
			sb.append(r.getDisplayString());
			if(i != rules.size()-1){
				sb.append(Constants.RULE_SEPARATOR);
			}
		}
		return sb.toString();
	}
}
