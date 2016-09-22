package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class Mean extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Mean";

	public Mean(String positiveClass, String negativeClass, ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
	}

	@Override
	public List<Prediction> fusePredictions(List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			double scoreSum = 0;
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				Prediction prediction = predictions.get(predictionNr).get(rowNr);
				double score = getPositiveClass().equals(prediction.getPrediction()) ? prediction.getConfidence() : 1 - prediction.getConfidence();
				scoreSum += score;
			}
			double score = scoreSum / predictions.size();
			String predictionClass = score >= 0.5 ? getPositiveClass() : getNegativeClass();
			double confidence = score >= 0.5 ? score : 1 - score;
			fusedPredictions.add(new Prediction(rowNr, getPositiveClass().equals(predictionClass), predictionClass, confidence, null));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
