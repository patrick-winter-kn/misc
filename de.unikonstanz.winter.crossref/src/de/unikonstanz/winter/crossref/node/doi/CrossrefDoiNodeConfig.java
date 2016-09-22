package de.unikonstanz.winter.crossref.node.doi;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * @author Patrick Winter, University of Konstanz
 */
class CrossrefDoiNodeConfig {

	private static final String DOI_COLUMN_CFG = "doiColumn";

	private static final String DOI_COLUMN_DEFAULT = "";

	private String m_doiColumn = DOI_COLUMN_DEFAULT;

	/**
	 * @return The doiColumn
	 */
	public String getDoiColumn() {
		return m_doiColumn;
	}

	/**
	 * @param doiColumn
	 *            The doiColumn to set
	 */
	public void setDoiColumn(final String doiColumn) {
		m_doiColumn = doiColumn;
	}

	/**
	 * Load previous settings.
	 * 
	 * @param settings
	 *            The settings to load from
	 * @throws InvalidSettingsException
	 *             If the settings are invalid
	 */
	public void load(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_doiColumn = settings.getString(DOI_COLUMN_CFG);
	}

	/**
	 * Load settings or use defaults if loading fails.
	 * 
	 * @param settings
	 *            The settings to load from
	 */
	public void loadInDialog(final NodeSettingsRO settings) {
		m_doiColumn = settings.getString(DOI_COLUMN_CFG, DOI_COLUMN_DEFAULT);
	}

	/**
	 * Save current settings.
	 * 
	 * @param settings
	 *            The settings to save into
	 */
	public void save(final NodeSettingsWO settings) {
		settings.addString(DOI_COLUMN_CFG, m_doiColumn);
	}

}
