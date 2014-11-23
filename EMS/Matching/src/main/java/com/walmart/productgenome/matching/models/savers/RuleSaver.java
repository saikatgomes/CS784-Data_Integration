package com.walmart.productgenome.matching.models.savers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.utils.ParsingUtils;

public class RuleSaver {
	private static String getAllRulesFilePath(String projectName) {
		return Constants.ROOT_DIR + projectName + "/" +
				Constants.PROJECT_RULES_FILE_NAME;
	}
	
	public static void addRule(String projectName, Rule rule) throws IOException {
		String allRulesFilePath = getAllRulesFilePath(projectName);
		addRule(rule, allRulesFilePath);
	}

	private static void printRule(CSVPrinter rulePrinter, Rule rule) throws IOException {
		// ruleName, table1Name, table2Name, ruleString
		rulePrinter.print(rule.getName());
		rulePrinter.print(rule.getTable1Name());
		rulePrinter.print(rule.getTable2Name());
		rulePrinter.print(rule.getDisplayString());
		rulePrinter.println();
	}
	
	public static void addRule(Rule rule, String allRulesFilePath) throws IOException {
		File file = new File(allRulesFilePath);
		boolean exists = false;
		if(file.exists()){
			exists = true;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(allRulesFilePath, true));
		CSVPrinter rulePrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		if(!exists) {
			printHeader(rulePrinter);
		}
		printRule(rulePrinter, rule);
		rulePrinter.close();
		bw.close();
	}

	public static void saveRule(String projectName, Rule rule, Project project) throws IOException {
		String allRulesFilePath = getAllRulesFilePath(projectName);
		saveRule(rule, allRulesFilePath, project);
	}

	public static void saveRule(Rule rule, String allRulesFilePath, Project project) throws IOException {
		// save the rule in place if it exists
		// else add the rule
		
		FileReader reader = new FileReader(allRulesFilePath);
		CSVParser parser = new CSVParser(reader);
		List<CSVRecord> records = parser.getRecords();
		reader.close();

		List<Rule> rules = new ArrayList<Rule>();
		int size = records.size();
		boolean found = false;
		for(int i = 1; i < size; i++){
			CSVRecord rec = records.get(i);
			String ruleName = rec.get(0).trim();
			if(ruleName.equals(rule.getName())) {
				rules.add(rule);
				found = true;
			}
			else {
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String ruleString = rec.get(3).trim();
				List<Term> terms = ParsingUtils.parseRuleFromDisplayString(project, ruleString);
				String projectName = project.getName();
				Rule r = new Rule(ruleName, projectName, table1Name, table2Name, terms);
				rules.add(r);
			}
		}
		
		if(!found) {
			rules.add(rule);
		}
		
		// save all the rules
		saveAllRules(rules, allRulesFilePath);
	}
	
	public static void saveAllRules(Project project) throws IOException {
		String projectName = project.getName();
		String allRulesFilePath = getAllRulesFilePath(projectName);
		saveAllRules(project.getRules(), allRulesFilePath);
	}
	
	private static void printHeader(CSVPrinter rulePrinter) throws IOException {
		// print the header
		rulePrinter.print("rule_name");
		rulePrinter.print("table1_name");
		rulePrinter.print("table2_name");
		rulePrinter.print("rule_string");
		rulePrinter.println();
	}
	
	private static void saveAllRules(List<Rule> rules,
			String allRulesFilePath) throws IOException {
		
		// rewrite all.rules file
		BufferedWriter bw = new BufferedWriter(new FileWriter(allRulesFilePath));
		CSVPrinter rulePrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		printHeader(rulePrinter);
		
		for(Rule rule : rules) {
			printRule(rulePrinter, rule);	
		}
		
		rulePrinter.close();
		bw.close();
	}
}
