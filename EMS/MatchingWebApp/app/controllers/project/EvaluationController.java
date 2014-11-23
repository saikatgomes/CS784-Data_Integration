package controllers.project;

import static play.data.Form.form;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.daos.EvaluateDao;
import com.walmart.productgenome.matching.daos.MatchingDao;
import com.walmart.productgenome.matching.daos.RuleDao;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.service.debug.Debug;
import com.walmart.productgenome.matching.service.debug.MatchingSummary;

import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

public class EvaluationController extends Controller {
	/*
	public static Result evaluateWithGold(String projectName)
	{
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String evaluationName = form.get("evaluation_name");
		String matchesName = form.get("matches_name_e");
		String matchesId1 = form.get("matches_id1");
		String matchesId2 = form.get("matches_id2");
		String matchesLabelId = form.get("matches_label_id");
		String goldName = form.get("gold_name_e");
		String goldId1 = form.get("gold_id1");
		String goldId2 = form.get("gold_id2");
		String goldLabelId = form.get("gold_label_id");
		
		try {
			EvaluationSummary evaluationSummary = EvaluateDao.evaluateWithGold(evaluationName, projectName, matchesName,
					matchesId1, matchesId2, matchesLabelId, goldName, goldId1, goldId2, goldLabelId);
			ProjectController.statusMessage = "Successfully evaluated Matches " +
					matchesName + " with Gold " + goldName + ".\nEvaluation summary: " + 
					evaluationSummary;
			EvaluateDao.addEvaluationSummary(projectName, evaluationSummary);
		} catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
	*/
	
	public static Result evaluateWithGold(String projectName)
	{
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String evaluationName = form.get("evaluation_name");
		String matchesName = form.get("matches_name_e");
		String goldName = form.get("gold_name_e");
		
		try {
			EvaluationSummary evaluationSummary =
					EvaluateDao.evaluateWithGold(evaluationName, projectName,
							matchesName, goldName);
			ProjectController.statusMessage = "Successfully evaluated Matches " +
					matchesName + " with Gold " + goldName + ".\nEvaluation summary: " + 
					evaluationSummary;
			EvaluateDao.addEvaluationSummary(projectName, evaluationSummary);
		} catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
	
	public static Result evaluateWithLabeledData(String projectName)
	{
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name_el");
		String table2Name = form.get("table2_name_el");
		String labeledDataName = form.get("labeled_data_el");
		String matcherName = form.get("matcher_name_el");
		String matchesName = form.get("matches_name_el");
		String matchingSummaryName = form.get("matching_summary_name_el");
		String evaluationSummaryName = form.get("evaluation_summary_name_el");
		
		Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
		boolean saveToDisk = false;
		if(null != form.get("matches_default")){
			defaultTypes.add(DefaultType.MATCHES);
		}
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try {
			// perform matching on labeled data in debug mode
			Map<Tuple, ItemPairAudit> itemPairAudits = new HashMap<Tuple, ItemPairAudit>();
			Table matches = MatchingDao.match(projectName, labeledDataName, matcherName,
					matchesName, itemPairAudits, null, null);
			MatchingSummary matchingSummary = Debug.getMatchingSummary(itemPairAudits);

			// save the matches - this automatically updates and saves the project
			TableDao.save(matches, defaultTypes, saveToDisk);
			
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
			matchingSummary.setCandsetName(labeledDataName);
			matchingSummary.setMatchesName(matchesName);
			matchingSummary.setMatcherName(matcherName);
			matchingSummary.setItemPairAudits(itemPairAudits);
			
			// add matching summary - this automatically updates and saves the project
			MatchingDao.addMatchingSummary(projectName, matchingSummary);
			
			// perform evaluation on labeled data
			EvaluationSummary evaluationSummary =
					EvaluateDao.evaluateWithGold(evaluationSummaryName, projectName,
							matchesName, labeledDataName);
			ProjectController.statusMessage += "\n\nSuccessfully evaluated Matches " +
					matchesName + " with Labeled Data " + labeledDataName + ".\nEvaluation summary: " + 
					evaluationSummary;
			EvaluateDao.addEvaluationSummary(projectName, evaluationSummary);
			
		} catch (IOException e) {
			flash("error", e.getMessage());
			ProjectController.statusMessage = "Error: " + e.getMessage();
		}
		
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
}
