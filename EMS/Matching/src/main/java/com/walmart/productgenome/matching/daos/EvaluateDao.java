package com.walmart.productgenome.matching.daos;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.evaluate.EvaluationSummary.IdPair;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.savers.EvaluationSummarySaver;
import com.walmart.productgenome.matching.service.debug.Debug;
import com.walmart.productgenome.matching.service.debug.MatchingSummary;

public class EvaluateDao {

	public static EvaluationSummary evaluateWithGold(String evaluationName,
			String projectName, String matchesName, String matchesId1,
			String matchesId2, String matchesLabel, String goldName, String goldId1,
			String goldId2, String goldLabelId) throws IOException {
		Table matches = TableDao.open(projectName, matchesName);
		return evaluateWithGold(evaluationName, projectName, matches, matchesId1,
				matchesId2, matchesLabel, goldName, goldId1, goldId2, goldLabelId);	
	}

	public static EvaluationSummary evaluateWithGold(String evaluationName,
			String projectName, Table matches, String matchesId1,
			String matchesId2, String matchesLabel, String goldName, String goldId1,
			String goldId2, String goldLabelId) throws IOException {

		EvaluationSummary evaluationSummary = null;

		Table gold = TableDao.open(projectName, goldName);

		Attribute matchesId1Attr = matches.getAttributeByName(matchesId1);
		Attribute matchesId2Attr = matches.getAttributeByName(matchesId2);
		Attribute matchesLabelAttr = matches.getAttributeByName(matchesLabel);

		Attribute goldId1Attr = gold.getAttributeByName(goldId1);
		Attribute goldId2Attr = gold.getAttributeByName(goldId2);
		Attribute goldLabelIdAttr = gold.getAttributeByName(goldLabelId);

		evaluationSummary = new EvaluationSummary(evaluationName, projectName,
				matches.getName(), goldName);

		// create a hash map from the (id1,id2) pair in gold
		Map<IdPair, Integer> goldMap = new HashMap<IdPair, Integer>();
		for(Tuple goldTuple : gold.getAllTuples()){
			Object id1 = goldTuple.getAttributeValue(goldId1Attr);
			Object id2 = goldTuple.getAttributeValue(goldId2Attr);
			Integer label = (Integer) goldTuple.getAttributeValue(goldLabelIdAttr);
			IdPair idPair = new IdPair(id1, id2);
			goldMap.put(idPair, label);
			if(MatchStatus.MATCH.getLabel() == label){
				evaluationSummary.addActualPositive(idPair);
			}
		}

		// iterate through matches
		for(Tuple matchTuple : matches.getAllTuples()){
			Object id1 = matchTuple.getAttributeValue(matchesId1Attr);
			Object id2 = matchTuple.getAttributeValue(matchesId2Attr);
			Integer matchLabel = (Integer) matchTuple.getAttributeValue(matchesLabelAttr);

			if(MatchStatus.MATCH.getLabel() == matchLabel){
				// predicted positive
				IdPair idPair = new IdPair(id1, id2);
				Integer goldLabel = goldMap.get(idPair);
				if(null != goldLabel && MatchStatus.MATCH.getLabel() == goldLabel) {
					// true positive
					evaluationSummary.addTruePositive(idPair);
				}
				else{
					// false positive
					evaluationSummary.addFalsePositive(idPair);
				}	
			}
		}

		evaluationSummary.computeAccuracyEstimates();

		return evaluationSummary;
	}

	public static EvaluationSummary evaluateWithGold(String evaluationName,
			String projectName, String matchesName, String goldName) throws IOException {
		Table matches = TableDao.open(projectName, matchesName);
		return evaluateWithGold(evaluationName, projectName, matches, goldName);	
	}

	public static EvaluationSummary evaluateWithLabeledData(String projectName,
			String matcherName, String labeledDataName, String matchesName,
			String matchingSummaryName, String evaluationSummaryName) throws IOException {
		return null;	
	}
	
	public static EvaluationSummary evaluateWithGold(String evaluationName,
			String projectName, Table matches, String goldName) throws IOException {

		EvaluationSummary evaluationSummary = null;

		Table gold = TableDao.open(projectName, goldName);

		List<Attribute> matchAttributes = matches.getAttributes();
		Attribute matchesId1Attr = matchAttributes.get(1);
		Attribute matchesId2Attr = matchAttributes.get(2);
		Attribute matchesLabelAttr = matchAttributes.get(matchAttributes.size()-1);

		List<Attribute> goldAttributes = gold.getAttributes();
		Attribute goldId1Attr = goldAttributes.get(1);
		Attribute goldId2Attr = goldAttributes.get(2);
		Attribute goldLabelAttr = goldAttributes.get(goldAttributes.size()-1);
		
		evaluationSummary = new EvaluationSummary(evaluationName, projectName,
				matches.getName(), goldName);

		// create a hash map from the (id1,id2) pair in gold
		Map<IdPair, Integer> goldMap = new HashMap<IdPair, Integer>();
		for(Tuple goldTuple : gold.getAllTuples()){
			Object id1 = goldTuple.getAttributeValue(goldId1Attr);
			Object id2 = goldTuple.getAttributeValue(goldId2Attr);
			Integer label = (Integer) goldTuple.getAttributeValue(goldLabelAttr);
			IdPair idPair = new IdPair(id1, id2);
			goldMap.put(idPair, label);
			if(MatchStatus.MATCH.getLabel() == label){
				evaluationSummary.addActualPositive(idPair);
			}
		}

		// iterate through matches
		for(Tuple matchTuple : matches.getAllTuples()){
			Object id1 = matchTuple.getAttributeValue(matchesId1Attr);
			Object id2 = matchTuple.getAttributeValue(matchesId2Attr);
			Integer matchLabel = (Integer) matchTuple.getAttributeValue(matchesLabelAttr);

			if(MatchStatus.MATCH.getLabel() == matchLabel){
				// predicted positive
				IdPair idPair = new IdPair(id1, id2);
				Integer goldLabel = goldMap.get(idPair);
				if(null != goldLabel && MatchStatus.MATCH.getLabel() == goldLabel) {
					// true positive
					evaluationSummary.addTruePositive(idPair);
				}
				else{
					// false positive
					evaluationSummary.addFalsePositive(idPair);
				}	
			}
		}

		evaluationSummary.computeAccuracyEstimates();

		return evaluationSummary;
	}
	
	public static void addEvaluationSummary(String projectName,
			EvaluationSummary evaluationSummary) throws IOException {
		EvaluationSummarySaver.saveEvaluationSummary(evaluationSummary);
		Project project = ProjectDao.open(projectName);
		project.addEvaluationSummary(evaluationSummary);
		
		//ProjectDao.save(project);
	}
}