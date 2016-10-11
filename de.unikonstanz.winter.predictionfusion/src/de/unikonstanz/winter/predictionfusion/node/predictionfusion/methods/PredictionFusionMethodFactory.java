package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods;

import org.knime.core.node.ExecutionMonitor;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.Maximum;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.Mean;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.Median;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.RankMaximum;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.RankMean;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.impl.RankMedian;

public class PredictionFusionMethodFactory {
	
	public static String[] getAvailablePredictionFusionMethods() {
		return new String[]{Maximum.NAME, Mean.NAME, Median.NAME, Maximum.NAME_INVERTED, RankMaximum.NAME, RankMean.NAME, RankMedian.NAME, RankMaximum.NAME_INVERTED};
	}
	
	public static PredictionFusionMethod getPredictionFusionMethod(final String methodName, final String positiveClass, final String negativeClass, final ExecutionMonitor exec) {
		if (Maximum.NAME.equals(methodName)) {
			return new Maximum(positiveClass, negativeClass, false, exec);
		}
		if (Mean.NAME.equals(methodName)) {
			return new Mean(positiveClass, negativeClass, exec);
		}
		if (Median.NAME.equals(methodName)) {
			return new Median(positiveClass, negativeClass, exec);
		}
		if (Maximum.NAME_INVERTED.equals(methodName)) {
			return new Maximum(positiveClass, negativeClass, true, exec);
		}
		if (RankMaximum.NAME.equals(methodName)) {
			return new RankMaximum(positiveClass, negativeClass, false, exec);
		}
		if (RankMean.NAME.equals(methodName)) {
			return new RankMean(positiveClass, negativeClass, exec);
		}
		if (RankMedian.NAME.equals(methodName)) {
			return new RankMedian(positiveClass, negativeClass, exec);
		}
		if (RankMaximum.NAME_INVERTED.equals(methodName)) {
			return new RankMaximum(positiveClass, negativeClass, true, exec);
		}
		return null;
	}

}
