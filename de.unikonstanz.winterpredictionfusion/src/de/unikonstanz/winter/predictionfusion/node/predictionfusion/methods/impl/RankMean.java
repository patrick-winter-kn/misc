package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class RankMean extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Rank Mean";

	public RankMean(String positiveClass, String negativeClass, ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
	}

	@Override
	public List<Prediction> fusePredictions(List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		for (List<Prediction> predictionList : predictions) {
			PredictionFusionUtil.assignRanks(predictionList);
		}
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			double rankSum = 0;
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				Prediction prediction = predictions.get(predictionNr).get(rowNr);
				rankSum += prediction.getRank();
			}
			double rank = rankSum / predictions.size();
			fusedPredictions.add(new Prediction(rowNr, false, null, null, rank));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
