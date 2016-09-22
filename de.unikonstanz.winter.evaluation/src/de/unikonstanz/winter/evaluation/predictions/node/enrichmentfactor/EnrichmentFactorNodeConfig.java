package de.unikonstanz.winter.evaluation.predictions.node.enrichmentfactor;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * @author Patrick Winter, University of Konstanz
 */
class EnrichmentFactorNodeConfig {

	private static final String CLASS_COLUMN_CFG = "classColumn";
	private static final String SORT_COLUMN_CFG = "sortColumn";
	private static final String POSITIVE_CLASS_CFG = "positiveClass";
	private static final String DESCENDING_CFG = "descending";
	private static final String NUMBER_OF_FRACTIONS_CFG = "numberOfFractions";
	
	private static final String CLASS_COLUMN_DEFAULT = "";
	private static final String SORT_COLUMN_DEFAULT = "";
	private static final String POSITIVE_CLASS_DEFAULT = "";
	private static final boolean DESCENDING_DEFAULT = false;
	private static final int NUMBER_OF_FRACTIONS_DEFAULT = 100;
	
	private String m_classColumn = CLASS_COLUMN_DEFAULT;
	private String m_sortColumn = SORT_COLUMN_DEFAULT;
	private String m_positiveClass = POSITIVE_CLASS_DEFAULT;
	private boolean m_descending = DESCENDING_DEFAULT;
	private int m_numberOfFractions = NUMBER_OF_FRACTIONS_DEFAULT;
	
	public String getClassColumn() {
		return m_classColumn;
	}
	
	public void setClassColumn(final String classColumn) {
		m_classColumn = classColumn;
	}
	
	public String getSortColumn() {
		return m_sortColumn;
	}
	
	public void setSortColumn(final String sortColumn) {
		m_sortColumn = sortColumn;
	}
	
	public String getPositiveClass() {
		return m_positiveClass;
	}
	
	public void setPositiveClass(final String positiveClass) {
		m_positiveClass = positiveClass;
	}
	
	public boolean getDescending() {
		return m_descending;
	}
	
	public void setDescending(final boolean descending) {
		m_descending = descending;
	}
	
	public int getNumberOfFractions() {
		return m_numberOfFractions;
	}
	
	public void setNumberOfFractions(final int numberOfFractions) {
		m_numberOfFractions = numberOfFractions;
	}
	
	public Double[] getFractions() {
		List<Double> fractions = new ArrayList<Double>();
		for (int i=0; i < m_numberOfFractions; i++) {
			fractions.add((i+1)/(double)m_numberOfFractions);
		}
		return fractions.toArray(new Double[fractions.size()]);
	}
	
	public void load(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_classColumn = settings.getString(CLASS_COLUMN_CFG);
		m_sortColumn = settings.getString(SORT_COLUMN_CFG);
		m_positiveClass = settings.getString(POSITIVE_CLASS_CFG);
		m_descending = settings.getBoolean(DESCENDING_CFG);
		m_numberOfFractions = settings.getInt(NUMBER_OF_FRACTIONS_CFG);
	}

	public void loadInDialog(final NodeSettingsRO settings) {
		m_classColumn = settings.getString(CLASS_COLUMN_CFG, CLASS_COLUMN_DEFAULT);
		m_sortColumn = settings.getString(SORT_COLUMN_CFG, SORT_COLUMN_DEFAULT);
		m_positiveClass = settings.getString(POSITIVE_CLASS_CFG, POSITIVE_CLASS_DEFAULT);
		m_descending = settings.getBoolean(DESCENDING_CFG, DESCENDING_DEFAULT);
		m_numberOfFractions = settings.getInt(NUMBER_OF_FRACTIONS_CFG, NUMBER_OF_FRACTIONS_DEFAULT);
	}

	public void save(final NodeSettingsWO settings) {
		settings.addString(CLASS_COLUMN_CFG, m_classColumn);
		settings.addString(SORT_COLUMN_CFG, m_sortColumn);
		settings.addString(POSITIVE_CLASS_CFG, m_positiveClass);
		settings.addBoolean(DESCENDING_CFG, m_descending);
		settings.addInt(NUMBER_OF_FRACTIONS_CFG, m_numberOfFractions);
	}

}
