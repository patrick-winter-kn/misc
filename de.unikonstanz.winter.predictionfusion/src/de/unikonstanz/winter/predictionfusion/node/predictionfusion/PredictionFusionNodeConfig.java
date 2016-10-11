package de.unikonstanz.winter.predictionfusion.node.predictionfusion;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * @author Patrick Winter, University of Konstanz
 */
class PredictionFusionNodeConfig {
	
	private static final String METHOD_CFG = "method";
	private static final String POSITIVE_CLASS_CFG = "positiveClass";
	private static final String PREDICTION_COLUMNS_CFG = "predictionColumns";
	private static final String PREDICTION_CONFIDENCE_COLUMNS_CFG = "predictionConfidenceColumns";

	private static final String METHOD_DEFAULT = "";
	private static final String POSITIVE_CLASS_DEFAULT = "";
	private static final String[] PREDICTION_COLUMNS_DEFAULT = new String[]{"",""};
	private static final String[] PREDICTION_CONFIDENCE_COLUMNS_DEFAULT = new String[]{"",""};

	private String m_method = METHOD_DEFAULT;
	private String m_positiveClass = POSITIVE_CLASS_DEFAULT;
	private String[] m_predictionColumns = PREDICTION_COLUMNS_DEFAULT;
	private String[] m_predictionConfidenceColumns = PREDICTION_CONFIDENCE_COLUMNS_DEFAULT;
	
	public String getMethod() {
		return m_method;
	}
	
	public void setMethod(final String method) {
		m_method = method;
	}
	
	public String getPositiveClass() {
		return m_positiveClass;
	}
	
	public void setPositiveClass(final String positiveClass) {
		m_positiveClass = positiveClass;
	}
	
	public String[] getPredictionColumns() {
		return m_predictionColumns;
	}
	
	public void setPredictionColumns(final String[] predictionColumns) {
		m_predictionColumns = predictionColumns;
	}
	
	public String[] getPredictionConfidenceColumns() {
		return m_predictionConfidenceColumns;
	}
	
	public void setPredictionConfidenceColumns(final String[] predictionConfidenceColumns) {
		m_predictionConfidenceColumns = predictionConfidenceColumns;
	}
	
	public void load(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_method = settings.getString(METHOD_CFG);
		m_positiveClass = settings.getString(POSITIVE_CLASS_CFG);
		m_predictionColumns = settings.getStringArray(PREDICTION_COLUMNS_CFG);
		m_predictionConfidenceColumns = settings.getStringArray(PREDICTION_CONFIDENCE_COLUMNS_CFG);
	}

	public void loadWithDefaults(final NodeSettingsRO settings) {
		m_method = settings.getString(METHOD_CFG, METHOD_DEFAULT);
		m_positiveClass = settings.getString(POSITIVE_CLASS_CFG, POSITIVE_CLASS_DEFAULT);
		m_predictionColumns = settings.getStringArray(PREDICTION_COLUMNS_CFG, PREDICTION_COLUMNS_DEFAULT);
		m_predictionConfidenceColumns = settings.getStringArray(PREDICTION_CONFIDENCE_COLUMNS_CFG, PREDICTION_CONFIDENCE_COLUMNS_DEFAULT);
	}

	public void save(final NodeSettingsWO settings) {
		settings.addString(METHOD_CFG, m_method);
		settings.addString(POSITIVE_CLASS_CFG, m_positiveClass);
		settings.addStringArray(PREDICTION_COLUMNS_CFG, m_predictionColumns);
		settings.addStringArray(PREDICTION_CONFIDENCE_COLUMNS_CFG, m_predictionConfidenceColumns);
	}

}
