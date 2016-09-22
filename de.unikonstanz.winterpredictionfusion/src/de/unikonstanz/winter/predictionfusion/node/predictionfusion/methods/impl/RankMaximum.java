package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.AbstractPredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

public class RankMaximum extends AbstractPredictionFusionMethod {
	
	public static final String NAME = "Rank Maximum";
	public static final String NAME_INVERTED = "Rank Minimum";
	
	private boolean m_inverted;

	public RankMaximum(final String positiveClass, final String negativeClass, final boolean inverted, final ExecutionMonitor exec) {
		super(positiveClass, negativeClass, exec);
		m_inverted = inverted;
	}

	@Override
	public List<Prediction> fusePredictions(final List<List<Prediction>> predictions) {
		List<Prediction> fusedPredictions = new ArrayList<Prediction>(predictions.get(0).size());
		for (List<Prediction> predictionList : predictions) {
			PredictionFusionUtil.assignRanks(predictionList);
		}
		for (int rowNr = 0; rowNr < predictions.get(0).size(); rowNr++) {
			getExecutionMonitor().setProgress(rowNr/(double)fusedPredictions.size());
			double bestRank = m_inverted ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			for (int predictionNr = 0; predictionNr < predictions.size(); predictionNr++) {
				Prediction prediction = predictions.get(predictionNr).get(rowNr);
				double rank = prediction.getRank();
				if ((!m_inverted && rank > bestRank) || (m_inverted && rank < bestRank)) {
					bestRank = rank;
				}
			}
			fusedPredictions.add(new Prediction(rowNr, false, null, null, bestRank));
		}
		PredictionFusionUtil.assignRanks(fusedPredictions);
		return fusedPredictions;
	}

}
