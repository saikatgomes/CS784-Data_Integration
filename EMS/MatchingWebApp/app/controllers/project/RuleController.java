package controllers.project;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.productgenome.matching.daos.MatchingDao;
import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.daos.RuleDao;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.service.debug.Debug;
import com.walmart.productgenome.matching.service.debug.MatchingSummary;
import com.walmart.productgenome.matching.service.explorer.AttrStats;
import com.walmart.productgenome.matching.service.explorer.ExplorerDriver;
import com.walmart.productgenome.matching.service.recommender.FunctionRecommender;

public class RuleController extends Controller {

	public static Result addFunction(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String functionName = form.get("function_name");
		String functionDesc = form.get("function_desc");
		String functionClass = form.get("function_class");
		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		try{
			RuleDao.addFunction(name, functionName, functionDesc, functionClass, saveToDisk);
			ProjectController.statusMessage = "Successfully added Function " + functionName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		catch (SecurityException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (IllegalArgumentException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (ClassNotFoundException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (NoSuchMethodException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (InstantiationException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (IllegalAccessException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (InvocationTargetException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result importFunctions(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());

		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("csv_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();

			try{
				// import the functions - this automatically updates the project but does not save it
				List<Function> functions = RuleDao.importFunctions(name, file.getAbsolutePath(), saveToDisk);
				ProjectController.statusMessage = "Successfully imported " + functions.size() + " functions.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				ProjectController.statusMessage = "Error: " + ioe.getMessage();
			} catch (SecurityException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (IllegalArgumentException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (ClassNotFoundException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (NoSuchMethodException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (InstantiationException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (IllegalAccessException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			} catch (InvocationTargetException e) {
				flash("error", e.getMessage());
				ProjectController.statusMessage = "Error: " + e.getMessage();
			}
		} else {
			flash("error", "Missing file");
			ProjectController.statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result addFeature(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String featureName = form.get("feature_name");
		String functionName = form.get("function_name");
		String table1Name = form.get("table1_name_f");
		String table2Name = form.get("table2_name_f");
		String attr1Name = form.get("attr1_name_f");
		String attr2Name = form.get("attr2_name_f");

		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try{
			RuleDao.addFeature(name, featureName, functionName, table1Name,
					table2Name, attr1Name, attr2Name, saveToDisk);
			ProjectController.statusMessage = "Successfully added Feature " + featureName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	// TODO: Sanjib, review
	public static Result getRecommendedFunctions(String name) {

		List<String> functionNames = new ArrayList<String>();

		JsonNode json = request().body().asJson();

		String table1Name = json.findPath("table1_name_f").asText();
		String table2Name = json.findPath("table2_name_f").asText();
		String attr1Name = json.findPath("attr1_name_f").asText();
		String attr2Name = json.findPath("attr2_name_f").asText();

		ObjectNode result = Json.newObject();
		result.put("functionNames", Json.toJson(functionNames));

		if (json.size() == 0 || attr1Name.equals("null") || attr2Name.equals("null")) {
			return ok(Json.toJson(result));
		}

		try {
			Project project = ProjectDao.open(name);
			Table table1 = TableDao.open(name, table1Name);
			Table table2 = TableDao.open(name, table2Name);
			Attribute attr1 = table1.getAttributeByName(attr1Name);
			Attribute attr2 = table2.getAttributeByName(attr2Name);

			if (attr1 != null && attr2 != null) {
				// Note: topkfrequency is not going to be used hence the 0 arguments.
				AttrStats attr1Stats = ExplorerDriver.getAttrStats(table1, attr1, 0, 0);
				AttrStats attr2Stats = ExplorerDriver.getAttrStats(table2, attr2, 0, 0);
				List<Function> allFunctions = project.getFunctions();
				List<Function> functions1 = FunctionRecommender.getRecFunctionsForAttr(
						allFunctions, attr1Stats);
				List<Function> functions2 = FunctionRecommender.getRecFunctionsForAttr(
						allFunctions, attr2Stats);

				// get the intersection of recommended functions.
				for (Function function: functions1) {
					if (functions2.contains(function)) {
						functionNames.add(function.getName());
					}
				}
			}
		} catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		result.put("functionNames", Json.toJson(functionNames));

		return ok(result);
	}

	private static List<String> getFormArrayParameters(DynamicForm form, String param, int start) {
		List<String> vals = new ArrayList<String>();
		String val;
		while((val = form.get(param + start)) != null) {
			vals.add(val);
			start++;
		}
		return vals;
	}
	
	public static Result addRule(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String ruleName = form.get("rule_name");
		String table1Name = form.get("table1_name_r");
		String table2Name = form.get("table2_name_r");
		List<String> featureNames = getFormArrayParameters(form, "feature", 1);
		List<String> operators = getFormArrayParameters(form, "op", 1);
		List<String> values = getFormArrayParameters(form, "val", 1);

		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try{
			RuleDao.addRule(name, ruleName, table1Name, table2Name, featureNames,
					operators, values, saveToDisk);
			ProjectController.statusMessage = "Successfully added Rule " + ruleName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result editRule(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String ruleName = form.get("rule_name_e");
		String ruleString = form.get("rule_string");
		
		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try{
			RuleDao.editRule(name, ruleName, ruleString, saveToDisk);
			ProjectController.statusMessage = "Successfully edited Rule " + ruleName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result addMatcher(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String matcherName = form.get("matcher_name");
		String table1Name = form.get("table1_name_m");
		String table2Name = form.get("table2_name_m");
		String rule1Name = form.get("rule1");
		String rule2Name = form.get("rule2");
		String rule3Name = form.get("rule3");
		String rule4Name = form.get("rule4");
		String rule5Name = form.get("rule5");

		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}

		try{
			RuleDao.addMatcher(name, matcherName, table1Name, table2Name, rule1Name,
					rule2Name, rule3Name, rule4Name, rule5Name, saveToDisk);
			ProjectController.statusMessage = "Successfully added Matcher " + matcherName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result importFeatures(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("csv_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();
			Project project = ProjectDao.open(name);

			try{
				Table table1 = TableDao.open(name, table1Name);
				Table table2 = TableDao.open(name, table2Name);
				List<Feature> features = RuleDao.importFeaturesFromCSVWithHeader(project,
						table1, table2, file.getAbsolutePath());
				// save the features - this automatically updates and saves the project
				System.out.println(features);
				System.out.println(name);
				RuleDao.save(name, features);
				ProjectController.statusMessage = "Successfully imported " + features.size() + " features.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				ProjectController.statusMessage = "Error: " + ioe.getMessage();
			}
		} else {
			flash("error", "Missing file");
			ProjectController.statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result importRules(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("csv_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();
			Project project = ProjectDao.open(name);

			try{
				List<Rule> rules = RuleDao.importRulesFromCSVWithHeader(project,
						table1Name, table2Name, file.getAbsolutePath());
				// save the features - this automatically updates and saves the project
				RuleDao.saveRules(name, rules);
				ProjectController.statusMessage = "Successfully imported " + rules.size() + " rules.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				ProjectController.statusMessage = "Error: " + ioe.getMessage();
			}
		} else {
			flash("error", "Missing file");
			ProjectController.statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result importMatchers(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("csv_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();
			Project project = ProjectDao.open(name);

			try{
				List<Matcher> matchers = RuleDao.importMatchersFromCSVWithHeader(project,
						table1Name, table2Name, file.getAbsolutePath());
				// save the features - this automatically updates and saves the project
				RuleDao.saveMatchers(name, matchers);
				ProjectController.statusMessage = "Successfully imported " + matchers.size() + " matchers.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				ProjectController.statusMessage = "Error: " + ioe.getMessage();
			}
		} else {
			flash("error", "Missing file");
			ProjectController.statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result editMatcher(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String matcherName = form.get("matcher_name_ed");
		String matcherString = form.get("matcher_string");
		
		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try{
			RuleDao.editMatcher(name, matcherName, matcherString, saveToDisk);
			ProjectController.statusMessage = "Successfully edited Matcher " + matcherName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	public static Result editFeature(String name){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String featureName = form.get("feature_name_ed");
		String featureString = form.get("feature_string");
		
		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try{
			RuleDao.editFeature(name, featureName, featureString, saveToDisk);
			ProjectController.statusMessage = "Successfully edited Feature " + featureName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(name));
	}

	private static int getNumMatches(Table matches) {
		int numMatches = 0;
		List<Attribute> attributes = matches.getAttributes();
		Attribute labelAttr = attributes.get(attributes.size()-1);
		for(Object obj : matches.getAllValuesForAttribute(labelAttr)) {
			int label = (Integer) obj;
			if(MatchStatus.MATCH.getLabel() == label) {
				numMatches++;
			}
		}
		return numMatches;
	}

	public static Result doMatch(String projectName)
	{
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String candsetName = form.get("candset_name");
		String matcherName = form.get("matcher_name");
		String matchesName = form.get("matches_name");
		String[] table1AttributeNames = request().body().asFormUrlEncoded().get("attr1_names_m[]");
		String[] table2AttributeNames = request().body().asFormUrlEncoded().get("attr2_names_m[]");

		Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
		boolean saveToDisk = false;
		if(null != form.get("matches_default")){
			defaultTypes.add(DefaultType.MATCHES);
		}
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}

		try {
			Table matches = MatchingDao.match(projectName, candsetName, matcherName,
					matchesName, table1AttributeNames, table2AttributeNames);
			// save the matches - this automatically updates and saves the project
			TableDao.save(matches, defaultTypes, saveToDisk);
			int numMatches = getNumMatches(matches);
			int totalPairs = (int) matches.getSize();
			ProjectController.statusMessage = "Successfully created Matches " +
					matchesName + " having " + numMatches +
					" positive matches and " + (totalPairs-numMatches) +
					" negative matches.\n";
		} catch (IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage(); 
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result doMatchDebug(String projectName)
	{
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name_d");
		String table2Name = form.get("table2_name_d");
		String candsetName = form.get("candset_name");
		String matcherName = form.get("matcher_name");
		String matchesName = form.get("matches_name");
		String matchingSummaryName = form.get("matching_summary_name");
		Map<Tuple, ItemPairAudit> itemPairAudits = new HashMap<Tuple, ItemPairAudit>();

		String[] table1AttributeNames = request().body().asFormUrlEncoded().get("attr1_names_d[]");
		String[] table2AttributeNames = request().body().asFormUrlEncoded().get("attr2_names_d[]");

		Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
		boolean saveToDisk = false;
		if(null != form.get("matches_default")){
			defaultTypes.add(DefaultType.MATCHES);
		}
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try {
			Table matches = MatchingDao.match(projectName, candsetName, matcherName,
					matchesName, itemPairAudits, table1AttributeNames, table2AttributeNames);
			
			// save the matches - this automatically updates and saves the project
			TableDao.save(matches, defaultTypes, saveToDisk);
			//int numMatches = getNumMatches(matches);
			
			MatchingSummary matchingSummary = Debug.getMatchingSummary(itemPairAudits);
			int numMatches = matchingSummary.getTotalMatches();
			int totalPairs = matchingSummary.getTotalPairs();
			
			ProjectController.statusMessage = "Successfully created Matches " +
					matchesName + " having " + numMatches +
					" positive matches and " + (totalPairs-numMatches) +
					" negative matches.\n";
			matchingSummary.setName(matchingSummaryName);
			matchingSummary.setProjectName(projectName);
			matchingSummary.setTable1Name(table1Name);
			matchingSummary.setTable2Name(table2Name);
			matchingSummary.setCandsetName(candsetName);
			matchingSummary.setMatchesName(matchesName);
			matchingSummary.setMatcherName(matcherName);
			matchingSummary.setItemPairAudits(itemPairAudits);

			// add matching summary - this automatically updates and saves the project
			MatchingDao.addMatchingSummary(projectName, matchingSummary);
		} catch (IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage(); 
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result getRuleString(String projectName, String ruleName) {
		String ruleString = "";
		if(null != ruleName && !"null".equals(ruleName)) {
			ruleString = RuleDao.getRuleString(projectName, ruleName);
		}
		ObjectNode result = Json.newObject();
		result.put("ruleString", ruleString);
		return ok(result);
	}

	public static Result getMatcherString(String projectName, String matcherName) {
		String matcherString = "";
		if(null != matcherName && !"null".equals(matcherName)) {
			matcherString = RuleDao.getMatcherString(projectName, matcherName);
		}	
		ObjectNode result = Json.newObject();
		result.put("matcherString", matcherString);
		return ok(result);
	}

	public static Result getFeatureString(String projectName, String featureName) {
		String featureString = "";
		if(null != featureName && !"null".equals(featureName)) {
			featureString = RuleDao.getFeatureString(projectName, featureName);
		}	
		ObjectNode result = Json.newObject();
		result.put("featureString", featureString);
		return ok(result);
	}

	public static Result saveFunction(String projectName){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String functionName = form.get("function_name");

		try{
			RuleDao.saveFunction(projectName, functionName);
			ProjectController.statusMessage = "Successfully saved Function " + functionName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		catch (SecurityException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (IllegalArgumentException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (ClassNotFoundException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (NoSuchMethodException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (InstantiationException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (IllegalAccessException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		} catch (InvocationTargetException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result saveAllFunctions(String projectName) {
		try {
			// save the functions - this automatically updates the project but does not save it
			RuleDao.saveAllFunctions(projectName);
			ProjectController.statusMessage = "Successfully saved unsaved functions to disk.";
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
	
	public static Result saveFeature(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String featureName = form.get("feature_name");

		try{
			RuleDao.saveFeature(projectName, featureName);
			ProjectController.statusMessage = "Successfully saved Feature " + featureName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result saveAllFeatures(String projectName) {
		try {
			// save the features - this automatically updates the project but does not save it
			RuleDao.saveAllFeatures(projectName);
			ProjectController.statusMessage = "Successfully saved all unsaved features to disk.";
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
		
	public static Result saveRule(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String ruleName = form.get("rule_name");

		try{
			RuleDao.saveRule(projectName, ruleName);
			ProjectController.statusMessage = "Successfully saved Rule " + ruleName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result saveAllRules(String projectName) {
		try {
			// save the rules - this automatically updates the project but does not save it
			RuleDao.saveAllRules(projectName);
			ProjectController.statusMessage = "Successfully saved all unsaved rules to disk.";
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
	
	public static Result saveMatcher(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String matcherName = form.get("matcher_name");

		try{
			RuleDao.saveMatcher(projectName, matcherName);
			ProjectController.statusMessage = "Successfully saved Matcher " + matcherName + ".";
		}
		catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result saveAllMatchers(String projectName) {
		try {
			// save the matchers - this automatically updates the project but does not save it
			RuleDao.saveAllMatchers(projectName);
			ProjectController.statusMessage = "Successfully saved all unsaved matchers to disk.";
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			ProjectController.statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
}
