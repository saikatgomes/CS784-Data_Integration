package com.walmart.productgenome.matching.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.evaluate.EvaluationSummary.IdPair;
import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.loaders.EvaluationSummaryLoader;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class JSONUtils {

	/* Common Constants */
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	
	/* Table Constants */
	public static final String TABLE = "table";
	public static final String ID_ATTRIB = "idAttrib";
	public static final String TYPE = "type";
	public static final String PROJECT_NAME = "projectName";
	public static final String ATTRIBUTES = "attributes";

	/* Project Constants */
	public static final String PROJECT = "project";
	public static final String CREATED_ON = "createdOn";
	public static final String LAST_MODIFIED_ON = "lastModifiedOn";
	public static final String TABLES = "tables";
	public static final String TABLE1 = "table1";
	public static final String TABLE2 = "table2";
	public static final String ATTRIBUTE1 = "attribute1";
	public static final String ATTRIBUTE2 = "attribute2";
	public static final String FUNCTION = "function";
	public static final String CLASS_NAME = "className";
	public static final String FUNCTIONS = "functions";
	public static final String FEATURES = "features";
	public static final String RULES = "rules";
	public static final String RULE_STRING = "ruleString";
	public static final String MATCHERS = "matchers";
	public static final String FUNCTION_NAME = "functionName";
	public static final String EVALUATION_SUMMARIES = "evaluationSummaries";
	
	public static final String ATTRIBUTE1_NAME = "attribute1_name";
	public static final String ATTRIBUTE2_NAME = "attribute2_name";
	public static final String ATTRIBUTE1_TYPE = "attribute1_type";
	public static final String ATTRIBUTE2_TYPE = "attribute2_type";
	
	public static final String DEFAULTS = "defaults";
	public static final String CANDSET = "candset";
	public static final String MATCHES = "matches";
	public static final String GOLD = "gold";
	
	/* Evaluation summary constants */
	public static final String EVALUATION_SUMMARY = "evaluation_summary";
	public static final String PRECISION = "precision";
	public static final String RECALL = "recall";
	public static final String F1 = "f1";
	public static final String ACTUAL_POSITIVES = "actualPositives";
	public static final String TRUE_POSITIVES = "truePositives";
	public static final String FALSE_POSITIVES = "falsePositives";
	public static final String ID1 = "id1";
	public static final String ID2 = "id2";
	
	private static Attribute getAttributeFromJSON(JsonObject attribObj){
		String attrName = attribObj.getString(NAME);
		String type = attribObj.getString(TYPE);
		Attribute.Type attrType = Attribute.Type.valueOf(type);
		return new Attribute(attrName,attrType);
	}

	private static Function getFunctionFromJSON(JsonObject functionObj) throws
		ClassNotFoundException, SecurityException, NoSuchMethodException,
		IllegalArgumentException, InstantiationException, IllegalAccessException,
		InvocationTargetException{
		String functionName = functionObj.getString(NAME);
        String functionDescription = functionObj.getString(DESCRIPTION);
        String className = functionObj.getString(CLASS_NAME);
        return getFunction(functionName, functionDescription, className);
	}
	
	private static Function getFunction(String functionName,
			String functionDescription, String className) throws
	ClassNotFoundException, SecurityException, NoSuchMethodException,
	IllegalArgumentException, InstantiationException, IllegalAccessException,
	InvocationTargetException{
		Class<?> functionClass = Class.forName(className);
		Constructor<?> constructor = functionClass.getConstructor(String.class, String.class);
		return (Function) constructor.newInstance(functionName, functionDescription);
	}
	
	public static Table getTableFromJSON(String tableJSON){
		JsonReader reader = Json.createReader(new StringReader(tableJSON));
		JsonObject obj = reader.readObject();
		JsonObject tableObj = obj.getJsonObject(TABLE);
		String name = tableObj.getString(NAME);
		String description = tableObj.getString(DESCRIPTION);
		JsonObject idAttribObj = tableObj.getJsonObject(ID_ATTRIB);
		Attribute idAttrib = null;
		if(null != idAttribObj){
			idAttrib = getAttributeFromJSON(idAttribObj);
		}
		String projectName = tableObj.getString(PROJECT_NAME);

		JsonArray attribs = tableObj.getJsonArray(ATTRIBUTES);
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (JsonObject attr : attribs.getValuesAs(JsonObject.class)) {
			attributes.add(getAttributeFromJSON(attr));
		}

		Table table = new Table(name,idAttrib,attributes,projectName);
		table.setDescription(description);

		return table;
	}
	
	public static String getTableJSON(Table table){
		Map<String, Object> properties = new HashMap<String, Object>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
		Writer sw = new StringWriter();
		JsonGenerator jg = jgf.createGenerator(sw);
		Attribute idAttribute = table.getIdAttribute();
		jg = jg.writeStartObject()                    				// {
			.writeStartObject(TABLE)         						//    "table":{
			.write(NAME, table.getName())             				//        "name":name,
			.write(DESCRIPTION, table.getDescription());     		//        "description":description,
		if(null != idAttribute){
			jg = jg.writeStartObject(ID_ATTRIB)						//		  "idAttrib": {
				.write(NAME,table.getIdAttribute().getName())  		//        	"name":name,
				.write(TYPE,table.getIdAttribute().getType().name())//        	"type":type
			.writeEnd();											// 		  }
		}
		jg = jg.write(PROJECT_NAME, table.getProjectName())  		//        "projectName":projectName,
			.writeStartArray(ATTRIBUTES);							//        "attributes": [
		for(Attribute attribute : table.getAttributes()){
			jg = jg.writeStartObject()              				//        	{
					.write(NAME, attribute.getName()) 				//            	"name":name,
					.write(TYPE, attribute.getType().name())  		//            	"type":type
					.writeEnd();									//        	},
		}
		jg.writeEnd()                          						//    	  ]	
		.writeEnd()													//	   }
		.writeEnd()                              					// }
		.close();

		return sw.toString();
	}
	
	private static IdPair getIdPairFromJSON(JsonObject idPairObj) {
		String id1 = idPairObj.getString(ID1);
		String id2 = idPairObj.getString(ID2);
		return new IdPair(id1,id2);
	}
	
	public static EvaluationSummary getEvaluationSummaryFromJSON(String evalSumJSON) {
		JsonReader reader = Json.createReader(new StringReader(evalSumJSON));
		JsonObject obj = reader.readObject();
		JsonObject evalSumObj = obj.getJsonObject(EVALUATION_SUMMARY);
		String name = evalSumObj.getString(NAME);
		String projectName = evalSumObj.getString(PROJECT_NAME);
		String matchesName = evalSumObj.getString(MATCHES);
		String goldName = evalSumObj.getString(GOLD);
		EvaluationSummary evalSum = new EvaluationSummary(name, projectName, matchesName, goldName);
		
		float precision = (float) evalSumObj.getJsonNumber(PRECISION).doubleValue();
		float recall = (float) evalSumObj.getJsonNumber(RECALL).doubleValue();
		float f1 = (float) evalSumObj.getJsonNumber(F1).doubleValue();
		evalSum.setPrecision(precision);
		evalSum.setRecall(recall);
		evalSum.setF1(f1);
		
		JsonArray actualPositivesArray = evalSumObj.getJsonArray(ACTUAL_POSITIVES);
		for (JsonObject idPair : actualPositivesArray.getValuesAs(JsonObject.class)) {
			evalSum.addActualPositive(getIdPairFromJSON(idPair));
		}
		JsonArray truePositivesArray = evalSumObj.getJsonArray(TRUE_POSITIVES);
		for (JsonObject idPair : truePositivesArray.getValuesAs(JsonObject.class)) {
			evalSum.addTruePositive(getIdPairFromJSON(idPair));
		}
		JsonArray falsePositivesArray = evalSumObj.getJsonArray(FALSE_POSITIVES);
		for (JsonObject idPair : falsePositivesArray.getValuesAs(JsonObject.class)) {
			evalSum.addFalsePositive(getIdPairFromJSON(idPair));
		}
		
		return evalSum;
	}
	
	public static String getEvaluationSummaryJSON(EvaluationSummary evaluationSummary){
		Map<String, Object> properties = new HashMap<String, Object>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
		Writer sw = new StringWriter();
		JsonGenerator jg = jgf.createGenerator(sw);
		jg = jg.writeStartObject()                    				// {
			.writeStartObject(EVALUATION_SUMMARY)         			//    "evaluation_summary":{
			.write(NAME, evaluationSummary.getName())             	//        "name":name,
			.write(PROJECT_NAME, evaluationSummary.getProjectName())//        "projectName":projectName,
			.write(MATCHES, evaluationSummary.getMatchesName())		//        "matches":matches,
			.write(GOLD, evaluationSummary.getGoldName())			//        "gold":gold,
			.write(PRECISION, evaluationSummary.getPrecision())		//        "precision":precision,
			.write(RECALL, evaluationSummary.getRecall())			//        "recall":recall,
			.write(F1, evaluationSummary.getF1())					//        "f1":f1,
			.writeStartArray(ACTUAL_POSITIVES);						//		  "actualPositives": [
		for(IdPair idPair : evaluationSummary.getActualPositives()) {
			jg = jg.writeStartObject()								//			{
					.write(ID1,idPair.getId1().toString())			//				"id1":id1,
					.write(ID2,idPair.getId2().toString())			//				"id2":id2
					.writeEnd();									//			}
		}
		jg.writeEnd()												//		  ],
			.writeStartArray(TRUE_POSITIVES);						//		  "truePositives": [
		for(IdPair idPair : evaluationSummary.getTruePositives()) {
			jg = jg.writeStartObject()								//			{
					.write(ID1,idPair.getId1().toString())			//				"id1":id1,
					.write(ID2,idPair.getId2().toString())			//				"id2":id2
					.writeEnd();									//			}
		}
		jg.writeEnd()												//		  ],
			.writeStartArray(FALSE_POSITIVES);						//		  "falsePositives": [
		for(IdPair idPair : evaluationSummary.getFalsePositives()) {
			jg = jg.writeStartObject()								//			{
					.write(ID1,idPair.getId1().toString())			//				"id1":id1,
					.write(ID2,idPair.getId2().toString())			//				"id2":id2
					.writeEnd();									//			}
		}
		jg.writeEnd()												//		  ]	
		.writeEnd()													//	   }
		.writeEnd()                              					// }
		.close();

		return sw.toString();
	}
	
	public static Project getProjectFromJSONFile(String projectName) throws ParseException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException{
		File infoFile = new File(Constants.ROOT_DIR + "/" +
				projectName + "/" + projectName + Constants.PROJECT_EXTENSION);
		JsonReader reader = Json.createReader(new FileReader(infoFile));
        JsonObject obj = reader.readObject();
        JsonObject projObj = obj.getJsonObject(PROJECT);
        String name = projObj.getString(NAME);
        String description = projObj.getString(DESCRIPTION);
        String createdOn = projObj.getString(CREATED_ON);
        String lastModifiedOn = projObj.getString(LAST_MODIFIED_ON);
        
        Project p = new Project(name,description);
        p.setCreatedOn(createdOn);
        p.setLastModifiedOn(lastModifiedOn);
        
        /*
        JsonArray tables = projObj.getJsonArray(TABLES);
        List<String> tableNames = new ArrayList<String>();
        for (JsonObject table : tables.getValuesAs(JsonObject.class)) {
            String tableName = table.getString(NAME);
            tableNames.add(tableName);
        }
        p.addTableNames(tableNames);
        */
        
        String projectDirectoryName = Constants.ROOT_DIR + "/" + projectName;
        File projectDirectory = new File(projectDirectoryName);
        File[] tableFiles = projectDirectory.listFiles(new FilenameFilter() { 
        	public boolean accept(File dir, String filename) {
        		return filename.endsWith(Constants.TABLE_EXTENSION);
        	}
        } );
        
        // sort the files on the last modification time
        // TODO: sorting on creation time seems to be a pain
        Arrays.sort(tableFiles, new Comparator<File>() {
        	public int compare(File f1, File f2) {
        		return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
        	}
        });
        
        List<String> tableNames = new ArrayList<String>();
        for(File tableFile : tableFiles) {
        	String tableName = tableFile.getName();
        	if(tableName.indexOf(".") > 0) {
        		// has an extension
        	    tableName = tableName.substring(0, tableName.lastIndexOf("."));
        	}
        	tableNames.add(tableName);
        }
        p.addTableNames(tableNames);
        
        /*
        JsonArray functionList = projObj.getJsonArray(FUNCTIONS);
        List<Function> functions = new ArrayList<Function>();
        for (JsonObject functionObj : functionList.getValuesAs(JsonObject.class)) {
            Function function = getFunctionFromJSON(functionObj);
            functions.add(function);
        }
        p.addFunctions(functions);
        */
        
        String csvFunctionFilePath = Constants.ROOT_DIR + "/" +
				projectName + "/" + Constants.PROJECT_FUNCTIONS_FILE_NAME;
        List<Function> functions = CSVUtils.loadFunctions(csvFunctionFilePath);
        p.addFunctions(functions);
        
        /*
        JsonArray featureList = projObj.getJsonArray(FEATURES);
        List<Feature> features = new ArrayList<Feature>();
        for (JsonObject featureObj : featureList.getValuesAs(JsonObject.class)) {
            String featureName = featureObj.getString(NAME);
            String table1Name = featureObj.getString(TABLE1);
            String table2Name = featureObj.getString(TABLE2);
            String functionName  = featureObj.getString(FUNCTION_NAME); 
            Function function = p.findFunctionByName(functionName);
            Attribute attribute1 = new Attribute(featureObj.getString(ATTRIBUTE1_NAME),
            		Attribute.Type.valueOf(featureObj.getString(ATTRIBUTE1_TYPE)));
            Attribute attribute2 = new Attribute(featureObj.getString(ATTRIBUTE2_NAME),
            		Attribute.Type.valueOf(featureObj.getString(ATTRIBUTE2_TYPE)));
            Feature feature = new Feature(featureName, function, name, table1Name,
            		table2Name, attribute1, attribute2);
            features.add(feature);
        }
        */
        
        String csvFeatureFilePath = Constants.ROOT_DIR + "/" +
				projectName + "/" + Constants.PROJECT_FEATURES_FILE_NAME;
        List<Feature> features = CSVUtils.loadFeatures(p, csvFeatureFilePath);
        p.addFeatures(features);
        
        /*
        JsonArray ruleList = projObj.getJsonArray(RULES);
        List<Rule> rules = new ArrayList<Rule>();
        for (JsonObject ruleObj : ruleList.getValuesAs(JsonObject.class)) {
            String ruleName = ruleObj.getString(NAME);
            String table1Name = ruleObj.getString(TABLE1);
            String table2Name = ruleObj.getString(TABLE2);
            String ruleString = ruleObj.getString(RULE_STRING);
            List<Term> terms = ParsingUtils.parseRule(p, ruleString);
            rules.add(new Rule(ruleName, name, table1Name, table2Name, terms));
        }
        */
        
        String csvRuleFilePath = Constants.ROOT_DIR + "/" +
				projectName + "/" + Constants.PROJECT_RULES_FILE_NAME;
        List<Rule> rules = CSVUtils.loadRules(p, csvRuleFilePath);
        p.addRules(rules);
        
        /*
        JsonArray matcherList = projObj.getJsonArray(MATCHERS);
        List<Matcher> matchers = new ArrayList<Matcher>();
        for (JsonObject matcherObj : matcherList.getValuesAs(JsonObject.class)) {
            String matcherName = matcherObj.getString(NAME);
            String table1Name = matcherObj.getString(TABLE1);
            String table2Name = matcherObj.getString(TABLE2);
            JsonArray matcherRuleList = matcherObj.getJsonArray(RULES);
            List<Rule> matcherRules = new ArrayList<Rule>();
            for (JsonObject ruleObj : matcherRuleList.getValuesAs(JsonObject.class)) {
                String ruleName = ruleObj.getString(NAME);
            	Rule rule = p.findRuleByName(ruleName);
                matcherRules.add(rule);
            }
            matchers.add(new Matcher(matcherName, name, table1Name, table2Name, matcherRules));
        }
        */
        
        String csvMatcherFilePath = Constants.ROOT_DIR + "/" +
				projectName + "/" + Constants.PROJECT_MATCHERS_FILE_NAME;
        List<Matcher> matchers = CSVUtils.loadMatchers(p, csvMatcherFilePath);
        p.addMatchers(matchers);
        
        File[] evaluationSummaryFiles = projectDirectory.listFiles(new FilenameFilter() { 
        	public boolean accept(File dir, String filename) {
        		return filename.endsWith(Constants.EVALUATION_SUMMARY_EXTENSION);
        	}
        } );
        
        // sort the files on the last modification time
        // TODO: sorting on creation time seems to be a pain
        Arrays.sort(evaluationSummaryFiles, new Comparator<File>() {
        	public int compare(File f1, File f2) {
        		return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
        	}
        });
        
        List<EvaluationSummary> evaluationSummaries = new ArrayList<EvaluationSummary>();
        for(File evalSumFile : evaluationSummaryFiles) {
        	EvaluationSummary evalSum = EvaluationSummaryLoader.loadEvaluationSummary(evalSumFile);
        	evaluationSummaries.add(evalSum);
        }
        p.addEvaluationSummaries(evaluationSummaries);
        
        JsonObject defaultsObj = projObj.getJsonObject(DEFAULTS);
        if(null != defaultsObj) {
        	String table1Name = defaultsObj.getString(TABLE1);
        	String table2Name = defaultsObj.getString(TABLE2);
        	String candsetName = defaultsObj.getString(CANDSET);
        	String matchesName = defaultsObj.getString(MATCHES);
        	String goldName = defaultsObj.getString(GOLD);
        	p.setDefaultTable1(table1Name);
        	p.setDefaultTable2(table2Name);
        	p.setDefaultCandset(candsetName);
        	p.setDefaultMatches(matchesName);
        	p.setDefaultGold(goldName);
        }
        
        return p;
    }
	
	public static String getProjectJSON(Project project){
		Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
        Writer sw = new StringWriter();
        JsonGenerator jg = jgf.createGenerator(sw);
         
        jg = jg.writeStartObject()                    						// {
            .writeStartObject(PROJECT)         								//    "project":{
                .write(NAME, project.getName())             				//        "name":name,
                .write(DESCRIPTION, project.getDescription())  				//        "description":description,
                .write(CREATED_ON, project.getCreatedOnString())  			//        "createdOn":createdOn,
                .write(LAST_MODIFIED_ON, project.getLastModifiedOnString())	//        "lastModifiedOn":lastModifiedOn
                .writeStartArray(TABLES);									//		  "tables":[
        for(String s : project.getTableNames()){
        	jg = jg.writeStartObject()										//			{
                		.write(NAME,s)										//				"name":name
                	.writeEnd();											//			}
        }
        jg.writeEnd()														//	  	  ],
    		.writeStartArray(FUNCTIONS);									//		  "functions":[
        for(Function f : project.getFunctions()){
        	jg = jg.writeStartObject()										//			{
            		.write(NAME, f.getName())								//				"name":name
            	.writeEnd();												//			}
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartArray(FEATURES);										//		  "features":[
        for(Feature f : project.getFeatures()){
        	jg = jg.writeStartObject()										//			{
                		.write(NAME, f.getName())							//				"name":name
                	.writeEnd();											//			}
        }
        jg.writeEnd()														//	  	  ],
    		.writeStartArray(RULES);										//		  "rules":[
        for(Rule r : project.getRules()){
        	jg = jg.writeStartObject()										//			{
            		.write(NAME,r.getName())								//				"name":name
            	.writeEnd();												//			}
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartArray(MATCHERS);										//		  "matchers":[
        for(Matcher m : project.getMatchers()){
        	jg = jg.writeStartObject()										//			{
                	.write(NAME,m.getName())								//				"name":name
            	.writeEnd();												//			},
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartArray(EVALUATION_SUMMARIES);							//		  "evaluationSummaries":[
        for(String evaluationSummaryName : project.getEvaluationSummaryNames()){
        	jg = jg.writeStartObject()										//			{
                	.write(NAME,evaluationSummaryName)						//				"name":name
            	.writeEnd();												//			},
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartObject(DEFAULTS)										//		  "defaults":{
        		.write(TABLE1,project.getDefaultTable1())					//			"table1":table1Name,
        		.write(TABLE2,project.getDefaultTable2())					//			"table2":table2Name,
        		.write(CANDSET,project.getDefaultCandset())					//			"candset":candsetName,
        		.write(MATCHES,project.getDefaultMatches())					//			"matches":matchesName,
        		.write(GOLD,project.getDefaultGold())						//			"gold":goldName,
        	.writeEnd()														//		  }
        .writeEnd()															// 		}
        .writeEnd()															// }
        .close();
        
        return sw.toString();
	}
}
