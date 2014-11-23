package com.walmart.productgenome.matching.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class JSONUtilsNew {

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
	
  public static final String ATTRIBUTE1_NAME = "attribute1_name";
  public static final String ATTRIBUTE2_NAME = "attribute2_name";
  public static final String ATTRIBUTE1_TYPE = "attribute1_type";
  public static final String ATTRIBUTE2_TYPE = "attribute2_type";

	
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
	
	public static Project getProjectFromJSONFile(File infoFile) throws FileNotFoundException, ParseException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException{
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
        
        JsonArray tables = projObj.getJsonArray(TABLES);
        List<String> tableNames = new ArrayList<String>();
        for (JsonObject table : tables.getValuesAs(JsonObject.class)) {
            String tableName = table.getString(NAME);
            tableNames.add(tableName);
        }
        p.addTableNames(tableNames);
        
        JsonArray functionList = projObj.getJsonArray(FUNCTIONS);
        List<Function> functions = new ArrayList<Function>();
        for (JsonObject functionObj : functionList.getValuesAs(JsonObject.class)) {
            Function function = getFunctionFromJSON(functionObj);
            functions.add(function);
        }
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
            		.write(NAME, f.getName())								//				"name":name,
            		.write(DESCRIPTION, f.getDescription())					//				"description":description,
            		.write(CLASS_NAME, f.getClass().getName())				//				"className":className
            	.writeEnd();												//			}
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartArray(FEATURES);										//		  "features":[
        for(Feature f : project.getFeatures()){
          
          System.out.println(f);
          Function func = f.getFunction();
          jg = jg.writeStartObject()                    //      {
                    .write(NAME, f.getName())             //        "name":name,
                    .write(TABLE1, f.getTable1Name())         //        "table1":table1Name,
                    .write(TABLE2, f.getTable2Name())         //        "table2":table2Name,
                    .write(FUNCTION_NAME, func.getName())       //        "functionName":functionName         
                    .write(ATTRIBUTE1_NAME, f.getAttribute1().getName())//        "attribute1_name":attribute1Name
                    .write(ATTRIBUTE2_NAME, f.getAttribute2().getName())//        "attribute2_name":attribute2Name
                    .write(ATTRIBUTE1_TYPE, f.getAttribute1().getType().name())//       "attribute1_type":attribute1Type
                    .write(ATTRIBUTE2_TYPE, f.getAttribute2().getType().name())//       "attribute2_type":attribute2Type
                  .writeEnd();                      //      }
        }

        jg.writeEnd()														//	  	  ],
    		.writeStartArray(RULES);										//		  "rules":[
        for(Rule r : project.getRules()){
        	jg = jg.writeStartObject()										//			{
            		.write(NAME,r.getName())								//				"name":name
            		.write(TABLE1,r.getTable1Name())						//				"table1":table1Name,
            		.write(TABLE2,r.getTable2Name())						//				"table2":table2Name,
            		.write(RULE_STRING, r.getRuleString())					//				"ruleString":ruleString
            	.writeEnd();												//			}
        }
        jg.writeEnd()														//	  	  ],
        	.writeStartArray(MATCHERS);										//		  "matchers":[
        for(Matcher p : project.getMatchers()){
        	List<Rule> rules = p.getRules();
        	jg = jg.writeStartObject()										//			{
                		.write(NAME,p.getName())							//				"name":name
                		.write(TABLE1,p.getTable1Name())					//				"table1":table1Name,
                		.write(TABLE2,p.getTable2Name())					//				"table2":table2Name,
                		.writeStartArray(RULES);							//				"rules":[
            for(Rule r : rules){
            	jg = jg.writeStartObject()									//					{
            				.write(NAME,r.getName())						//						"name":name	
        				.writeEnd();										//					},
            }
            jg = jg.writeEnd()												//				]
                	.writeEnd();											//			},
        }
        jg.writeEnd()														//	  	  ]
        .writeEnd()															// 		}
        .writeEnd()															// }
        .close();
        
        return sw.toString();
	}
}
