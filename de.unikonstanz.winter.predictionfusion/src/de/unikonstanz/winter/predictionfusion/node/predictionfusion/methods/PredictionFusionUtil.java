package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionMonitor;

public class PredictionFusionUtil {
	
	@SuppressWarnings("deprecation")
	public static List<List<Prediction>> extractPredictions(final BufferedDataTable table, final List<String> predictionColumns, final List<String> predictionConfidenceColumns, final String positiveClass, final ExecutionMonitor exec) {
		List<List<Prediction>> predictions = new ArrayList<List<Prediction>>(predictionColumns.size());
		for (int i = 0; i < predictionColumns.size(); i++) {
			predictions.add(new ArrayList<Prediction>(table.getRowCount()));
		}
		List<Integer> predictionColumnIndexes = new ArrayList<Integer>(predictionColumns.size());
		for (String predictionColumn : predictionColumns) {
			predictionColumnIndexes.add(table.getDataTableSpec().findColumnIndex(predictionColumn));
		}
		List<Integer> predictionConfidenceColumnIndexes = new ArrayList<Integer>(predictionConfidenceColumns.size());
		for (String predictionConfidenceColumn : predictionConfidenceColumns) {
			predictionConfidenceColumnIndexes.add(table.getDataTableSpec().findColumnIndex(predictionConfidenceColumn));
		}
		int rowNr = 0;
		for (DataRow row : table) {
			exec.setProgress(rowNr/(double)table.getRowCount());
			for (int predictionNr = 0; predictionNr < predictionColumnIndexes.size(); predictionNr++) {
				Prediction prediction = getPrediction(rowNr, row, predictionColumnIndexes.get(predictionNr), predictionConfidenceColumnIndexes.get(predictionNr), positiveClass);
				predictions.get(predictionNr).add(prediction);
				rowNr++;
			}
		}
		return predictions;
	}
	
	private static Prediction getPrediction(final int rowNr, final DataRow row, final int predictionColumnIndex, final int predictionConfidenceColumnIndex, final String positiveClass) {
		String prediction = null;
		Double predictionConfidence = null;
		DataCell predictionCell = row.getCell(predictionColumnIndex);
		if (predictionCell != null && !predictionCell.isMissing()) {
			prediction = ((StringValue)predictionCell).getStringValue();
		}
		if (predictionConfidenceColumnIndex >= 0) {
			DataCell predictionConfidenceCell = row.getCell(predictionConfidenceColumnIndex);
			if (predictionConfidenceCell != null && !predictionConfidenceCell.isMissing()) {
				predictionConfidence = ((DoubleValue)predictionConfidenceCell).getDoubleValue();
			}
		}
		return new Prediction(rowNr, positiveClass.equals(prediction), prediction, predictionConfidence, null);
	}
	
	public static void assignRanks(final List<Prediction> predictions) {
		List<Prediction> sortedPredictions = new ArrayList<Prediction>(predictions);
		Collections.sort(sortedPredictions);
		for (int i = 0; i < sortedPredictions.size(); i++) {
			sortedPredictions.get(i).setRank((double)i + 1);
		}
	}

}
