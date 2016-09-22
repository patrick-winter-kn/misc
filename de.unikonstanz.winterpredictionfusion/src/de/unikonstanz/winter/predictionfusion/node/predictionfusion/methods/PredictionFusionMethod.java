package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods;

import java.util.List;

public interface PredictionFusionMethod {
	
	List<Prediction> fusePredictions(List<List<Prediction>> predictions);

}
