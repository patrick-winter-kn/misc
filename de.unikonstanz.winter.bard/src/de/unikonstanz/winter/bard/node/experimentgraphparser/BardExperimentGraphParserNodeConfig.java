package de.unikonstanz.winter.bard.node.experimentgraphparser;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * @author Patrick Winter, University of Konstanz
 */
class BardExperimentGraphParserNodeConfig {

	private static final String PID_COLUMN_CFG = "pidColumn";
	
	private static final String SVG_COLUMN_CFG = "svgColumn";

	private static final String PID_COLUMN_DEFAULT = "";
	
	private static final String SVG_COLUMN_DEFAULT = "";

	private String m_pidColumn = PID_COLUMN_DEFAULT;
	
	private String m_svgColumn = SVG_COLUMN_DEFAULT;

	/**
	 * @return The pidColumn
	 */
	public String getPidColumn() {
		return m_pidColumn;
	}

	/**
	 * @param pidColumn
	 *            The pidColumn to set
	 */
	public void setPidColumn(final String pidColumn) {
		m_pidColumn = pidColumn;
	}

	/**
	 * @return The svgColumn
	 */
	public String getSvgColumn() {
		return m_svgColumn;
	}

	/**
	 * @param svgColumn
	 *            The svgColumn to set
	 */
	public void setSvgColumn(final String svgColumn) {
		m_svgColumn = svgColumn;
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
		m_pidColumn = settings.getString(PID_COLUMN_CFG);
		m_svgColumn = settings.getString(SVG_COLUMN_CFG);
	}

	/**
	 * Load settings or use defaults if loading fails.
	 * 
	 * @param settings
	 *            The settings to load from
	 */
	public void loadInDialog(final NodeSettingsRO settings) {
		m_pidColumn = settings.getString(PID_COLUMN_CFG, PID_COLUMN_DEFAULT);
		m_svgColumn = settings.getString(SVG_COLUMN_CFG, SVG_COLUMN_DEFAULT);
	}

	/**
	 * Save current settings.
	 * 
	 * @param settings
	 *            The settings to save into
	 */
	public void save(final NodeSettingsWO settings) {
		settings.addString(PID_COLUMN_CFG, m_pidColumn);
		settings.addString(SVG_COLUMN_CFG, m_svgColumn);
	}

}
