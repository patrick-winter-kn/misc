package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class Median extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Median";

	public Median(String positiveClass, String negativeClass, ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
	}

	@Override
	public List<Prediction> fusePredictions(List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		int middle = (int) (Math.floor(predictions.size() / (double) 2) / predictions.size());
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			List<Prediction> predictionList = new ArrayList<Prediction>();
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				predictionList.add(predictions.get(predictionNr).get(rowNr));
			}
			Collections.sort(predictionList);
			Prediction median = predictionList.get(middle);
			String predictionClass = median.getPrediction();
			double confidence = median.getConfidence();
			fusedPredictions.add(new Prediction(rowNr, getPositiveClass().equals(predictionClass), predictionClass, confidence, null));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
