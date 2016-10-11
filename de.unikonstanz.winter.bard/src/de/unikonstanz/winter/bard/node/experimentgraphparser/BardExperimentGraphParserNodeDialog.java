package de.unikonstanz.winter.bard.node.experimentgraphparser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.base.data.xml.SvgValue;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.IntValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class BardExperimentGraphParserNodeDialog extends NodeDialogPane {

	@SuppressWarnings("unchecked")
	private ColumnSelectionComboxBox m_cidColumn = new ColumnSelectionComboxBox((Border) null, IntValue.class);

	@SuppressWarnings("unchecked")
	private ColumnSelectionComboxBox m_svgColumn = new ColumnSelectionComboxBox((Border) null, SvgValue.class);

	/**
	 * Create this nodes dialog.
	 */
	public BardExperimentGraphParserNodeDialog() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel("PID column"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_cidColumn, gbc);
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.gridy++;
		panel.add(new JLabel("Graph column"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_svgColumn, gbc);
		addTab("Config", panel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
		BardExperimentGraphParserNodeConfig config = new BardExperimentGraphParserNodeConfig();
		config.setPidColumn(m_cidColumn.getSelectedColumn());
		config.setSvgColumn(m_svgColumn.getSelectedColumn());
		config.save(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
			throws NotConfigurableException {
		BardExperimentGraphParserNodeConfig config = new BardExperimentGraphParserNodeConfig();
		config.loadInDialog(settings);
		m_cidColumn.update(specs[0], config.getPidColumn());
		m_svgColumn.update(specs[0], config.getSvgColumn());
	}

}
