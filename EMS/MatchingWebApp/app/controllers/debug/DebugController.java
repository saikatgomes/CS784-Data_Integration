package controllers.debug;

import static play.data.Form.form;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.common.common_main;
import views.html.common.common_topnav;
import views.html.debug.debug_topbar;
import views.html.facets_layout.facets_layout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.productgenome.matching.daos.EvaluateDao;
import com.walmart.productgenome.matching.daos.MatchingDao;
import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.evaluate.EvaluationSummary.IdPair;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.audit.RuleAudit;
import com.walmart.productgenome.matching.models.audit.TermAudit;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Table.QueryOps;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.Term;
import com.walmart.productgenome.matching.service.debug.Debug;
import com.walmart.productgenome.matching.service.debug.MatchingSummary;
import com.walmart.productgenome.matching.service.skyline.Skyline;
import com.walmart.productgenome.matching.service.skyline.Skyline.SimSelection;

public class DebugController extends Controller {

	/*
	 * Note: In order to maintain usability of the system I am making assumptions
	 * about the columns present in the candidate set and gold table.
	 * Here are the assumptions:
	 * 1) The second and third column in the candidate set table refer to table1.id and table2.id
	 * and are named <table1>.id and <table2>.id
	 * 2) In the match table at least the columns <table1>.id, <table2>.id, label exists
	 * 3) The gold table at least has columns named: "id1", "id2", "label"
	 */

	private static Project project;
	private static Table table1;
	private static Table table2;
	private static Table candset;
	private static Table matches;
	private static Matcher matcher;
	private static Attribute candsetAttr1;
	private static Attribute candsetAttr2;
	private static Map<Tuple, ItemPairAudit> itemPairAudits;
	private static Map<Object, Map<String, Float>> generatedFeatures;
	private static MatchingSummary matchingSummary;
	private static EvaluationSummary evalSummary;
	private static int operationMode;// 1: both eval and matching present. 2: only matching present 3: neither are present.

	public static Result getIndex(String projectName, String table1Name, 
			String table2Name, String candsetName, String matcherName, String goldName) {

		operationMode = 1;

		readTables(projectName, table1Name, table2Name, candsetName);

		String matchesName = "matches";

		itemPairAudits = new HashMap<Tuple, ItemPairAudit>();
		project = ProjectDao.open(projectName);
		matcher = project.findMatcherByName(matcherName);

		try {
			matches = MatchingDao.match(projectName, candset, table1, table2, matcherName, matchesName, itemPairAudits, null, null);
			// evalSummary = EvaluateDao.evaluateWithGold("tmp", projectName, matches, table1Name + ".id", table2Name + ".id", "label", goldName, "id1", "id2", "label");
			evalSummary = EvaluateDao.evaluateWithGold("tmp", projectName, matches, goldName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		matchingSummary = Debug.getMatchingSummary(itemPairAudits);

		candsetAttr1 = candset.getAttributes().get(1);
		candsetAttr2 = candset.getAttributes().get(2);

		generateFeatures();

		return showPairs();
	}

	public static Result postIndexUsingResults(String projectName) {

		DynamicForm form = form().bindFromRequest();
		String matchingSummaryName = form.get("matching_summary_name");
		String evaluationSummaryName = form.get("evaluation_summary_name");
		project = ProjectDao.open(projectName);
		matchingSummary = project.findMatchingSummaryByName(matchingSummaryName);

		String table1Name = matchingSummary.getTable1Name();
		String table2Name = matchingSummary.getTable2Name();
		String candsetName = matchingSummary.getCandsetName();
		String matcherName = matchingSummary.getMatcherName();

		readTables(projectName, table1Name, table2Name, candsetName);

		// I need to read the matches table to filter by matches.
		try {
			// Logger.info("matches name: " + matchingSummary.getMatchesName());
			matches = TableDao.open(projectName, matchingSummary.getMatchesName());
			// Logger.info("matches: " + matches);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		operationMode = 1;
		if (evaluationSummaryName.equals("NO_EVAL_SUMMARY")) {
			operationMode = 2;
		} 

		if (operationMode == 1) {
			evalSummary = project.findEvaluationSummaryByName(evaluationSummaryName);
		} else {
			evalSummary = null;
		}

		matcher = project.findMatcherByName(matcherName);
		itemPairAudits = matchingSummary.getItemPairAudits();

		candsetAttr1 = candset.getAttributes().get(1);
		candsetAttr2 = candset.getAttributes().get(2);

		generateFeatures();

		return showPairs();

	}

	public static Result postIndexOnFlyWithLabeledData(String projectName) {

		DynamicForm form = form().bindFromRequest();
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");
		String labeledDataName= form.get("labeled_data");
		String matcherName = form.get("matcher_name");

		operationMode = 1;
		readTables(projectName, table1Name, table2Name, labeledDataName);

		String matchesName = "matches";

		itemPairAudits = new HashMap<Tuple, ItemPairAudit>();
		project = ProjectDao.open(projectName);
		matcher = project.findMatcherByName(matcherName);

		try {
			matches = MatchingDao.match(projectName, candset, table1, table2, matcherName, matchesName, itemPairAudits, null, null);
			evalSummary = EvaluateDao.evaluateWithGold("tmp", projectName, matches, labeledDataName);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		matchingSummary = Debug.getMatchingSummary(itemPairAudits);

		candsetAttr1 = candset.getAttributes().get(1);
		candsetAttr2 = candset.getAttributes().get(2);

		generateFeatures();

		return showPairs();
	}

	public static Result postIndexOnFly(String projectName) {

		DynamicForm form = form().bindFromRequest();
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");
		String candsetName= form.get("labeled_data");
		String matcherName = form.get("matcher_name");
		String goldName = form.get("gold_name");

		operationMode = 1;
		if (goldName.equals("NO_GOLD")) {
			operationMode = 2;
		} 

		readTables(projectName, table1Name, table2Name, candsetName);

		String matchesName = "matches";

		itemPairAudits = new HashMap<Tuple, ItemPairAudit>();
		project = ProjectDao.open(projectName);
		matcher = project.findMatcherByName(matcherName);

		try {
			matches = MatchingDao.match(projectName, candset, table1, table2, matcherName, matchesName, itemPairAudits, null, null);

			if (operationMode == 1) {
				// evalSummary = EvaluateDao.evaluateWithGold("tmp", projectName, matches, table1Name + ".id", table2Name + ".id", "label", goldName, "id1", "id2", "label");
				evalSummary = EvaluateDao.evaluateWithGold("tmp", projectName, matches, goldName);  
			} else {
				evalSummary = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		matchingSummary = Debug.getMatchingSummary(itemPairAudits);

		candsetAttr1 = candset.getAttributes().get(1);
		candsetAttr2 = candset.getAttributes().get(2);

		generateFeatures();

		return showPairs();
	}

	public static Result getIndexCandsetView(String projectName, String tableName) {

		try {
			candset = TableDao.open(projectName, tableName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String table1Name = null;
		String table2Name = null;

		// Assumption: First and second rows that have .id in their attribute name.
		// correspond to first and second table.
		String pattern = "(.*)(.id)(.*)";
		Pattern r = Pattern.compile(pattern);

		for (Attribute attr: candset.getAttributes()) {
			String name = attr.getName();
			java.util.regex.Matcher m = r.matcher(name);
			boolean found = m.find();

			if (found && table1Name == null) {
				table1Name = m.group(1);
				continue;
			}
			if (found && table2Name == null) {
				table2Name = m.group(1);
			}
		}

		operationMode = 3;

		project = ProjectDao.open(projectName);
		try {
			table1 = TableDao.open(projectName, table1Name);
			table2 = TableDao.open(projectName, table2Name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		candsetAttr1 = candset.getAttributes().get(1);
		candsetAttr2 = candset.getAttributes().get(2);

		generateFeatures();

		return showPairs();
	}

	/**** BASIC DATA HANDLING FOR FRONT END ****/
	public static Result getOperationMode() {
		return ok(Json.toJson(operationMode));
	}

	public static Result getAllIds() {
		List<Object> ids = candset.getAllIdsInOrder();
		return ok(Json.toJson(ids));
	}

	public static Result getAllRuleNames() {
		List<String> ruleNames = new ArrayList<String>();

		for (Rule rule: matcher.getRules()) {
			ruleNames.add(rule.getName());
		}

		return ok(Json.toJson(ruleNames));
	}

	public static Result getAllFeatureNames() {
		List<String> featureNames = new ArrayList<String>();

		for (Feature feature: project.getFeatures()) {
			featureNames.add(feature.getName());
		}

		return ok(Json.toJson(featureNames));
	}

	public static Result getTuplesForIds() {

		List<Object> ids = new ArrayList<Object>();
		List<Tuple> tuples = new ArrayList<Tuple>();
		Attribute idAttr = candset.getIdAttribute();    

		JsonNode json = request().body().asJson();
		JsonNode postIds = json.findPath("ids");

		for (int i = 0; i < postIds.size(); i++) {
			ids.add(idAttr.convertValueToObject(postIds.get(i).asText()));
		}    

		for (Object id: ids) { 
			tuples.add(candset.getTuple(id));          
		}   

		ObjectNode tuplesJson = Json.newObject();

		for (Tuple tuple: tuples) {
			Object itemPairId = tuple.getAttributeValue(candset.getIdAttribute());
			ObjectNode tupleJson = Json.newObject();
			Tuple item1 = table1.getTuple(tuple.getAttributeValue(candsetAttr1));
			Tuple item2 = table2.getTuple(tuple.getAttributeValue(candsetAttr2));
			tupleJson.put("item1", getItemJson(item1, table1.getName(), table1.getAttributes()));
			tupleJson.put("item2", getItemJson(item2, table2.getName(), table2.getAttributes()));
			tupleJson.put("features", getFeaturesJson(itemPairId));

			if (operationMode == 1 || operationMode == 2) {
				tupleJson.put("audit", getAuditInfoJson(itemPairAudits.get(tuple)));
			}

			if (operationMode == 1) {
				tupleJson.put("gold", getGoldInfoJson(tuple));
			}

			tuplesJson.put(String.valueOf(tuple.getAttributeValue(candset.getIdAttribute())), tupleJson);
		}

		return ok(tuplesJson);
	}

	private static ObjectNode getItemJson(Tuple item, String tableName, List<Attribute> attrs) {

		ObjectNode itemJson = Json.newObject();
		itemJson.put("table-name", tableName);

		List<List<String>> rows = new ArrayList<List<String>>();

		for (Attribute attr: attrs) {
			List<String> row = new ArrayList<String>();
			row.add(attr.getName());
			row.add(String.valueOf(item.getAttributeValue(attr)));

			rows.add(row);
		}

		itemJson.put("rows", Json.toJson(rows));

		return itemJson;
	}

	private static ObjectNode getAuditInfoJson(ItemPairAudit itemPairAudit) {
		DecimalFormat df = new DecimalFormat("##.##");

		ObjectNode itemJson = Json.newObject();
		itemJson.put("match-status", String.valueOf(itemPairAudit.getStatus()));

		ObjectNode rulesJson = Json.newObject();

		for (RuleAudit ruleAudit: itemPairAudit.getRuleAuditValues()) {

			ObjectNode ruleJson = Json.newObject();
			ObjectNode termsJson = Json.newObject();

			ruleJson.put("match-status", String.valueOf(ruleAudit.getStatus()));

			int termId = 1;
			for (TermAudit termAudit: ruleAudit.getTermAuditValues()) {
				ObjectNode termJson = Json.newObject();
				termJson.put("match-status", String.valueOf(termAudit.getStatus()));
				termJson.put("feature1", termAudit.getTerm().getFeature1().getName()); 
				termJson.put("operand", termAudit.getTerm().getRelop().getName()); 
				termJson.put("threshold", df.format(termAudit.getTerm().getValue())); 
				termJson.put("calculated", df.format(termAudit.getCalculatedScore()));
				termsJson.put(String.valueOf(termId), termJson);
				termId += 1;
			}

			ruleJson.put("terms", termsJson);
			rulesJson.put(ruleAudit.getRule().getName(), ruleJson);
		}

		itemJson.put("rules", rulesJson);

		return itemJson;
	}

	public static ObjectNode getGoldInfoJson(Tuple tuple) {
		ObjectNode json = Json.newObject();
		json.put("gold-match-status", isMatchingInGOLD(tuple));
		return json;
	}

	public static Result getMatchingSummaryJson() {

		DecimalFormat df = new DecimalFormat("##.##");

		ObjectNode summary = Json.newObject();

		if (operationMode == 1) {
			ObjectNode eval = Json.newObject();
			eval.put("precision", df.format(evalSummary.getPrecision()));
			eval.put("recall", df.format(evalSummary.getRecall()));
			eval.put("F1", df.format(evalSummary.getF1()));
			eval.put("FP", evalSummary.getFalsePositives().size());
			eval.put("FN", evalSummary.getFalseNegatives().size());
			summary.put("eval", eval);
		}

		if (operationMode == 1 || operationMode == 2) {
			ObjectNode matching = Json.newObject();
			matching.put("total-pairs", candset.getSize());
			matching.put("match-size", matchingSummary.getTotalMatches());
			matching.put("rule-count", matcher.getNumRules());

			ObjectNode rules = Json.newObject();
			for (Rule rule: matcher.getRules()) {
				if (matchingSummary.getTotalMatchesByRule().containsKey(rule.getName())) {
					rules.put(rule.getName(), matchingSummary.getTotalMatchesByRule().get(rule.getName()));
				} else {
					rules.put(rule.getName(), 0);
				}
			}    
			summary.put("matching", matching);
			summary.put("rulesSummary", rules);
		}

		return ok(summary);    
	}

	private static ObjectNode getFeaturesJson(Object itemPairId) {

		DecimalFormat df = new DecimalFormat("##.##");

		ObjectNode features = Json.newObject();

		for (Entry<String, Float> fd: generatedFeatures.get(itemPairId).entrySet()) {
			features.put(fd.getKey(), df.format(fd.getValue()));      
		}
		return features;
	}

	/**** Filter Operations ****/
	public static Result getIdsByMatchingStatus(String status) {

		List<Object> ids = null;

		SelectStatus selectStatus = SelectStatus.valueOf(status);
		switch (selectStatus) {
		case ALL:
			ids = candset.getAllIdsInOrder();
			break;
		case MATCHED:
			ids = getAllMatchedIds();
			break;
		case NONMATCHED:
			ids = candset.getAllIdsInOrder();
			ids.removeAll(getAllMatchedIds());
			break;
		default:
			break;
		}
		return ok(Json.toJson(ids));
	}

	private static List<Object> getAllMatchedIds() {
		List<Object> ids = new ArrayList<Object>();

		Logger.info("matches:" + matches);
		Attribute label = matches.getAttributeByName("label");
		Logger.info("label:" + label);
		for (Object id: matches.getAllRowIdsWithAttrValue(label, 1)) {
			ids.add(id);
		}
		return ids;
	}

	public static Result getIdsByErrorStatus(String goldStatus) {

		GoldStatus status = GoldStatus.valueOf(goldStatus);

		List<Object> ids = null;

		switch(status) {
		case FN:
			ids = convertIdPairsToCandSetIds(evalSummary.getFalseNegatives());
			break;
		case FP:
			ids = convertIdPairsToCandSetIds(evalSummary.getFalsePositives());
			break;
		default:
			break;
		}
		return ok(Json.toJson(ids));
	}

	public static Result getIdsByRuleName(String ruleName) {

		List<Object> ids = new ArrayList<Object>();

		for (Map.Entry<Tuple, ItemPairAudit> entry : itemPairAudits.entrySet()) {
			Tuple tuple = entry.getKey();
			ItemPairAudit itemPairAudit = entry.getValue();
			if (itemPairAudit.getMatchingRuleNames().contains(ruleName)) {
				ids.add(tuple.getAttributeValue(candset.getIdAttribute()));
			}
		}

		return ok(Json.toJson(ids));
	}

	public static Result getIdsBySkyline() {    
		List<Feature> skylineFeatures = null;
		if (operationMode == 3) {
			skylineFeatures = project.getFeatures();
		} else {
			skylineFeatures = allFeaturesInRules();
		}

		JsonNode json = request().body().asJson();
		String simSelection = json.findPath("simSelection").asText();
		String simCount = json.findPath("simCount").asText();

		List<Object> reqIds = new ArrayList<Object>();
		JsonNode postIds = json.findPath("ids");

		Attribute idAttr = candset.getIdAttribute();    

		for (int i = 0; i < postIds.size(); i++) {
			reqIds.add(idAttr.convertValueToObject(postIds.get(i).asText()));
		}

		SimSelection selection = SimSelection.valueOf(simSelection);
		int count = Integer.valueOf(simCount);

		Map<Object, List<Float>> shownFeatures = new HashMap<Object, List<Float>>();

		for (Object id: reqIds) {
			ArrayList<Float> flist = new ArrayList<Float>();
			Map<String, Float> fmap = generatedFeatures.get(id);
			if (fmap == null) {
				fmap =  generatedFeatures.get(id);
			}
			for (Feature feature: skylineFeatures) {
				flist.add(fmap.get(feature.getName()));
			}
			shownFeatures.put(id, flist);
		}   

		List<Object> ids = Skyline.getHintTuples(shownFeatures, selection, count);

		return ok(Json.toJson(ids));
	}

	public static Result getDebugQueryResults() {

		JsonNode json = request().body().asJson();

		JsonNode featureNames = json.findPath("featureNames");
		JsonNode vals = json.findPath("vals");
		JsonNode jsonops = json.findPath("ops");

		List<String> features = new ArrayList<String>();
		List<Float> values = new ArrayList<Float>();
		List<QueryOps> ops = new ArrayList<QueryOps>();

		for (int i = 0; i < featureNames.size(); i++) {
			String val = vals.get(i).asText();
			if (!val.isEmpty()) {
				// TODO: raise error if value is not convertable.
				try {
					Float value = Float.parseFloat(vals.get(i).asText());
					values.add(value);
				} catch (NumberFormatException e) {
					// gracefully handle the error. Simply not consider
					// this condition.
					continue;
				}
				features.add(featureNames.get(i).asText());
				// TODO: raise error if operation is not convertable.
				QueryOps op = QueryOps.valueOf(jsonops.get(i).asText());
				ops.add(op);
			}
		}

		List<Object> ids = getAllRowsWithQueryCondition(features, values, ops);
		return ok(Json.toJson(ids));
	}

	/**** JAVASCRIPT ROUTING ****/
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(
				Routes.javascriptRouter("debugJsRoutes",
						controllers.debug.routes.javascript.DebugController.getAllIds(),
						controllers.debug.routes.javascript.DebugController.getAllRuleNames(),
						controllers.debug.routes.javascript.DebugController.getAllFeatureNames(),
						controllers.debug.routes.javascript.DebugController.getDebugQueryResults(), 
						controllers.debug.routes.javascript.DebugController.getTuplesForIds(),
						controllers.debug.routes.javascript.DebugController.getMatchingSummaryJson(),
						controllers.debug.routes.javascript.DebugController.getIdsByMatchingStatus(),
						controllers.debug.routes.javascript.DebugController.getIdsByErrorStatus(),
						controllers.debug.routes.javascript.DebugController.getIdsByRuleName(),
						controllers.debug.routes.javascript.DebugController.getIdsBySkyline(),
						controllers.debug.routes.javascript.DebugController.getOperationMode()
						)
				);
	}

	/**** PRIVATE OPERATIONS ****/
	private static Result showPairs() {

		String pageTitle = "Debug";

		Html topBar = debug_topbar.render(project.getName());
		Html topNav = common_topnav.render(project);
		Html content = facets_layout.render();

		List<Call> dynamicJs = new ArrayList<Call>();
		dynamicJs.add(controllers.project.routes.ProjectController.javascriptRoutes());
		dynamicJs.add(controllers.routes.Assets.at("javascripts/project/project.js"));

		dynamicJs.add(controllers.debug.routes.DebugController.javascriptRoutes());
		dynamicJs.add(controllers.routes.Assets.at("javascripts/bootstrap/bootstrap-tree.js"));
		dynamicJs.add(controllers.routes.Assets.at("javascripts/facets_layout/facets_layout.js"));
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_error_filter.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_facets_layout.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_match_status_filter.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_matching_summary.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_rule_filter.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_feature_thresh_filter.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_skyline_filter.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug_table.js"));  
		dynamicJs.add(controllers.routes.Assets.at("javascripts/debug/debug.js")); 

		List<Call> dynamicCss = new ArrayList<Call>();
		dynamicCss.add(controllers.routes.Assets.at("stylesheets/bootstrap/bootstrap-tree.min.css"));
		dynamicCss.add(controllers.routes.Assets.at("stylesheets/facets_layout/facets_layout.css"));
		dynamicCss.add(controllers.routes.Assets.at("stylesheets/debug/debug.css"));

		Html page =  common_main.render(pageTitle, topBar, topNav, content,
				dynamicCss, dynamicJs);

		return ok(page);
	}

	private enum SelectStatus {
		ALL, MATCHED, NONMATCHED
	}

	private enum GoldStatus {
		FP, FN
	}

	private static List<Object> convertIdPairsToCandSetIds(Set<IdPair> idPairs) {
		List<Object> ids = new ArrayList<Object>();
		for (IdPair idPair: idPairs) {
			Tuple tuple  = findTupleInCandSet(idPair);
			// If tuple == null, then it was removed at the blocking step.
			if (tuple != null) {
				ids.add(tuple.getAttributeValue(candset.getIdAttribute()));
			}
		}
		return ids;
	}

	private static Tuple findTupleInCandSet(IdPair idPair) {
		for(Tuple tuple: candset.getAllTuples()) {
			if (tuple.getAttributeValue(candsetAttr1).equals(idPair.getId1())
					&& tuple.getAttributeValue(candsetAttr2).equals(idPair.getId2())
					) {
				return tuple;
			}
		}
		return null;
	}

	// TODO: This will be expensive....It should be handled by a dictionary.
	// The discrepancy between types is causing all these problems.
	private static boolean isMatchingInGOLD(Tuple tuple) {

		for (IdPair idPair: evalSummary.getActualPositives()) {
			if (tuple.getAttributeValue(candsetAttr1).equals(idPair.getId1())
					&& tuple.getAttributeValue(candsetAttr2).equals(idPair.getId2())){ 
				return true;
			}
		}

		return false;
	}

	// TODO: Note: feature generation should be eventually moved to some other place
	// in the pipeline.
	private static void generateFeatures() {

		System.out.println("STARTED GENERATING FEATURES");

		generatedFeatures = new LinkedHashMap<Object, Map<String, Float>>();

		List<Feature> features = project.getFeatures();

		for(Tuple ctuple: candset.getAllTuples()) {
			Map<String, Float> generatedFeature = new LinkedHashMap<String, Float>();

			Tuple t1 = table1.getTuple(ctuple.getAttributeValue(candsetAttr1));
			Tuple t2 = table2.getTuple(ctuple.getAttributeValue(candsetAttr2));

			for (Feature feature: features) {
				Float score = feature.compute(t1, t2);
				generatedFeature.put(feature.getName(), score);
			}
			generatedFeatures.put(ctuple.getAttributeValue(candset.getIdAttribute()), generatedFeature);
		}

		System.out.println("DONE GENERATNIG FEATURES");
	}

	private static List<Feature> allFeaturesInRules() {
		// Note: Heuristics. We will use all the features
		// used in the rules.
		List<Feature> features = new ArrayList<Feature>();
		for (Rule rule: matcher.getRules()) {
			for (Term term: rule.getTerms()) {
				features.add(term.getFeature1());
				if (term.getFeature2() != null) {
					features.add(term.getFeature2());
				}
			}
		}
		return features;
	}

	private static List<Object> getAllRowsWithQueryCondition(
			List<String> featureNames, List<Float> vals, List<QueryOps> ops) {

		if (featureNames.size() == 0) {
			return candset.getAllIdsInOrder();
		}

		List<Object> ids = new ArrayList<Object>();

		for (Entry<Object, Map<String, Float>> entry:generatedFeatures.entrySet()) {
			Object id = entry.getKey();
			boolean matches = true;

			Map<String, Float> fScores = entry.getValue();
			for (int i = 0; i < featureNames.size(); i++) {
				if (!Table.compareUsingOps(
						Type.FLOAT, ops.get(i), 
						fScores.get(featureNames.get(i)), 
						vals.get(i))) {
					matches = false;
				}          
			}

			if (matches) {
				ids.add(id);
			}
		}
		return ids;
	}

	/**
	 * Retrieve the table objects.
	 * @param projectName
	 * @param table1Name
	 * @param table2Name
	 * @param candsetName
	 */
	private static void readTables(String projectName, String table1Name, String table2Name, String candsetName) {

		try {

			System.out.println("STARTED READING");

			candset = TableDao.open(projectName, candsetName);
			table1 = TableDao.open(projectName, table1Name);
			table2 = TableDao.open(projectName, table2Name);

			System.out.println("DONE READING");

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("WARNING: COULD NOT READ TABLE.");
		}
	}

}
