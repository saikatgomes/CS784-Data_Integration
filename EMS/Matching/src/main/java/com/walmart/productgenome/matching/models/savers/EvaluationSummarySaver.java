package com.walmart.productgenome.matching.models.savers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class EvaluationSummarySaver {

	private static String getEvaluationSummaryFilePath(String projectName,
			String evaluationSummaryName) {
		return Constants.ROOT_DIR + projectName + "/" + evaluationSummaryName +
				Constants.EVALUATION_SUMMARY_EXTENSION;
	}
	
	public static void saveEvaluationSummary(EvaluationSummary evaluationSummary) throws IOException {
		String projectName = evaluationSummary.getProjectName();
		String evaluationSummaryName = evaluationSummary.getName();
		String evaluationSummaryFilePath = getEvaluationSummaryFilePath(projectName, evaluationSummaryName);
		String evaluationSummaryJSON = JSONUtils.getEvaluationSummaryJSON(evaluationSummary);
		BufferedWriter bw = new BufferedWriter(new FileWriter(evaluationSummaryFilePath));
		bw.write(evaluationSummaryJSON);
		bw.close();
	}
}
