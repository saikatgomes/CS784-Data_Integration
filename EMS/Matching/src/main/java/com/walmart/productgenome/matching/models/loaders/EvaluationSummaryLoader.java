package com.walmart.productgenome.matching.models.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.walmart.productgenome.matching.evaluate.EvaluationSummary;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class EvaluationSummaryLoader {

	public static EvaluationSummary loadEvaluationSummary(File evaluationSummaryFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(evaluationSummaryFile));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String evalSumJSON = sb.toString();
		EvaluationSummary evalSum = JSONUtils.getEvaluationSummaryFromJSON(evalSumJSON);
		return evalSum;
	}
}
