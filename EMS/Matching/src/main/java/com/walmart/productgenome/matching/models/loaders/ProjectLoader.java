package com.walmart.productgenome.matching.models.loaders;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.utils.CSVUtils;
import com.walmart.productgenome.matching.utils.ParsingUtils;

public class ProjectLoader {

	public static Project loadProject(String projectName) {
		File projectFile = new File(Constants.ROOT_DIR + "/" +
				projectName + "/" + projectName + Constants.PROJECT_EXTENSION);
		return loadProject(projectFile);
	}
	
	public static Project loadProject(File projectFile) {
		JsonReader reader = Json.createReader(new FileReader(projectFile));
        JsonObject obj = reader.readObject();
        JsonObject projObj = obj.getJsonObject(PROJECT);
        String name = projObj.getString(NAME);
        String description = projObj.getString(DESCRIPTION);
        String createdOn = projObj.getString(CREATED_ON);
        String lastModifiedOn = projObj.getString(LAST_MODIFIED_ON);
        
        Project p = new Project(name,description);
        p.setCreatedOn(createdOn);
        p.setLastModifiedOn(lastModifiedOn);
        
        JsonArray tables = projObj.getJsonArray(TABLES);
        List<String> tableNames = new ArrayList<String>();
        for (JsonObject table : tables.getValuesAs(JsonObject.class)) {
            String tableName = table.getString(NAME);
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
        
        // System.out.println("No. of features: " + features.size());
        
        p.addFeatures(features);
        
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
        p.addRules(rules);
        
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
        p.addMatchers(matchers);
        
        return p;
	}
}
