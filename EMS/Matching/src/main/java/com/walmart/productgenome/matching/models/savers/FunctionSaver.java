package com.walmart.productgenome.matching.models.savers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class FunctionSaver {

	private static String getAllFunctionsFilePath(String projectName) {
		return Constants.ROOT_DIR + projectName + "/" +
				Constants.PROJECT_FUNCTIONS_FILE_NAME;
	}
	
	public static void addFunction(String projectName, Function function) throws IOException {
		String allFunctionsFilePath = getAllFunctionsFilePath(projectName);
		addFunction(function, allFunctionsFilePath);
	}

	public static void addFunctions(String projectName, List<Function> functions) throws IOException {
		String allFunctionsFilePath = getAllFunctionsFilePath(projectName);
		addFunctions(functions, allFunctionsFilePath);
	}
	
	public static void addFunction(Function function, String allFunctionsFilePath) throws IOException {
		File file = new File(allFunctionsFilePath);
		boolean exists = false;
		if(file.exists()){
			exists = true;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(allFunctionsFilePath, true));
		CSVPrinter functionPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		if(!exists) {
			printHeader(functionPrinter);
		}
		printFunction(functionPrinter, function);
		functionPrinter.close();
		bw.close();
	}

	public static void addFunctions(List<Function> functions, String allFunctionsFilePath) throws IOException {
		File file = new File(allFunctionsFilePath);
		boolean exists = false;
		if(file.exists()){
			exists = true;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(allFunctionsFilePath, true));
		CSVPrinter functionPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
		if(!exists) {
			printHeader(functionPrinter);
		}
		for(Function function : functions) {
			printFunction(functionPrinter, function);
		}	
		functionPrinter.close();
		bw.close();
	}
	
	public static void saveFunction(String projectName, Function function) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String allFunctionsFilePath = getAllFunctionsFilePath(projectName);
		saveFunction(function, allFunctionsFilePath);
	}

	public static void saveFunction(Function function, String allFunctionsFilePath) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// save the function in place if it exists
		// else add the function
		
		FileReader r = new FileReader(allFunctionsFilePath);
		CSVParser parser = new CSVParser(r);
		List<CSVRecord> records = parser.getRecords();
		r.close();

		List<Function> functions = new ArrayList<Function>();
		int size = records.size();
		boolean found = false;
		for(int i = 1; i < size; i++){
			CSVRecord rec = records.get(i);
			String functionName = rec.get(0).trim();
			if(functionName.equals(function.getName())) {
				functions.add(function);
				found = true;
			}
			else {
				String functionDescription = rec.get(1).trim();
				String className = rec.get(2).trim();
				Class<?> functionClass = Class.forName(className);
				Constructor<?> constructor = functionClass.getConstructor(String.class, String.class);
				Function f = (Function) constructor.newInstance(functionName, functionDescription);
				functions.add(f);
			}
		}
		
		if(!found) {
			functions.add(function);
		}
		
		// save all the functions
		saveAllFunctions(functions, allFunctionsFilePath);
	}
	
	public static void saveAllFunctions(Project project) throws IOException {
		String projectName = project.getName();
		String allFunctionsFilePath = getAllFunctionsFilePath(projectName);
		saveAllFunctions(project.getFunctions(), allFunctionsFilePath);
	}
	
	private static void saveAllFunctions(List<Function> functions,
			String allFunctionsFilePath) throws IOException {
		
		// rewrite the all.functions file
		BufferedWriter bw = new BufferedWriter(new FileWriter(allFunctionsFilePath));

		CSVPrinter functionPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());

		// print the header
		printHeader(functionPrinter);
		
		for(Function function : functions) {
			printFunction(functionPrinter, function);	
		}
		
		functionPrinter.close();
		bw.close();
	}
	
	private static void printHeader(CSVPrinter functionPrinter) throws IOException {
		// print the header
		// function_name,function_description,function_class_name
		functionPrinter.print("function_name");
		functionPrinter.print("function_description");
		functionPrinter.print("function_class_name");
		functionPrinter.println();
	}
	
	private static void printFunction(CSVPrinter functionPrinter, Function function) throws IOException {
		// functionName,functionDescription,functionClassName 
		functionPrinter.print(function.getName());
		functionPrinter.print(function.getDescription());
		functionPrinter.print(function.getClass().getName());
		functionPrinter.println();
	}
}
