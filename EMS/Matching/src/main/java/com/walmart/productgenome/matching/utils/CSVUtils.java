package com.walmart.productgenome.matching.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class CSVUtils {

	public static List<Function> loadFunctions(String csvFunctionFilePath) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		List<Function> functions = new ArrayList<Function>();
		try {
			FileReader r = new FileReader(csvFunctionFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();
			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				String functionName = rec.get(0).trim();
				String functionDescription = rec.get(1).trim();
				String className = rec.get(2).trim();
				Class<?> functionClass = Class.forName(className);
				Constructor<?> constructor = functionClass.getConstructor(String.class, String.class);
				Function function = (Function) constructor.newInstance(functionName, functionDescription);
				functions.add(function);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}

		// System.out.println("No. of functions: " + functions.size());
		return functions;
	}

	public static List<Feature> readFeatures(Project project, Table table1,
			Table table2, String csvFeatureFilePath) throws IOException{
		List<Feature> features = new ArrayList<Feature>();

		try {
			FileReader r = new FileReader(csvFeatureFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				String featureName = rec.get(0);
				Function function = project.findFunctionByName(rec.get(1));

				System.out.println("Function: " + function);
				Attribute attribute1 = table1.getAttributeByName(rec.get(2));
				Attribute attribute2 = table2.getAttributeByName(rec.get(3));
				Feature feature = new Feature(featureName, function, project.getName(),
						table1.getName(), table2.getName(), attribute1, attribute2); 
				features.add(feature);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		// System.out.println("No. of features: " + features.size());
		return features;
	}

	public static List<Feature> loadFeatures(Project project, String csvFeatureFilePath) throws IOException {
		List<Feature> features = new ArrayList<Feature>();

		try {
			FileReader r = new FileReader(csvFeatureFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				// feature_name, table1_name, table2_name, function_name, attribute1_name,
				// attribute1_type, attribute2_name, attribute2_type
				String featureName = rec.get(0).trim();
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String functionName  = rec.get(3).trim();
				String attribute1Name = rec.get(4).trim();
				String attribute1Type = rec.get(5).trim();
				String attribute2Name = rec.get(6).trim();
				String attribute2Type  = rec.get(7).trim();

				String projectName = project.getName();
				Function function = project.findFunctionByName(functionName);
				Attribute attribute1 = new Attribute(attribute1Name,
						Attribute.Type.valueOf(attribute1Type));
				Attribute attribute2 = new Attribute(attribute2Name,
						Attribute.Type.valueOf(attribute2Type));
				Feature feature = new Feature(featureName, function, projectName, table1Name,
						table2Name, attribute1, attribute2);

				features.add(feature);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		return features;
	}

	public static List<Rule> readRules(Project project, String table1Name,
			String table2Name, String csvRuleFilePath) throws IOException{
		List<Rule> rules = new ArrayList<Rule>();
		try {
			FileReader r = new FileReader(csvRuleFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				String ruleName = rec.get(0).trim();
				String ruleString = rec.get(1).trim();
				List<Term> terms = ParsingUtils.parseRule(project, ruleString);
				Rule rule = new Rule(ruleName, project.getName(), table1Name,
						table2Name, terms); 
				rules.add(rule);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		// System.out.println("No. of rules: " + rules.size());
		return rules;
	}

	public static List<Rule> loadRules(Project project, String csvRuleFilePath) throws IOException {
		List<Rule> rules = new ArrayList<Rule>();
		try {
			FileReader r = new FileReader(csvRuleFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				// rule_name, table1_name, table2_name, rule_string
				String ruleName = rec.get(0).trim();
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String ruleString  = rec.get(3).trim();

				String projectName = project.getName();
				List<Term> terms = ParsingUtils.parseRuleFromDisplayString(project, ruleString);
				Rule rule = new Rule(ruleName, projectName, table1Name,
						table2Name, terms); 
				rules.add(rule);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		return rules;
	}

	public static List<Matcher> loadMatchers(Project project, String csvMatcherFilePath) throws IOException {
		List<Matcher> matchers = new ArrayList<Matcher>();

		try {
			FileReader r = new FileReader(csvMatcherFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				// matcher_name, table1_name, table2_name, matcher_string
				String matcherName = rec.get(0).trim();
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String matcherString  = rec.get(3).trim();

				String projectName = project.getName();
				List<Rule> rules = ParsingUtils.parseMatcherFromShortDisplayString(project, matcherString);
				Matcher matcher = new Matcher(matcherName, projectName, table1Name,
						table2Name, rules); 
				matchers.add(matcher);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		return matchers;
	}

	public static List<Matcher> readMatchers(Project project, String table1Name,
			String table2Name, String csvMatcherFilePath) throws IOException{
		List<Matcher> matchers = new ArrayList<Matcher>();
		try {
			FileReader r = new FileReader(csvMatcherFilePath);
			CSVParser parser = new CSVParser(r);
			List<CSVRecord> records = parser.getRecords();
			r.close();

			int size = records.size(); 
			for(int i = 1; i < size; i++){
				CSVRecord rec = records.get(i);
				String matcherName = rec.get(0).trim();
				String matcherString = rec.get(1);
				String[] ruleNames = matcherString.split(Constants.RULE_SEPARATOR);
				List<Rule> rules = new ArrayList<Rule>();
				for(String ruleName : ruleNames){
					Rule rule = project.findRuleByName(ruleName);
					rules.add(rule);
				}
				Matcher matcher = new Matcher(matcherName, project.getName(),
						table1Name, table2Name, rules); 
				matchers.add(matcher);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("File not found: " + fnfe.getMessage());
		}
		//System.out.println("No. of matchers: " + matchers.size());
		return matchers;
	}

	public static void addFunction(String projectName, Function function) {

	}

	public static void main(String[] args) {
	}

}
