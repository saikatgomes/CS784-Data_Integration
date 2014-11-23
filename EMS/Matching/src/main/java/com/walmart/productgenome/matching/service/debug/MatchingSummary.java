package com.walmart.productgenome.matching.service.debug;

import java.util.Map;

import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.data.Tuple;

public class MatchingSummary {

	private String name;
	private String projectName;
	private String table1Name;
	private String table2Name;
	private String candsetName;
	private String matcherName;
	private String matchesName;
	private Map<Tuple, ItemPairAudit> itemPairAudits;

	private final int totalPairs;
	private final int totalMatches;
	private final Map<String, Integer> totalMatchesByRule;


	public MatchingSummary(int totalPairs, int totalMatches,
			Map<String, Integer> totalMatchesByRule) {
		super();
		this.totalPairs = totalPairs;
		this.totalMatches = totalMatches;
		this.totalMatchesByRule = totalMatchesByRule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getMatcherName() {
		return matcherName;
	}

	public void setMatcherName(String matcherName) {
		this.matcherName = matcherName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCandsetName() {
		return candsetName;
	}

	public void setCandsetName(String candsetName) {
		this.candsetName = candsetName;
	}

	public String getMatchesName() {
		return matchesName;
	}

	public void setMatchesName(String matchesName) {
		this.matchesName = matchesName;
	}

	public Map<Tuple, ItemPairAudit> getItemPairAudits() {
		return itemPairAudits;
	}

	public void setItemPairAudits(Map<Tuple, ItemPairAudit> itemPairAudits) {
		this.itemPairAudits = itemPairAudits;
	}
	
	public int getTotalPairs() {
		return totalPairs;
	}


	public int getTotalMatches() {
		return totalMatches;
	}

	public Map<String, Integer> getTotalMatchesByRule() {
		return totalMatchesByRule;
	}

}
