package com.walmart.productgenome.matching.models.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.service.debug.MatchingSummary;

public class Project {

	private static final DateFormat formatter = new SimpleDateFormat();
	
	private String name;	
	private String description;
	private Date createdOn = new Date();
	private Date lastModifiedOn = new Date();
	
	private ProjectDefaults defaults = new ProjectDefaults(); 
	
	// Tables
	private Set<String> tableNames = new LinkedHashSet<String>();
	
	// Rules
	private Map<String, Function> functionMap = new LinkedHashMap<String, Function>(); 
	private Map<String, Feature> featureMap = new LinkedHashMap<String, Feature>();
	private Map<String, Rule> ruleMap = new LinkedHashMap<String, Rule>();
	private Map<String, Matcher> matcherMap = new LinkedHashMap<String, Matcher>();
	
	// Summaries
	private Map<String, MatchingSummary> matchingSummaryMap = new LinkedHashMap<String, MatchingSummary>();
	private Map<String, EvaluationSummary> evaluationSummaryMap = new LinkedHashMap<String, EvaluationSummary>();
	
	private Set<String> unsavedTables = new HashSet<String>();
	private Set<String> unsavedFunctions = new HashSet<String>();
	private Set<String> unsavedFeatures = new HashSet<String>();
	private Set<String> unsavedRules = new HashSet<String>();
	private Set<String> unsavedMatchers = new HashSet<String>();
	
	public Project(){
	}

	public Project(String name, String description){
		this.name = name;
		this.description = description;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public String getCreatedOnString() {
		return formatter.format(createdOn);
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public String getLastModifiedOnString() {
		return formatter.format(lastModifiedOn);
	}
	
	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public void setCreatedOn(String createdOn) throws ParseException {
		this.createdOn = (Date)formatter.parse(createdOn);
	}
	
	public void setLastModifiedOn(String lastModifiedOn) throws ParseException {
		this.lastModifiedOn = (Date)formatter.parse(lastModifiedOn);
	}
	
	/* Tables */
	
	public List<String> getTableNames(){
		return new ArrayList<String>(tableNames);
	}
	
	public void addTableNames(Collection<String> tableNames){
		this.tableNames.addAll(tableNames);
	}
	
	public void addTableName(String tableName){
		tableNames.add(tableName);
	}
	
	public boolean deleteTableName(String tableName){
		return tableNames.remove(tableName);
	}
	
	/* Functions */
	
	public List<Function> getFunctions(){
		return new ArrayList<Function>(functionMap.values());
	}
	
	public List<String> getFunctionNames(){
		return new ArrayList<String>(functionMap.keySet());
	}
	
	public void addFunctions(Collection<Function> functions){
		for(Function f : functions){
			functionMap.put(f.getName(), f);
		}
	}
	
	public void addFunction(Function function){
		functionMap.put(function.getName(), function);
	}
	
	// TODO: change the return later
	public boolean deleteFunction(Function function){
		functionMap.remove(function.getName());
		return true;
	}
	
	public Function findFunctionByName(String functionName){
		return functionMap.get(functionName);
	}
	
	/* Features */
	
	public List<Feature> getFeatures(){
		return new ArrayList<Feature>(featureMap.values());
	}
	
	public List<String> getFeatureNames(){
		return new ArrayList<String>(featureMap.keySet());
	}
	
	public void addFeatures(Collection<Feature> features){
		for(Feature f : features){
			featureMap.put(f.getName(), f);
		}
	}
	
	public void addFeature(Feature feature){
		featureMap.put(feature.getName(),feature);
	}
	
	// TODO: change the return later
	public boolean deleteFeature(Feature feature){
		featureMap.remove(feature.getName());
		return true;
	}
	
	public Feature findFeatureByName(String featureName){
		return featureMap.get(featureName);
	}
	
	/* Rules */
	
	public List<Rule> getRules(){
		return new ArrayList<Rule>(ruleMap.values());
	}
	
	public List<String> getRuleNames(){
		return new ArrayList<String>(ruleMap.keySet());
	}
	
	public void addRules(Collection<Rule> rules){
		for(Rule r : rules){
			ruleMap.put(r.getName(), r);
		}
	}
	
	public void addRule(Rule rule){
		ruleMap.put(rule.getName(), rule);
	}

	// TODO: change the return later
	public boolean deleteRule(Rule rule){
		ruleMap.remove(rule.getName());
		return true;
	}

	public Rule findRuleByName(String ruleName){
		return ruleMap.get(ruleName);
	}
	
	/* Matchers */
	
	public List<Matcher> getMatchers(){
		return new ArrayList<Matcher>(matcherMap.values());
	}
	
	public List<String> getMatcherNames(){
		return new ArrayList<String>(matcherMap.keySet());
	}
	
	public void addMatchers(Collection<Matcher> matchers) {
		for(Matcher m : matchers){
			matcherMap.put(m.getName(), m);
		}
	}
	
	public void addMatcher(Matcher matcher) {
		matcherMap.put(matcher.getName(), matcher);
	}

	// TODO: change the return later
	public boolean deleteMatcher(Matcher matcher) {
		matcherMap.remove(matcher.getName());
		return true;
	}

	public Matcher findMatcherByName(String matcherName) {
		return matcherMap.get(matcherName);
	}
	
	/* Matching Summaries */
	
	public List<MatchingSummary> getMatchingSummary() {
		return new ArrayList<MatchingSummary>(matchingSummaryMap.values());
	}
	
	public List<String> getMatchingSummaryNames() {
		return new ArrayList<String>(matchingSummaryMap.keySet());
	}
	
	public void addMatchingSummaries(Collection<MatchingSummary> matchingSummaries) {
		for(MatchingSummary ms : matchingSummaries){
			matchingSummaryMap.put(ms.getName(), ms);
		}
	}
	
	public void addMatchingSummary(MatchingSummary matchingSummary) {
		matchingSummaryMap.put(matchingSummary.getName(), matchingSummary);
	}

	// TODO: change the return later
	public boolean deleteMatchingSummary(MatchingSummary matchingSummary) {
		matchingSummaryMap.remove(matchingSummary.getName());
		return true;
	}

	public MatchingSummary findMatchingSummaryByName(String matchingSummaryName){
		return matchingSummaryMap.get(matchingSummaryName);
	}
	
	/* Evaluation Summaries */
	
	public List<EvaluationSummary> getEvaluationSummary() {
		return new ArrayList<EvaluationSummary>(evaluationSummaryMap.values());
	}
	
	public List<String> getEvaluationSummaryNames() {
		return new ArrayList<String>(evaluationSummaryMap.keySet());
	}
	
	public void addEvaluationSummaries(Collection<EvaluationSummary> evaluationSummaries) {
		for(EvaluationSummary es : evaluationSummaries){
			evaluationSummaryMap.put(es.getName(), es);
		}
	}
	
	public void addEvaluationSummary(EvaluationSummary evaluationSummary) {
		evaluationSummaryMap.put(evaluationSummary.getName(), evaluationSummary);
	}

	// TODO: change the return later
	public boolean deleteEvaluationSummary(EvaluationSummary evaluationSummary) {
		evaluationSummaryMap.remove(evaluationSummary.getName());
		return true;
	}

	public EvaluationSummary findEvaluationSummaryByName(String evaluationSummaryName){
		return evaluationSummaryMap.get(evaluationSummaryName);
	}
	
	/* Defaults */
	public String getDefaultTable1() {
		return defaults.getDefaultTable1();
	}

	public void setDefaultTable1(String defaultTable1) {
		defaults.setDefaultTable1(defaultTable1);
	}

	public String getDefaultTable2() {
		return defaults.getDefaultTable2();
	}

	public void setDefaultTable2(String defaultTable2) {
		defaults.setDefaultTable2(defaultTable2);
	}

	public String getDefaultCandset() {
		return defaults.getDefaultCandset();
	}

	public void setDefaultCandset(String defaultCandset) {
		defaults.setDefaultCandset(defaultCandset);
	}

	public String getDefaultMatches() {
		return defaults.getDefaultMatches();
	}

	public void setDefaultMatches(String defaultMatches) {
		defaults.setDefaultMatches(defaultMatches);
	}

	public String getDefaultGold() {
		return defaults.getDefaultGold();
	}

	public void setDefaultGold(String defaultGold) {
		defaults.setDefaultGold(defaultGold);
	}

	/*
	public String getDefaultMatchesIdAttr1() {
		return defaults.getDefaultMatchesIdAttr1();
	}

	public void setDefaultMatchesIdAttr1(String defaultMatchesIdAttr1) {
		defaults.setDefaultMatchesIdAttr1(defaultMatchesIdAttr1);
	}

	public String getDefaultMatchesIdAttr2() {
		return defaults.getDefaultMatchesIdAttr2();
	}

	public void setDefaultMatchesIdAttr2(String defaultMatchesIdAttr2) {
		defaults.setDefaultMatchesIdAttr2(defaultMatchesIdAttr2);
	}

	public String getDefaultMatchesLabelAttr() {
		return defaults.getDefaultMatchesLabelAttr();
	}

	public void setDefaultMatchesLabelAttr(String defaultMatchesLabelAttr) {
		defaults.setDefaultMatchesLabelAttr(defaultMatchesLabelAttr);
	}

	public String getDefaultGoldIdAttr1() {
		return defaults.getDefaultGoldIdAttr1();
	}

	public void setDefaultGoldIdAttr1(String defaultGoldIdAttr1) {
		defaults.setDefaultGoldIdAttr1(defaultGoldIdAttr1);
	}

	public String getDefaultGoldIdAttr2() {
		return defaults.getDefaultGoldIdAttr2();
	}

	public void setDefaultGoldIdAttr2(String defaultGoldIdAttr2) {
		defaults.setDefaultGoldIdAttr2(defaultGoldIdAttr2);
	}

	public String getDefaultGoldLabelAttr() {
		return defaults.getDefaultGoldLabelAttr();
	}

	public void setDefaultGoldLabelAttr(String defaultGoldLabelAttr) {
		defaults.setDefaultGoldLabelAttr(defaultGoldLabelAttr);
	}
	*/
	
	public Set<String> getUnsavedTables() {
		return unsavedTables;
	}
	
	public Set<String> getUnsavedFunctions() {
		return unsavedFunctions;
	}
	
	public Set<String> getUnsavedFeatures() {
		return unsavedFeatures;
	}
	
	public Set<String> getUnsavedRules() {
		return unsavedRules;
	}
	
	public Set<String> getUnsavedMatchers() {
		return unsavedMatchers;
	}
	
	public boolean hasUnsavedArtifacts() {
		return !(unsavedTables.isEmpty() && unsavedFunctions.isEmpty()
				&& unsavedFeatures.isEmpty() && unsavedRules.isEmpty()
				&& unsavedMatchers.isEmpty());
	}
	
	public void addUnsavedTable(String tableName) {
		unsavedTables.add(tableName);
	}
	
	public boolean isUnsavedTable(String tableName) {
		return unsavedTables.contains(tableName);
	}
	
	public void removeUnsavedTable(String tableName) {
		unsavedTables.remove(tableName);
	}
	
	public void addUnsavedFunction(String functionName) {
		unsavedFunctions.add(functionName);
	}
	
	public boolean isUnsavedFunction(String functionName) {
		return unsavedFunctions.contains(functionName);
	}
	
	public void removeUnsavedFunction(String functionName) {
		unsavedFunctions.remove(functionName);
	}
	
	public void clearUnsavedFunctions() {
		unsavedFunctions.clear();
	}
	
	public void addUnsavedFeature(String featureName) {
		unsavedFeatures.add(featureName);
	}
	
	public boolean isUnsavedFeature(String featureName) {
		return unsavedFeatures.contains(featureName);
	}
	
	public void removeUnsavedFeature(String featureName) {
		unsavedFeatures.remove(featureName);
	}
	
	public void clearUnsavedFeatures() {
		unsavedFeatures.clear();
	}
	
	public void addUnsavedRule(String ruleName) {
		unsavedRules.add(ruleName);
	}
	
	public boolean isUnsavedRule(String ruleName) {
		return unsavedRules.contains(ruleName);
	}
	
	public void removeUnsavedRule(String ruleName) {
		unsavedRules.remove(ruleName);
	}
	
	public void clearUnsavedRules() {
		unsavedRules.clear();
	}
	
	public void addUnsavedMatcher(String matcherName) {
		unsavedMatchers.add(matcherName);
	}
	
	public boolean isUnsavedMatcher(String matcherName) {
		return unsavedMatchers.contains(matcherName);
	}
	
	public void removeUnsavedMatcher(String matcherName) {
		unsavedMatchers.remove(matcherName);
	}
	
	public void clearUnsavedMatchers() {
		unsavedMatchers.clear();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(name);
		sb.append(",");
		sb.append(description);
		sb.append(",");
		sb.append(createdOn);
		sb.append(",");
		sb.append(lastModifiedOn);
		sb.append("]");
		return sb.toString();
	}

  public ProjectDefaults getUserSetDefaults() {
    return defaults;
  }

  public void setUserSetDefaults(ProjectDefaults userSetDefaults) {
    this.defaults = userSetDefaults;
  }
	
}