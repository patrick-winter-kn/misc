package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class Maximum extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Maximum";
	public static final String NAME_INVERTED = "Minimum";
	
	private boolean m_inverted;

	public Maximum(final String positiveClass, final String negativeClass, final boolean inverted, final ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
		m_inverted = inverted;
	}

	@Override
	public List<Prediction> fusePredictions(final List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			double bestScore = m_inverted ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				Prediction prediction = predictions.get(predictionNr).get(rowNr);
				double score = getPositiveClass().equals(prediction.getPrediction()) ? prediction.getConfidence() : 1 - prediction.getConfidence();
				if ((!m_inverted && score > bestScore) || (m_inverted && score < bestScore)) {
					bestScore = score;
				}
			}
			String predictionClass = bestScore >= 0.5 ? getPositiveClass() : getNegativeClass();
			double confidence = bestScore >= 0.5 ? bestScore : 1 - bestScore;
			fusedPredictions.add(new Prediction(rowNr, getPositiveClass().equals(predictionClass), predictionClass, confidence, null));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
