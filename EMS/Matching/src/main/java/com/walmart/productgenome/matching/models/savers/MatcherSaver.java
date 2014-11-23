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
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.utils.ParsingUtils;

public class MatcherSaver {

	private static String getAllMatchersFilePath(String projectName) {
		return Constants.ROOT_DIR + projectName + "/" +
				Constants.PROJECT_MATCHERS_FILE_NAME;
	}
	
	public static void addMatcher(String projectName, Matcher matcher) throws IOException {
		String allMatchersFilePath = getAllMatchersFilePath(projectName);
		addMatcher(matcher, allMatchersFilePath);
	}

	private static void printMatcher(CSVPrinter matcherPrinter, Matcher matcher) throws IOException {
		// matcherName, table1Name, table2Name, matcherString
		matcherPrinter.print(matcher.getName());
		matcherPrinter.print(matcher.getTable1Name());
		matcherPrinter.print(matcher.getTable2Name());
		matcherPrinter.print(matcher.getShortDisplayString());
		matcherPrinter.println();
	}
	
	public static void addMatcher(Matcher matcher, String allMatchersFilePath) throws IOException {
		File file = new File(allMatchersFilePath);
		boolean exists = false;
		if(file.exists()){
			exists = true;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(allMatchersFilePath, true));
		CSVPrinter matcherPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		if(!exists) {
			printHeader(matcherPrinter);
		}
		printMatcher(matcherPrinter, matcher);
		matcherPrinter.close();
		bw.close();
	}

	public static void saveMatcher(String projectName, Matcher matcher, Project project) throws IOException {
		String allMatchersFilePath = getAllMatchersFilePath(projectName);
		saveMatcher(matcher, allMatchersFilePath, project);
	}

	public static void saveMatcher(Matcher matcher, String allMatchersFilePath, Project project) throws IOException {
		// save the matcher in place if it exists
		// else add the matcher
		
		FileReader reader = new FileReader(allMatchersFilePath);
		CSVParser parser = new CSVParser(reader);
		List<CSVRecord> records = parser.getRecords();
		reader.close();

		List<Matcher> matchers = new ArrayList<Matcher>();
		int size = records.size();
		boolean found = false;
		for(int i = 1; i < size; i++){
			CSVRecord rec = records.get(i);
			String matcherName = rec.get(0).trim();
			if(matcherName.equals(matcher.getName())) {
				matchers.add(matcher);
				found = true;
			}
			else {
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String matcherString = rec.get(3).trim();
				List<Rule> rules = ParsingUtils.parseMatcherFromShortDisplayString(project, matcherString);
				String projectName = project.getName();
				Matcher m = new Matcher(matcherName, projectName, table1Name, table2Name, rules);
				matchers.add(m);
			}
		}
		
		if(!found) {
			matchers.add(matcher);
		}
		
		// save all the matchers
		saveAllMatchers(matchers, allMatchersFilePath);
	}
	
	public static void saveAllMatchers(Project project) throws IOException {
		String projectName = project.getName();
		String allMatchersFilePath = getAllMatchersFilePath(projectName);
		saveAllMatchers(project.getMatchers(), allMatchersFilePath);
	}
	
	private static void printHeader(CSVPrinter matcherPrinter) throws IOException {
		// print the header
		matcherPrinter.print("matcher_name");
		matcherPrinter.print("table1_name");
		matcherPrinter.print("table2_name");
		matcherPrinter.print("matcher_string");
		matcherPrinter.println();
	}
	
	private static void saveAllMatchers(List<Matcher> matchers,
			String allMatchersFilePath) throws IOException {
		
		// rewrite all.rules file
		BufferedWriter bw = new BufferedWriter(new FileWriter(allMatchersFilePath));
		CSVPrinter matcherPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		printHeader(matcherPrinter);
		
		for(Matcher matcher : matchers) {
			printMatcher(matcherPrinter, matcher);	
		}
		
		matcherPrinter.close();
		bw.close();
	}
	
}
