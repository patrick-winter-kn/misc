package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class RankMedian extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Rank Median";

	public RankMedian(String positiveClass, String negativeClass, ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
	}

	@Override
	public List<Prediction> fusePredictions(List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		int middle = (int) (Math.floor(predictions.size() / (double) 2) / predictions.size());
		for (List<Prediction> predictionList : predictions) {
			PredictionFusionUtil.assignRanks(predictionList);
		}
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			List<Prediction> predictionList = new ArrayList<Prediction>();
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				predictionList.add(predictions.get(predictionNr).get(rowNr));
			}
			Collections.sort(predictionList);
			Prediction median = predictionList.get(middle);
			fusedPredictions.add(new Prediction(rowNr, false, null, null, median.getRank()));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
