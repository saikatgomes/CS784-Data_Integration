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
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class FeatureSaver {

	private static String getAllFeaturesFilePath(String projectName) {
		return Constants.ROOT_DIR + projectName + "/" +
				Constants.PROJECT_FEATURES_FILE_NAME;
	}
	
	public static void addFeature(String projectName, Feature feature) throws IOException {
		String allFeaturesFilePath = getAllFeaturesFilePath(projectName);
		addFeature(feature, allFeaturesFilePath);
	}

	private static void printFeature(CSVPrinter featurePrinter, Feature feature) throws IOException {
		// featureName, table1Name, table2Name, functionName, attr1Name, attr1Type, attr2Name, attr2Type
		featurePrinter.print(feature.getName());
		featurePrinter.print(feature.getTable1Name());
		featurePrinter.print(feature.getTable2Name());
		featurePrinter.print(feature.getFunction().getName());
		featurePrinter.print(feature.getAttribute1().getName());
		featurePrinter.print(feature.getAttribute1().getType().name());
		featurePrinter.print(feature.getAttribute2().getName());
		featurePrinter.print(feature.getAttribute2().getType().name());
		featurePrinter.println();
	}
	
	public static void addFeature(Feature feature, String allFeaturesFilePath) throws IOException {
		File file = new File(allFeaturesFilePath);
		boolean exists = false;
		if(file.exists()){
			exists = true;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(allFeaturesFilePath, true));
		CSVPrinter featurePrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		if(!exists) {
			printHeader(featurePrinter);
		}
		printFeature(featurePrinter, feature);
		featurePrinter.close();
		bw.close();
	}

	public static void saveFeature(String projectName, Feature feature, Project project) throws IOException {
		String allFeaturesFilePath = getAllFeaturesFilePath(projectName);
		saveFeature(feature, allFeaturesFilePath, project);
	}

	public static void saveFeature(Feature feature, String allFeaturesFilePath, Project project) throws IOException {
		// save the feature in place if it exists
		// else add the feature
		
		FileReader r = new FileReader(allFeaturesFilePath);
		CSVParser parser = new CSVParser(r);
		List<CSVRecord> records = parser.getRecords();
		r.close();

		List<Feature> features = new ArrayList<Feature>();
		int size = records.size();
		boolean found = false;
		for(int i = 1; i < size; i++){
			CSVRecord rec = records.get(i);
			String featureName = rec.get(0).trim();
			if(featureName.equals(feature.getName())) {
				features.add(feature);
				found = true;
			}
			else {
				String table1Name = rec.get(1).trim();
				String table2Name = rec.get(2).trim();
				String functionName = rec.get(3).trim();
				String attr1Name = rec.get(4).trim();
				String attr1Type = rec.get(5).trim();
				String attr2Name = rec.get(6).trim();
				String attr2Type = rec.get(7).trim();
				
				String projectName = project.getName();
				Function function = project.findFunctionByName(functionName);
				Attribute attribute1 = new Attribute(attr1Name, Attribute.Type.valueOf(attr1Type));
				Attribute attribute2 = new Attribute(attr2Name, Attribute.Type.valueOf(attr2Type));
				Feature f = new Feature(featureName, function, projectName, table1Name, table2Name, attribute1, attribute2);
				features.add(f);
			}
		}
		
		if(!found) {
			features.add(feature);
		}
		
		// save all the features
		saveAllFeatures(features, allFeaturesFilePath);
	}
	
	public static void saveAllFeatures(Project project) throws IOException {
		String projectName = project.getName();
		String allFeaturesFilePath = getAllFeaturesFilePath(projectName);
		saveAllFeatures(project.getFeatures(), allFeaturesFilePath);
	}
	
	private static void printHeader(CSVPrinter featurePrinter) throws IOException {
		// print the header
		featurePrinter.print("feature_name");
		featurePrinter.print("table1_name");
		featurePrinter.print("table2_name");
		featurePrinter.print("function_name");
		featurePrinter.print("attribute1_name");
		featurePrinter.print("attribute1_type");
		featurePrinter.print("attribute2_name");
		featurePrinter.print("attribute2_type");
		featurePrinter.println();
	}
	
	private static void saveAllFeatures(List<Feature> features,
			String allFeaturesFilePath) throws IOException {
		
		// rewrite the all.features file
		BufferedWriter bw = new BufferedWriter(new FileWriter(allFeaturesFilePath));
		CSVPrinter featurePrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		printHeader(featurePrinter);
		
		for(Feature feature : features) {
			printFeature(featurePrinter, feature);	
		}
		
		featurePrinter.close();
		bw.close();
	}
	
}
