package de.unikonstanz.winter.crossref.node.doi;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class CrossrefDoiNodeDialog extends NodeDialogPane {

	@SuppressWarnings("unchecked")
	private ColumnSelectionComboxBox m_doiColumn = new ColumnSelectionComboxBox((Border) null, StringValue.class);

	/**
	 * Create this nodes dialog.
	 */
	public CrossrefDoiNodeDialog() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel("DOI column"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_doiColumn, gbc);
		addTab("Config", panel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
		CrossrefDoiNodeConfig config = new CrossrefDoiNodeConfig();
		config.setDoiColumn(m_doiColumn.getSelectedColumn());
		config.save(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
			throws NotConfigurableException {
		CrossrefDoiNodeConfig config = new CrossrefDoiNodeConfig();
		config.loadInDialog(settings);
		m_doiColumn.update(specs[0], config.getDoiColumn());
	}

}
