package com.walmart.productgenome.matching.evaluate;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;

public class EvaluationSummary {

	public static class IdPair {
		private Object id1;
		private Object id2;

		public Object getId1() {
			return id1;
		}

		public Object getId2() {
			return id2;
		}

		public IdPair(Object id1, Object id2){
			this.id1 = id1;
			this.id2 = id2;
		}

		@Override
		public int hashCode(){
			return Objects.hashCode(id1, id2);
		}

		@Override
		public boolean equals(Object object){
			if (object instanceof IdPair) {
				IdPair that = (IdPair) object;
				return Objects.equal(this.id1, that.id1)
						&& Objects.equal(this.id2, that.id2);
			}
			return false;
		}
	}
	
	private String name;
	private String projectName;
	private String matchesName;
	private String goldName;
	
	private float precision = Float.NaN;
	private float recall = Float.NaN;
	private float f1 = Float.NaN;
	
	private Set<IdPair> actualPositives = new HashSet<IdPair>(); // these come from gold
	private Set<IdPair> truePositives = new HashSet<IdPair>(); // these come from matches
	private Set<IdPair> falsePositives = new HashSet<IdPair>(); // these come from matches
	
	public EvaluationSummary(String name, String projectName, String matchesName,
			String goldName){
		this.name = name;
		this.projectName = projectName;
		this.matchesName = matchesName;
		this.goldName = goldName;
	}
	
	public void computeAccuracyEstimates(){
		int numTruePositives = truePositives.size();
		int numFalsePositives = falsePositives.size();
		int numActualPositives = actualPositives.size();
		
		int numPredictedPositives = numTruePositives + numFalsePositives;
		if(numActualPositives != 0){
			precision = (float) ((1.0f * numTruePositives)/numPredictedPositives);
			recall = (float) ((1.0f * numTruePositives)/numActualPositives);
		}
		if(precision == 0.0f && recall == 0.0f){
			f1 = 0.0f;
		}
		else if(precision != Float.NaN && recall != Float.NaN){
			f1 = (float) (2.0f * precision * recall)/(precision + recall);
		}
	}

	public String getName() {
		return name;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getMatchesName() {
		return matchesName;
	}

	public String getGoldName() {
		return goldName;
	}

	public float getPrecision() {
		return precision;
	}

	public float getRecall() {
		return recall;
	}

	public float getF1() {
		return f1;
	}

	public Set<IdPair> getActualPositives() {
		return actualPositives;
	}

	public Set<IdPair> getTruePositives() {
		return truePositives;
	}

	public Set<IdPair> getFalsePositives() {
		return falsePositives;
	}
	
	public Set<IdPair> getFalseNegatives() {
		Set<IdPair> falseNegatives = new HashSet<IdPair>();
		for(IdPair idPair : actualPositives){
			if(!truePositives.contains(idPair)){
				falseNegatives.add(idPair);
			}
		}
		return falseNegatives;
	}
	
	public void addActualPositive(IdPair idPair) {
		actualPositives.add(idPair);
	}
	
	public void addTruePositive(IdPair idPair) {
		truePositives.add(idPair);
	}
	
	public void addFalsePositive(IdPair idPair) {
		falsePositives.add(idPair);
	}
	
	public void setPrecision(float precision) {
		this.precision = precision;
	}
	
	public void setRecall(float recall) {
		this.recall = recall;
	}
	
	public void setF1(float f1) {
		this.f1 = f1;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Precision: ");
		sb.append(precision);
		sb.append(", Recall: ");
		sb.append(recall);
		sb.append(", F1: ");
		sb.append(f1);
		sb.append(", Actual Positives (in gold): ");
		sb.append(actualPositives.size());
		sb.append(", True Positives: ");
		sb.append(truePositives.size());
		sb.append(", False Positives: ");
		sb.append(falsePositives.size());
		sb.append("]");
		return sb.toString();
	}
	
}
